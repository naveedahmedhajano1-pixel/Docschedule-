package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Doctor
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDao {
    @Query("SELECT * FROM doctors ORDER BY isOnLeave ASC, id ASC")
    fun getAllDoctors(): Flow<List<Doctor>>

    @Query("SELECT * FROM doctors WHERE id = :id")
    fun getDoctorById(id: Int): Flow<Doctor?>

    @Query("SELECT COUNT(*) FROM doctors")
    suspend fun getDoctorCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(doctors: List<Doctor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doctor: Doctor): Long

    @Update
    suspend fun update(doctor: Doctor)

    @Query("""
        UPDATE doctors 
        SET isOnLeave = :isOnLeave, 
            leaveReason = :leaveReason, 
            leaveStartDate = :startDate, 
            leaveEndDate = :endDate, 
            leaveReturnDate = :returnDate,
            leaveNotes = :notes,
            sittingStatus = :sittingStatus,
            nextAvailabilityText = :nextAvailability
        WHERE id = :doctorId
    """)
    suspend fun updateDoctorLeaveStatus(
        doctorId: Int,
        isOnLeave: Boolean,
        leaveReason: String?,
        startDate: String?,
        endDate: String?,
        returnDate: String?,
        notes: String?,
        sittingStatus: String,
        nextAvailability: String
    )

    @Query("""
        UPDATE doctors 
        SET sittingStatus = :sittingStatus, 
            nextAvailabilityText = :nextAvailability
        WHERE id = :doctorId
    """)
    suspend fun updateDoctorSittingStatus(
        doctorId: Int,
        sittingStatus: String,
        nextAvailability: String
    )

    @Query("DELETE FROM doctors WHERE id = :doctorId")
    suspend fun deleteDoctor(doctorId: Int)
}
