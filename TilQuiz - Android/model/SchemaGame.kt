package com.madortilofficialapps.tilquiz.model

import android.content.Context
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.model.GameSession.PlayerData.State.*
import java.lang.Exception

class SchemaGame (context: Context, usingSubLibraries: Array<Boolean>, override var isMultiplayer: Boolean) : Game() {

    val library: SchemaLibrary

    init {
        if (!DatabaseFacade.schemaLibrary.isNullOrEmpty()) {
            this.library = SchemaLibrary(DatabaseFacade.schemaLibrary!!, usingSubLibraries)
        } else {
            DatabaseFacade.fetchSchema()
            throw Exception("No library initialized by database")
        }
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

    override var minScore: Int = context.resources.getInteger(R.integer.schemaMinScore)

    override var maxScore: Int = context.resources.getInteger(R.integer.schemaMaxScore)

    // MARK: - Timer

    override var maxTime = context.resources.getInteger(R.integer.schemaMaxTime).toDouble()

    // MARK: - Game Session Changes Listeners

    override var myPlayerData: GameSession.PlayerData? = null
        set(d) {
            val oldValue = myPlayerData?.clone()
            field = d
            // Responds to my progression changes
            if (oldValue?.progression != myPlayerData?.progression) {
                library.currentStep += 1
                if (library.isFinished) {
                    if (isMultiplayer) {
                        if (myPlayerData?.progression == opponentPlayerData?.progression) {
                            val temp = myPlayerData?.clone()
                            temp?.gameState = complete
                            myPlayerData = temp
                        }
                    } else {
                        val temp = myPlayerData?.clone()
                        temp?.gameState = complete
                        myPlayerData = temp
                    }
                } else {
                    delegate?.gameNextQuestionReady()
                }
            }
            myPlayerDataDidSet(oldValue)
        }


    override var opponentPlayerData: GameSession.PlayerData? = null
        set(d) {
            val oldValue = opponentPlayerData?.clone()
            field = d
            // Responds to opponent progression changes
            if (oldValue?.progression != opponentPlayerData?.progression && library.isFinished
                    && opponentPlayerData?.progression == myPlayerData?.progression) {
                val temp = myPlayerData?.clone()
                temp?.gameState = complete
                myPlayerData = temp
            }
            opponentPlayerDataDidSet(oldValue)
        }

}