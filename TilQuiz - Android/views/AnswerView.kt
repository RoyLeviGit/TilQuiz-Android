package com.madortilofficialapps.tilquiz.views

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.madortilofficialapps.tilquiz.R
import java.util.*
import kotlin.concurrent.schedule

abstract class AnswerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : Button(context, attrs, defStyleAttr) {

    abstract var activity: FragmentActivity?

    abstract var originalXOrigin: Float?
    abstract var originalYOrigin: Float?

    abstract var inAnimationDuration: Long
    abstract var outAnimationDuration: Long
    abstract var answerShownDelay: Long
    abstract var answerColorChangeDuration: Long

    open fun animateToOriginalPosition(animationCompleted: (() -> Unit)? = null) {
        activity?.runOnUiThread {
            setTextColor(ContextCompat.getColor(context, R.color.colorTextWhite))
            animate().setDuration(inAnimationDuration)
                    .translationX(originalXOrigin!!).translationY(originalYOrigin!!).withEndAction {
                        animationCompleted?.invoke()
                    }.start()
        }
    }

    fun animateCorrectAnswerPicked(animationCompleted: (() -> Unit)? = null) {
        activity?.runOnUiThread {
            animate().setDuration(outAnimationDuration)
                    .translationXBy(resources.displayMetrics.widthPixels.toFloat())
                    .rotation(0F).withEndAction {
                        animationCompleted?.invoke()
                    }.start()
        }
    }

    fun animateIncorrectAnswerPicked(animationCompleted: (() -> Unit)? = null) {
        activity?.runOnUiThread {
            animate().setDuration(outAnimationDuration).setInterpolator(AccelerateDecelerateInterpolator())
                    .translationXBy(-resources.displayMetrics.widthPixels.toFloat())
                    .rotation(0F).withEndAction {
                        animationCompleted?.invoke()
                    }.start()
        }
    }

    fun animateCorrectAnswer(animationCompleted: (() -> Unit)? = null) {
        activity?.runOnUiThread {
            val animation = ObjectAnimator.ofObject(
                    this, "textColor", ArgbEvaluator(), ContextCompat.getColor(context, R.color.colorTextWhite), Color.GREEN)
                    .setDuration(answerColorChangeDuration)

            animation.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    Timer().schedule(answerShownDelay) {
                        animationCompleted?.invoke()
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })
            animation.start()
        }
    }

    fun animateIncorrectAnswer(animationCompleted: (() -> Unit)? = null) {
        activity?.runOnUiThread {
            val animation = ObjectAnimator.ofObject(
                    this, "textColor", ArgbEvaluator(), ContextCompat.getColor(context, R.color.colorTextWhite), Color.RED)
                    .setDuration(answerColorChangeDuration)

            animation.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    Timer().schedule(answerShownDelay) {
                        animationCompleted?.invoke()
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })
            animation.start()
        }
    }
}