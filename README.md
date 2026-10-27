# Child Timer 🦁⏱️

A cheerful, modern Android visual countdown timer for children, built with Kotlin and Jetpack Compose. Inspired by visual time discs (like the Time Timer), **Child Timer** makes the passage of time concrete, friendly, and engaging for toddlers and young kids through delightful animal companions, routine presets, sound cues, and celebratory star rewards.

---

## ✨ Features

- **12 Adorable Animal Companions:**
  - 🦁 **Leo the Lion** (*Léo le Lion*) - Brave & energetic
  - 🦊 **Rusty the Fox** (*Rox le Renard*) - Clever & quick
  - 🐰 **Bunny Hop** (*Pompom le Lapin*) - Playful & joyful
  - 🐼 **Bamboo Panda** (*Bambou le Panda*) - Calm & serene
  - 🦒 **Gigi the Giraffe** (*Gigi la Girafe*) - Tall & curious
  - 🐻 **Barnaby Bear** (*Barnabé l'Ours*) - Big warm hugs
  - 🐒 **Milo the Monkey** (*Ouistiti le Singe*) - Cheerful acrobat
  - 🐊 **Chomp the Croc** (*Croc le Croco*) - Sparkling shiny teeth
  - 🦉 **Hoot the Owl** (*Hibou Sage*) - Wise & focused
  - 🦓 **Zigzag Zebra** (*Zigzag le Zèbre*) - Fast striped sprinter
  - 🐿️ **Nutty Squirrel** (*Noisette l'Écureuil*) - Acorn collector
  - 🐘 **Peanut Elephant** (*Pif l'Éléphant*) - Strong & patient

- **Visual Countdown Dial:**
  - Time-Timer style colored sweep wedge that shrinks as time passes.
  - Centered animal avatar with breathing pulse animation during countdown.
  - Big, legible MM:SS digital readout.

- **Routine Presets:**
  - 🪥 **Brush Teeth:** 2 minutes
  - 🧸 **Tidy Up Toys:** 5 minutes
  - 📖 **Story Time:** 10 minutes
  - 🎨 **Drawing & Craft:** 15 minutes
  - 🛁 **Bath & Bubbles:** 20 minutes
  - 💤 **Quiet Nap / Rest:** 30 minutes
  - ⏱️ **Custom Time:** Quick adjustment (+1m, +5m, -1m)

- **Sound & Haptics Feedback:**
  - Sweet synthesized chimes for Start, Pause, and Completion fanfare via Android's `ToneGenerator`.
  - Optional soft ticking every second.
  - Haptic vibration feedback for tactile responses.

- **Celebration & Rewards:**
  - Bursting confetti animation on countdown completion.
  - Celebratory animal victory quotes.
  - Star Trophy counter (`+1 ⭐`) to celebrate positive habits and completed routines.

- **Child Lock & Screen Awake:**
  - Child lock preventing toddlers from accidentally stopping or altering the timer.
  - Keeps the device screen awake during active countdowns.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin 2.3
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** MVVM (Model-View-ViewModel) with Kotlin Coroutines and StateFlow
- **Navigation:** Android Navigation 3
- **Audio & Haptics:** `android.media.ToneGenerator` & `android.os.Vibrator`
- **Target SDK:** 36 (Android 16) / Min SDK: 24 (Android 7.0)
