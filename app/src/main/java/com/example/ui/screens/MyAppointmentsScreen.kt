package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.ui.theme.ClinicalBlueSecondary
import com.example.ui.theme.MedicalRedError
import com.example.ui.theme.MedicalRedErrorContainer
import com.example.ui.theme.MedicalRedOnErrorContainer
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.StatusSittingIn

@Composable
fun MyAppointmentsScreen(
    appointments: List<Appointment>,
    doctors: List<Doctor>,
    onCancelAppointment: (Int) -> Unit,
    onBookNewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cancelTargetId by remember { mutableStateOf<Int?>(null) }

    if (cancelTargetId != null) {
        AlertDialog(
            onDismissRequest = { cancelTargetId = null },
            title = { Text("Cancel Appointment?") },
            text = { Text("Are you sure you want to cancel this doctor appointment?") },
            confirmButton = {
                Button(
                    onClick = {
                        cancelTargetId?.let(onCancelAppointment)
                        cancelTargetId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { cancelTargetId = null }) {
                    Text("Keep Appointment")
                }
            }
        )
    }

    if (appointments.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("empty_appointments_view"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Appointments Booked",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Browse available doctors and their sitting timings to book your consultation slot.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onBookNewClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("empty_book_doctor_btn")
                ) {
                    Text("Browse Doctors & Timings")
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("my_appointments_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Doctor Appointments",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${appointments.count { it.status == "CONFIRMED" }} active bookings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onBookNewClick,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Book New", fontSize = 12.sp)
                    }
                }
            }

            items(appointments, key = { it.id }) { appointment ->
                val doctor = doctors.find { it.id == appointment.doctorId }
                val isDoctorOnLeave = doctor?.isOnLeave == true

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("appointment_card_${appointment.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header: Token Number + Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = appointment.tokenNumber,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = appointment.appointmentType,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Status Tag
                            val isCancelled = appointment.status == "CANCELLED"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isCancelled) MaterialTheme.colorScheme.surfaceVariant else StatusSittingIn.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isCancelled) "CANCELLED" else "CONFIRMED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCancelled) MaterialTheme.colorScheme.onSurfaceVariant else StatusSittingIn
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Doctor & Cabin Info
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = appointment.doctorName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = appointment.doctorSpecialty,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ClinicalBlueSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MeetingRoom,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = appointment.doctorRoom,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Timing & Date Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(appointment.appointmentDate, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${appointment.appointmentTimeSlot} (${appointment.shift})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // CRITICAL NOTICE: If doctor is currently marked on leave!
                        if (isDoctorOnLeave && appointment.status == "CONFIRMED") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MedicalRedErrorContainer)
                                    .border(1.dp, MedicalRedError.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = MedicalRedError,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Doctor is Currently On Leave",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MedicalRedOnErrorContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${doctor?.name} is on leave (${doctor?.leaveReason ?: "Medical leave"}). Returns ${doctor?.leaveReturnDate ?: "soon"}. Please check with reception for covered doctor or reschedule.",
                                        fontSize = 11.sp,
                                        color = MedicalRedOnErrorContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Patient info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Patient: ${appointment.patientName} (${appointment.patientAge} yrs)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (appointment.status == "CONFIRMED") {
                                OutlinedButton(
                                    onClick = { cancelTargetId = appointment.id },
                                    modifier = Modifier.height(34.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Cancel", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
