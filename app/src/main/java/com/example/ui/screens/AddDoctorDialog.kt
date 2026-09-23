package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AddDoctorDialog(
    onDismiss: () -> Unit,
    onAddDoctor: (
        name: String,
        specialty: String,
        qualifications: String,
        experienceYears: Int,
        department: String,
        roomNumber: String,
        consultationFee: Int,
        morningIn: String,
        morningOut: String,
        eveningIn: String,
        eveningOut: String,
        sittingDays: String,
        isOnLeave: Boolean,
        leaveReason: String?,
        returnDate: String?,
        biography: String,
        clinicAddress: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("Cardiologist") }
    var qualifications by remember { mutableStateOf("MBBS, MD") }
    var experienceYears by remember { mutableStateOf("10") }
    var department by remember { mutableStateOf("Internal Medicine") }
    var roomNumber by remember { mutableStateOf("Cabin 206") }
    var consultationFee by remember { mutableStateOf("50") }
    var biography by remember { mutableStateOf("Experienced clinical specialist dedicated to comprehensive diagnostic evaluation and compassionate patient-centered healthcare.") }
    var clinicAddress by remember { mutableStateOf("DocSchedule Medical Plaza, Wing A, 450 Lexington Ave, New York, NY") }
    var morningIn by remember { mutableStateOf("09:00 AM") }
    var morningOut by remember { mutableStateOf("01:00 PM") }
    var eveningIn by remember { mutableStateOf("05:00 PM") }
    var eveningOut by remember { mutableStateOf("08:30 PM") }
    var sittingDays by remember { mutableStateOf("Mon - Sat") }
    var isOnLeave by remember { mutableStateOf(false) }
    var leaveReason by remember { mutableStateOf("Annual Medical Conference") }
    var returnDate by remember { mutableStateOf("Monday, Sep 29") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .imePadding()
                .testTag("add_doctor_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Doctor & Timings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Doctor Name *") },
                    placeholder = { Text("e.g. Dr. Arthur Conan") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_doctor_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = specialty,
                        onValueChange = { specialty = it },
                        label = { Text("Specialty") },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("new_doctor_specialty_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = { roomNumber = it },
                        label = { Text("Cabin / Room") },
                        modifier = Modifier
                            .weight(0.8f)
                            .testTag("new_doctor_room_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = qualifications,
                        onValueChange = { qualifications = it },
                        label = { Text("Qualifications") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = experienceYears,
                        onValueChange = { experienceYears = it },
                        label = { Text("Experience (Yrs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.7f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = consultationFee,
                        onValueChange = { consultationFee = it },
                        label = { Text("Fee ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.6f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Biography
                OutlinedTextField(
                    value = biography,
                    onValueChange = { biography = it },
                    label = { Text("Doctor Biography & Background") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Clinic Address
                OutlinedTextField(
                    value = clinicAddress,
                    onValueChange = { clinicAddress = it },
                    label = { Text("Clinic & Hospital Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Sitting In & Out Timings Group
                Text(
                    text = "SITTING IN & OUT CONSULTATION TIMINGS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = morningIn,
                        onValueChange = { morningIn = it },
                        label = { Text("Morning In") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = morningOut,
                        onValueChange = { morningOut = it },
                        label = { Text("Morning Out") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = eveningIn,
                        onValueChange = { eveningIn = it },
                        label = { Text("Evening In") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = eveningOut,
                        onValueChange = { eveningOut = it },
                        label = { Text("Evening Out") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sittingDays,
                    onValueChange = { sittingDays = it },
                    label = { Text("Working Sitting Days") },
                    placeholder = { Text("Mon - Sat") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Leave toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Doctor Currently On Leave?", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Flag if currently not available in clinic", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = isOnLeave, onCheckedChange = { isOnLeave = it })
                }

                if (isOnLeave) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = leaveReason,
                        onValueChange = { leaveReason = it },
                        label = { Text("Leave Reason") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = returnDate,
                        onValueChange = { returnDate = it },
                        label = { Text("Return Date") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Please provide doctor name."
                                return@Button
                            }
                            val exp = experienceYears.toIntOrNull() ?: 5
                            val fee = consultationFee.toIntOrNull() ?: 50
                            onAddDoctor(
                                name.trim(),
                                specialty.trim(),
                                qualifications.trim(),
                                exp,
                                department.trim(),
                                roomNumber.trim(),
                                fee,
                                morningIn.trim(),
                                morningOut.trim(),
                                eveningIn.trim(),
                                eveningOut.trim(),
                                sittingDays.trim(),
                                isOnLeave,
                                if (isOnLeave) leaveReason else null,
                                if (isOnLeave) returnDate else null,
                                biography.trim(),
                                clinicAddress.trim()
                            )
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("save_doctor_btn")
                    ) {
                        Text("Add Doctor", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
