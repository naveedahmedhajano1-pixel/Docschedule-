package com.example

import com.example.data.model.Doctor
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun doctorModel_containsBiographyExperienceAndClinicAddress() {
    val doctor = Doctor(
      id = 1,
      name = "Dr. Sarah Jenkins",
      specialty = "Cardiologist",
      qualifications = "MBBS, MD (Cardiology)",
      experienceYears = 14,
      department = "Heart & Vascular Institute",
      roomNumber = "Cabin 204",
      consultationFee = 65,
      biography = "Interventional cardiologist with 14 years of clinical experience.",
      clinicAddress = "DocSchedule Heart Institute, Pavilion A, Room 204, New York, NY",
      morningInTime = "09:00 AM",
      morningOutTime = "01:00 PM",
      eveningInTime = "05:00 PM",
      eveningOutTime = "08:30 PM",
      sittingDays = "Mon - Sat",
      sittingStatus = "SITTING_IN"
    )

    assertEquals(14, doctor.experienceYears)
    assertTrue(doctor.biography.contains("14 years of clinical experience"))
    assertTrue(doctor.clinicAddress.contains("New York, NY"))
    assertEquals("SITTING_IN", doctor.sittingStatus)
    assertFalse(doctor.isOnLeave)
  }
}
