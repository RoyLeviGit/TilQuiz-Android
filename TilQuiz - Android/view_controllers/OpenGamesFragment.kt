package com.madortilofficialapps.tilquiz.view_controllers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.madortilofficialapps.tilquiz.model.DatabaseFacade
import com.madortilofficialapps.tilquiz.model.DatabaseFacadeDelegate

import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.model.GameSession.GameType.*
import com.madortilofficialapps.tilquiz.views.OpenGamesRecyclerViewAdapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_open_games.*

/**
 * A simple [Fragment] subclass.
 *
 */
class OpenGamesFragment : Fragment(), DatabaseFacadeDelegate {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openGamesRecyclerView.layoutManager = LinearLayoutManager(activity)
        if (!DatabaseFacade.gameSessions.isNullOrEmpty()) gameSessionsChanged()
        DatabaseFacade.delegate = this
        DatabaseFacade.fetchGameSessions()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (DatabaseFacade.delegate == this) DatabaseFacade.delegate = null
        clearFindViewByIdCache()
    }

    override fun gameSessionsChanged() {
        if (DatabaseFacade.gameSessions != null) {
            openGamesRecyclerView.adapter = OpenGamesRecyclerViewAdapter(DatabaseFacade.gameSessions!!) {
                if (DatabaseFacade.isSignedIn) {
                    DatabaseFacade.joinGameSession(it.gameKey)
                    when (it.gameType) {
                        trivia -> {
                            if (it.topics != null) findNavController().navigate(
                                    OpenGamesFragmentDirections.actionOpenGamesFragmentToTriviaGameFragment(
                                            "true", it.topics!!.joinToString()))
                        }
                        schema -> {
                            if (it.usingSubLibraries != null) findNavController().navigate(
                                    OpenGamesFragmentDirections.actionOpenGamesFragmentToSchemaGameFragment(
                                            "true", it.usingSubLibraries!!.toTypedArray().joinToString()))
                        }
                    }
                }
            }
        }
    }
}
