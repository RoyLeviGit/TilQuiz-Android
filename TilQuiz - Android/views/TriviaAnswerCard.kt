package com.madortilofficialapps.tilquiz.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.madortilofficialapps.tilquiz.R

class TriviaAnswerCard @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AnswerView(context, attrs, defStyleAttr) {

    override var activity: FragmentActivity? = null

    override var originalXOrigin: Float? = null
    override var originalYOrigin: Float? = null

    var cardLeanRotation: CardLeanRotation = CardLeanRotation.Right

    enum class CardLeanRotation {
        Right, Left
    }

    private val leanAngle: Float
        get() = when (cardLeanRotation) {
            CardLeanRotation.Right -> -22.5F
            CardLeanRotation.Left -> 22.5F
        }

    override var inAnimationDuration: Long = resources.getInteger(R.integer.triviaInAnimationDuration).toLong()
    override var outAnimationDuration: Long = resources.getInteger(R.integer.triviaOutAnimationDuration).toLong()
    override var answerShownDelay: Long = resources.getInteger(R.integer.triviaAnswerShownDelay).toLong()
    override var answerColorChangeDuration: Long = resources.getInteger(R.integer.triviaAnswerColorChangeDuration).toLong()

    override fun animateToOriginalPosition(animationCompleted: (() -> Unit)?) {
        activity?.runOnUiThread {
            setTextColor(ContextCompat.getColor(context, R.color.colorTextWhite))
            animate().setDuration(inAnimationDuration).setInterpolator(AccelerateDecelerateInterpolator())
                    .translationX(originalXOrigin!!).translationY(originalYOrigin!!)
                    .rotation(leanAngle).withEndAction {
                        animationCompleted?.invoke()
                    }.start()
        }
    }
}