package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Doctor
import com.example.ui.components.DoctorScheduleCard
import com.example.ui.theme.ClinicalBlueSecondary
import com.example.ui.theme.MedicalRedError
import com.example.ui.theme.MedicalRedErrorContainer
import com.example.ui.theme.MedicalRedOnErrorContainer
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.MedicalTealPrimaryContainer
import com.example.ui.theme.StatusSittingIn
import com.example.ui.viewmodel.FilterStatus

@Composable
fun DoctorsListScreen(
    doctors: List<Doctor>,
    allDoctorsCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStatus: FilterStatus,
    onStatusChange: (FilterStatus) -> Unit,
    selectedSpecialty: String,
    onSpecialtyChange: (String) -> Unit,
    onBookDoctor: (Doctor) -> Unit,
    onManageLeaveDoctor: (Doctor) -> Unit,
    onDoctorDetail: (Doctor) -> Unit,
    modifier: Modifier = Modifier
) {
    val specialties = listOf("All", "Cardiology", "Neurology", "Pediatrics", "Orthopedic", "Dermatology", "General")
    val onLeaveDoctorsCount = doctors.count { it.isOnLeave }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("doctors_list_screen"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.doctor_consultation_banner_1790192048980),
                        contentDescription = "Medical Consultation Clinic",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay for contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x770F766E),
                                        Color(0xCC0F172A)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "DocSchedule Center",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Consultation Sitting In/Out Shifts & Leave Status",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCCFBF1)
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("doctor_search_bar"),
                    placeholder = { Text("Search doctor, specialty, or cabin...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    singleLine = true
                )
            }
        }

        // Status Filter Chips (All, Sitting In, Sitting Out, On Leave)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == FilterStatus.ALL,
                    onClick = { onStatusChange(FilterStatus.ALL) },
                    label = { Text("All ($allDoctorsCount)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                    modifier = Modifier.testTag("filter_all_doctors")
                )

                FilterChip(
                    selected = selectedStatus == FilterStatus.SITTING_IN,
                    onClick = { onStatusChange(FilterStatus.SITTING_IN) },
                    label = { Text("● Sitting In Now", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusSittingIn.copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFF065F46)
                    ),
                    modifier = Modifier.testTag("filter_sitting_in")
                )

                FilterChip(
                    selected = selectedStatus == FilterStatus.SITTING_OUT,
                    onClick = { onStatusChange(FilterStatus.SITTING_OUT) },
                    label = { Text("○ Sitting Out", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                    modifier = Modifier.testTag("filter_sitting_out")
                )

                FilterChip(
                    selected = selectedStatus == FilterStatus.ON_LEAVE,
                    onClick = { onStatusChange(FilterStatus.ON_LEAVE) },
                    label = { Text("✕ On Leave ($onLeaveDoctorsCount)", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MedicalRedErrorContainer,
                        selectedLabelColor = MedicalRedOnErrorContainer
                    ),
                    modifier = Modifier.testTag("filter_on_leave")
                )
            }
        }

        // Specialty Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                specialties.forEach { spec ->
                    FilterChip(
                        selected = selectedSpecialty == spec,
                        onClick = { onSpecialtyChange(spec) },
                        label = { Text(spec, fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_spec_${spec.lowercase()}")
                    )
                }
            }
        }

        // Results summary line
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${doctors.size} Doctors Available",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (selectedStatus != FilterStatus.ALL || selectedSpecialty != "All" || searchQuery.isNotBlank()) {
                    TextButton(
                        onClick = {
                            onSearchQueryChange("")
                            onStatusChange(FilterStatus.ALL)
                            onSpecialtyChange("All")
                        }
                    ) {
                        Text("Reset Filters", fontSize = 11.sp)
                    }
                }
            }
        }

        // Empty state
        if (doctors.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Doctors Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Try adjusting your search query or filter chips.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Doctor Cards List
        items(doctors, key = { it.id }) { doctor ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                DoctorScheduleCard(
                    doctor = doctor,
                    onBookClick = { onBookDoctor(doctor) },
                    onManageLeaveClick = { onManageLeaveDoctor(doctor) },
                    onDetailClick = { onDoctorDetail(doctor) }
                )
            }
        }
    }
}
