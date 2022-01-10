package com.madortilofficialapps.tilquiz.model

data class GameSession(var gameKey: String = "",
                       var creatorData: PlayerData = PlayerData(),
                       var joinerData: PlayerData? = null,
                       var gameType: GameType = GameType.trivia,
                       var topics: List<@JvmSuppressWildcards TriviaQuestion.Topic>? = null,
                       var usingSubLibraries: List<Boolean>? = null) {

    enum class GameType {
        trivia, schema
    }

    data class PlayerData(var name: String = "",
                     var score: Int = 0,
                     var progression: Int = 0,
                     var gameState: State = State.open) {
        enum class State {
            open, ongoing, ended, complete
        }
        fun clone() : PlayerData {
            return PlayerData(this.name, this.score, this.progression, this.gameState)
        }
    }

}