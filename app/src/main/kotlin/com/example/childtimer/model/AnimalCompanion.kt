package com.example.childtimer.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.childtimer.R

data class AnimalCompanion(
    val id: String,
    val name: String,
    val frenchName: String,
    @DrawableRes val drawableRes: Int,
    val primaryColor: Color,
    val accentColor: Color,
    val soundMotto: String,
    val cheeringQuote: String,
    val celebrationQuote: String
)

object AnimalCompanions {
    val all: List<AnimalCompanion> = listOf(
        AnimalCompanion(
            id = "lion",
            name = "Leo the Lion",
            frenchName = "Léo le Lion",
            drawableRes = R.drawable.lion,
            primaryColor = Color(0xFFFFB300),
            accentColor = Color(0xFFFF6F00),
            soundMotto = "Roaaar !",
            cheeringQuote = "Be brave and stay focused like a little lion!",
            celebrationQuote = "Magnificent roar! You did it like a champion!"
        ),
        AnimalCompanion(
            id = "renard",
            name = "Rusty the Fox",
            frenchName = "Rox le Renard",
            drawableRes = R.drawable.renard,
            primaryColor = Color(0xFFFF7043),
            accentColor = Color(0xFFD84315),
            soundMotto = "Yip yip !",
            cheeringQuote = "Clever and quick! Keep watching the clock turn!",
            celebrationQuote = "Super smart fox! Activity fully accomplished!"
        ),
        AnimalCompanion(
            id = "lapin",
            name = "Bunny Hop",
            frenchName = "Pompom le Lapin",
            drawableRes = R.drawable.lapin,
            primaryColor = Color(0xFFF48FB1),
            accentColor = Color(0xFFC2185B),
            soundMotto = "Boing boing !",
            cheeringQuote = "Hop, hop, hop! Almost at the finish line!",
            celebrationQuote = "Hippity-hop! Wonderful job, little bunny!"
        ),
        AnimalCompanion(
            id = "panda",
            name = "Bamboo Panda",
            frenchName = "Bambou le Panda",
            drawableRes = R.drawable.panda,
            primaryColor = Color(0xFF4DB6AC),
            accentColor = Color(0xFF00796B),
            soundMotto = "Crunch crunch !",
            cheeringQuote = "Calm and peaceful, time flows gently like a stream.",
            celebrationQuote = "Sweet serenity! Bravo for taking your time!"
        ),
        AnimalCompanion(
            id = "girafe",
            name = "Gigi the Giraffe",
            frenchName = "Gigi la Girafe",
            drawableRes = R.drawable.girafe,
            primaryColor = Color(0xFFFFCA28),
            accentColor = Color(0xFFF57F17),
            soundMotto = "Stretch high !",
            cheeringQuote = "Reach for the stars! You have a great view of your goal!",
            celebrationQuote = "Head held high! You finished everything!"
        ),
        AnimalCompanion(
            id = "ours",
            name = "Barnaby Bear",
            frenchName = "Barnabé l'Ours",
            drawableRes = R.drawable.ours,
            primaryColor = Color(0xFFA1887F),
            accentColor = Color(0xFF5D4037),
            soundMotto = "Grrr-hugs !",
            cheeringQuote = "Strong and cozy! Big warm hugs while you finish!",
            celebrationQuote = "A giant bear hug for your awesome success!"
        ),
        AnimalCompanion(
            id = "singe",
            name = "Milo the Monkey",
            frenchName = "Ouistiti le Singe",
            drawableRes = R.drawable.singe,
            primaryColor = Color(0xFFB0BEC5),
            accentColor = Color(0xFF37474F),
            soundMotto = "Ooh ooh aah !",
            cheeringQuote = "Swing from minute to minute with a big smile!",
            celebrationQuote = "Banana party! You swung all the way through!"
        ),
        AnimalCompanion(
            id = "croco",
            name = "Chomp the Croc",
            frenchName = "Croc le Croco",
            drawableRes = R.drawable.croco,
            primaryColor = Color(0xFF81C784),
            accentColor = Color(0xFF2E7D32),
            soundMotto = "Chomp chomp !",
            cheeringQuote = "Keep those teeth sparkly clean and bright!",
            celebrationQuote = "Dazzling smile! Pristine and neat!"
        ),
        AnimalCompanion(
            id = "hibou",
            name = "Hoot the Owl",
            frenchName = "Hibou Sage",
            drawableRes = R.drawable.hibou,
            primaryColor = Color(0xFF7986CB),
            accentColor = Color(0xFF283593),
            soundMotto = "Hoot hoot !",
            cheeringQuote = "Wise little owl, concentrating quietly.",
            celebrationQuote = "Super wise! You mastered every second!"
        ),
        AnimalCompanion(
            id = "zebre",
            name = "Zigzag Zebra",
            frenchName = "Zigzag le Zèbre",
            drawableRes = R.drawable.zebre,
            primaryColor = Color(0xFF64B5F6),
            accentColor = Color(0xFF1565C0),
            soundMotto = "Clip clop !",
            cheeringQuote = "Galloping along! You're pacing yourself wonderfully!",
            celebrationQuote = "Crossed the finish stripe in flying colors!"
        ),
        AnimalCompanion(
            id = "ecureuil",
            name = "Nutty Squirrel",
            frenchName = "Noisette l'Écureuil",
            drawableRes = R.drawable.ecureuil,
            primaryColor = Color(0xFFFF8A65),
            accentColor = Color(0xFFBF360C),
            soundMotto = "Chitter chatter !",
            cheeringQuote = "Gathering up good habits like acorns!",
            celebrationQuote = "A whole basket of success! Fantastic!"
        ),
        AnimalCompanion(
            id = "elephant",
            name = "Peanut Elephant",
            frenchName = "Pif l'Éléphant",
            drawableRes = R.drawable.elephant,
            primaryColor = Color(0xFFBA68C8),
            accentColor = Color(0xFF6A1B9A),
            soundMotto = "Tooooot !",
            cheeringQuote = "Step by big step, steady and reliable!",
            celebrationQuote = "Trumpet fanfare! What a tremendous accomplishment!"
        )
    )

    val default: AnimalCompanion = all[0] // Leo the Lion
}
