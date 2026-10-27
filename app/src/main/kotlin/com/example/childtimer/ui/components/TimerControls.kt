package com.example.childtimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.childtimer.model.AnimalCompanion

@Composable
fun TimerControls(
    isRunning: Boolean,
    isPaused: Boolean,
    isLocked: Boolean,
    animal: AnimalCompanion,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onAdjustMinutes: (Int) -> Unit,
    onToggleLock: () -> Unit,
    onOpenAnimalPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLocked) {
            // Child Lock Active Notice Banner with Unlock Button
            var unlockHint by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                    .border(1.5.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .testTag("child_lock_active_banner"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Child Lock Active 🔒",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (unlockHint) "Great! Tapping Unlock..." else "Press button to unlock controls",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onToggleLock,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("child_lock_unlock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Unlock",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unlock Controls")
                    }
                }
            }
        } else {
            // Quick +/- time adjustments (available when timer is paused or not running)
            if (!isRunning || isPaused) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { onAdjustMinutes(-1) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("adjust_minus_1_btn")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Minus 1 min", modifier = Modifier.size(16.dp))
                        Text(" -1m", fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = { onAdjustMinutes(1) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("adjust_plus_1_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Plus 1 min", modifier = Modifier.size(16.dp))
                        Text(" +1m", fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = { onAdjustMinutes(5) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("adjust_plus_5_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Plus 5 min", modifier = Modifier.size(16.dp))
                        Text(" +5m", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Main Primary Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isRunning) {
                    // Start Button
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(64.dp)
                            .testTag("timer_start_button"),
                        shape = RoundedCornerShape(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = animal.primaryColor),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Timer",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "START",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                } else if (isPaused) {
                    // Resume & Reset Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReset,
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .testTag("timer_reset_button"),
                            shape = RoundedCornerShape(29.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onResume,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(58.dp)
                                .testTag("timer_resume_button"),
                            shape = RoundedCornerShape(29.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = animal.primaryColor)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Resume", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                } else {
                    // Running: Pause & Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReset,
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .testTag("timer_reset_button"),
                            shape = RoundedCornerShape(29.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onPause,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(58.dp)
                                .testTag("timer_pause_button"),
                            shape = RoundedCornerShape(29.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = "Pause", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pause", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary Actions Row: Buddy Selector & Child Lock Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Change animal buddy
                ElevatedButton(
                    onClick = onOpenAnimalPicker,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("change_animal_buddy_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = "Animal Buddies",
                        tint = animal.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = animal.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Lock toggle button
                IconButton(
                    onClick = onToggleLock,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("child_lock_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = "Lock Controls",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
