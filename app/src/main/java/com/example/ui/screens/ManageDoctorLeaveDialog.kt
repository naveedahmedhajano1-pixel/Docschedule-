package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Doctor
import com.example.ui.theme.MedicalRedError
import com.example.ui.theme.MedicalRedErrorContainer
import com.example.ui.theme.MedicalRedOnErrorContainer
import com.example.ui.theme.StatusSittingIn
import com.example.ui.theme.StatusSittingInBg
import com.example.ui.theme.StatusSittingInText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ManageDoctorLeaveDialog(
    doctor: Doctor,
    onDismiss: () -> Unit,
    onSaveLeaveStatus: (
        isOnLeave: Boolean,
        reason: String?,
        startDate: String?,
        endDate: String?,
        returnDate: String?,
        notes: String?
    ) -> Unit,
    onQuickSittingStatusChange: (newStatus: String) -> Unit
) {
    var isOnLeave by remember { mutableStateOf(doctor.isOnLeave) }
    var leaveReason by remember {
        mutableStateOf(doctor.leaveReason ?: "Attending Medical Conference & CME Training")
    }
    var startDate by remember {
        mutableStateOf(doctor.leaveStartDate ?: "Sep 24, 2026")
    }
    var endDate by remember {
        mutableStateOf(doctor.leaveEndDate ?: "Sep 28, 2026")
    }
    var returnDate by remember {
        mutableStateOf(doctor.leaveReturnDate ?: "Monday, Sep 29, 2026")
    }
    var leaveNotes by remember {
        mutableStateOf(doctor.leaveNotes ?: "Prescriptions and emergency cover available at OPD desk.")
    }

    val predefinedReasons = listOf(
        "Medical Conference & Surgery Workshop",
        "Annual Leave / Vacation",
        "Emergency Medical Leave",
        "Sabbatical & Research Program",
        "Out of Station / Traveling"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .imePadding()
                .testTag("manage_leave_dialog"),
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Manage Duty & Leave",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${doctor.name} • ${doctor.roomNumber}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_leave_dialog_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sitting In / Sitting Out Quick Toggles (When NOT on leave)
                if (!isOnLeave) {
                    Text(
                        text = "REAL-TIME SITTING STATUS",
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
                        // Sitting In
                        val isSittingIn = doctor.sittingStatus == "SITTING_IN"
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onQuickSittingStatusChange("SITTING_IN") },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSittingIn) StatusSittingInBg else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isSittingIn) StatusSittingInText else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Sitting In",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSittingIn) StatusSittingInText else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "In Cabin Now",
                                        fontSize = 10.sp,
                                        color = if (isSittingIn) StatusSittingInText else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Sitting Out
                        val isSittingOut = doctor.sittingStatus == "SITTING_OUT"
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onQuickSittingStatusChange("SITTING_OUT") },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSittingOut) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (isSittingOut) MaterialTheme.colorScheme.onTertiaryContainer else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Sitting Out",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSittingOut) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Next Shift",
                                        fontSize = 10.sp,
                                        color = if (isSittingOut) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Primary Feature: "Doctor is on leave" Master Switch
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOnLeave) MedicalRedErrorContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnLeave) MedicalRedError else MaterialTheme.colorScheme.outline),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isOnLeave) Icons.Default.EventBusy else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Doctor is On Leave",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOnLeave) MedicalRedOnErrorContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isOnLeave) "Consultations paused, leave badge visible" else "Doctor is active for appointments",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isOnLeave) MedicalRedOnErrorContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isOnLeave,
                            onCheckedChange = { isOnLeave = it },
                            modifier = Modifier.testTag("toggle_doctor_leave_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MedicalRedError
                            )
                        )
                    }
                }

                // If marked ON LEAVE, show details configuration
                if (isOnLeave) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "LEAVE REASON",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Reason Quick Chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        predefinedReasons.forEach { reason ->
                            FilterChip(
                                selected = leaveReason == reason,
                                onClick = { leaveReason = reason },
                                label = { Text(text = reason, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MedicalRedErrorContainer,
                                    selectedLabelColor = MedicalRedOnErrorContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = leaveReason,
                        onValueChange = { leaveReason = it },
                        label = { Text("Custom Reason") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("leave_reason_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dates: Start, End, Return Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("Leave From") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("leave_start_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = { Text("Leave Until") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("leave_end_input"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = returnDate,
                        onValueChange = { returnDate = it },
                        label = { Text("Expected Return to Clinic Date") },
                        placeholder = { Text("e.g. Monday, Sep 29, 2026") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("leave_return_date_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = leaveNotes,
                        onValueChange = { leaveNotes = it },
                        label = { Text("Notice for Patients & Emergency Cover") },
                        placeholder = { Text("e.g. Dr. Marcus Bell is covering emergencies in Room 112.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("leave_notes_input"),
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onSaveLeaveStatus(
                                isOnLeave,
                                if (isOnLeave) leaveReason else null,
                                if (isOnLeave) startDate else null,
                                if (isOnLeave) endDate else null,
                                if (isOnLeave) returnDate else null,
                                if (isOnLeave) leaveNotes else null
                            )
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("save_leave_status_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOnLeave) MedicalRedError else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isOnLeave) "Set Doctor On Leave" else "Save as Active Duty",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
