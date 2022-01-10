package com.madortilofficialapps.tilquiz.view_controllers

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.model.DatabaseFacade
import com.madortilofficialapps.tilquiz.model.GameSession
import com.madortilofficialapps.tilquiz.model.TriviaQuestion
import com.madortilofficialapps.tilquiz.model.TriviaTopicItem
import com.madortilofficialapps.tilquiz.views.TriviaTopicsRecyclerViewAdapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_trivia_topics.*
import kotlinx.android.synthetic.main.trivia_topic_item.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class TriviaTopicsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trivia_topics, container, false)
    }


    private var topicsSelected: MutableList<TriviaTopicItem> = TriviaTopicItem.triviaTopicItemsArrayFrom(
            TriviaQuestion.Topic.values()).toMutableList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("wabalabadubdub", topicsToPlay.joinToString())
        triviaTopicsRecyclerView.layoutManager = LinearLayoutManager(activity)
        triviaTopicsRecyclerView.adapter = TriviaTopicsRecyclerViewAdapter(topicsSelected) { topic ->
            topic.isSelected = !topic.isSelected
            Log.d("wabalabadubdub", topicsToPlay.joinToString())
        }

        allTriviaTopics.triviaTopicTextView.text = "הכל הולך חביבי"
        allTriviaTopics.topicOutlineImageView.setImageResource(R.drawable.all_topics_outline)
        allTriviaTopics.topicBubbleImageView.setImageResource(R.drawable.all_topics_bubble)
        allTriviaTopics.setOnClickListener {
            allTriviaTopics.vImageView.isInvisible = !allTriviaTopics.vImageView.isInvisible
            (triviaTopicsRecyclerView.adapter as TriviaTopicsRecyclerViewAdapter).setAllVsVisibilityTo(
                    !allTriviaTopics.vImageView.isInvisible)
            Log.d("wabalabadubdub", topicsToPlay.joinToString())
        }

        startButton.setOnClickListener {
            val isMultiplayer = TriviaTopicsFragmentArgs.fromBundle(arguments!!).isMultiplayer
            if (isMultiplayer.toBoolean()) DatabaseFacade.createGameSession(
                    GameSession.GameType.trivia, topicsToPlay)
            findNavController().navigate(
                    TriviaTopicsFragmentDirections.actionTriviaTopicsFragmentToTriviaGameFragment(
                            isMultiplayer,
                            topicsToPlay.joinToString()))
        }
        (startButton.background as AnimationDrawable).start()
    }

    private val topicsToPlay: List<TriviaQuestion.Topic>
        get() = topicsSelected.mapNotNull { when(it.isSelected) {
            true -> it.topic
            false -> null
        }}

    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
    }
}