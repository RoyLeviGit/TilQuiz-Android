package com.madortilofficialapps.tilquiz.model

interface GameDelegate {
    fun gameNextQuestionReady() {}
    fun gameWaitingForOpponent() {}
    fun gameOutOfTime() {}
    fun gameWaitingForPlayerToJoin() {}
    fun gameEnded() {}
    fun gameComplete() {}
    fun gameMyScoreUpdated() {}
    fun gameTimeElapsedUpdated() {}
    fun gameOpponentNameUpdated() {}
    fun gameOpponentScoreUpdated() {}
    fun gameOpponentPresent() {}
}