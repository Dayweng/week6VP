package com.darren.week6vp.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darren.week6vp.viewmodel.CampusViewModel

@Composable
fun CommunitiesScreen(
    viewModel: CampusViewModel,
    onOpenClub: (String) -> Unit
) {
    val joinedIds by viewModel.joinedIds.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text("Communities Directory", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text("Pilih komunitas untuk membuka Club Projects", color = Color.Gray, fontSize = 12.sp)
            }
        }

        items(viewModel.communities, key = { it.id }) { community ->
            val isJoined = community.id in joinedIds

            Card(
                // the ViewModel decides if the student is allowed in
                onClick = {
                    if (viewModel.tryOpenCommunity(community.id)) {
                        onOpenClub(community.id)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBox(community.icon)
                        Column {
                            Text(community.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "${community.subtitle} • ${community.members} Mahasiswa",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Text(community.description, fontSize = 13.sp)

                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isJoined) {
                            Text(
                                "Unlocked • Buka Proyek →",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            OutlinedButton(
                                onClick = { viewModel.toggleJoin(community.id) },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text(" Joined")
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(" Terkunci (Gate Akses)", color = Color.Gray, fontSize = 12.sp)
                            }
                            Button(onClick = { viewModel.toggleJoin(community.id) }) {
                                Text("+ Join")
                            }
                        }
                    }
                }
            }
        }
    }
}