package com.darren.week6vp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.darren.week6vp.model.Community
import com.darren.week6vp.model.ProjectForm
import com.darren.week6vp.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubProjectsScreen(
    viewModel: CampusViewModel,
    communityId: String,
    onBack: () -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val form by viewModel.form.collectAsState()

    val community = viewModel.communities.first { it.id == communityId }
    val clubProjects = projects.filter { it.communityId == communityId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("${community.name} Projects", fontWeight = FontWeight.Bold)
                        Text(community.subtitle, fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openDialog() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Project") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(community.subtitle, fontWeight = FontWeight.Bold)
                        Text(
                            "${community.members} Anggota • ${clubProjects.size} Proyek",
                            fontSize = 12.sp
                        )
                    }
                }
            }
            item {
                Text(
                    text = "Daftar Proyek Mahasiswa (${clubProjects.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (clubProjects.isEmpty()) {
                item { Text("Belum ada proyek yang dicatat", color = Color.Gray) }
            }

            items(clubProjects, key = { it.id }) { project ->
                val statusColor = if (project.status == "Completed") Color(0xFF2E9E6B) else Color(0xFFF59E0B)

                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconBox(community.icon)

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(project.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(project.description, color = Color.Gray, fontSize = 12.sp, maxLines = 3)
                            TechTagRow(project.techStack)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ID: ${project.id}", color = Color.Gray, fontSize = 10.sp)
                                Text(project.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // red delete button - remove proj
                        IconButton(
                            onClick = { viewModel.deleteProject(project.id) },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddProjectDialog(
            community = community,
            form = form,
            techOptions = viewModel.techOptions,
            onTitleChange = { viewModel.onTitleChange(it) },
            onDescriptionChange = { viewModel.onDescriptionChange(it) },
            onToggleTech = { viewModel.toggleTech(it) },
            onCancel = { viewModel.closeDialog() },
            onSave = { viewModel.saveProject(communityId) }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddProjectDialog(
    community: Community,
    form: ProjectForm,
    techOptions: List<String>,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onToggleTech: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        title = { Text("Add Project", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Komunitas: ${community.name} (${community.subtitle})", color = Color.Gray, fontSize = 12.sp)

                OutlinedTextField(
                    value = form.title,
                    onValueChange = onTitleChange,
                    label = { Text("Judul Proyek *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = form.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Deskripsi Proyek (Minimal 3 Baris) *") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tech Stack (Pilihan Multi-Selection) *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${form.techStack.size} Dipilih", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    techOptions.forEach { tech ->
                        val selected = tech in form.techStack
                        FilterChip(
                            selected = selected,
                            onClick = { onToggleTech(tech) },
                            label = { Text(tech) },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                            } else null
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave, enabled = form.isValid) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}