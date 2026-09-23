package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Doctor
import com.example.ui.components.DoctorStatusBadge
import com.example.ui.theme.AccentAmberTertiary
import com.example.ui.theme.ClinicalBlueSecondary
import com.example.ui.theme.MedicalRedError
import com.example.ui.theme.MedicalRedErrorContainer
import com.example.ui.theme.MedicalRedOnErrorContainer
import com.example.ui.theme.StatusOnLeave
import com.example.ui.theme.StatusSittingIn
import com.example.ui.theme.StatusSittingOut

@Composable
fun LiveSittingBoardScreen(
    doctors: List<Doctor>,
    onDoctorClick: (Doctor) -> Unit,
    onManageDoctorClick: (Doctor) -> Unit,
    modifier: Modifier = Modifier
) {
    val sittingInCount = doctors.count { it.sittingStatus == "SITTING_IN" && !it.isOnLeave }
    val sittingOutCount = doctors.count { it.sittingStatus == "SITTING_OUT" && !it.isOnLeave }
    val onLeaveCount = doctors.count { it.isOnLeave }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_sitting_board_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary Header Card (Digital Hospital Lobby Display)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LIVE CLINIC SITTING & DUTY BOARD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Real-time cabin presence & leave notices",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // In Cabin
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StatusSittingIn.copy(alpha = 0.15f))
                                .padding(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("$sittingInCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StatusSittingIn)
                                Text("In Cabin", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = StatusSittingIn)
                            }
                        }

                        // Sitting Out
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StatusSittingOut.copy(alpha = 0.15f))
                                .padding(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("$sittingOutCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StatusSittingOut)
                                Text("Shift Break", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = StatusSittingOut)
                            }
                        }

                        // On Leave
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StatusOnLeave.copy(alpha = 0.15f))
                                .padding(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("$onLeaveCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StatusOnLeave)
                                Text("On Leave", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = StatusOnLeave)
                            }
                        }
                    }
                }
            }
        }

        // List of all Doctors with in/out timetable and live status
        items(doctors, key = { it.id }) { doctor ->
            val isLeave = doctor.isOnLeave

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("board_row_doctor_${doctor.id}")
                    .clickable { onDoctorClick(doctor) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Top Row: Doctor Info + Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(doctor.avatarColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = doctor.avatarInitials,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = doctor.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = doctor.specialty,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ClinicalBlueSecondary
                                )
                            }
                        }

                        DoctorStatusBadge(
                            status = doctor.sittingStatus,
                            isOnLeave = doctor.isOnLeave,
                            compact = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sitting In & Out Timetable Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Morning In/Out
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = AccentAmberTertiary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Morn: ${doctor.morningInTime} - ${doctor.morningOutTime}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Evening In/Out
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Nightlight, contentDescription = null, tint = ClinicalBlueSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Eve: ${doctor.eveningInTime} - ${doctor.eveningOutTime}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Leave notice or Cabin info
                    if (isLeave) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MedicalRedErrorContainer)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ON LEAVE: ${doctor.leaveReason ?: "Medical leave"} • Back on ${doctor.leaveReturnDate ?: "Notice"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalRedOnErrorContainer
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(doctor.roomNumber, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = doctor.nextAvailabilityText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
