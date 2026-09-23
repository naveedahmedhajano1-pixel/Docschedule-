package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Doctor
import com.example.ui.theme.AccentAmberTertiary
import com.example.ui.theme.ClinicalBlueSecondary
import com.example.ui.theme.MedicalRedError
import com.example.ui.theme.MedicalRedErrorContainer
import com.example.ui.theme.MedicalRedOnErrorContainer
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.MedicalTealPrimaryContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingDialog(
    doctor: Doctor,
    onDismiss: () -> Unit,
    onConfirmBooking: (
        patientName: String,
        patientPhone: String,
        patientAge: Int,
        date: String,
        timeSlot: String,
        shift: String,
        type: String,
        symptoms: String
    ) -> Unit
) {
    // Dates available for booking
    val dates = listOf(
        BookingDateOption("Today", "Sep 24", isWithinCurrentLeave = doctor.isOnLeave),
        BookingDateOption("Tomorrow", "Sep 25", isWithinCurrentLeave = doctor.isOnLeave),
        BookingDateOption("Fri", "Sep 26", isWithinCurrentLeave = doctor.isOnLeave && (doctor.leaveEndDate?.contains("26") == true || doctor.leaveEndDate?.contains("28") == true)),
        BookingDateOption("Sat", "Sep 27", isWithinCurrentLeave = doctor.isOnLeave && doctor.leaveEndDate?.contains("28") == true),
        BookingDateOption("Mon", "Sep 29", isWithinCurrentLeave = false),
        BookingDateOption("Tue", "Sep 30", isWithinCurrentLeave = false),
        BookingDateOption("Wed", "Oct 01", isWithinCurrentLeave = false)
    )

    var selectedDateIndex by remember {
        // If doctor is on leave, default to the first available post-leave date
        val firstAvailable = dates.indexOfFirst { !it.isWithinCurrentLeave }
        mutableStateOf(if (firstAvailable != -1 && doctor.isOnLeave) firstAvailable else 0)
    }

    val selectedDate = dates.getOrElse(selectedDateIndex) { dates[0] }
    val isSelectedDateOnLeave = selectedDate.isWithinCurrentLeave

    // Consultation shifts slots based on doctor's in/out sitting timings
    val morningSlots = listOf("09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:15 AM", "12:00 PM")
    val eveningSlots = listOf("05:00 PM", "05:30 PM", "06:15 PM", "07:00 PM", "07:45 PM", "08:15 PM")

    var selectedShift by remember { mutableStateOf("Morning") }
    var selectedTimeSlot by remember { mutableStateOf(morningSlots[0]) }

    var patientName by remember { mutableStateOf("") }
    var patientPhone by remember { mutableStateOf("") }
    var patientAge by remember { mutableStateOf("28") }
    var appointmentType by remember { mutableStateOf("General Consultation") }
    var symptomsNotes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val visitTypes = listOf("General Consultation", "Follow-up", "Report Review", "Priority Visit")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .imePadding()
                .testTag("booking_dialog"),
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
                            text = "Book Appointment",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${doctor.name} (${doctor.specialty})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_booking_dialog_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sitting hours & cabin indicator
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
                        Icon(
                            imageVector = Icons.Default.MeetingRoom,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = doctor.roomNumber,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "Fee: $${doctor.consultationFee}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date Selection
                Text(
                    text = "SELECT APPOINTMENT DATE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dates.indices.toList()) { index ->
                        val dateOpt = dates[index]
                        val isSelected = selectedDateIndex == index
                        val dateOnLeave = dateOpt.isWithinCurrentLeave

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    when {
                                        isSelected && dateOnLeave -> MedicalRedErrorContainer
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        dateOnLeave -> MedicalRedErrorContainer.copy(alpha = 0.4f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    }
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = when {
                                        isSelected && dateOnLeave -> MedicalRedError
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        dateOnLeave -> MedicalRedError.copy(alpha = 0.4f)
                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    },
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedDateIndex = index
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dateOpt.dayLabel,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isSelected && dateOnLeave -> MedicalRedOnErrorContainer
                                        isSelected -> Color.White
                                        dateOnLeave -> MedicalRedOnErrorContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                Text(
                                    text = dateOpt.dateText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isSelected && dateOnLeave -> MedicalRedOnErrorContainer
                                        isSelected -> Color.White
                                        dateOnLeave -> MedicalRedOnErrorContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (dateOnLeave) {
                                    Text(
                                        text = "On Leave",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MedicalRedError
                                    )
                                }
                            }
                        }
                    }
                }

                // CRITICAL WARNING IF SELECTED DATE IS DURING LEAVE
                if (isSelectedDateOnLeave) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MedicalRedErrorContainer)
                            .border(1.dp, MedicalRedError.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MedicalRedError,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DOCTOR IS ON LEAVE ON THIS DATE",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MedicalRedOnErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Reason: ${doctor.leaveReason ?: "Scheduled Leave"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MedicalRedOnErrorContainer
                            )
                            if (!doctor.leaveReturnDate.isNullOrBlank()) {
                                Text(
                                    text = "Doctor returns on: ${doctor.leaveReturnDate}. Please select a date on or after return.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalRedOnErrorContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Shifts & Timing Slots
                Text(
                    text = "SELECT SITTING TIME SLOT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Shift Selector: Morning / Evening
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Morning Shift Tab
                    val isMorning = selectedShift == "Morning"
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedShift = "Morning"
                                selectedTimeSlot = morningSlots[0]
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMorning) AccentAmberTertiary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = if (isMorning) androidx.compose.foundation.BorderStroke(1.5.dp, AccentAmberTertiary) else null,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = AccentAmberTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Morning Shift", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${doctor.morningInTime} - ${doctor.morningOutTime}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Evening Shift Tab
                    val isEvening = selectedShift == "Evening"
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedShift = "Evening"
                                selectedTimeSlot = eveningSlots[0]
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEvening) ClinicalBlueSecondary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = if (isEvening) androidx.compose.foundation.BorderStroke(1.5.dp, ClinicalBlueSecondary) else null,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Nightlight,
                                contentDescription = null,
                                tint = ClinicalBlueSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Evening Shift", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${doctor.eveningInTime} - ${doctor.eveningOutTime}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Time Slots Grid
                val currentSlots = if (selectedShift == "Morning") morningSlots else eveningSlots
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentSlots.forEach { slot ->
                        val isSlotSelected = selectedTimeSlot == slot
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSlotSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .border(
                                    width = if (isSlotSelected) 1.5.dp else 1.dp,
                                    color = if (isSlotSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedTimeSlot = slot }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (isSlotSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = slot,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSlotSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSlotSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Visit Type
                Text(
                    text = "VISIT TYPE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    visitTypes.forEach { type ->
                        FilterChip(
                            selected = appointmentType == type,
                            onClick = { appointmentType = type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Patient Details Form
                Text(
                    text = "PATIENT DETAILS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = patientName,
                    onValueChange = {
                        patientName = it
                        errorMessage = null
                    },
                    label = { Text("Patient Full Name *") },
                    placeholder = { Text("e.g. Alex Miller") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patient_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = patientPhone,
                        onValueChange = { patientPhone = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("+1 (555) 000-0000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("patient_phone_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = patientAge,
                        onValueChange = { patientAge = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.7f)
                            .testTag("patient_age_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = symptomsNotes,
                    onValueChange = { symptomsNotes = it },
                    label = { Text("Reason for visit / Symptoms") },
                    placeholder = { Text("Brief description of symptoms or questions...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("symptoms_input"),
                    maxLines = 2
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Confirm Button
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
                            if (patientName.isBlank()) {
                                errorMessage = "Please enter patient name."
                                return@Button
                            }
                            if (isSelectedDateOnLeave) {
                                errorMessage = "Doctor is on leave on ${selectedDate.dayLabel} (${selectedDate.dateText}). Please select an available date."
                                return@Button
                            }
                            val ageInt = patientAge.toIntOrNull() ?: 30
                            onConfirmBooking(
                                patientName.trim(),
                                patientPhone.ifBlank { "+1 (555) 345-6789" },
                                ageInt,
                                "${selectedDate.dayLabel}, ${selectedDate.dateText}",
                                selectedTimeSlot,
                                selectedShift,
                                appointmentType,
                                symptomsNotes.trim()
                            )
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("confirm_booking_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelectedDateOnLeave) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isSelectedDateOnLeave) "Doctor On Leave" else "Confirm Booking",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private data class BookingDateOption(
    val dayLabel: String,
    val dateText: String,
    val isWithinCurrentLeave: Boolean
)
