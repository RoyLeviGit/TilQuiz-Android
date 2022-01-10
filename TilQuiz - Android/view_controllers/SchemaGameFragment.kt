package com.madortilofficialapps.tilquiz.view_controllers

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.extensions.lastStepLayer
import com.madortilofficialapps.tilquiz.extensions.lastStepTitle
import com.madortilofficialapps.tilquiz.extensions.subLibraryIndexCoorespondingBubbleID
import com.madortilofficialapps.tilquiz.model.GameDelegate
import com.madortilofficialapps.tilquiz.model.GameSession
import com.madortilofficialapps.tilquiz.model.SchemaGame
import com.madortilofficialapps.tilquiz.model.SchemaLibrary
import com.madortilofficialapps.tilquiz.views.SchemaAnswerBlob
import kotlinx.android.synthetic.main.fragment_schema_game.*
import kotlinx.android.synthetic.main.game_bar.view.*
import kotlinx.android.synthetic.main.game_menu.view.*
import kotlinx.android.synthetic.main.game_overview.view.*
import org.jetbrains.anko.toast

/**
 * A simple [Fragment] subclass.
 *
 */
class SchemaGameFragment : Fragment(), GameDelegate {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schema_game, container, false)
    }

    private var answerButtons: List<SchemaAnswerBlob> = emptyList()

    private var game: SchemaGame? = null
    private var usingSubLibraries: Array<Boolean>? = null
    private var isMultiplayer: Boolean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            usingSubLibraries = SchemaGameFragmentArgs.fromBundle(arguments!!).usingSubLibraries.split(",").map {
                it.trim().toBoolean()
            }.toTypedArray()
            if (usingSubLibraries!!.none { it }) throw java.lang.Exception("Can't start empty selection")
            isMultiplayer = SchemaStagesFragmentArgs.fromBundle(arguments!!).isMultiplayer.toBoolean()
            game = SchemaGame(context!!, usingSubLibraries!!,isMultiplayer!!)
        } catch (e: Exception) {
            activity?.toast(e.localizedMessage)
            findNavController().popBackStack()
            return
        }

        setupDisplay()

        game?.delegate = this
        game?.start()
    }

    private fun setupDisplay() {
        gameBar.setupLayout(GameSession.GameType.schema, isMultiplayer!!)
        gameBar?.menuImageButton?.setOnClickListener {gameMenu.isVisible = true }
        // Pass max time var from game to gameBar for time indication
        gameBar?.maxTime = game!!.maxTime

        // Setup answerButtons click listeners
        answerButtons = listOf(answer1Button, answer2Button, answer3Button, answer4Button)
        answerButtons.forEach { answerCard ->
            answerCard.setOnClickListener {
                answerButtonClicked(answerCard)
            }
        }

        // Setup answerButtons needed traits for future animations
        for (index in answerButtons.indices) {
            answerButtons[index].activity = activity
            answerButtons[index].originalXOrigin = answerButtons[index].x
            answerButtons[index].originalYOrigin = answerButtons[index].y
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

    private fun displaySchemaSteps(animationCompleted: (() -> Unit)? = null) {
        if (!gameBar.opponentPlayerAnimationDrawable.isRunning) {
            gameBar.opponentPlayerAnimationDrawable.start()
        }

        if (game?.library?.currentStepLayer in 0..1) {
            lastSubStepTextView.text = ""
            lastSubStepTextView.isInvisible = true
            if (game?.library?.lastStepLayer in 0..1) questionTextView.text = game?.library?.lastStepTitle
        } else {
            lastSubStepTextView.isInvisible = false
            if (game?.library?.lastStepLayer in 0..1) questionTextView.text = game?.library?.lastStepTitle
            else lastSubStepTextView.text = game?.library?.lastStepTitle
        }
        if (game?.library?.currentStepLayer == 0) {
            // Changing sub library bubble
            val index = usingSubLibraries?.indexOf(true)
            if (index != null) {
                usingSubLibraries?.set(index, false)
                subLibraryBubbleImageView.setImageResource(SchemaLibrary.subLibraryIndexCoorespondingBubbleID(index))
            }
        }

        for (index in answerButtons.indices) {
            answerButtons[index].text = game?.library?.currentOptions?.get(index)
            answerButtons[index].setBackgroundResource(blobImageResourceForLayer(game?.library?.currentStepLayer))
            answerButtons[index].isEnabled = true
            answerButtons[index].alpha = 1F
        }

        answerButtons[0].animateToOriginalPosition()
        answerButtons[1].animateToOriginalPosition()
        answerButtons[2].animateToOriginalPosition()
        answerButtons[3].animateToOriginalPosition {
            animationCompleted?.invoke()
        }
    }
    private fun blobImageResourceForLayer(layer: Int?) : Int {
        return when (layer) {
            0 -> R.drawable.schema_answer_blue
            1 -> R.drawable.schema_answer_red
            2 -> R.drawable.schema_answer_green
            else -> R.drawable.schema_answer_blue
        }
    }

    private fun answerButtonClicked(sender: SchemaAnswerBlob?) {
        answerButtons.forEach {
            it.isEnabled = false
            it.alpha = 0.8F
        }
        if (sender != null) {
            if (answerButtons.indexOf(sender) == game!!.library.currentCorrectOption) {
                correctAnswerClicked(sender)
            } else {
                incorrectAnswerClicked(sender)
            }
        } else {
            incorrectAnswerClicked(null)
        }
    }
    private fun correctAnswerClicked(sender: SchemaAnswerBlob) {
        game!!.correctAnswerPicked()

        sender.animateCorrectAnswer {
            gameBar.timeElapsed = 0.0
            answerButtons[0].animateCorrectAnswerPicked()
            answerButtons[1].animateCorrectAnswerPicked()
            answerButtons[2].animateCorrectAnswerPicked()
            answerButtons[3].animateCorrectAnswerPicked {
                game!!.readyForNextQuestion()
            }
        }
    }
    private fun incorrectAnswerClicked(sender: SchemaAnswerBlob?) {
        game!!.incorrectAnswerPicked()

        sender?.animateIncorrectAnswer()

        answerButtons[game!!.library.currentCorrectOption].animateCorrectAnswer {
            gameBar?.timeElapsed = 0.0
            answerButtons[0].animateIncorrectAnswerPicked()
            answerButtons[1].animateIncorrectAnswerPicked()
            answerButtons[2].animateIncorrectAnswerPicked()
            answerButtons[3].animateIncorrectAnswerPicked {
                game!!.readyForNextQuestion()
            }
        }
    }

    override fun gameNextQuestionReady() {
        activity?.runOnUiThread {
            displaySchemaSteps()
            game?.restartTimer()

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
        if (isMultiplayer != null && isMultiplayer!!) {
            activity?.runOnUiThread {
                gameBar?.opponentNameTextView?.text = game?.opponentPlayerData?.name
            }
        }
    }
    override fun gameOpponentScoreUpdated() {
        if (isMultiplayer != null && isMultiplayer!!) {
            activity?.runOnUiThread {
                gameBar?.opponentScoreTextView?.text = game?.opponentPlayerData?.score.toString()
            }
        }
    }
    override fun gameWaitingForPlayerToJoin() {
        if (isMultiplayer != null && isMultiplayer!!) {
            Log.d("wabalabadubdub", "waiting for player to join")
            gameBar.opponentPlayerAnimationDrawable.stop()
        }
    }
    override fun gameWaitingForOpponent() {
        if (isMultiplayer != null && isMultiplayer!!) {
            Log.d("wabalabadubdub", "waiting for opponent")
            gameBar.opponentPlayerAnimationDrawable.stop()
        }
    }
    override fun gameOpponentPresent() {
        if (isMultiplayer != null && isMultiplayer!!) {
            activity?.runOnUiThread {
                Log.d("wabalabadubdub", "opponent present")
                game?.readyForNextQuestion()
            }
        }
    }
}
