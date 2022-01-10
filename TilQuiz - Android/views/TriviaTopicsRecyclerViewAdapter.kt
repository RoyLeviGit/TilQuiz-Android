package com.madortilofficialapps.tilquiz.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.extensions.correspondingBubbleIDForTopic
import com.madortilofficialapps.tilquiz.extensions.titleResourceForTopic
import com.madortilofficialapps.tilquiz.model.TriviaQuestion
import com.madortilofficialapps.tilquiz.model.TriviaTopicItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.trivia_topic_item.*
import kotlinx.android.synthetic.main.trivia_topic_item.view.*

class TriviaTopicsRecyclerViewAdapter(private val triviaTopicsItems: List<TriviaTopicItem>,
                                      private val onClick: (TriviaTopicItem) -> Unit)
    : RecyclerView.Adapter<TriviaTopicsRecyclerViewAdapter.TriviaTopicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TriviaTopicViewHolder {
        return TriviaTopicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.trivia_topic_item, parent, false))
    }

    override fun getItemCount(): Int {
        return triviaTopicsItems.size
    }

    override fun onBindViewHolder(holder: TriviaTopicViewHolder, position: Int) {
        holder.bind(triviaTopicsItems[position], position, onClick)
    }

    override fun onViewAttachedToWindow(holder: TriviaTopicViewHolder) {
        super.onViewAttachedToWindow(holder)

        holder.containerView.vImageView.isInvisible = !triviaTopicsItems[holder.containerView.tag as Int].isSelected
    }

    fun setAllVsVisibilityTo(visibility: Boolean) {
        triviaTopicsItems.forEach {
            it.isSelected = visibility
        }
        notifyDataSetChanged()
    }

    class TriviaTopicViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(triviaTopicItem: TriviaTopicItem, positionInList: Int, onClick: (TriviaTopicItem) -> Unit) {
            containerView.tag = positionInList
            containerView.setOnClickListener {
                onClick(triviaTopicItem)
                vImageView.isInvisible = !triviaTopicItem.isSelected
            }
            triviaTopicTextView.text = containerView.resources.getString(
                    TriviaQuestion.Topic.titleResourceForTopic(triviaTopicItem.topic))
            topicBubbleImageView.setImageResource(TriviaQuestion.Topic.correspondingBubbleIDForTopic(triviaTopicItem.topic))
        }
    }
}