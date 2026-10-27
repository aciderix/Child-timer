package com.example.childtimer.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

class SoftSoundPlayer(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val sampleRate = 44100
    private var isMuted = false

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    fun isMuted(): Boolean = isMuted

    // Soft water-drop / wood-block tap when touching an animal
    fun playAnimalTap(scope: CoroutineScope) {
        if (isMuted) return
        vibrateShort(25)
        scope.launch(Dispatchers.Default) {
            playTone(frequency = 587.33, durationMs = 90, volume = 0.45) // D5
        }
    }

    // Gentle ascending chime when starting the timer
    fun playStartChime(scope: CoroutineScope) {
        if (isMuted) return
        vibrateShort(40)
        scope.launch(Dispatchers.Default) {
            playTone(frequency = 523.25, durationMs = 140, volume = 0.4) // C5
            kotlinx.coroutines.delay(100)
            playTone(frequency = 659.25, durationMs = 200, volume = 0.45) // E5
        }
    }

    // Gentle completion chime played ONCE at the end of the timer
    fun playCompletionChime(scope: CoroutineScope) {
        if (isMuted) return
        vibrateCelebration()
        scope.launch(Dispatchers.Default) {
            val notes = listOf(
                523.25 to 220, // C5
                659.25 to 220, // E5
                783.99 to 260, // G5
                1046.50 to 550 // C6
            )
            for ((freq, dur) in notes) {
                playTone(frequency = freq, durationMs = dur, volume = 0.5)
            }
        }
    }
    // Feedback when toggling sound via 3s long press
    fun playToggleSoundFeedback(unmuted: Boolean, scope: CoroutineScope) {
        vibrateShort(60)
        if (unmuted) {
            scope.launch(Dispatchers.Default) {
                playTone(frequency = 523.25, durationMs = 120, volume = 0.4) // C5
                kotlinx.coroutines.delay(90)
                playTone(frequency = 783.99, durationMs = 180, volume = 0.45) // G5
            }
        }
    }

    // Feedback for 10-tap secret reset
    fun playResetFeedback() {
        vibrateCelebration()
    }

    private fun playTone(frequency: Double, durationMs: Int, volume: Double = 0.5) {
        try {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)
            val attackSamples = (sampleRate * 0.015).toInt().coerceAtLeast(1) // 15ms soft attack

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Fundamental sine wave + subtle warm harmonic overtone (octave)
                val fundamental = sin(2.0 * Math.PI * frequency * t)
                val harmonic = 0.25 * sin(4.0 * Math.PI * frequency * t)
                val rawWave = fundamental + harmonic

                // Envelope: smooth cubic attack + natural exponential decay
                val envelope = when {
                    i < attackSamples -> (i.toDouble() / attackSamples).let { it * it }
                    else -> exp(-4.5 * (i - attackSamples).toDouble() / numSamples)
                }

                val sampleVal = (rawWave * envelope * volume * Short.MAX_VALUE).coerceIn(
                    Short.MIN_VALUE.toDouble(),
                    Short.MAX_VALUE.toDouble()
                )
                samples[i] = sampleVal.toInt().toShort()
            }

            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(numSamples * 2)

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, numSamples)
            audioTrack.play()

            // Release after playing
            Thread {
                try {
                    Thread.sleep(durationMs.toLong() + 50)
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }.start()
        } catch (_: Exception) {}
    }

    private fun vibrateShort(durationMs: Long = 30) {
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
                val timings = longArrayOf(0, 120, 100, 150, 100, 250)
                val amplitudes = intArrayOf(0, 140, 0, 180, 0, 220)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 120, 100, 150, 100, 250), -1)
            }
        } catch (_: Exception) {}
    }
}
