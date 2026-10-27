package com.example.childtimer.ui.main

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.childtimer.R
import com.example.childtimer.ui.components.AnimalPickerSheet
import com.example.childtimer.ui.components.CelebrationOverlay
import com.example.childtimer.ui.components.RoutinePresetsRow
import com.example.childtimer.ui.components.SettingsSheet
import com.example.childtimer.ui.components.TimerControls
import com.example.childtimer.ui.components.VisualTimerDial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Keep screen awake while countdown is actively running
    DisposableEffect(uiState.isRunning, uiState.keepScreenAwake) {
        val window = (context as? Activity)?.window
        if (uiState.isRunning && uiState.keepScreenAwake && window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🦁 " + stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                actions = {
                    // Star Trophy Counter Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFFFECB3))
                            .clickable { viewModel.openSettings() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("stars_badge_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = Color(0xFFFF8F00),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.totalStars}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }

                    // Settings Button
                    IconButton(
                        onClick = { viewModel.openSettings() },
                        modifier = Modifier.testTag("settings_top_bar_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visual Timer Dial (Clock dial with shrinking sector + central animal)
            VisualTimerDial(
                remainingSeconds = uiState.remainingSeconds,
                totalSeconds = uiState.totalSeconds,
                isRunning = uiState.isRunning,
                isPaused = uiState.isPaused,
                animal = uiState.selectedAnimal,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Routine Presets Row (Teeth, Tidy, Reading, Craft, Bath, Nap)
            if (!uiState.isLocked) {
                RoutinePresetsRow(
                    selectedRoutine = uiState.selectedRoutine,
                    onSelectRoutine = { routine -> viewModel.selectRoutine(routine) }
                )
            }

            // Primary & Secondary Timer Controls
            TimerControls(
                isRunning = uiState.isRunning,
                isPaused = uiState.isPaused,
                isLocked = uiState.isLocked,
                animal = uiState.selectedAnimal,
                onStart = { viewModel.startTimer() },
                onPause = { viewModel.pauseTimer() },
                onResume = { viewModel.resumeTimer() },
                onReset = { viewModel.resetTimer() },
                onAdjustMinutes = { delta -> viewModel.adjustMinutes(delta) },
                onToggleLock = { viewModel.toggleLock() },
                onOpenAnimalPicker = { viewModel.openAnimalPicker() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Celebration Overlay when timer is completed
    if (uiState.showCelebration) {
        CelebrationOverlay(
            animal = uiState.selectedAnimal,
            routineTitle = uiState.selectedRoutine?.title,
            totalStars = uiState.totalStars,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }

    // Animal Picker Bottom Sheet
    if (uiState.showAnimalPicker) {
        AnimalPickerSheet(
            selectedAnimal = uiState.selectedAnimal,
            onSelectAnimal = { animal -> viewModel.selectAnimal(animal) },
            onDismiss = { viewModel.closeAnimalPicker() }
        )
    }

    // Settings Bottom Sheet
    if (uiState.showSettings) {
        SettingsSheet(
            isSoundEnabled = uiState.isSoundEnabled,
            isTickEnabled = uiState.isTickEnabled,
            isVibrationEnabled = uiState.isVibrationEnabled,
            isKeepScreenAwake = uiState.keepScreenAwake,
            totalStars = uiState.totalStars,
            onToggleSound = { viewModel.toggleSound() },
            onToggleTick = { viewModel.toggleTick() },
            onToggleVibration = { viewModel.toggleVibration() },
            onToggleKeepScreenAwake = { viewModel.toggleKeepScreenAwake() },
            onDismiss = { viewModel.closeSettings() }
        )
    }
}
