package com.madortilofficialapps.tilquiz.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.madortilofficialapps.tilquiz.model.GameSession
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.extensions.correspondingBubbleIDForTopic
import com.madortilofficialapps.tilquiz.extensions.subLibraryIndexCoorespondingBubbleID
import com.madortilofficialapps.tilquiz.model.SchemaLibrary
import com.madortilofficialapps.tilquiz.model.TriviaQuestion
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.open_game_item.*

class OpenGamesRecyclerViewAdapter(private val openGamesItems: List<GameSession>,
                                   private val onClick: (GameSession) -> Unit)
    : RecyclerView.Adapter<OpenGamesRecyclerViewAdapter.OpenGameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenGameViewHolder {
        return OpenGameViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.open_game_item, parent, false))
    }

    override fun getItemCount(): Int {
        return openGamesItems.size
    }

    override fun onBindViewHolder(holder: OpenGameViewHolder, position: Int) {
        holder.bind(openGamesItems[position], onClick)
    }

    class OpenGameViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(openGameItem: GameSession, onClick: (GameSession) -> Unit) {
            containerView.setOnClickListener { onClick(openGameItem) }
            gameCreatorNameTextView.text = openGameItem.creatorData.name

            when (openGameItem.gameType) {
                GameSession.GameType.trivia -> {
                    gameTypeImageView.setImageResource(R.drawable.trivia_on)
                    openGameItem.topics?.forEach {
                        addBubbleToDescriptionScrollView(TriviaQuestion.Topic.correspondingBubbleIDForTopic(it))
                    }
                }
                GameSession.GameType.schema -> {
                    gameTypeImageView.setImageResource(R.drawable.schema_on)
                    if (openGameItem.usingSubLibraries != null) {
                        for (index in openGameItem.usingSubLibraries!!.indices) {
                            if (openGameItem.usingSubLibraries!![index])
                                addBubbleToDescriptionScrollView(SchemaLibrary.subLibraryIndexCoorespondingBubbleID(index))
                        }
                    }
                }
            }
        }
        private fun addBubbleToDescriptionScrollView(imageResource: Int) {
            val imageView = ImageView(containerView.context)
            imageView.setImageResource(imageResource)
            imageView.adjustViewBounds = true
            descriptionScrollView.addView(imageView)
        }
    }
}