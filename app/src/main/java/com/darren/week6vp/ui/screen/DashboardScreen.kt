package com.darren.week6vp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darren.week6vp.viewmodel.CampusViewModel

@Composable
fun DashboardScreen(
    viewModel: CampusViewModel
) {
    val student = viewModel.student
    val joinedIds by viewModel.joinedIds.collectAsState()
    val projects by viewModel.projects.collectAsState()

    val joinedCommunities = viewModel.communities.filter { it.id in joinedIds }
    val blue = Color(0xFF1E4FD8)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("CampusTech Hub", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text("Student Profile & Portfolio Dashboard", color = Color.Gray, fontSize = 12.sp)
                }
                Avatar(student.initials, 40.dp, blue, Color.White)
            }
        }

        // profile card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = blue)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF1E40AF), Color(0xFF3B6EF5))))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(student.initials, 64.dp, Color.White, blue)
                    Column {
                        Text(
                            text = "MAHASISWA AKTIF",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                        Text(student.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("${student.major} • ${student.batch}", color = Color.White, fontSize = 12.sp)
                        Text("NIM: ${student.nim}", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        // two counters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CounterCard(
                    title = "Joined Clubs",
                    value = joinedIds.size,
                    caption = "Komunitas yang Diikuti",
                    icon = Icons.Default.Person,
                    iconColor = Color(0xFF1E4FD8),
                    iconBackground = Color(0xFFE3ECFF),
                    modifier = Modifier.weight(1f)
                )
                CounterCard(
                    title = "Total Projects",
                    value = projects.size,
                    caption = "Seluruh Proyek Dibuat",
                    icon = Icons.Default.ContentCopy,
                    iconColor = Color(0xFF7E57C2),
                    iconBackground = Color(0xFFEDE7F6),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // joined communities
        item {
            Text(
                text = "Semua Komunitas yang Diikuti (${joinedCommunities.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        if (joinedCommunities.isEmpty()) {
            item { Text("Belum bergabung dengan komunitas", color = Color.Gray) }
        } else {
            items(joinedCommunities, key = { "club-${it.id}" }) { community ->
                val projectCount = projects.count { it.communityId == community.id }
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBox(community.icon)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(community.name, fontWeight = FontWeight.Bold)
                            Text(
                                "${community.subtitle} • $projectCount Proyek",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "Joined ✓",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // all projects
        item {
            Text(
                text = "Semua Proyek Mahasiswa (${projects.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        if (projects.isEmpty()) {
            item { Text("Belum ada proyek yang dicatat", color = Color.Gray) }
        } else {
            items(projects, key = { "project-${it.id}" }) { project ->
                val community = viewModel.communities.first { it.id == project.communityId }
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
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(community.name, color = blue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(project.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            TechTagRow(project.techStack)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CounterCard(
    title: String,
    value: Int,
    caption: String,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    iconBackground: Color
) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value.toString(), fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text(title, fontWeight = FontWeight.Medium)
            Text(caption, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun Avatar(
    initials: String,
    size: Dp,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = textColor, fontWeight = FontWeight.Bold)
    }
}