package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.DoctorAppointmentRepository
import com.example.ui.screens.AddDoctorDialog
import com.example.ui.screens.BookingConfirmationDialog
import com.example.ui.screens.BookingDialog
import com.example.ui.screens.DoctorDetailDialog
import com.example.ui.screens.DoctorsListScreen
import com.example.ui.screens.LiveSittingBoardScreen
import com.example.ui.screens.ManageDoctorLeaveDialog
import com.example.ui.screens.MyAppointmentsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DoctorViewModel
import com.example.ui.viewmodel.DoctorViewModelFactory

enum class AppTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DOCTORS("Doctors", Icons.Default.MedicalServices),
    LIVE_BOARD("Sitting Board", Icons.Default.Schedule),
    APPOINTMENTS("Appointments", Icons.Default.CalendarMonth)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DocScheduleApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocScheduleApp() {
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val database = remember { AppDatabase.getDatabase(context, coroutineScope) }
    val repository = remember {
        DoctorAppointmentRepository(
            doctorDao = database.doctorDao(),
            appointmentDao = database.appointmentDao()
        )
    }
    val viewModel: DoctorViewModel = viewModel(
        factory = DoctorViewModelFactory(repository)
    )

    var currentTab by remember { mutableStateOf(AppTab.DOCTORS) }
    val snackbarHostState = remember { SnackbarHostState() }

    val allDoctors by viewModel.allDoctors.collectAsStateWithLifecycle()
    val filteredDoctors by viewModel.filteredDoctors.collectAsStateWithLifecycle()
    val appointments by viewModel.allAppointments.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedFilterStatus.collectAsStateWithLifecycle()
    val selectedSpecialty by viewModel.selectedSpecialty.collectAsStateWithLifecycle()

    val bookingDoctor by viewModel.bookingDoctor.collectAsStateWithLifecycle()
    val detailDoctor by viewModel.detailDoctor.collectAsStateWithLifecycle()
    val manageLeaveDoctor by viewModel.manageLeaveDoctor.collectAsStateWithLifecycle()
    val showAddDoctorDialog by viewModel.showAddDoctorDialog.collectAsStateWithLifecycle()
    val confirmedBooking by viewModel.confirmedBooking.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    androidx.compose.foundation.layout.Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DocSchedule",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (currentTab) {
                                AppTab.DOCTORS -> "Sitting In/Out Shifts & Leave Tracker"
                                AppTab.LIVE_BOARD -> "Real-Time Clinic Sitting Monitor"
                                AppTab.APPOINTMENTS -> "My Consultation Bookings"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openAddDoctorDialog() },
                        modifier = Modifier.testTag("action_add_doctor")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Doctor",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .testTag("bottom_nav_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            if (tab == AppTab.APPOINTMENTS && appointments.isNotEmpty()) {
                                BadgedBox(
                                    badge = {
                                        Badge {
                                            Text("${appointments.count { it.status == "CONFIRMED" }}")
                                        }
                                    }
                                ) {
                                    Icon(imageVector = tab.icon, contentDescription = tab.title)
                                }
                            } else {
                                Icon(imageVector = tab.icon, contentDescription = tab.title)
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentTab == AppTab.DOCTORS) {
                FloatingActionButton(
                    onClick = { viewModel.openAddDoctorDialog() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("fab_add_doctor")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Doctor")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.DOCTORS -> {
                    DoctorsListScreen(
                        doctors = filteredDoctors,
                        allDoctorsCount = allDoctors.size,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        selectedStatus = selectedStatus,
                        onStatusChange = { viewModel.setFilterStatus(it) },
                        selectedSpecialty = selectedSpecialty,
                        onSpecialtyChange = { viewModel.setSelectedSpecialty(it) },
                        onBookDoctor = { viewModel.openBooking(it) },
                        onManageLeaveDoctor = { viewModel.openManageLeave(it) },
                        onDoctorDetail = { viewModel.openDoctorDetail(it) }
                    )
                }
                AppTab.LIVE_BOARD -> {
                    LiveSittingBoardScreen(
                        doctors = allDoctors,
                        onDoctorClick = { viewModel.openDoctorDetail(it) },
                        onManageDoctorClick = { viewModel.openManageLeave(it) }
                    )
                }
                AppTab.APPOINTMENTS -> {
                    MyAppointmentsScreen(
                        appointments = appointments,
                        doctors = allDoctors,
                        onCancelAppointment = { viewModel.cancelAppointment(it) },
                        onBookNewClick = { currentTab = AppTab.DOCTORS }
                    )
                }
            }
        }
    }

    // Dialogs
    bookingDoctor?.let { doc ->
        BookingDialog(
            doctor = doc,
            onDismiss = { viewModel.closeBooking() },
            onConfirmBooking = { name, phone, age, date, slot, shift, type, symptoms ->
                viewModel.bookAppointment(doc, name, phone, age, date, slot, shift, type, symptoms)
            }
        )
    }

    detailDoctor?.let { doc ->
        DoctorDetailDialog(
            doctor = doc,
            onDismiss = { viewModel.closeDoctorDetail() },
            onBookClick = {
                viewModel.closeDoctorDetail()
                viewModel.openBooking(doc)
            },
            onManageLeaveClick = {
                viewModel.closeDoctorDetail()
                viewModel.openManageLeave(doc)
            }
        )
    }

    manageLeaveDoctor?.let { doc ->
        ManageDoctorLeaveDialog(
            doctor = doc,
            onDismiss = { viewModel.closeManageLeave() },
            onSaveLeaveStatus = { isOnLeave, reason, start, end, returnDate, notes ->
                viewModel.toggleDoctorLeaveStatus(doc, isOnLeave, reason, start, end, returnDate, notes)
            },
            onQuickSittingStatusChange = { newStatus ->
                viewModel.quickToggleSittingStatus(doc, newStatus)
            }
        )
    }

    confirmedBooking?.let { apt ->
        BookingConfirmationDialog(
            appointment = apt,
            onDismiss = {
                viewModel.clearConfirmedBooking()
                currentTab = AppTab.APPOINTMENTS
            }
        )
    }

    if (showAddDoctorDialog) {
        AddDoctorDialog(
            onDismiss = { viewModel.closeAddDoctorDialog() },
            onAddDoctor = { name, spec, qual, exp, dept, room, fee, mIn, mOut, eIn, eOut, days, onLeave, reason, returnDate, bio, clinicAddr ->
                viewModel.addNewDoctor(
                    name, spec, qual, exp, dept, room, fee, mIn, mOut, eIn, eOut, days, onLeave, reason, returnDate, bio, clinicAddr
                )
            }
        )
    }
}

/**
 * Kept for test and preview compatibility.
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
