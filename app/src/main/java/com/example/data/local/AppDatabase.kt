package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Doctor::class, Appointment::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doctorDao(): DoctorDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "docschedule_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.doctorDao(), database.appointmentDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(doctorDao: DoctorDao, appointmentDao: AppointmentDao) {
            val initialDoctors = listOf(
                Doctor(
                    id = 1,
                    name = "Dr. Sarah Jenkins",
                    specialty = "Cardiologist",
                    qualifications = "MBBS, MD (Cardiology), FACC",
                    experienceYears = 14,
                    biography = "Senior Interventional Cardiologist with over 14 years of specialized clinical experience in preventive cardiovascular screening, hypertension management, echocardiography, and coronary artery disease care. Fellow of the American College of Cardiology (FACC). Formerly served as Senior Cardiology Fellow at Johns Hopkins Hospital.",
                    clinicAddress = "DocSchedule Heart Institute, Pavilion A, Suite 204, 750 Lexington Ave, New York, NY 10022",
                    department = "Heart & Vascular Institute",
                    roomNumber = "Cabin 204 (2nd Floor)",
                    consultationFee = 65,
                    rating = 4.9f,
                    reviewCount = 184,
                    morningInTime = "09:00 AM",
                    morningOutTime = "01:00 PM",
                    eveningInTime = "05:00 PM",
                    eveningOutTime = "08:30 PM",
                    sittingDays = "Mon, Tue, Wed, Thu, Fri, Sat",
                    sittingStatus = "SITTING_IN",
                    nextAvailabilityText = "Available Now in Cabin 204",
                    isOnLeave = false,
                    avatarInitials = "SJ",
                    avatarColorHex = 0xFF0F766E
                ),
                Doctor(
                    id = 2,
                    name = "Dr. Alexander Hayes",
                    specialty = "Neurologist & Neurosurgeon",
                    qualifications = "MBBS, MS, M.Ch (Neurosurgery)",
                    experienceYears = 18,
                    biography = "Distinguished Neurosurgeon with 18 years of clinical and surgical leadership. Specializes in minimally invasive spine decompression, cerebrovascular interventions, and complex neuro-trauma recovery. Active researcher in neuro-regeneration therapy.",
                    clinicAddress = "DocSchedule Neurosciences Complex, Tower B, 3rd Floor, Suite 310, 820 Madison Ave, New York, NY 10021",
                    department = "Neurosciences & Brain Health",
                    roomNumber = "Cabin 310 (3rd Floor)",
                    consultationFee = 80,
                    rating = 4.8f,
                    reviewCount = 210,
                    morningInTime = "08:30 AM",
                    morningOutTime = "12:30 PM",
                    eveningInTime = "04:30 PM",
                    eveningOutTime = "07:30 PM",
                    sittingDays = "Mon - Fri",
                    sittingStatus = "ON_LEAVE",
                    nextAvailabilityText = "On Leave until Monday, Sep 29",
                    isOnLeave = true,
                    leaveReason = "Attending International Neuro-Spine Conference & Advanced Surgery Workshop",
                    leaveStartDate = "Sep 22, 2026",
                    leaveEndDate = "Sep 28, 2026",
                    leaveReturnDate = "Monday, Sep 29, 2026",
                    leaveNotes = "For acute neurological emergencies, please contact Dr. Marcus Bell or duty triage.",
                    avatarInitials = "AH",
                    avatarColorHex = 0xFFDC2626
                ),
                Doctor(
                    id = 3,
                    name = "Dr. Elena Rostova",
                    specialty = "Senior Pediatrician",
                    qualifications = "MBBS, DCH, MD (Pediatrics)",
                    experienceYears = 11,
                    biography = "Compassionate Pediatric Specialist with 11 years dedicated to comprehensive newborn care, early childhood development, pediatric allergy management, and adolescent preventative wellness. Known for a gentle and reassuring bedside manner.",
                    clinicAddress = "DocSchedule Children's Pavilion, East Wing, Suite 108, 330 Park Avenue, New York, NY 10022",
                    department = "Child & Adolescent Health",
                    roomNumber = "Cabin 108 (1st Floor)",
                    consultationFee = 50,
                    rating = 4.9f,
                    reviewCount = 156,
                    morningInTime = "10:00 AM",
                    morningOutTime = "02:00 PM",
                    eveningInTime = "06:00 PM",
                    eveningOutTime = "09:00 PM",
                    sittingDays = "Mon - Sat",
                    sittingStatus = "SITTING_OUT",
                    nextAvailabilityText = "Next Sitting: 06:00 PM Today",
                    isOnLeave = false,
                    avatarInitials = "ER",
                    avatarColorHex = 0xFF0284C7
                ),
                Doctor(
                    id = 4,
                    name = "Dr. Marcus Bell",
                    specialty = "Orthopedic & Joint Surgeon",
                    qualifications = "MBBS, MS (Orthopedics), FRCS",
                    experienceYears = 15,
                    biography = "Consultant Orthopedic Surgeon with 15 years specializing in robotic-assisted joint replacement, arthroscopic knee ligament reconstruction, fracture trauma, and sports injury rehabilitation. Team physician for regional sports academies.",
                    clinicAddress = "DocSchedule Orthopedics & Sports Medicine, Ground Floor, Suite 112, 540 5th Avenue, New York, NY 10036",
                    department = "Bone & Joint Care",
                    roomNumber = "Cabin 112 (1st Floor)",
                    consultationFee = 70,
                    rating = 4.7f,
                    reviewCount = 142,
                    morningInTime = "09:00 AM",
                    morningOutTime = "01:30 PM",
                    eveningInTime = "05:30 PM",
                    eveningOutTime = "09:00 PM",
                    sittingDays = "Mon, Tue, Thu, Fri, Sat",
                    sittingStatus = "SITTING_IN",
                    nextAvailabilityText = "Available Now in Cabin 112",
                    isOnLeave = false,
                    avatarInitials = "MB",
                    avatarColorHex = 0xFF059669
                ),
                Doctor(
                    id = 5,
                    name = "Dr. Priya Sharma",
                    specialty = "Dermatologist & Cosmetologist",
                    qualifications = "MBBS, MD (Dermatology)",
                    experienceYears = 9,
                    biography = "Expert Clinical and Aesthetic Dermatologist with 9 years providing tailored care for complex dermatoses, cystic acne, eczema, psoriasis, scar reduction, and advanced fractional laser resurfacing. Member of American Academy of Dermatology.",
                    clinicAddress = "DocSchedule Derma & Skin Wellness Suite, 2nd Floor, Cabin 215, 600 Columbus Ave, New York, NY 10024",
                    department = "Dermatology & Skin Center",
                    roomNumber = "Cabin 215 (2nd Floor)",
                    consultationFee = 55,
                    rating = 4.8f,
                    reviewCount = 98,
                    morningInTime = "09:30 AM",
                    morningOutTime = "01:00 PM",
                    eveningInTime = "04:30 PM",
                    eveningOutTime = "08:00 PM",
                    sittingDays = "Mon - Sat",
                    sittingStatus = "ON_LEAVE",
                    nextAvailabilityText = "On Leave until Friday, Sep 26",
                    isOnLeave = true,
                    leaveReason = "Specialized Laser Treatment Symposium & CME",
                    leaveStartDate = "Sep 23, 2026",
                    leaveEndDate = "Sep 25, 2026",
                    leaveReturnDate = "Friday, Sep 26, 2026",
                    leaveNotes = "Skin care follow-ups can collect prescriptions from the OPD desk.",
                    avatarInitials = "PS",
                    avatarColorHex = 0xFFE11D48
                ),
                Doctor(
                    id = 6,
                    name = "Dr. David Chen",
                    specialty = "General Physician & Diabetologist",
                    qualifications = "MBBS, MD (Internal Medicine)",
                    experienceYears = 16,
                    biography = "Compassionate Internal Medicine Consultant with 16 years managing adult chronic ailments, diabetes management, metabolic disorders, geriatric assessment, and personalized preventative lifestyle counseling.",
                    clinicAddress = "DocSchedule Primary Care & Wellness Pavilion, Ground Floor, Cabin 101, 410 Broadway, New York, NY 10013",
                    department = "Internal Medicine & Wellness",
                    roomNumber = "Cabin 101 (Ground Floor)",
                    consultationFee = 45,
                    rating = 4.9f,
                    reviewCount = 320,
                    morningInTime = "08:00 AM",
                    morningOutTime = "12:00 PM",
                    eveningInTime = "03:30 PM",
                    eveningOutTime = "07:30 PM",
                    sittingDays = "Mon - Sun",
                    sittingStatus = "SITTING_IN",
                    nextAvailabilityText = "Available Now in Cabin 101",
                    isOnLeave = false,
                    avatarInitials = "DC",
                    avatarColorHex = 0xFF2563EB
                ),
                Doctor(
                    id = 7,
                    name = "Dr. Amanda Richardson",
                    specialty = "Obstetrician & Gynecologist",
                    qualifications = "MBBS, MS (OB-GYN), MRCOG",
                    experienceYears = 13,
                    biography = "Dedicated OB-GYN with 13 years of expertise in high-risk antenatal care, laparoscopic gynecologic procedures, prenatal diagnostic testing, reproductive health, and post-partum recovery planning.",
                    clinicAddress = "DocSchedule Women's Health & Maternity Wing, 2nd Floor, Suite 220, 900 5th Avenue, New York, NY 10021",
                    department = "Women's Health & Maternity",
                    roomNumber = "Cabin 220 (2nd Floor)",
                    consultationFee = 65,
                    rating = 4.9f,
                    reviewCount = 175,
                    morningInTime = "09:00 AM",
                    morningOutTime = "01:00 PM",
                    eveningInTime = "05:00 PM",
                    eveningOutTime = "08:00 PM",
                    sittingDays = "Mon, Tue, Wed, Fri, Sat",
                    sittingStatus = "SITTING_OUT",
                    nextAvailabilityText = "Next Sitting: 05:00 PM Today",
                    isOnLeave = false,
                    avatarInitials = "AR",
                    avatarColorHex = 0xFF9333EA
                ),
                Doctor(
                    id = 8,
                    name = "Dr. Robert Vance",
                    specialty = "ENT & Head Neck Specialist",
                    qualifications = "MBBS, MS (ENT), DLO",
                    experienceYears = 12,
                    biography = "Consultant ENT Surgeon with 12 years of specialized practice treating chronic sinusitis via endoscopic techniques (FESS), hearing disorders, sleep apnea, pediatric adenoids, and throat disorders.",
                    clinicAddress = "DocSchedule ENT & Audiology Care Center, Tower C, Suite 305, 520 8th Avenue, New York, NY 10018",
                    department = "Ear, Nose, Throat & Audiology",
                    roomNumber = "Cabin 305 (3rd Floor)",
                    consultationFee = 55,
                    rating = 4.7f,
                    reviewCount = 110,
                    morningInTime = "10:30 AM",
                    morningOutTime = "02:30 PM",
                    eveningInTime = "06:00 PM",
                    eveningOutTime = "09:30 PM",
                    sittingDays = "Tue - Sun",
                    sittingStatus = "SITTING_IN",
                    nextAvailabilityText = "Available Now in Cabin 305",
                    isOnLeave = false,
                    avatarInitials = "RV",
                    avatarColorHex = 0xFFD97706
                )
            )
            doctorDao.insertAll(initialDoctors)

            // Seed a sample appointment so the user can immediately experience the "My Appointments" tab
            val sampleAppointment = Appointment(
                id = 1,
                doctorId = 1,
                doctorName = "Dr. Sarah Jenkins",
                doctorSpecialty = "Cardiologist",
                doctorRoom = "Cabin 204 (2nd Floor)",
                patientName = "Alex Miller",
                patientPhone = "+1 (555) 234-5678",
                patientAge = 34,
                appointmentDate = "Today, Sep 24",
                appointmentTimeSlot = "11:30 AM",
                shift = "Morning",
                appointmentType = "Routine Heart Health Checkup",
                symptomsNotes = "Mild palpitations after cardio exercises.",
                tokenNumber = "DOC-102",
                status = "CONFIRMED"
            )
            appointmentDao.insert(sampleAppointment)
        }
    }
}
