package com.example.childtimer.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundHelper(private val context: Context) {
    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playTick(soundEnabled: Boolean, tickEnabled: Boolean) {
        if (!soundEnabled || !tickEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
        } catch (_: Exception) {}
    }

    fun playStart(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
            } catch (_: Exception) {}
        }
        if (vibrationEnabled) {
            vibrateShort(60)
        }
    }

    fun playPause(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 100)
            } catch (_: Exception) {}
        }
        if (vibrationEnabled) {
            vibrateShort(40)
        }
    }

    fun playCelebrationFanfare(soundEnabled: Boolean, vibrationEnabled: Boolean, scope: CoroutineScope) {
        if (vibrationEnabled) {
            vibrateCelebration()
        }
        if (soundEnabled) {
            scope.launch(Dispatchers.IO) {
                try {
                    val tones = listOf(
                        ToneGenerator.TONE_PROP_ACK to 120L,
                        ToneGenerator.TONE_PROP_PROMPT to 150L,
                        ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD to 350L
                    )
                    for ((tone, duration) in tones) {
                        toneGenerator?.startTone(tone, duration.toInt())
                        delay(duration + 40L)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun vibrateShort(durationMs: Long = 40) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateCelebration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 100, 80, 120, 80, 220)
                val amplitudes = intArrayOf(0, 150, 0, 200, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 100, 80, 120, 80, 220), -1)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
