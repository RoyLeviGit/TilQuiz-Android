package com.madortilofficialapps.tilquiz.model

class TriviaTopicItem(val topic: TriviaQuestion.Topic, var isSelected: Boolean) {
    companion object {
        fun triviaTopicItemsArrayFrom(triviaTopicsArray: Array<TriviaQuestion.Topic>) : Array<TriviaTopicItem> {
            return Array(triviaTopicsArray.size) {
                TriviaTopicItem(triviaTopicsArray[it], true)
            }
        }
    }
}