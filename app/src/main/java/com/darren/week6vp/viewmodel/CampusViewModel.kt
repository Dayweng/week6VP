package com.darren.week6vp.viewmodel

import androidx.lifecycle.ViewModel
import com.darren.week6vp.model.Community
import com.darren.week6vp.model.Project
import com.darren.week6vp.model.ProjectForm
import com.darren.week6vp.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CampusViewModel : ViewModel() {

    val student = Student("Aditya Pratama", "Teknik Informatika", "Angkatan 2022", "22001045", "AP")

    val techOptions = listOf("Kotlin", "Jetpack Compose", "Firebase", "Ktor", "Room", "Python", "Maps SDK")

    // the directory never changes, so a plain list is enough
    val communities = listOf(
        Community("gdsc", "GDSC", "Google Developer Student Club",
            "Fokus pada ekosistem Google: Android Compose, Flutter, Firebase, dan Google Cloud Platform.", 142, "💻"),
        Community("cybersec", "CyberSecurity Guild", "Security & Pentesting",
            "Eksplorasi ethical hacking, CTF challenges, reverse engineering, dan pertahanan jaringan kampus.", 88, "🛡️"),
        Community("ai-society", "AI Society", "Machine Learning & LLM",
            "Riset terapan deep learning, computer vision, agen cerdas, dan fine-tuning large language models.", 115, "✨"),
        Community("mobile-dev", "Mobile Dev Community", "Native Jetpack Compose & iOS",
            "Berbagi praktik terbaik pengembangan aplikasi mobile native dan lintas platform.", 95, "📱"),
        Community("data-science", "Data Science Club", "Analytics & Visualization",
            "Analisis data, visualisasi, dan kompetisi data science antar kampus.", 76, "📊"),
        Community("iot-lab", "IoT Lab", "Embedded & Smart Devices",
            "Membangun perangkat pintar dengan sensor, mikrokontroler, dan konektivitas cloud.", 64, "🔌")
    )

    // ids of the joined communities
    private val _joinedIds = MutableStateFlow(setOf("gdsc", "cybersec", "mobile-dev"))
    val joinedIds: StateFlow<Set<String>> = _joinedIds

    private val _projects = MutableStateFlow(
        listOf(
            Project("proj-gdsc-01", "gdsc", "Smart Campus Navigation",
                "Navigasi peta gedung perkuliahan, lab komputer, dan fasilitas kampus real-time.",
                listOf("Kotlin", "Jetpack Compose", "Room", "Maps SDK"), "In Progress"),
            Project("proj-gdsc-02", "gdsc", "Student Attendance QR Scanner",
                "Pindai barcode dan QR presensi kehadiran workshop berbasis CameraX dan Firebase.",
                listOf("Jetpack Compose", "Firebase"), "Completed"),
            Project("proj-gdsc-03", "gdsc", "Developer RSVP Portal",
                "Portal pendaftaran webinar dan bootcamp teknologi sinkronisasi Ktor API server.",
                listOf("Kotlin", "Ktor", "Jetpack Compose"), "In Progress"),
            Project("proj-cybersec-01", "cybersec", "CTF Practice Platform",
                "Platform latihan challenge CTF untuk anggota guild.",
                listOf("Python", "Firebase"), "In Progress")
        )
    )
    val projects: StateFlow<List<Project>> = _projects

    // text for the snackbar, null means nothing to show
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    private val _form = MutableStateFlow(ProjectForm())
    val form: StateFlow<ProjectForm> = _form

    // used to give every new project its own id
    private var nextProjectNumber = 10

    fun toggleJoin(id: String) {
        _joinedIds.value = if (id in _joinedIds.value) _joinedIds.value - id else _joinedIds.value + id
    }

    // gate access: returns true only if the student has joined
    fun tryOpenCommunity(id: String): Boolean {
        if (id in _joinedIds.value) return true
        _message.value = "Harap bergabung ke komunitas terlebih dahulu!"
        return false
    }

    fun clearMessage() {
        _message.value = null
    }

    fun deleteProject(id: String) {
        _projects.value = _projects.value.filter { it.id != id }
    }

    fun openDialog() {
        _showDialog.value = true
    }

    // closing always wipes the form
    fun closeDialog() {
        _showDialog.value = false
        _form.value = ProjectForm()
    }

    fun onTitleChange(title: String) {
        _form.value = _form.value.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        _form.value = _form.value.copy(description = description)
    }

    fun toggleTech(tech: String) {
        val current = _form.value.techStack
        _form.value = _form.value.copy(
            techStack = if (tech in current) current - tech else current + tech
        )
    }

    fun saveProject(communityId: String) {
        val form = _form.value
        if (!form.isValid) return

        val project = Project(
            id = "proj-$communityId-$nextProjectNumber",
            communityId = communityId,
            title = form.title.trim(),
            description = form.description.trim(),
            // keep the same order as the chips
            techStack = techOptions.filter { it in form.techStack },
            status = "In Progress"
        )
        nextProjectNumber++
        _projects.value = _projects.value + project
        closeDialog()
    }
}