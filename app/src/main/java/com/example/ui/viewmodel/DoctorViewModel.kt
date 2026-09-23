package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.repository.DoctorAppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class FilterStatus {
    ALL,
    SITTING_IN,
    SITTING_OUT,
    ON_LEAVE
}

class DoctorViewModel(
    private val repository: DoctorAppointmentRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.ensureDataPopulated()
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilterStatus = MutableStateFlow(FilterStatus.ALL)
    val selectedFilterStatus: StateFlow<FilterStatus> = _selectedFilterStatus.asStateFlow()

    private val _selectedSpecialty = MutableStateFlow("All")
    val selectedSpecialty: StateFlow<String> = _selectedSpecialty.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Dialog & Flow States
    private val _bookingDoctor = MutableStateFlow<Doctor?>(null)
    val bookingDoctor: StateFlow<Doctor?> = _bookingDoctor.asStateFlow()

    private val _detailDoctor = MutableStateFlow<Doctor?>(null)
    val detailDoctor: StateFlow<Doctor?> = _detailDoctor.asStateFlow()

    private val _manageLeaveDoctor = MutableStateFlow<Doctor?>(null)
    val manageLeaveDoctor: StateFlow<Doctor?> = _manageLeaveDoctor.asStateFlow()

    private val _showAddDoctorDialog = MutableStateFlow(false)
    val showAddDoctorDialog: StateFlow<Boolean> = _showAddDoctorDialog.asStateFlow()

    private val _confirmedBooking = MutableStateFlow<Appointment?>(null)
    val confirmedBooking: StateFlow<Appointment?> = _confirmedBooking.asStateFlow()

    val allAppointments: StateFlow<List<Appointment>> = repository.allAppointments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allDoctors: StateFlow<List<Doctor>> = repository.allDoctors
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered doctors based on search, status filter, and specialty
    val filteredDoctors: StateFlow<List<Doctor>> = combine(
        repository.allDoctors,
        _searchQuery,
        _selectedFilterStatus,
        _selectedSpecialty
    ) { doctors, query, filterStatus, specialty ->
        doctors.filter { doc ->
            val matchesQuery = query.isBlank() ||
                doc.name.contains(query, ignoreCase = true) ||
                doc.specialty.contains(query, ignoreCase = true) ||
                doc.department.contains(query, ignoreCase = true) ||
                doc.roomNumber.contains(query, ignoreCase = true)

            val matchesStatus = when (filterStatus) {
                FilterStatus.ALL -> true
                FilterStatus.SITTING_IN -> doc.sittingStatus == "SITTING_IN" && !doc.isOnLeave
                FilterStatus.SITTING_OUT -> doc.sittingStatus == "SITTING_OUT" && !doc.isOnLeave
                FilterStatus.ON_LEAVE -> doc.isOnLeave || doc.sittingStatus == "ON_LEAVE"
            }

            val matchesSpecialty = specialty == "All" || doc.specialty.contains(specialty, ignoreCase = true)

            matchesQuery && matchesStatus && matchesSpecialty
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterStatus(status: FilterStatus) {
        _selectedFilterStatus.value = status
    }

    fun setSelectedSpecialty(specialty: String) {
        _selectedSpecialty.value = specialty
    }

    fun openBooking(doctor: Doctor) {
        _bookingDoctor.value = doctor
    }

    fun closeBooking() {
        _bookingDoctor.value = null
    }

    fun openDoctorDetail(doctor: Doctor) {
        _detailDoctor.value = doctor
    }

    fun closeDoctorDetail() {
        _detailDoctor.value = null
    }

    fun openManageLeave(doctor: Doctor) {
        _manageLeaveDoctor.value = doctor
    }

    fun closeManageLeave() {
        _manageLeaveDoctor.value = null
    }

    fun openAddDoctorDialog() {
        _showAddDoctorDialog.value = true
    }

    fun closeAddDoctorDialog() {
        _showAddDoctorDialog.value = false
    }

    fun clearConfirmedBooking() {
        _confirmedBooking.value = null
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun toggleDoctorLeaveStatus(
        doctor: Doctor,
        isOnLeave: Boolean,
        reason: String?,
        startDate: String?,
        endDate: String?,
        returnDate: String?,
        notes: String?
    ) {
        viewModelScope.launch {
            repository.setDoctorLeaveStatus(
                doctorId = doctor.id,
                isOnLeave = isOnLeave,
                reason = reason,
                startDate = startDate,
                endDate = endDate,
                returnDate = returnDate,
                notes = notes
            )
            val msg = if (isOnLeave) {
                "${doctor.name} marked ON LEAVE until ${returnDate ?: "notice"}."
            } else {
                "${doctor.name} marked BACK ON DUTY (Sitting In)."
            }
            _userMessage.value = msg
            _manageLeaveDoctor.value = null
            // Update detail doctor if open
            if (_detailDoctor.value?.id == doctor.id) {
                _detailDoctor.value = _detailDoctor.value?.copy(
                    isOnLeave = isOnLeave,
                    sittingStatus = if (isOnLeave) "ON_LEAVE" else "SITTING_IN",
                    leaveReason = reason,
                    leaveReturnDate = returnDate
                )
            }
        }
    }

    fun quickToggleSittingStatus(doctor: Doctor, newStatus: String) {
        viewModelScope.launch {
            val nextTime = when (newStatus) {
                "SITTING_IN" -> "Available Now in ${doctor.roomNumber}"
                "SITTING_OUT" -> "Next Sitting: ${doctor.eveningInTime}"
                "BREAK" -> "Clinic Break - Back in 20m"
                "ON_LEAVE" -> "On Leave until ${doctor.leaveReturnDate ?: "Notice"}"
                else -> "Check desk"
            }
            repository.setDoctorSittingStatus(doctor.id, newStatus, nextTime)
            _userMessage.value = "${doctor.name} status updated to ${newStatus.replace('_', ' ')}"
        }
    }

    fun bookAppointment(
        doctor: Doctor,
        patientName: String,
        patientPhone: String,
        patientAge: Int,
        appointmentDate: String,
        timeSlot: String,
        shift: String,
        type: String,
        symptoms: String
    ) {
        viewModelScope.launch {
            val tokenNum = "DOC-${Random.nextInt(100, 999)}"
            val appointment = Appointment(
                doctorId = doctor.id,
                doctorName = doctor.name,
                doctorSpecialty = doctor.specialty,
                doctorRoom = doctor.roomNumber,
                patientName = patientName,
                patientPhone = patientPhone,
                patientAge = patientAge,
                appointmentDate = appointmentDate,
                appointmentTimeSlot = timeSlot,
                shift = shift,
                appointmentType = type,
                symptomsNotes = symptoms,
                tokenNumber = tokenNum,
                status = "CONFIRMED",
                doctorOnLeaveAlert = doctor.isOnLeave
            )
            repository.bookAppointment(appointment)
            _bookingDoctor.value = null
            _confirmedBooking.value = appointment
            _userMessage.value = "Appointment booked for $patientName! Token: $tokenNum"
        }
    }

    fun cancelAppointment(appointmentId: Int) {
        viewModelScope.launch {
            repository.cancelAppointment(appointmentId)
            _userMessage.value = "Appointment cancelled."
        }
    }

    fun deleteAppointment(appointmentId: Int) {
        viewModelScope.launch {
            repository.deleteAppointment(appointmentId)
            _userMessage.value = "Appointment record removed."
        }
    }

    fun addNewDoctor(
        name: String,
        specialty: String,
        qualifications: String,
        experienceYears: Int,
        department: String,
        roomNumber: String,
        consultationFee: Int,
        morningIn: String,
        morningOut: String,
        eveningIn: String,
        eveningOut: String,
        sittingDays: String,
        isOnLeave: Boolean,
        leaveReason: String?,
        returnDate: String?,
        biography: String = "Specialist physician dedicated to evidence-based clinical care and patient well-being.",
        clinicAddress: String = "DocSchedule Medical Plaza, Wing A, 450 Lexington Ave, Suite 200, New York, NY"
    ) {
        viewModelScope.launch {
            val initials = name.split(" ")
                .filter { it.isNotBlank() && !it.startsWith("Dr", ignoreCase = true) }
                .take(2)
                .map { it.first().uppercaseChar() }
                .joinToString("")
                .ifEmpty { "DR" }

            val doc = Doctor(
                name = if (name.startsWith("Dr.", ignoreCase = true)) name else "Dr. $name",
                specialty = specialty,
                qualifications = qualifications,
                experienceYears = experienceYears,
                department = department,
                roomNumber = roomNumber,
                consultationFee = consultationFee,
                biography = biography.ifBlank { "Specialist physician dedicated to evidence-based clinical care and patient well-being." },
                clinicAddress = clinicAddress.ifBlank { "DocSchedule Medical Plaza, Wing A, 450 Lexington Ave, Suite 200, New York, NY" },
                morningInTime = morningIn,
                morningOutTime = morningOut,
                eveningInTime = eveningIn,
                eveningOutTime = eveningOut,
                sittingDays = sittingDays,
                sittingStatus = if (isOnLeave) "ON_LEAVE" else "SITTING_IN",
                nextAvailabilityText = if (isOnLeave) "On Leave until ${returnDate ?: "Notice"}" else "Available in $roomNumber",
                isOnLeave = isOnLeave,
                leaveReason = leaveReason,
                leaveReturnDate = returnDate,
                avatarInitials = initials,
                avatarColorHex = 0xFF0F766E
            )
            repository.addDoctor(doc)
            _showAddDoctorDialog.value = false
            _userMessage.value = "${doc.name} added to hospital directory."
        }
    }
}

class DoctorViewModelFactory(
    private val repository: DoctorAppointmentRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
            return DoctorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
