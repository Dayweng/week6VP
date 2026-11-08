package com.darren.week6vp.model

data class Project(
    val id: String,
    val communityId: String,
    val title: String,
    val description: String,
    val techStack: List<String>,
    val status: String
)

// state of the Add Project dialog
data class ProjectForm(
    val title: String = "",
    val description: String = "",
    val techStack: Set<String> = emptySet()
) {
    val isValid: Boolean
        get() = title.isNotBlank() && description.isNotBlank() && techStack.isNotEmpty()
}