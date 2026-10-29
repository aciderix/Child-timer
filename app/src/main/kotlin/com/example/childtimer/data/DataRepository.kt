package com.example.childtimer.data

import android.content.Context
import android.content.SharedPreferences
import com.example.childtimer.model.AnimalCompanion
import com.example.childtimer.model.AnimalCompanions
import com.example.childtimer.model.CadranDisplayMode
import com.example.childtimer.model.RoutinePreset
import com.example.childtimer.model.RoutinePresets
import com.example.childtimer.model.TimerThemeColor
import com.example.childtimer.model.TimerThemeColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ChildTimerPreferencesRepository {
    val selectedAnimal: StateFlow<AnimalCompanion>
    val selectedRoutine: StateFlow<RoutinePreset?>
    val isSoundEnabled: StateFlow<Boolean>
    val isTickEnabled: StateFlow<Boolean>
    val isVibrationEnabled: StateFlow<Boolean>
    val isKeepScreenAwake: StateFlow<Boolean>
    val totalStarsEarned: StateFlow<Int>
    val displayMode: StateFlow<CadranDisplayMode>
    val themeColor: StateFlow<TimerThemeColor>

    fun setAnimal(animal: AnimalCompanion)
    fun setRoutine(routine: RoutinePreset?)
    fun setSoundEnabled(enabled: Boolean)
    fun setTickEnabled(enabled: Boolean)
    fun setVibrationEnabled(enabled: Boolean)
    fun setKeepScreenAwake(enabled: Boolean)
    fun setDisplayMode(mode: CadranDisplayMode)
    fun setThemeColor(color: TimerThemeColor)
    fun addStar(): Int
}

class DefaultChildTimerPreferencesRepository(context: Context) : ChildTimerPreferencesRepository {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("child_timer_prefs", Context.MODE_PRIVATE)

    private val _selectedAnimal = MutableStateFlow(loadInitialAnimal())
    override val selectedAnimal: StateFlow<AnimalCompanion> = _selectedAnimal.asStateFlow()

    private val _selectedRoutine = MutableStateFlow<RoutinePreset?>(RoutinePresets.default)
    override val selectedRoutine: StateFlow<RoutinePreset?> = _selectedRoutine.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean("sound_enabled", true))
    override val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _isTickEnabled = MutableStateFlow(prefs.getBoolean("tick_enabled", true))
    override val isTickEnabled: StateFlow<Boolean> = _isTickEnabled.asStateFlow()

    private val _isVibrationEnabled = MutableStateFlow(prefs.getBoolean("vibration_enabled", true))
    override val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled.asStateFlow()

    private val _isKeepScreenAwake = MutableStateFlow(prefs.getBoolean("keep_awake", true))
    override val isKeepScreenAwake: StateFlow<Boolean> = _isKeepScreenAwake.asStateFlow()

    private val _totalStarsEarned = MutableStateFlow(prefs.getInt("total_stars", 3))
    override val totalStarsEarned: StateFlow<Int> = _totalStarsEarned.asStateFlow()

    private val _displayMode = MutableStateFlow(loadInitialDisplayMode())
    override val displayMode: StateFlow<CadranDisplayMode> = _displayMode.asStateFlow()

    private val _themeColor = MutableStateFlow(loadInitialThemeColor())
    override val themeColor: StateFlow<TimerThemeColor> = _themeColor.asStateFlow()

    private fun loadInitialAnimal(): AnimalCompanion {
        val savedId = prefs.getString("animal_id", AnimalCompanions.default.id)
        return AnimalCompanions.all.find { it.id == savedId } ?: AnimalCompanions.default
    }

    private fun loadInitialDisplayMode(): CadranDisplayMode {
        val savedMode = prefs.getString("display_mode", CadranDisplayMode.ANIMALS.name)
        return try {
            CadranDisplayMode.valueOf(savedMode ?: CadranDisplayMode.ANIMALS.name)
        } catch (_: Exception) {
            CadranDisplayMode.ANIMALS
        }
    }

    private fun loadInitialThemeColor(): TimerThemeColor {
        val savedColorId = prefs.getString("theme_color_id", TimerThemeColors.default.id)
        return TimerThemeColors.forId(savedColorId ?: TimerThemeColors.default.id)
    }

    override fun setAnimal(animal: AnimalCompanion) {
        _selectedAnimal.value = animal
        prefs.edit().putString("animal_id", animal.id).apply()
    }

    override fun setRoutine(routine: RoutinePreset?) {
        _selectedRoutine.value = routine
    }

    override fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
    }

    override fun setTickEnabled(enabled: Boolean) {
        _isTickEnabled.value = enabled
        prefs.edit().putBoolean("tick_enabled", enabled).apply()
    }

    override fun setVibrationEnabled(enabled: Boolean) {
        _isVibrationEnabled.value = enabled
        prefs.edit().putBoolean("vibration_enabled", enabled).apply()
    }

    override fun setKeepScreenAwake(enabled: Boolean) {
        _isKeepScreenAwake.value = enabled
        prefs.edit().putBoolean("keep_awake", enabled).apply()
    }

    override fun setDisplayMode(mode: CadranDisplayMode) {
        _displayMode.value = mode
        prefs.edit().putString("display_mode", mode.name).apply()
    }

    override fun setThemeColor(color: TimerThemeColor) {
        _themeColor.value = color
        prefs.edit().putString("theme_color_id", color.id).apply()
    }

    override fun addStar(): Int {
        val updated = _totalStarsEarned.value + 1
        _totalStarsEarned.value = updated
        prefs.edit().putInt("total_stars", updated).apply()
        return updated
    }
}
