package com.madortilofficialapps.tilquiz.model

import android.util.Log
import com.madortilofficialapps.tilquiz.model.GameSession.PlayerData.State.*
import java.util.*
import kotlin.concurrent.schedule

abstract class Game: DatabaseFacadeDelegate {

    var delegate: GameDelegate? = null

    var isCreator: Boolean = DatabaseFacade.isCreator ?: true
    abstract var isMultiplayer: Boolean

    fun start() {
        if (isMultiplayer) {
            DatabaseFacade.delegate = this
            myPlayerData = DatabaseFacade.myGameSessionData
            opponentPlayerData = DatabaseFacade.opponentGameSessionData
            if (!isCreator) {
                myPlayerData?.gameState = ongoing
            }
        } else {
            myPlayerData = GameSession.PlayerData("Singleplayer",0,0,ongoing)
        }
    }
    override fun opponentGameSessionDataChanged() {
        Log.d("wabalabadubdub","opponent game session data updated From database")
        opponentPlayerData = DatabaseFacade.opponentGameSessionData?.clone()
    }

    // MARK: - Game Flow

    abstract fun readyForNextQuestion()
    abstract fun correctAnswerPicked()
    abstract fun incorrectAnswerPicked()

    abstract var minScore: Int
    abstract var maxScore: Int
    fun scoreIncrement(): Int {
        return minScore + ((maxScore - minScore).toDouble() * (maxTime - timeElapsed) / maxTime).toInt()
    }
    fun scoreDecrement(): Int {
        return minScore / 2
    }

    // MARK: - Timer

    abstract var maxTime: Double
    var timeElapsed: Double = 0.0
        set(t) {
            field = t
            if (timeElapsed > maxTime) {
                timerUpdater?.cancel()
                delegate?.gameOutOfTime()
            }
            delegate?.gameTimeElapsedUpdated()
        }
    var startTime: Double = 0.0
    var timerUpdater: TimerTask? = null
    fun restartTimer() {
        timeElapsed = 0.0
        startTime = Date().time.toDouble() / 1000
        timerUpdater?.cancel()
        timerUpdater = Timer().schedule(0, 100) {
            timeElapsed = Date().time.toDouble() / 1000 - startTime
        }
    }

    // MARK: - Game Session Changes Listeners

    abstract var myPlayerData: GameSession.PlayerData?
    fun myPlayerDataDidSet(oldValue: GameSession.PlayerData?) {
        // Responds to my game state changes
        if (oldValue?.gameState != myPlayerData?.gameState && myPlayerData?.gameState != null) {
            respondToGameStateChanges(myPlayerData!!.gameState)
        }
        // Responds to my score changes
        if (oldValue?.score != myPlayerData?.score) {
            delegate?.gameMyScoreUpdated()
        }
        // Uploads changes to database if needed
        if (isMultiplayer) {
            DatabaseFacade.myGameSessionData = myPlayerData
        }
    }
    abstract var opponentPlayerData: GameSession.PlayerData?
    fun opponentPlayerDataDidSet(oldValue: GameSession.PlayerData?) {
        if (opponentPlayerData != null) {
            // Responds to opponent game state changes
            if (oldValue?.gameState != opponentPlayerData?.gameState && opponentPlayerData?.gameState != null) {
                // No need for imitating the opponent game state in case of joining a game, where it is going to be open
                if (opponentPlayerData?.gameState != open && myPlayerData?.gameState != opponentPlayerData?.gameState) {
                    val temp = myPlayerData?.clone()
                    temp?.gameState = opponentPlayerData?.gameState!!
                    myPlayerData = temp
                }
            }
            // Responds to opponent name changes
            if (oldValue?.name != opponentPlayerData?.name) {
                delegate?.gameOpponentNameUpdated()
            }
            // Responds to opponent score changes
            if (oldValue?.score != opponentPlayerData?.score) {
                delegate?.gameOpponentScoreUpdated()
            }
        } else if (oldValue != null) {
            val temp = myPlayerData?.clone()
            temp?.gameState = ended
            myPlayerData = temp
        } else {
            delegate?.gameWaitingForPlayerToJoin()
        }
    }
    private fun respondToGameStateChanges(state: GameSession.PlayerData.State) {
        when (state) {
            ended -> {
                delegate?.gameEnded()
            }
            complete -> {
                // Will add score whether a singleplayer or a multiplayer game was complete
                if (myPlayerData?.score != null) {
                    DatabaseFacade.addGameScoreToMyScore(myPlayerData!!.score)
                }
                delegate?.gameComplete()
            }
            ongoing -> {
                delegate?.gameOpponentPresent()
            }
            else -> {}
        }
    }
    fun cleanUp() {
        timerUpdater?.cancel()
        delegate = null
        DatabaseFacade.exitCurrentGameSession()
    }
}