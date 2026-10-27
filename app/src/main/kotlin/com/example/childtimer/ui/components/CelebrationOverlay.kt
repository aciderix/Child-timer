package com.example.childtimer.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.childtimer.model.AnimalCompanion
import kotlin.random.Random

private data class ConfettiParticle(
    val xPercent: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val initialDelay: Float
)

@Composable
fun CelebrationOverlay(
    animal: AnimalCompanion,
    routineTitle: String?,
    totalStars: Int,
    onDismiss: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.2f) }
    val confettiAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        confettiAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2600, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    val confettiColors = listOf(
        Color(0xFFFF5252),
        Color(0xFFFFD700),
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFF4081),
        Color(0xFF7C4DFF),
        Color(0xFFFF9800)
    )

    val particles = remember {
        List(40) {
            ConfettiParticle(
                xPercent = Random.nextFloat(),
                speed = 0.6f + Random.nextFloat() * 0.8f,
                size = 8f + Random.nextFloat() * 14f,
                color = confettiColors.random(),
                initialDelay = Random.nextFloat() * 0.5f
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .testTag("celebration_dialog_scrim"),
            contentAlignment = Alignment.Center
        ) {
            // Animated Falling Confetti
            Canvas(modifier = Modifier.fillMaxSize()) {
                val progress = confettiAnim.value
                particles.forEach { p ->
                    val effectiveProgress = (progress * p.speed + p.initialDelay) % 1f
                    val y = effectiveProgress * size.height
                    val x = p.xPercent * size.width + kotlin.math.sin(effectiveProgress * 12.0).toFloat() * 20f

                    drawRect(
                        color = p.color,
                        topLeft = Offset(x, y),
                        size = Size(p.size, p.size * 0.6f)
                    )
                }
            }

            // Celebration Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .scale(scaleAnim.value)
                    .padding(16.dp)
                    .testTag("celebration_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 BRAVO ! 🎉",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = animal.accentColor,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Time is up!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bouncing Animal Buddy
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(animal.primaryColor.copy(alpha = 0.2f))
                            .border(4.dp, animal.accentColor, CircleShape)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = animal.drawableRes),
                            contentDescription = animal.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = animal.celebrationQuote,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Reward Star Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF8E1))
                            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star Reward",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "+1 Star Earned! (Total: $totalStars ⭐)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(52.dp)
                            .testTag("celebration_continue_button"),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = animal.primaryColor)
                    ) {
                        Text(
                            text = "Super! Merci!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
