package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class SupabaseStatus {
    NOT_CONFIGURED,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class SupabaseConnectionState(
    val status: SupabaseStatus = SupabaseStatus.NOT_CONFIGURED,
    val url: String = "",
    val anonKey: String = "",
    val message: String = "Supabase credentials are not configured yet."
)

class SupabaseClient private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("desiconnect_supabase_prefs", Context.MODE_PRIVATE)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    var supabaseUrl: String = prefs.getString(PREF_URL, "") ?: ""
        private set

    var supabaseAnonKey: String = prefs.getString(PREF_KEY, "") ?: ""
        private set

    var authToken: String? = prefs.getString(PREF_AUTH_TOKEN, null)
        private set

    val isConfigured: Boolean
        get() = supabaseUrl.isNotBlank() &&
                supabaseAnonKey.isNotBlank() &&
                !supabaseUrl.contains("YOUR_") &&
                supabaseUrl.startsWith("http")

    fun updateConfig(url: String, anonKey: String) {
        supabaseUrl = url.trim().removeSuffix("/")
        supabaseAnonKey = anonKey.trim()
        prefs.edit()
            .putString(PREF_URL, supabaseUrl)
            .putString(PREF_KEY, supabaseAnonKey)
            .apply()
    }

    fun setAuthToken(token: String?) {
        authToken = token
        prefs.edit().putString(PREF_AUTH_TOKEN, token).apply()
    }

    suspend fun checkHealth(): Result<String> = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            return@withContext Result.failure(
                IllegalStateException("Supabase URL and Anon Key are missing. Please configure them in setup.")
            )
        }
        try {
            val request = Request.Builder()
                .url("$supabaseUrl/rest/v1/")
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer $supabaseAnonKey")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful || response.code == 200 || response.code == 404) {
                Result.success("Connected to Supabase successfully!")
            } else {
                Result.failure(Exception("Supabase returned HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Health check failed", e)
            Result.failure(e)
        }
    }

    suspend fun postToTable(table: String, jsonBody: String): Result<String> = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            return@withContext Result.failure(IllegalStateException("Supabase not configured"))
        }
        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBuilder = Request.Builder()
                .url("$supabaseUrl/rest/v1/$table")
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${authToken ?: supabaseAnonKey}")
                .addHeader("Prefer", "return=representation")
                .post(jsonBody.toRequestBody(mediaType))

            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            val body = response.body?.string() ?: ""
            if (response.isSuccessful) {
                Result.success(body)
            } else {
                Result.failure(Exception("HTTP ${response.code}: $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val PREF_URL = "supabase_url"
        private const val PREF_KEY = "supabase_anon_key"
        private const val PREF_AUTH_TOKEN = "supabase_auth_token"

        @Volatile
        private var instance: SupabaseClient? = null

        fun getInstance(context: Context): SupabaseClient {
            return instance ?: synchronized(this) {
                instance ?: SupabaseClient(context.applicationContext).also { instance = it }
            }
        }
    }
}
