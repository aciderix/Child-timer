package com.example.childtimer.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.childtimer.data.ChildTimerPreferencesRepository
import com.example.childtimer.data.DefaultChildTimerPreferencesRepository
import com.example.childtimer.model.AnimalCompanion
import com.example.childtimer.model.AnimalCompanions
import com.example.childtimer.model.RoutinePreset
import com.example.childtimer.model.RoutinePresets
import com.example.childtimer.sound.SoundHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class TimerUiState(
    val totalSeconds: Int = 120,
    val remainingSeconds: Int = 120,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val selectedAnimal: AnimalCompanion = AnimalCompanions.default,
    val selectedRoutine: RoutinePreset? = RoutinePresets.default,
    val isLocked: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val isTickEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val keepScreenAwake: Boolean = true,
    val totalStars: Int = 3,
    val showCelebration: Boolean = false,
    val showAnimalPicker: Boolean = false,
    val showSettings: Boolean = false
)

class MainScreenViewModel(
    application: Application,
    private val preferencesRepository: ChildTimerPreferencesRepository = DefaultChildTimerPreferencesRepository(application)
) : AndroidViewModel(application) {

    private val soundHelper = SoundHelper(application)
    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(
        TimerUiState(
            selectedAnimal = preferencesRepository.selectedAnimal.value,
            selectedRoutine = preferencesRepository.selectedRoutine.value,
            isSoundEnabled = preferencesRepository.isSoundEnabled.value,
            isTickEnabled = preferencesRepository.isTickEnabled.value,
            isVibrationEnabled = preferencesRepository.isVibrationEnabled.value,
            keepScreenAwake = preferencesRepository.isKeepScreenAwake.value,
            totalStars = preferencesRepository.totalStarsEarned.value,
            totalSeconds = (preferencesRepository.selectedRoutine.value?.durationMinutes ?: 2) * 60,
            remainingSeconds = (preferencesRepository.selectedRoutine.value?.durationMinutes ?: 2) * 60
        )
    )
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.selectedAnimal.collect { animal ->
                _uiState.update { it.copy(selectedAnimal = animal) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.totalStarsEarned.collect { stars ->
                _uiState.update { it.copy(totalStars = stars) }
            }
        }
    }

    fun startTimer() {
        if (_uiState.value.remainingSeconds <= 0) {
            val resetSecs = if (_uiState.value.totalSeconds > 0) _uiState.value.totalSeconds else 120
            _uiState.update { it.copy(remainingSeconds = resetSecs, totalSeconds = resetSecs) }
        }

        soundHelper.playStart(_uiState.value.isSoundEnabled, _uiState.value.isVibrationEnabled)
        _uiState.update { it.copy(isRunning = true, isPaused = false, isFinished = false) }
        launchTimerLoop()
    }

    fun pauseTimer() {
        soundHelper.playPause(_uiState.value.isSoundEnabled, _uiState.value.isVibrationEnabled)
        timerJob?.cancel()
        _uiState.update { it.copy(isPaused = true) }
    }

    fun resumeTimer() {
        soundHelper.playStart(_uiState.value.isSoundEnabled, _uiState.value.isVibrationEnabled)
        _uiState.update { it.copy(isPaused = false) }
        launchTimerLoop()
    }

    fun resetTimer() {
        soundHelper.playPause(_uiState.value.isSoundEnabled, _uiState.value.isVibrationEnabled)
        timerJob?.cancel()
        val defaultSec = _uiState.value.totalSeconds
        _uiState.update {
            it.copy(
                isRunning = false,
                isPaused = false,
                isFinished = false,
                remainingSeconds = defaultSec
            )
        }
    }

    private fun launchTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _uiState.value.remainingSeconds > 0) {
                delay(1000L)
                if (!_uiState.value.isPaused) {
                    val next = _uiState.value.remainingSeconds - 1
                    _uiState.update { it.copy(remainingSeconds = next) }

                    if (next > 0) {
                        soundHelper.playTick(_uiState.value.isSoundEnabled, _uiState.value.isTickEnabled)
                    } else {
                        // Timer completed!
                        onTimerFinished()
                        break
                    }
                }
            }
        }
    }

    private fun onTimerFinished() {
        val updatedStars = preferencesRepository.addStar()
        _uiState.update {
            it.copy(
                isRunning = false,
                isPaused = false,
                isFinished = true,
                totalStars = updatedStars,
                showCelebration = true
            )
        }
        soundHelper.playCelebrationFanfare(
            _uiState.value.isSoundEnabled,
            _uiState.value.isVibrationEnabled,
            viewModelScope
        )
    }

    fun selectRoutine(routine: RoutinePreset) {
        timerJob?.cancel()
        preferencesRepository.setRoutine(routine)
        val sec = routine.durationMinutes * 60
        _uiState.update {
            it.copy(
                selectedRoutine = routine,
                totalSeconds = sec,
                remainingSeconds = sec,
                isRunning = false,
                isPaused = false,
                isFinished = false
            )
        }

        // Recommend animal if not manually overridden
        val rec = AnimalCompanions.all.find { it.id == routine.recommendedAnimalId }
        if (rec != null) {
            selectAnimal(rec)
        }
    }

    fun adjustMinutes(deltaMinutes: Int) {
        val currentMinutes = _uiState.value.totalSeconds / 60
        val newMinutes = (currentMinutes + deltaMinutes).coerceIn(1, 60)
        val newSeconds = newMinutes * 60
        _uiState.update {
            it.copy(
                totalSeconds = newSeconds,
                remainingSeconds = newSeconds,
                selectedRoutine = null // Custom time
            )
        }
        soundHelper.vibrateShort(25)
    }

    fun selectAnimal(animal: AnimalCompanion) {
        preferencesRepository.setAnimal(animal)
        _uiState.update { it.copy(selectedAnimal = animal) }
        soundHelper.vibrateShort(35)
    }

    fun toggleLock() {
        val next = !_uiState.value.isLocked
        _uiState.update { it.copy(isLocked = next) }
        soundHelper.vibrateShort(50)
    }

    fun toggleSound() {
        val next = !_uiState.value.isSoundEnabled
        preferencesRepository.setSoundEnabled(next)
        _uiState.update { it.copy(isSoundEnabled = next) }
    }

    fun toggleTick() {
        val next = !_uiState.value.isTickEnabled
        preferencesRepository.setTickEnabled(next)
        _uiState.update { it.copy(isTickEnabled = next) }
    }

    fun toggleVibration() {
        val next = !_uiState.value.isVibrationEnabled
        preferencesRepository.setVibrationEnabled(next)
        _uiState.update { it.copy(isVibrationEnabled = next) }
    }

    fun toggleKeepScreenAwake() {
        val next = !_uiState.value.keepScreenAwake
        preferencesRepository.setKeepScreenAwake(next)
        _uiState.update { it.copy(keepScreenAwake = next) }
    }

    fun openAnimalPicker() {
        _uiState.update { it.copy(showAnimalPicker = true) }
    }

    fun closeAnimalPicker() {
        _uiState.update { it.copy(showAnimalPicker = false) }
    }

    fun openSettings() {
        _uiState.update { it.copy(showSettings = true) }
    }

    fun closeSettings() {
        _uiState.update { it.copy(showSettings = false) }
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(showCelebration = false) }
        resetTimer()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        soundHelper.release()
    }
}
