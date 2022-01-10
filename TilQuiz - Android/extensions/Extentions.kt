package com.madortilofficialapps.tilquiz.extensions

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.util.Patterns
import android.view.View
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.model.SchemaLibrary
import com.madortilofficialapps.tilquiz.model.TriviaQuestion
import com.madortilofficialapps.tilquiz.model.TriviaQuestion.Topic.*

fun String.isValidEmail() : Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword() : Boolean {
    return length >= 6
}

fun String.isValidNickname() : Boolean {
    return length >= 3
}

fun Int.random(): Int {
    return java.util.Random().nextInt(this)
}

fun View.shake() {
    (AnimatorInflater.loadAnimator(context, R.animator.shake) as AnimatorSet).apply {
        setTarget(this@shake)
        start()
    }
}

val SchemaLibrary.lastStepTitle: String?
    get() {
        if (currentStep > 0 && currentStep <= library.size) {
            return library[currentStep-1].title
        }
        return null
    }
val SchemaLibrary.lastStepLayer: Int
    get() {
        if (currentStep > 0 && currentStep <= library.size) {
            return library[currentStep - 1].layer
        }
        return 0
    }
fun SchemaLibrary.Companion.subLibraryIndexCoorespondingBubbleID(subLibraryIndex: Int): Int {
    return when (subLibraryIndex) {
        0 -> R.drawable.s_bubble
        1 -> R.drawable.a_bubble
        2 -> R.drawable.b_bubble
        3 -> R.drawable.c_bubble
        4 -> R.drawable.d_bubble
        5 -> R.drawable.e_bubble
        else -> R.drawable.s_bubble
    }
}

fun TriviaQuestion.Topic.Companion.correspondingBubbleIDForTopic(topic: TriviaQuestion.Topic): Int {
    return when (topic) {
        anatomy -> R.drawable.anatomy_bubble
        cpr -> R.drawable.cpr_bubble
        routine -> R.drawable.rutine_bubble
        medicine -> R.drawable.medicine_bubble
        anamnesis -> R.drawable.anamnesis_bubble
        trauma -> R.drawable.trauma_bubble
        mentalHealth -> R.drawable.mental_health_bubble
        publicHealth -> R.drawable.public_health_bubble
        nbc -> R.drawable.nbc_bubble
    }
}
fun TriviaQuestion.Topic.Companion.titleResourceForTopic(topic: TriviaQuestion.Topic) : Int {
    return when (topic) {
        anatomy -> R.string.title_trivia_topic_anatomy
        cpr -> R.string.title_trivia_topic_cpr
        routine -> R.string.title_trivia_topic_routine
        medicine -> R.string.title_trivia_topic_medicine
        anamnesis -> R.string.title_trivia_topic_anamnesis
        trauma -> R.string.title_trivia_topic_trauma
        mentalHealth -> R.string.title_trivia_topic_mentalHealth
        publicHealth -> R.string.title_trivia_topic_publicHealth
        nbc -> R.string.title_trivia_topic_nbc
    }
}

