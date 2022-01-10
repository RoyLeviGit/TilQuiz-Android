package com.madortilofficialapps.tilquiz.model

interface DatabaseFacadeDelegate {
    fun schemaLibraryChanged() {}
    fun triviaQuestionsChanged() {}
    fun gameSessionsChanged() {}
    fun currentGameSessionChanged() {}
    fun opponentGameSessionDataChanged() {}
    fun scoreboardChanged() {}
    fun myScoreChanged() {}
}