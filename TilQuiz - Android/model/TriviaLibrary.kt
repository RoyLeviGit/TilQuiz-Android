package com.madortilofficialapps.tilquiz.model

class TriviaLibrary {

    constructor(fullLibrary: List<TriviaQuestion>?, topics: List<TriviaQuestion.Topic>) {
        this.usingTopics = topics
        this.fullLibrary = fullLibrary
        this.library = emptyList()
        initiateLibrary()
    }

    var fullLibrary: List<TriviaQuestion>?
        set(fullLibrary) {
            field = fullLibrary
            initiateLibrary()
        }

    var usingTopics: List<TriviaQuestion.Topic>
        set(usingTopics) {
            field = usingTopics
            initiateLibrary()
        }

    var library: List<TriviaQuestion>
        private set(library) {
            field = library
        }

    private fun initiateLibrary() {
        library = if (fullLibrary != null) {
            fullLibrary!!.filter { triviaQuestion ->
                usingTopics.contains(triviaQuestion.topic)
            }
        } else {
            emptyList()
        }
    }

    var currentLibraryQuestion = 0
    val currentQuestion: String get() = library[currentLibraryQuestion].question
    val currentAnswers: List<String> get() = library[currentLibraryQuestion].answers
    val currentCorrectAnswer: Int get() = library[currentLibraryQuestion].correctAnswer
    val currentTopic: TriviaQuestion.Topic get() = library[currentLibraryQuestion].topic
}