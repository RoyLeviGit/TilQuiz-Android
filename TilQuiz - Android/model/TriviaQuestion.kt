package com.madortilofficialapps.tilquiz.model

import androidx.room.*

@Entity(tableName = "trivia_question_table")
@TypeConverters(TriviaQuestion.AnswersConverters::class, TriviaQuestion.Topic.TopicConverters::class)
data class TriviaQuestion(
        @PrimaryKey var question: String = "",
        var answers: List<String> = emptyList(),
        var correctAnswer: Int = 0,
        var topic: Topic = Topic.anatomy)
{
    @Ignore constructor(): this("", emptyList(),0, Topic.anatomy)

    class AnswersConverters {
        @TypeConverter
        fun stringToAnswersList(answersString: String): List<String> {
            return answersString.split(",").map { it.trim() }
        }
        @TypeConverter
        fun answersListToString(answers: List<String>): String {
            return answers.joinToString()
        }
    }
    enum class Topic {
        anatomy,
        cpr,
        routine, medicine, anamnesis,
        trauma,
        mentalHealth, publicHealth,
        nbc;

        companion object

        class TopicConverters {
            @TypeConverter fun fromStringToTopic(topicString: String) : Topic {
                return Topic.valueOf(topicString)
            }
            @TypeConverter fun fromTopicToString(topic: Topic) : String {
                return topic.toString()
            }
        }
    }
}