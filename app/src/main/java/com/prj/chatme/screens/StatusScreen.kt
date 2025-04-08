package com.prj.chatme.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.prj.chatme.CMViewModel
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.R
import com.prj.chatme.data.Status
import com.prj.chatme.data.UserData
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeShapes
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography

@Composable
fun StatusScreen(
    navController: NavController,
    vm: CMViewModel
) {
    ChatMeTheme {
        val inProgress = vm.inProgress.value
        val statuses = vm.status.value
        val userData = vm.userData.value

        if (inProgress) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ChatMeColors.lightPrimary)
            }
        } else {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let { vm.uploadStatus(it) }
            }

            Scaffold(
                topBar = { StatusTopAppBar() },
                floatingActionButton = {
                    AddStatusFAB(onClick = { launcher.launch("image/*") })
                },
                bottomBar = {
                    BottomNavigationMenu(
                        selectedItem = BottomNavigationItem.STATUS,
                        navController = navController
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(ChatMeColors.lightBackground)
                ) {
                    when {
                        statuses.isEmpty() -> EmptyStatusPlaceholder()
                        else -> StatusList(
                            statuses = statuses,
                            userData = userData,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusTopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Status Updates",
                style = ChatMeTypography.titleLarge,
                color = ChatMeColors.darkPrimaryContainer
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = ChatMeColors.lightPrimaryContainer,
            titleContentColor = ChatMeColors.darkPrimaryContainer,
            actionIconContentColor = ChatMeColors.darkPrimaryContainer
        )
    )
}

@Composable
private fun StatusList(
    statuses: List<Status>,
    userData: UserData?,
    navController: NavController
) {
    val myStatuses = remember(statuses, userData) {
        statuses.filter { it.user.userId == userData?.userId }
    }
    val otherStatuses = remember(statuses, userData) {
        statuses.filter { it.user.userId != userData?.userId }
    }
    val uniqueUsers = remember(otherStatuses) {
        otherStatuses.map { it.user }.toSet().toList()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (myStatuses.isNotEmpty()) {
            item {
                Text(
                    text = "My Status",
                    style = ChatMeTypography.titleMedium,
                    color = ChatMeColors.lightOnSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                StatusItem(
                    status = myStatuses[0],
                    onClick = {
                        navigateTo(
                            navController,
                            DestinatinScreen.SingleStatus.createRoute(myStatuses[0].user.userId!!)
                        )

                    }
                )
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = ChatMeColors.divider
                )
            }
        }

        if (uniqueUsers.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Updates",
                    style = ChatMeTypography.titleMedium,
                    color = ChatMeColors.lightOnSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(uniqueUsers) { user ->
                StatusItem(
                    status = otherStatuses.first { it.user.userId == user.userId },
                    onClick = {
                        navigateTo(
                            navController,
                            DestinatinScreen.SingleStatus.createRoute(user.userId!!)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusItem(
    status: Status,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = ChatMeShapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = ChatMeColors.lightSurface,
            contentColor = ChatMeColors.lightOnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            AsyncImage(
                model = status.user.imageUrl,
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.user)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Status Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status.user.name ?: "Unknown",
                    style = ChatMeTypography.titleMedium,
                    color = ChatMeColors.lightOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Updated ${status.timestamp?.let { formatTimestamp(it) }}",
                    style = ChatMeTypography.bodySmall,
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.6f)
                )
            }

            // Status Preview
            AsyncImage(
                model = status.imageUrl,
                contentDescription = "Status preview",
                modifier = Modifier
                    .size(48.dp)
                    .clip(ChatMeShapes.small),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun AddStatusFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = ChatMeColors.lightPrimary,
        contentColor = ChatMeColors.lightOnPrimary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 72.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Add status"
        )
    }
}

@Composable
private fun EmptyStatusPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_empty_status),
                contentDescription = "No statuses",
                modifier = Modifier.size(64.dp),
                tint = ChatMeColors.lightPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No status updates",
                style = ChatMeTypography.titleMedium,
                color = ChatMeColors.lightOnSurface
            )
            Text(
                text = "Tap the + button to share your first status",
                style = ChatMeTypography.bodyMedium,
                color = ChatMeColors.lightOnSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    // Implement your timestamp formatting logic here
    return "recently" // Placeholder
}