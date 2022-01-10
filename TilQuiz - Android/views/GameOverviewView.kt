package com.madortilofficialapps.tilquiz.views

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.model.DatabaseFacade
import kotlinx.android.synthetic.main.game_overview.view.*

class GameOverviewView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.game_overview, this, true)
    }

    fun setupLayout(isMultiplayer: Boolean, didWin: Boolean?, opponentName: String?, scoreAppended: Int?) {
        if (scoreAppended != null) {
            if (isMultiplayer && didWin != null && opponentName != null) {
                if (didWin) {
                    didWinTextView.text = resources.getString(R.string.did_win_text_view_won)
                    scoreWonTextView.text = resources.getString(R.string.score_won_text_view_won, scoreAppended)
                    multiplayerTextView.text = resources.getString(R.string.multiplayer_text_view, opponentName)
                    kualaImageView.setImageResource(R.drawable.kuala_happy)
                } else {
                    didWinTextView.text = resources.getString(R.string.did_win_text_view_lost)
                    scoreWonTextView.text = resources.getString(R.string.score_won_text_view_lost, scoreAppended)
                    multiplayerTextView.text = resources.getString(R.string.multiplayer_text_view, opponentName)
                    kualaImageView.setImageResource(R.drawable.kuala_sad)
                }
            } else {
                didWinTextView.text = resources.getString(R.string.did_win_text_view_singleplayer)
                scoreWonTextView.text = resources.getString(R.string.score_won_text_view_singleplayer, scoreAppended)
                multiplayerTextView.text = ""
                kualaImageView.setImageResource(R.drawable.kuala_happy)
            }
        } else {
            didWinTextView.text = resources.getString(R.string.did_win_text_view_quit)
            scoreWonTextView.text = ""
            multiplayerTextView.text = ""
            kualaImageView.setImageResource(R.drawable.kuala_sad)
        }

        if (DatabaseFacade.isSignedIn && DatabaseFacade.myScore?.score != null) {
            myScoreReviewTextView.text = DatabaseFacade.myScore!!.score.toString()
        }
        else myScoreReviewTextView.text = ""

        (myScoreReviewTextView.background as AnimationDrawable).start()
    }
}