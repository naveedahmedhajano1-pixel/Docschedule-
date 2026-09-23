package com.example.data.repository

import com.example.data.local.AppointmentDao
import com.example.data.local.DoctorDao
import com.example.data.local.AppDatabase
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import kotlinx.coroutines.flow.Flow

class DoctorAppointmentRepository(
    private val doctorDao: DoctorDao,
    private val appointmentDao: AppointmentDao
) {
    val allDoctors: Flow<List<Doctor>> = doctorDao.getAllDoctors()
    val allAppointments: Flow<List<Appointment>> = appointmentDao.getAllAppointments()

    fun getDoctorById(id: Int): Flow<Doctor?> = doctorDao.getDoctorById(id)

    suspend fun ensureDataPopulated() {
        if (doctorDao.getDoctorCount() == 0) {
            AppDatabase.populateInitialData(doctorDao, appointmentDao)
        }
    }

    suspend fun setDoctorLeaveStatus(
        doctorId: Int,
        isOnLeave: Boolean,
        reason: String?,
        startDate: String?,
        endDate: String?,
        returnDate: String?,
        notes: String?
    ) {
        val sittingStatus = if (isOnLeave) "ON_LEAVE" else "SITTING_IN"
        val nextAvailability = if (isOnLeave) {
            "On Leave until ${returnDate ?: "Notice"}"
        } else {
            "Available Now in Cabin"
        }
        doctorDao.updateDoctorLeaveStatus(
            doctorId = doctorId,
            isOnLeave = isOnLeave,
            leaveReason = reason,
            startDate = startDate,
            endDate = endDate,
            returnDate = returnDate,
            notes = notes,
            sittingStatus = sittingStatus,
            nextAvailability = nextAvailability
        )
        // Also update any existing appointments for this doctor to show alert if on leave
        appointmentDao.updateDoctorLeaveAlert(doctorId, isOnLeave)
    }

    suspend fun setDoctorSittingStatus(
        doctorId: Int,
        sittingStatus: String,
        nextAvailability: String
    ) {
        doctorDao.updateDoctorSittingStatus(doctorId, sittingStatus, nextAvailability)
    }

    suspend fun addDoctor(doctor: Doctor): Long {
        return doctorDao.insert(doctor)
    }

    suspend fun updateDoctor(doctor: Doctor) {
        doctorDao.update(doctor)
    }

    suspend fun bookAppointment(appointment: Appointment): Long {
        return appointmentDao.insert(appointment)
    }

    suspend fun cancelAppointment(appointmentId: Int) {
        appointmentDao.updateStatus(appointmentId, "CANCELLED")
    }

    suspend fun deleteAppointment(appointmentId: Int) {
        appointmentDao.delete(appointmentId)
    }
}
