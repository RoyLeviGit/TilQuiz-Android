package com.madortilofficialapps.tilquiz.view_controllers


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.madortilofficialapps.tilquiz.model.DatabaseFacade
import com.madortilofficialapps.tilquiz.model.GameSession

import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.model.AppRoomDatabase
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_game_type.*

/**
 * A simple [Fragment] subclass.
 *
 */
class GameTypeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        AppRoomDatabase.getDatabase(context!!.applicationContext)
        DatabaseFacade.fetchScoreboard()
        DatabaseFacade.fetchTriviaQuestions()
        DatabaseFacade.fetchSchema()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        triviaButton.setOnClickListener {
            findNavController().navigate(
                    GameTypeFragmentDirections.actionGameTypeFragmentToPlayerCountFragment(
                    GameSession.GameType.trivia.toString()))
        }
        schemaButton.setOnClickListener {
            findNavController().navigate(
                    GameTypeFragmentDirections.actionGameTypeFragmentToPlayerCountFragment(
                    GameSession.GameType.schema.toString()))
        }
        signInOutButton.setOnClickListener {
            if (DatabaseFacade.isSignedIn) DatabaseFacade.signOut()
            findNavController().navigate(
                    GameTypeFragmentDirections.actionGameTypeFragmentToSignInFragment())
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("wabalabadubdub", DatabaseFacade.isSignedIn.toString())
        if (DatabaseFacade.isSignedIn) signInOutButton.text = getString(R.string.button_sign_out)
        else signInOutButton.text = getString(R.string.button_sign_in)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
    }
}
