package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.SupabaseConnectionState
import com.example.data.remote.SupabaseStatus
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronOrange

@Composable
fun SupabaseSetupDialog(
    connectionState: SupabaseConnectionState,
    onDismiss: () -> Unit,
    onSaveAndConnect: (url: String, key: String) -> Unit
) {
    var urlInput by remember { mutableStateOf(connectionState.url) }
    var keyInput by remember { mutableStateOf(connectionState.anonKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PeacockTeal.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "Supabase",
                        tint = PeacockTeal,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Supabase Backend Setup",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Status indicator
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (connectionState.status) {
                        SupabaseStatus.CONNECTED -> PeacockTeal.copy(alpha = 0.12f)
                        SupabaseStatus.NOT_CONFIGURED -> SaffronOrange.copy(alpha = 0.12f)
                        SupabaseStatus.CONNECTING -> MaterialTheme.colorScheme.surfaceVariant
                        SupabaseStatus.ERROR -> MaterialTheme.colorScheme.errorContainer
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (connectionState.status) {
                                SupabaseStatus.CONNECTED -> Icons.Default.CheckCircle
                                SupabaseStatus.NOT_CONFIGURED -> Icons.Default.Warning
                                else -> Icons.Default.Cloud
                            },
                            contentDescription = "Status",
                            tint = when (connectionState.status) {
                                SupabaseStatus.CONNECTED -> PeacockTeal
                                SupabaseStatus.NOT_CONFIGURED -> SaffronOrange
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = connectionState.message,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Enter your Supabase project credentials. The schema migration is ready in supabase_schema.sql.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("Supabase Project URL") },
                    placeholder = { Text("https://your-ref.supabase.co") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("supabase_url_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    label = { Text("Anon Public Key") },
                    placeholder = { Text("eyJhbGciOi...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("supabase_anon_key_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveAndConnect(urlInput, keyInput) },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronOrange),
                modifier = Modifier.testTag("supabase_save_connect_button")
            ) {
                Text("Save & Connect")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
