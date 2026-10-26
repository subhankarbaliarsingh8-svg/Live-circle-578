package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.UserProfile
import com.example.ui.theme.SaffronOrange

@Composable
fun EditProfileDialog(
    user: UserProfile,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onSave: (displayName: String, bio: String, avatarUrl: String) -> Unit
) {
    var nameInput by remember { mutableStateOf(user.displayName) }
    var bioInput by remember { mutableStateOf(user.bio) }
    var avatarInput by remember { mutableStateOf(user.avatarUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isHindi) "प्रोफ़ाइल संपादित करें" else "Edit Profile",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(if (isHindi) "नाम" else "Display Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bioInput,
                    onValueChange = { bioInput = it },
                    label = { Text(if (isHindi) "बायो" else "Bio") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_bio_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = avatarInput,
                    onValueChange = { avatarInput = it },
                    label = { Text(if (isHindi) "अवतार इमेज URL" else "Avatar Image URL") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_avatar_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(nameInput, bioInput, avatarInput) },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronOrange),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text(if (isHindi) "सहेजें" else "Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isHindi) "रद्द करें" else "Cancel")
            }
        }
    )
}
