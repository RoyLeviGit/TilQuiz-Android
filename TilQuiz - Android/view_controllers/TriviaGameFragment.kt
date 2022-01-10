package com.madortilofficialapps.tilquiz.view_controllers

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.extensions.correspondingBubbleIDForTopic
import com.madortilofficialapps.tilquiz.model.GameDelegate
import com.madortilofficialapps.tilquiz.model.GameSession
import com.madortilofficialapps.tilquiz.model.TriviaGame
import com.madortilofficialapps.tilquiz.model.TriviaQuestion
import com.madortilofficialapps.tilquiz.model.TriviaQuestion.Topic.*
import com.madortilofficialapps.tilquiz.views.TriviaAnswerCard
import kotlinx.android.synthetic.main.fragment_trivia_game.*
import kotlinx.android.synthetic.main.game_bar.view.*
import kotlinx.android.synthetic.main.game_menu.view.*
import kotlinx.android.synthetic.main.game_overview.view.*
import org.jetbrains.anko.toast
import java.util.*
import kotlin.concurrent.schedule

/**
 * A simple [Fragment] subclass.
 *
 */
class TriviaGameFragment : Fragment(), GameDelegate {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trivia_game, container, false)
    }

    private var answerCards: List<TriviaAnswerCard> = emptyList()

    private var game: TriviaGame? = null
    private var topics: List<TriviaQuestion.Topic>? = null
    private var isMultiplayer: Boolean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            topics = TriviaGameFragmentArgs.fromBundle(arguments!!).triviaTopics.split(",").map {
                if (it != "") {
                    TriviaQuestion.Topic.valueOf(it.trim())
                } else TriviaQuestion.Topic.trauma
            }
            if (topics.isNullOrEmpty()) throw java.lang.Exception("Can't start empty selection")
            isMultiplayer = TriviaGameFragmentArgs.fromBundle(arguments!!).isMultiplayer.toBoolean()
            game = TriviaGame(context!!, topics!!, isMultiplayer!!)
        } catch (e: Exception) {
            activity?.toast(e.localizedMessage)
            findNavController().popBackStack()
            return
        }

        setupDisplay()

        game!!.delegate = this
        game!!.start()
    }

    private fun setupDisplay() {
        // Game Bar
        gameBar.setupLayout(GameSession.GameType.trivia, isMultiplayer!!)
        gameBar?.menuImageButton?.setOnClickListener { gameMenu.isVisible = true }
        // Pass max time var from game to gameBar for time indication
        gameBar?.maxTime = game!!.maxTime

        // Setup answerCards click listeners
        answerCards = listOf(answer1Button, answer2Button, answer3Button, answer4Button)
        answerCards.forEach { answerCard ->
            answerCard.setOnClickListener {
                answerButtonClicked(answerCard)
            }
        }

        if (!isMultiplayer!!) gameBar?.opponentScoreTextView?.isInvisible = true



        // Setup answerCards needed traits for future animations
        for (index in answerCards.indices) {
            answerCards[index].activity = activity
            answerCards[index].originalXOrigin = answerCards[index].x
            answerCards[index].originalYOrigin = answerCards[index].y
            // Set the Right side of cards to lean Left (default is Right)
            if (index%2 == 1) {
                answerCards[index].cardLeanRotation = TriviaAnswerCard.CardLeanRotation.Left
            }
        }
        // Starting deck position TODO: Get it right
        for (index in answerCards.indices) {
            if (index%2 == 0) {
                answerCards[index].rotation = -90F
            } else {
                answerCards[index].rotation = 90F
            }
        }

        // Game Menu
        gameMenu.exitGameButton.setOnClickListener {
            Log.d("wabalabadubdub", "game quit")
            game?.cleanUp()
            gameOverview.setupLayout(isMultiplayer!!, null, null, null)
            gameOverview.isVisible = true
            gameMenu.isVisible = false
        }
        gameMenu.returnToGameButton.setOnClickListener {
            gameMenu.isVisible = false
        }

        // Game Overview
        gameOverview.continueButton.setOnClickListener {
            Log.d("wabalabadubdub", "game finished")
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        game?.cleanUp()
    }

    // MARK: - Game Flow

    private fun displayTriviaQuestion(animationCompleted: (() -> Unit)? = null) {
        if (!gameBar.opponentPlayerAnimationDrawable.isRunning) {
            gameBar.opponentPlayerAnimationDrawable.start()
        }
        questionTextView.text = game!!.library.currentQuestion
        if (game?.library?.currentTopic != null) {
            questionTopicBubbleImageView.setImageResource(TriviaQuestion.Topic.correspondingBubbleIDForTopic(
                    game!!.library.currentTopic))
        }
        val cImageResources = cardsImageResourcesForTopic(game?.library?.currentTopic)
        for (index in answerCards.indices) {
            answerCards[index].text = game!!.library.currentAnswers[index]
            answerCards[index].setBackgroundResource(cImageResources[index])
        }

        Timer().schedule(resources.getInteger(R.integer.triviaQuestionDisplayedDelay).toLong()) {
            activity?.runOnUiThread {
                answerCards.forEach {
                    it.isEnabled = true
                    it.alpha = 1F
                }
            }
            answerCards[0].animateToOriginalPosition()
            answerCards[1].animateToOriginalPosition()
            answerCards[2].animateToOriginalPosition()
            answerCards[3].animateToOriginalPosition {
                animationCompleted?.invoke()
            }
        }
    }
    private fun cardsImageResourcesForTopic(topic: TriviaQuestion.Topic?): List<Int> {
        return when (topic) {
            anatomy, medicine, mentalHealth ->
                listOf(R.drawable.trivia_answer_1b, R.drawable.trivia_answer_2b,
                        R.drawable.trivia_answer_3b, R.drawable.trivia_answer_4b)
            cpr, anamnesis, publicHealth ->
                listOf(R.drawable.trivia_answer_1r, R.drawable.trivia_answer_2r,
                        R.drawable.trivia_answer_3r, R.drawable.trivia_answer_4r)
            else ->
                listOf(R.drawable.trivia_answer_1g, R.drawable.trivia_answer_2g,
                        R.drawable.trivia_answer_3g, R.drawable.trivia_answer_4g)
        }
    }

    private fun answerButtonClicked(sender: TriviaAnswerCard?) {
        answerCards.forEach {
            it.isEnabled = false
            it.alpha = 0.8F
        }
        if (sender != null) {
            if (answerCards.indexOf(sender) == game!!.library.currentCorrectAnswer) {
                correctAnswerClicked(sender)
            } else {
                incorrectAnswerClicked(sender)
            }
        } else incorrectAnswerClicked(null)
    }

    private fun correctAnswerClicked(sender: TriviaAnswerCard) {
        game!!.correctAnswerPicked()

        sender.animateCorrectAnswer {
            gameBar?.timeElapsed = 0.0
            answerCards[0].animateCorrectAnswerPicked()
            answerCards[1].animateCorrectAnswerPicked()
            answerCards[2].animateCorrectAnswerPicked()
            answerCards[3].animateCorrectAnswerPicked {
                game!!.readyForNextQuestion()
            }
        }
    }
    private fun incorrectAnswerClicked(sender: TriviaAnswerCard?) {
        game!!.incorrectAnswerPicked()

        sender?.animateIncorrectAnswer()

        answerCards[game!!.library.currentCorrectAnswer].animateCorrectAnswer {
            gameBar?.timeElapsed = 0.0
            answerCards[0].animateIncorrectAnswerPicked()
            answerCards[1].animateIncorrectAnswerPicked()
            answerCards[2].animateIncorrectAnswerPicked()
            answerCards[3].animateIncorrectAnswerPicked {
                game!!.readyForNextQuestion()
            }
        }
    }

    override fun gameNextQuestionReady() {
        activity?.runOnUiThread {
            displayTriviaQuestion {
                game?.restartTimer()
            }
        }
    }
    override fun gameMyScoreUpdated() {
        activity?.runOnUiThread { gameBar?.myScoreTextView?.text = game?.myPlayerData?.score.toString() }
    }
    override fun gameTimeElapsedUpdated() {
        activity?.runOnUiThread { gameBar?.timeElapsed = game?.timeElapsed ?: 0.0 }
    }
    override fun gameOutOfTime() {
        activity?.runOnUiThread {
            Log.d("wabalabadubdub", "timer out of time")
            incorrectAnswerClicked(null)
        }
    }
    override fun gameEnded() {
        Log.d("wabalabadubdub", "game ended")
        game?.cleanUp()
        gameOverview.setupLayout(isMultiplayer!!, null, null, null)
        gameOverview.isVisible = true
    }
    override fun gameComplete() {
        Log.d("wabalabadubdub", "game complete")
        game?.cleanUp()
        if (game?.myPlayerData?.score != null && game?.opponentPlayerData?.score != null) {
            gameOverview.setupLayout(
                    isMultiplayer!!, game!!.myPlayerData!!.score > game!!.opponentPlayerData!!.score,
                    game!!.opponentPlayerData!!.name, game!!.myPlayerData!!.score)
        } else {
            gameOverview.setupLayout(isMultiplayer!!, null, null, game?.myPlayerData?.score)
        }
        gameOverview.isVisible = true
    }

    // MARK: - Multiplayer

    override fun gameOpponentNameUpdated() {
        activity?.runOnUiThread {
            gameBar?.opponentNameTextView?.text = game?.opponentPlayerData?.name
        }
    }
    override fun gameOpponentScoreUpdated() {
        activity?.runOnUiThread {
            gameBar?.opponentScoreTextView?.text = game?.opponentPlayerData?.score.toString()
        }
    }
    override fun gameWaitingForPlayerToJoin() {
        Log.d("wabalabadubdub", "waiting for player to join")
        gameBar.opponentPlayerAnimationDrawable.stop()
    }
    override fun gameWaitingForOpponent() {
        Log.d("wabalabadubdub", "waiting for opponent")
        gameBar.opponentPlayerAnimationDrawable.stop()
    }
    override fun gameOpponentPresent() {
        activity?.runOnUiThread {
            Log.d("wabalabadubdub", "opponent present")
            game?.readyForNextQuestion()
        }
    }
}
