package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a Patient Appointment booked with a Doctor.
 */
@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val doctorId: Int,
    val doctorName: String,
    val doctorSpecialty: String,
    val doctorRoom: String,
    val patientName: String,
    val patientPhone: String,
    val patientAge: Int,
    val appointmentDate: String, // e.g. "Sep 24, 2026"
    val appointmentTimeSlot: String, // e.g. "10:30 AM"
    val shift: String = "Morning", // "Morning" or "Evening"
    val appointmentType: String = "General Consultation",
    val symptomsNotes: String = "",
    val tokenNumber: String, // e.g. "TK-402"
    // "CONFIRMED", "COMPLETED", "CANCELLED", "DOCTOR_ON_LEAVE"
    val status: String = "CONFIRMED",
    val doctorOnLeaveAlert: Boolean = false,
    val bookedAtTimestamp: Long = System.currentTimeMillis()
)
