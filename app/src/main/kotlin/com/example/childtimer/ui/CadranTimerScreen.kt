package com.example.childtimer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.childtimer.data.DefaultChildTimerPreferencesRepository
import com.example.childtimer.model.CadranAnimal
import com.example.childtimer.model.CadranDisplayMode
import com.example.childtimer.model.TimerThemeColor
import com.example.childtimer.sound.SoftSoundPlayer
import com.example.childtimer.ui.cadran.AnimalCadran
import com.example.childtimer.ui.components.ParentSettingsSheet
import com.example.childtimer.ui.theme.WarmWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

enum class CadranTimerPhase {
    SELECTING_DURATION,
    RUNNING,
    FINISHED
}

@Composable
fun CadranTimerScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val soundPlayer = remember { SoftSoundPlayer(context) }
    val preferencesRepository = remember { DefaultChildTimerPreferencesRepository(context) }

    val displayMode by preferencesRepository.displayMode.collectAsStateWithLifecycle()
    val themeColor by preferencesRepository.themeColor.collectAsStateWithLifecycle()
    val isSoundEnabled by preferencesRepository.isSoundEnabled.collectAsStateWithLifecycle()

    var phase by remember { mutableStateOf(CadranTimerPhase.SELECTING_DURATION) }
    var selectedMinutes by remember { mutableStateOf<Int?>(null) }
    var timerStartTimeMs by remember { mutableLongStateOf(0L) }
    var timerTotalDurationMs by remember { mutableLongStateOf(0L) }
    var remainingRatio by remember { mutableFloatStateOf(1f) }
    var isSettingsOpen by remember { mutableStateOf(false) }

    // Sync sound player mute state with repository preference
    LaunchedEffect(isSoundEnabled) {
        soundPlayer.setMuted(!isSoundEnabled)
    }

    // Accurate real-time timer calculation loop using system clock reference
    LaunchedEffect(phase, timerStartTimeMs, timerTotalDurationMs) {
        if (phase == CadranTimerPhase.RUNNING && timerTotalDurationMs > 0) {
            while (isActive && phase == CadranTimerPhase.RUNNING) {
                val now = System.currentTimeMillis()
                val elapsed = now - timerStartTimeMs
                val ratio = (1f - (elapsed.toFloat() / timerTotalDurationMs)).coerceIn(0f, 1f)
                remainingRatio = ratio

                if (ratio <= 0f) {
                    phase = CadranTimerPhase.FINISHED
                    soundPlayer.playCompletionChime(scope)
                    break
                }
                delay(80L) // Smooth 12.5 fps updates for smooth counter-clockwise retraction
            }
        }
    }

    // Play button pulse animation when visible
    val infiniteTransition = rememberInfiniteTransition(label = "playPulse")
    val playPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "playScale"
    )

    // Gentle star sparkles animation on finish
    val sparklesTransition = rememberInfiniteTransition(label = "sparkles")
    val sparkleOffset by sparklesTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleOffset"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("cadran_screen_root")
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Horizontal center position of the cadran:
        // Left position (~35% of width) when selecting, exact center (50% of width) when running or finished
        val targetCadranCenterRatio = when (phase) {
            CadranTimerPhase.SELECTING_DURATION -> 0.35f
            CadranTimerPhase.RUNNING, CadranTimerPhase.FINISHED -> 0.50f
        }

        val animatedCenterRatio by animateFloatAsState(
            targetValue = targetCadranCenterRatio,
            animationSpec = tween(durationMillis = 750, easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)),
            label = "cadran_horizontal_center"
        )

        // Cadran diameter (ensuring comfortable room for 12 items and clean margins)
        val cadranDiameterDp = minOf(maxHeight * 0.94f, maxWidth * 0.58f)
        val cadranDiameterPx = with(density) { cadranDiameterDp.toPx() }

        // Position of the cadran
        val cadranCenterXPx = widthPx * animatedCenterRatio
        val cadranTopLeftXPx = cadranCenterXPx - (cadranDiameterPx / 2f)
        val cadranTopLeftYPx = (heightPx - cadranDiameterPx) / 2f

        // 1. Subtle, gentle celebratory floating stars when finished
        if (phase == CadranTimerPhase.FINISHED) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val starColors = listOf(
                    themeColor.primaryColor.copy(alpha = 0.5f),
                    themeColor.softColor.copy(alpha = 0.6f),
                    Color(0xFFFFD54F).copy(alpha = 0.5f)
                )
                val baseAngles = listOf(20f, 75f, 140f, 195f, 260f, 320f)
                baseAngles.forEachIndexed { idx, deg ->
                    val rad = Math.toRadians(deg.toDouble())
                    val dist = (cadranDiameterPx * 0.52f) + (sparkleOffset * 18f * (if (idx % 2 == 0) 1f else -1f))
                    val sx = cadranCenterXPx + (dist * kotlin.math.cos(rad)).toFloat()
                    val sy = (heightPx / 2f) + (dist * kotlin.math.sin(rad)).toFloat()
                    drawCircle(
                        color = starColors[idx % starColors.size],
                        radius = 6.dp.toPx() * (0.8f + sparkleOffset * 0.4f),
                        center = Offset(sx, sy)
                    )
                }
            }
        }

        // 2. The Cadran Component
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        cadranTopLeftXPx.roundToInt(),
                        cadranTopLeftYPx.roundToInt()
                    )
                }
                .size(cadranDiameterDp)
        ) {
            AnimalCadran(
                selectedMinutes = selectedMinutes,
                remainingRatio = if (phase == CadranTimerPhase.RUNNING) remainingRatio else 1f,
                isRunning = (phase == CadranTimerPhase.RUNNING),
                isFinished = (phase == CadranTimerPhase.FINISHED),
                displayMode = displayMode,
                themeColor = themeColor,
                onSelectAnimal = { item ->
                    if (phase == CadranTimerPhase.SELECTING_DURATION) {
                        selectedMinutes = item.minutes
                        soundPlayer.playAnimalTap(scope)
                    }
                },
                onResetClick = {
                    soundPlayer.playResetFeedback()
                    phase = CadranTimerPhase.SELECTING_DURATION
                    selectedMinutes = null
                    remainingRatio = 1f
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 3. Play Button Area (On the right side, visible only in SELECTING_DURATION when duration is picked)
        val playButtonTargetXPx = widthPx * 0.77f
        val playButtonSizeDp = (minOf(maxHeight, maxWidth) * 0.26f).coerceIn(84.dp, 120.dp)
        val playButtonSizePx = with(density) { playButtonSizeDp.toPx() }

        AnimatedVisibility(
            visible = (phase == CadranTimerPhase.SELECTING_DURATION && selectedMinutes != null),
            enter = fadeIn(tween(400)) + scaleIn(spring(dampingRatio = 0.65f, stiffness = 400f)),
            exit = fadeOut(tween(300)) + scaleOut(tween(300)),
            modifier = Modifier
                .offset {
                    IntOffset(
                        (playButtonTargetXPx - playButtonSizePx / 2f).roundToInt(),
                        (heightPx / 2f - playButtonSizePx / 2f).roundToInt()
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .size(playButtonSizeDp)
                    .scale(playPulseScale)
                    .shadow(elevation = 12.dp, shape = CircleShape, spotColor = themeColor.darkColor.copy(alpha = 0.45f))
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(themeColor.softColor, themeColor.primaryColor)
                        )
                    )
                    .border(3.dp, WarmWhite, CircleShape)
                    .clickable {
                        val minutes = selectedMinutes ?: return@clickable
                        timerTotalDurationMs = minutes * 60 * 1000L
                        timerStartTimeMs = System.currentTimeMillis()
                        remainingRatio = 1f
                        phase = CadranTimerPhase.RUNNING
                        soundPlayer.playStartChime(scope)
                    }
                    .testTag("play_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Démarrer",
                    tint = WarmWhite,
                    modifier = Modifier.size(playButtonSizeDp * 0.52f)
                )
            }
        }

        // 4. Semicircle handle on the right edge without icon (only visible in SELECTING_DURATION)
        AnimatedVisibility(
            visible = (phase == CadranTimerPhase.SELECTING_DURATION),
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(200)),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            val handleRadius = 28.dp
            val semicircleShape = RoundedCornerShape(
                topStart = handleRadius,
                bottomStart = handleRadius,
                topEnd = 0.dp,
                bottomEnd = 0.dp
            )

            Box(
                modifier = Modifier
                    .size(width = handleRadius, height = handleRadius * 2)
                    .clip(semicircleShape)
                    .background(Color(0xFF1E2226).copy(alpha = 0.92f))
                    .border(1.dp, Color(0xFF38404A), semicircleShape)
                    .clickable { isSettingsOpen = true }
                    .testTag("open_settings_handle"),
                contentAlignment = Alignment.Center
            ) {
                // Subtle tactile grip bar inside the semicircle
                Box(
                    modifier = Modifier
                        .size(width = 3.dp, height = 18.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(themeColor.primaryColor.copy(alpha = 0.75f))
                )
            }
        }

        // 5. Parent Settings & Help Drawer Panel
        ParentSettingsSheet(
            isOpen = isSettingsOpen,
            displayMode = displayMode,
            themeColor = themeColor,
            isSoundEnabled = isSoundEnabled,
            onDisplayModeChange = { preferencesRepository.setDisplayMode(it) },
            onThemeColorChange = { preferencesRepository.setThemeColor(it) },
            onSoundToggle = { preferencesRepository.setSoundEnabled(it) },
            onDismiss = { isSettingsOpen = false }
        )
    }
}
