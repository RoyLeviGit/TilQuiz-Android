package com.madortilofficialapps.tilquiz.views

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.model.GameSession
import com.madortilofficialapps.tilquiz.model.GameSession.GameType.*
import kotlinx.android.synthetic.main.game_bar.view.*

class GameBarView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.game_bar, this, true)
    }

    var timeElapsed = 0.0
        set(t) {
            field = t
            timeProgressBar.progress = (t/maxTime * 100).toInt()
        }

    var maxTime = 1.0

    lateinit var opponentPlayerAnimationDrawable: AnimationDrawable

    fun setupLayout(gameType: GameSession.GameType, isMultiplayer: Boolean) {
        when (gameType) {
            trivia -> {
                timeProgressBar.progressDrawable.setColorFilter(ContextCompat.getColor(context,R.color.triviaGameBarProgressBar), PorterDuff.Mode.SRC_IN)
            }

            schema -> {
                timeProgressBar.progressDrawable.setColorFilter(ContextCompat.getColor(context,R.color.schemaGameBarProgressBar), PorterDuff.Mode.SRC_IN)
            }
        }
        if (!isMultiplayer) {
            opponentPlayerImageView.isInvisible = true
            opponentScoreTextView.isInvisible = true
        }
        timeProgressBar.progress = 0

        (myPlayerImageView.drawable as AnimationDrawable).start()

        opponentPlayerAnimationDrawable = (opponentPlayerImageView.drawable as AnimationDrawable)
        opponentPlayerImageView.scaleX = -1F
        opponentPlayerAnimationDrawable.start()
    }
}