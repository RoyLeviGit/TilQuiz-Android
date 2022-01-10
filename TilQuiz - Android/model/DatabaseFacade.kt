package com.madortilofficialapps.tilquiz.model

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.Dao
import androidx.room.Query
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.room.Room
import androidx.room.Transaction
import org.jetbrains.anko.async
import java.lang.Exception

object DatabaseFacade {

    var delegate: DatabaseFacadeDelegate? = null

    private val mAuth = FirebaseAuth.getInstance()

    val isSignedIn: Boolean
        get() = mAuth.currentUser != null

    /**
     * Returns the users display name or null if no user is logged in
     */
    val userDisplayName: String?
        get() {
            return when {
                isSignedIn -> if (mAuth.currentUser?.displayName != null) mAuth.currentUser?.displayName else "User"
                else -> null
            }
        }

    fun signOut() {
        try {
            mAuth.signOut()
        } catch (error: Error) {
            Log.d("wabalabadubdub", error.localizedMessage)
        }
    }

    // MARK: - Database

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private var dao = AppRoomDatabase.getDatabase()?.dao()

    // MARK: - Schema

    var schemaLibrary: List<SchemaStep>? = null
        private set (schemaLibrary) {
            field = schemaLibrary
            delegate?.schemaLibraryChanged()
        }

