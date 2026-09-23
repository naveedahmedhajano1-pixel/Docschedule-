package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a Doctor in the clinic / hospital.
 * Includes sitting in & out timings, room/cabin assignment, and leave details.
 */
@Entity(tableName = "doctors")
data class Doctor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val specialty: String,
    val qualifications: String,
    val experienceYears: Int,
    val department: String,
    val roomNumber: String,
    val consultationFee: Int,
    val rating: Float = 4.8f,
    val reviewCount: Int = 120,
    // Biography & Clinic Details
    val biography: String = "Specialist physician dedicated to evidence-based clinical care and patient well-being.",
    val clinicAddress: String = "DocSchedule Medical Plaza, Wing A, 450 Lexington Ave, Suite 200, New York, NY",
    // Sitting In / Sitting Out Consultation Timings
    val morningInTime: String = "09:00 AM",
    val morningOutTime: String = "01:00 PM",
    val eveningInTime: String = "05:00 PM",
    val eveningOutTime: String = "08:30 PM",
    val sittingDays: String = "Mon - Sat",
    // Status: "SITTING_IN", "SITTING_OUT", "BREAK", "ON_LEAVE"
    val sittingStatus: String = "SITTING_IN",
    val nextAvailabilityText: String = "In Cabin Now",
    // Leave Information
    val isOnLeave: Boolean = false,
    val leaveReason: String? = null,
    val leaveStartDate: String? = null,
    val leaveEndDate: String? = null,
    val leaveReturnDate: String? = null,
    val leaveNotes: String? = null,
    // Visual branding
    val avatarInitials: String = "DR",
    val avatarColorHex: Long = 0xFF0F766E
)
