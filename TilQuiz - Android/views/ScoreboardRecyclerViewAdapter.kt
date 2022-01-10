package com.madortilofficialapps.tilquiz.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.madortilofficialapps.tilquiz.model.Score
import com.madortilofficialapps.tilquiz.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.score_item.*

class ScoreboardRecyclerViewAdapter(private val scoreItems: List<Score>) : RecyclerView.Adapter<ScoreboardRecyclerViewAdapter.ScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        return ScoreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.score_item, parent, false))
    }

    override fun getItemCount(): Int {
        return scoreItems.size
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bind(scoreItems[position], position)
    }

    class ScoreViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(scoreItem: Score, placement: Int) {
            nameTextView.text = scoreItem.displayName
            scoreTextView.text = scoreItem.score.toString()
            if (placement != 0) placementTextView.text = placement.toString()
            placementImageView.setImageResource(placementImageID(placement))
        }
    }

    companion object {
        fun placementImageID(placement: Int) : Int {
            return when (placement) {
                0 -> R.drawable.king_of_the_scoreboard
                1 -> R.drawable.score_placement_1
                2 -> R.drawable.score_placement_2
                3 -> R.drawable.score_placement_3
                else -> R.drawable.score_placement
            }
        }
    }
}