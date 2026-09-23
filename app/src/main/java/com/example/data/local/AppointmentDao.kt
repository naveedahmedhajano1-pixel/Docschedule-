package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Appointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY bookedAtTimestamp DESC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId")
    fun getAppointmentsForDoctor(doctorId: Int): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: Appointment): Long

    @Update
    suspend fun update(appointment: Appointment)

    @Query("UPDATE appointments SET status = :status WHERE id = :appointmentId")
    suspend fun updateStatus(appointmentId: Int, status: String)

    @Query("UPDATE appointments SET doctorOnLeaveAlert = :onLeaveAlert WHERE doctorId = :doctorId")
    suspend fun updateDoctorLeaveAlert(doctorId: Int, onLeaveAlert: Boolean)

    @Query("DELETE FROM appointments WHERE id = :appointmentId")
    suspend fun delete(appointmentId: Int)
}
