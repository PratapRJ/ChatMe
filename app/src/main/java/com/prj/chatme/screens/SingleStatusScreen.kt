package com.prj.chatme.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prj.chatme.CMViewModel
import com.prj.chatme.CommonImage
import com.prj.chatme.R
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography
import com.prj.chatme.ui.theme.DarkOverlay
import com.prj.chatme.ui.theme.StatusProgressActive
import com.prj.chatme.ui.theme.StatusProgressCompleted
import com.prj.chatme.ui.theme.StatusProgressInactive
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class State {
    INITIAL, ACTIVE, COMPLETED
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SingleStatusScreen(
    navController: NavController,
    vm: CMViewModel,
    statusId: String,
) {
    ChatMeTheme {
        val statuses = vm.status.value.filter { it.user.userId == statusId }
        val currentUser = vm.userData.value?.userId

        if (statuses.isNotEmpty()) {
            val currentStatus = remember { mutableStateOf(0) }
            val pauseAnimation = remember { mutableStateOf(false) }
            val showDeleteDialog = remember { mutableStateOf(false) }
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val touchAreaWidth = screenWidth / 3
            val progressStates = remember { mutableMapOf<Int, Float>() }

            LaunchedEffect(showDeleteDialog.value) {
                pauseAnimation.value = showDeleteDialog.value
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ChatMeColors.lightBackground)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                if (showDeleteDialog.value) return@detectTapGestures

                                // Left side tap - go to previous status
                                if (offset.x < touchAreaWidth.toPx()) {
                                    if (currentStatus.value > 0) {
                                        progressStates[currentStatus.value] = 0f
                                        currentStatus.value--
                                    } else {
                                        navController.popBackStack()
                                    }
                                }
                                // Right side tap - go to next status
                                else if (offset.x > (screenWidth.toPx() - touchAreaWidth.toPx())) {
                                    if (currentStatus.value < statuses.size - 1) {
                                        progressStates[currentStatus.value] = 1f
                                        currentStatus.value++
                                    } else {
                                        navController.popBackStack()
                                    }
                                }
                            },
                            onPress = {
                                if (showDeleteDialog.value) return@detectTapGestures

                                // On press start - pause animation
                                pauseAnimation.value = true
                                tryAwaitRelease()
                                // On release - resume animation
                                pauseAnimation.value = false
                            }
                        )
                    }
            ) {
                // Status content
                CommonImage(
                    data = statuses[currentStatus.value].imageUrl.toString(),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Gradient overlay at top for better visibility
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(ChatMeColors.darkOverlay, Color.Transparent),
                                startY = 0f,
                                endY = 150f
                            )
                        )
                )

                // User info at top
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        CommonImage(
                            data = statuses[currentStatus.value].user.imageUrl ?: "",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = statuses[currentStatus.value].user.name ?: "",
                                style = ChatMeTypography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "2h ago", // Replace with actual timestamp
                                style = ChatMeTypography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        // Delete button (only shown for current user's status)
                        if (statuses[currentStatus.value].user.userId == currentUser) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete status",
                                tint = ChatMeColors.error,
                                modifier = Modifier.size(30.dp).clickable {

                                    showDeleteDialog.value = true
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${currentStatus.value + 1}/${statuses.size}",
                            style = ChatMeTypography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Progress indicators
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        statuses.forEachIndexed { index, _ ->
                            CustomProgressIndicator(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .padding(horizontal = 2.dp),
                                state = when {
                                    currentStatus.value < index -> State.INITIAL
                                    currentStatus.value == index -> State.ACTIVE
                                    else -> State.COMPLETED
                                },
                                isPaused = pauseAnimation.value,
                                initialProgress = progressStates[index] ?: 0f,
                                onProgressUpdate = { progress ->
                                    progressStates[index] = progress
                                },
                                onCompleted = {
                                    if (currentStatus.value < statuses.size - 1) {
                                        progressStates[currentStatus.value] = 1f
                                        currentStatus.value++
                                    } else {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }
                    }
                }
                // Delete confirmation dialog
                if (showDeleteDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            showDeleteDialog.value = false
                            pauseAnimation.value = false
                        },
                        icon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = ChatMeColors.error
                            )
                        },
                        title = {
                            Text(
                                text = "Delete Status",
                                style = ChatMeTypography.titleLarge,
                                color = ChatMeColors.error
                            )
                        },
                        text = {
                            Text(
                                text = "Are you sure you want to delete this status?",
                                style = ChatMeTypography.bodyMedium,
                                color = ChatMeColors.lightOnSurface
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    vm.deleteStatus(statuses[currentStatus.value].timestamp.toString())
                                    showDeleteDialog.value = false
                                    navController.popBackStack()
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = ChatMeColors.error
                                )
                            ) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog.value = false
                                    pauseAnimation.value = false
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = ChatMeColors.lightOnSurface
                                )
                            ) {
                                Text("Cancel")
                            }
                        },
                        containerColor = ChatMeColors.lightSurface,
                        titleContentColor = ChatMeColors.error,
                        textContentColor = ChatMeColors.lightOnSurface
                    )
                }

            }
        }
    }}

    @Composable
    fun CustomProgressIndicator(
        modifier: Modifier,
        state: State,
        isPaused: Boolean = false,
        initialProgress: Float = 0f,
        onProgressUpdate: (Float) -> Unit = {},
        onCompleted: () -> Unit
    ) {
        var animationProgress by remember { mutableStateOf(initialProgress) }
        var animationJob by remember { mutableStateOf<Job?>(null) }

        LaunchedEffect(state) {
            // Reset animation when state changes
            animationJob?.cancel()
            animationProgress = when (state) {
                State.INITIAL -> 0f
                State.COMPLETED -> 1f
                State.ACTIVE -> initialProgress
            }
        }

        LaunchedEffect(state, isPaused) {
            if (state == State.ACTIVE) {
                if (!isPaused) {
                    // Start or resume animation
                    animationJob = coroutineScope {
                        launch {
                            val remainingTime = ((1 - animationProgress) * 5000).toInt()
                            animate(
                                initialValue = animationProgress,
                                targetValue = 1f,
                                animationSpec = tween(
                                    durationMillis = remainingTime,
                                    easing = LinearEasing
                                )
                            ) { value, _ ->
                                animationProgress = value
                                onProgressUpdate(value)
                                if (value >= 1f) {
                                    onCompleted()
                                }
                            }
                        }
                    }
                } else {
                    // Pause animation
                    animationJob?.cancel()
                    onProgressUpdate(animationProgress)
                }
            }
        }

        LinearProgressIndicator(
            modifier = modifier,
            progress = when (state) {
                State.COMPLETED -> 1f
                State.INITIAL -> 0f
                State.ACTIVE -> animationProgress
            },
            color = when (state) {
                State.COMPLETED -> ChatMeColors.statusProgressCompleted
                State.ACTIVE -> ChatMeColors.statusProgressActive
                State.INITIAL -> ChatMeColors.statusProgressInactive
            },
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }