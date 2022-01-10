package com.madortilofficialapps.tilquiz.model

import android.content.Context
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.extensions.random
import com.madortilofficialapps.tilquiz.model.GameSession.PlayerData.State.*
import java.lang.Exception

class TriviaGame(context: Context, topics: List<TriviaQuestion.Topic>, override var isMultiplayer: Boolean) : Game() {

    val library: TriviaLibrary

    init {
        if (!DatabaseFacade.triviaQuestions.isNullOrEmpty()) {
            this.library = TriviaLibrary(DatabaseFacade.triviaQuestions, topics)
        } else {
            DatabaseFacade.fetchTriviaQuestions()
            throw Exception("No library initialized by database")
        }
    }

    private var maxQuestions = context.resources.getInteger(R.integer.triviaMaxQuestions)

    private var pickedQuestionIndices: MutableList<Int> = mutableListOf()
    private fun pickRandomQuestion() {
        var index = library.library.size.random()
        // May become easily infinite
        while (pickedQuestionIndices.contains(index)) {
            index = library.library.size.random()
        }
        pickedQuestionIndices.add(index)
        library.currentLibraryQuestion = index
    }

    override fun readyForNextQuestion() {
        // All the logic happens in the didSet of the myPlayerData var
        if (myPlayerData != null) {
            val temp = myPlayerData?.clone()
            temp!!.progression += 1
            myPlayerData = temp
        }
    }
    override fun correctAnswerPicked() {
        timerUpdater?.cancel()
        if (myPlayerData != null) {
            val temp = myPlayerData?.clone()
            temp!!.score += scoreIncrement()
            myPlayerData = temp
        }
    }
    override fun incorrectAnswerPicked() {
        timerUpdater?.cancel()
        if (myPlayerData != null) {
            val temp = myPlayerData?.clone()
            temp!!.score -= scoreDecrement()
            myPlayerData = temp
        }
    }

    override var minScore: Int = context.resources.getInteger(R.integer.triviaMinScore)

    override var maxScore: Int = context.resources.getInteger(R.integer.triviaMaxScore)

    // MARK: - Timer

    override var maxTime = context.resources.getInteger(R.integer.triviaMaxTime).toDouble()

    // MARK: - Game Session Changes Listeners

    override var myPlayerData: GameSession.PlayerData? = null
        set(d) {
            val oldValue = myPlayerData?.clone()
            field = d
            // Responds to my progression changes
            if (oldValue?.progression != myPlayerData?.progression) {
                if (isMultiplayer) {
                    if (myPlayerData?.progression == maxQuestions + 1 && myPlayerData?.progression == opponentPlayerData?.progression) {
                        val temp = myPlayerData?.clone()
                        temp?.gameState = complete
                        myPlayerData = temp
                    } else {
                        if (myPlayerData?.progression != null && opponentPlayerData?.progression != null &&
                                myPlayerData!!.progression <= opponentPlayerData!!.progression) {
                            pickRandomQuestion()
                            delegate?.gameNextQuestionReady()
                        } else {
                            if (opponentPlayerData != null) {
                                delegate?.gameWaitingForOpponent()
                            }
                        }
                    }
                } else {
                    if (myPlayerData?.progression == maxQuestions + 1) {
                        val temp = myPlayerData?.clone()
                        temp?.gameState = complete
                        myPlayerData = temp
                    } else {
                        pickRandomQuestion()
                        delegate?.gameNextQuestionReady()
                    }
                }
            }
            myPlayerDataDidSet(oldValue)
        }
    override var opponentPlayerData: GameSession.PlayerData? = null
        set(value) {
            val oldValue = opponentPlayerData?.clone()
            field = value
            if (opponentPlayerData != null) {
                // Responds to opponent progression changes
                if (oldValue?.progression != opponentPlayerData?.progression) {
                    if (opponentPlayerData?.progression == maxQuestions + 1 &&
                            opponentPlayerData?.progression == myPlayerData?.progression) {
                        val temp = myPlayerData?.clone()
                        temp?.gameState = complete
                        myPlayerData = temp
                    } else {
                        if (myPlayerData?.progression != null && opponentPlayerData?.progression != null && oldValue?.progression != null &&
                                oldValue.progression < myPlayerData!!.progression && opponentPlayerData!!.progression >= myPlayerData!!.progression) {
                            pickRandomQuestion()
                            delegate?.gameNextQuestionReady()
                        }
                    }
                }
            }
            opponentPlayerDataDidSet(oldValue)
        }
}