    fun fetchSchema() {
        val schemaListener = object : ValueEventListener {
            override fun onDataChange(schemaDataSnapshot: DataSnapshot) {
                schemaLibrary = getListOf(schemaDataSnapshot)
                async { dao?.updateSchemaSteps(schemaLibrary!!) }
                Log.d("wabalabadubdub", schemaLibrary.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Fetching Schema failed
            }
        }
        async { schemaLibrary = dao?.loadSchemaSteps() }
        databaseReference.child("PHTLSSchema").addListenerForSingleValueEvent(schemaListener)
    }

    // MARK: - Trivia

    /**
     * This clients Trivia Questions. Gets stored locally but updates from the database
     */
    var triviaQuestions: List<TriviaQuestion>? = null
        private set(triviaQuestions) {
            field = triviaQuestions
            delegate?.triviaQuestionsChanged()
        }

    fun fetchTriviaQuestions() {
        val triviaQuestionsListener = object : ValueEventListener {
            override fun onDataChange(triviaQuestionsDataSnapshot: DataSnapshot) {
                triviaQuestions = getListOf(triviaQuestionsDataSnapshot)
                async { dao?.updateTriviaQuestions(triviaQuestions!!) }
                Log.d("wabalabadubdub", triviaQuestions.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Fetching Trivia failed
            }
        }
        async { triviaQuestions = dao?.loadTriviaQuestions() }
        databaseReference.child("TriviaQuestions").addListenerForSingleValueEvent(triviaQuestionsListener)
    }

    var gameSessions: List<GameSession>? = null
        private set(triviaQuestions) {
            field = triviaQuestions
            delegate?.gameSessionsChanged()
        }

    var isCreator: Boolean? = null
        private set(triviaQuestions) {
            field = triviaQuestions
        }

    /**
     * The GameSession currently being played
     */
    var currentGameSession: GameSession? = null
        private set(currentGameSession) {
            field = currentGameSession
            if (currentGameSession != null && isCreator != null) {
                if (isCreator!!) {
                    gameSessionsDatabaseReference.child(currentGameSession.gameKey).setValue(currentGameSession)
                    myGameSessionData = currentGameSession.creatorData
                    opponentGameSessionData = currentGameSession.joinerData
                } else {
                    myGameSessionData = currentGameSession.joinerData
                    opponentGameSessionData = currentGameSession.creatorData
                    delegate?.currentGameSessionChanged()
                }
            }
        }

    /**
     * Used to update data to the Firebase Database
     */
    var myGameSessionData: GameSession.PlayerData? = null
        set(myGameSessionData) {
            field = myGameSessionData
            if (myGameSessionData != null && currentGameSession != null && isCreator != null) {
                if (isCreator!!) {
                    gameSessionsDatabaseReference.child(currentGameSession!!.gameKey).child("creatorData").setValue(myGameSessionData)
                } else {
                    gameSessionsDatabaseReference.child(currentGameSession!!.gameKey).child("joinerData").setValue(myGameSessionData)
                }
            }
        }
    /**
     * Updated by the Firebase Database
     */
    var opponentGameSessionData: GameSession.PlayerData? = null
        private set(opponentGameSessionData) {
            field = opponentGameSessionData
            delegate?.opponentGameSessionDataChanged()
        }


    private var gameSessionsDatabaseReference = databaseReference.child("GameSessions")
    private var gameSessionsListener: ValueEventListener? = null

    /**
     * Fetches the most up to date GameSessions from the Firebase Database continuously
     */
    fun fetchGameSessions() {
        if (gameSessionsListener != null) {
            gameSessionsDatabaseReference.removeEventListener(gameSessionsListener!!)
        }
        gameSessionsListener = object : ValueEventListener {
            override fun onDataChange(gameSessionsDataSnapshot: DataSnapshot) {
                gameSessions = getListOf<GameSession>(gameSessionsDataSnapshot).filter {
                    it.creatorData.gameState == GameSession.PlayerData.State.open
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Fetching Game Sessions failed
            }
        }
        gameSessionsDatabaseReference.addValueEventListener(gameSessionsListener!!)
    }

    /**
     * Removes the database observers of the GameSessions array
     */
    fun removeGameSessionsDatabaseObservers() {
        if (gameSessionsListener != null) {
            gameSessionsDatabaseReference.removeEventListener(gameSessionsListener!!)
        }
    }

    /**
     * Creates a new standard GameSession and observes Opponent Game Session Data
     */
    fun createGameSession(gameType: GameSession.GameType, withTopics: List<TriviaQuestion.Topic>? = null, withSubLibraries: List<Boolean>? = null) {
        if (isSignedIn) {
            // Can only create one active game for each user
            isCreator = true
            currentGameSession = GameSession(mAuth.currentUser!!.uid,
                    GameSession.PlayerData(userDisplayName!!, 0, 0, GameSession.PlayerData.State.open),
                    null, gameType, withTopics, withSubLibraries)
            observeOpponentGameSessionData()
        }
    }

    /**
     * Join an existing GameSession and observes Opponent Game Session Data
     */
    fun joinGameSession(gameKey: String) {
        val gameSessionListener = object : ValueEventListener {
            override fun onDataChange(gameSessionDataSnapshot: DataSnapshot) {
                if (isSignedIn) {
                    // Can only create one active game for each user
                    isCreator = false
                    val tempCurrentGameSession = gameSessionDataSnapshot.getValue<GameSession>(GameSession::class.java)
                    tempCurrentGameSession?.joinerData = GameSession.PlayerData(userDisplayName!!, 0,
                            0, GameSession.PlayerData.State.open)
                    currentGameSession = tempCurrentGameSession
                    observeOpponentGameSessionData()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Fetching Game Session failed
            }
        }
        gameSessionsDatabaseReference.child(gameKey).addListenerForSingleValueEvent(gameSessionListener)
    }

    /**
     * Stops the Firebase Database observers and sets the relevant variable to null
     */
    fun exitCurrentGameSession() {
        if (opponentGameSessionDataListener != null) {
            opponentGameSessionDataDatabaseReference?.removeEventListener(opponentGameSessionDataListener!!)
        }
        if (currentGameSession != null) {
            gameSessionsDatabaseReference.child(currentGameSession!!.gameKey).removeValue()
        }
        isCreator = null
        currentGameSession = null
        myGameSessionData = null
        opponentGameSessionData = null
    }

    private var opponentGameSessionDataDatabaseReference: DatabaseReference? = null
    private var opponentGameSessionDataListener: ValueEventListener? = null
    private fun observeOpponentGameSessionData() {
        if (currentGameSession != null && isCreator != null) {
            opponentGameSessionDataDatabaseReference = if (isCreator!!) {
                gameSessionsDatabaseReference.child(currentGameSession!!.gameKey).child("joinerData")
            } else {
                gameSessionsDatabaseReference.child(currentGameSession!!.gameKey).child("creatorData")
            }

            if (opponentGameSessionDataListener != null) {
                opponentGameSessionDataDatabaseReference?.removeEventListener(opponentGameSessionDataListener!!)
            }
            opponentGameSessionDataListener = object : ValueEventListener {
                override fun onDataChange(opponentGameSessionDataDataSnapshot: DataSnapshot) {
                    opponentGameSessionData = opponentGameSessionDataDataSnapshot.getValue<GameSession.PlayerData>(GameSession.PlayerData::class.java)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Fetching Game Session failed
                }
            }
            opponentGameSessionDataDatabaseReference?.addValueEventListener(opponentGameSessionDataListener!!)
        }
    }

    // MARK: - Scoreboard

    /**
     * Scoreboard including all users scores. Unsorted
     */
    var scoreboard: List<Score>? = null
        private set(scoreboard) {
            field = scoreboard
            delegate?.scoreboardChanged()
        }

    /**
     * This clients score
     */
    var myScore: Score? = null
        private set(myScore) {
            field = myScore
            delegate?.myScoreChanged()
        }

    private var scoreboardListener: ValueEventListener? = null
    private var myScoreListener: ValueEventListener? = null
    /**
     * Fetches the most up to date Scoreboard and user personal score from the Firebase Database
     */
    fun fetchScoreboard() {
        if (scoreboardListener != null) {
            databaseReference.child("Scoreboard").removeEventListener(myScoreListener!!)
        }
        scoreboardListener = object : ValueEventListener {
            override fun onDataChange(scoreboardDataSnapshot: DataSnapshot) {
                scoreboard = getListOf(scoreboardDataSnapshot)
                Log.d("wabalabadubdub", "scoreboard: " + scoreboard.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Fetching myScore failed
            }
        }
        databaseReference.child("Scoreboard").addListenerForSingleValueEvent(scoreboardListener!!)
        fetchMyScore()
    }
    private fun fetchMyScore() {
        if (myScoreListener != null && isSignedIn) {
            databaseReference.child("Scoreboard").child(mAuth.currentUser!!.uid).removeEventListener(myScoreListener!!)
        }
        myScoreListener = object : ValueEventListener {
            override fun onDataChange(myScoreDataSnapshot: DataSnapshot) {
                myScore =  myScoreDataSnapshot.getValue<Score>(Score::class.java)
                Log.d("wabalabadubdub", myScore.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Fetching myScore failed
            }
        }
        if (isSignedIn) {
            databaseReference.child("Scoreboard").child(mAuth.currentUser!!.uid).addValueEventListener(myScoreListener!!)
        }
    }

    /**
     * Takes a certain score to add to the overall player score
     */
    fun addGameScoreToMyScore(score: Int) {
        if (isSignedIn) {
            val scoreToAppend = Score(userDisplayName!!, score)
            if (myScore != null) {
                scoreToAppend.score += myScore!!.score
            }
            myScore = scoreToAppend
            updateMyScoreToDatabase()
        }
    }
    /**
     * Update the myScore in the Firebase Database
     */
    private fun updateMyScoreToDatabase() {
        if (isSignedIn) {
            databaseReference.child("Scoreboard").child(mAuth.currentUser!!.uid)
        }
    }

    // MARK: - Convenience

    inline fun <reified T: Any> getListOf(dataSnapshot: DataSnapshot): List<T> {
        val list = mutableListOf<T>()
        dataSnapshot.children.toMutableList().mapNotNullTo(list) {
            try {
                it.getValue<T>(T::class.java)
            } catch (e: Exception) {
                return mutableListOf()
            }
        }
        return list
    }
}

// MARK: - Room database

@Database(version = 1, entities = [TriviaQuestion::class, SchemaStep::class], exportSchema = false)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun dao() : AppRoomDatabaseDao

    companion object {
        @Volatile
        private var shared: AppRoomDatabase? = null

        fun getDatabase(applicationContext: Context? = null): AppRoomDatabase? {
            if (shared == null && applicationContext != null) {
                synchronized(AppRoomDatabase::class.java) {
                    if (shared == null) {
                        shared = Room.databaseBuilder(applicationContext,
                                AppRoomDatabase::class.java, "app_room_database")
                                .build()
                    }
                }
            }
            return shared
        }
    }
}

@Dao
interface AppRoomDatabaseDao {

    @Transaction
    fun updateTriviaQuestions(triviaQuestions: List<TriviaQuestion>) {
        deleteAllTriviaQuestions()
        insertTriviaQuestions(triviaQuestions)
    }

    @Insert
    fun insertTriviaQuestions(triviaQuestions: List<TriviaQuestion>)

    @Query("DELETE FROM trivia_question_table")
    fun deleteAllTriviaQuestions()

    @Query("SELECT * FROM trivia_question_table")
    fun loadTriviaQuestions(): List<TriviaQuestion>


    @Transaction
    fun updateSchemaSteps(schemaSteps: List<SchemaStep>) {
        deleteAllSchemaSteps()
        insertSchemaSteps(schemaSteps)
    }
    @Insert
    fun insertSchemaSteps(schemaSteps: List<SchemaStep>)

    @Query("DELETE FROM schema_step_table")
    fun deleteAllSchemaSteps()

    @Query("SELECT * FROM schema_step_table")
    fun loadSchemaSteps(): List<SchemaStep>
}

