package com.madortilofficialapps.tilquiz.view_controllers


import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.madortilofficialapps.tilquiz.model.GameSession

import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.model.DatabaseFacade
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_player_count.*

/**
 * A simple [Fragment] subclass.
 *
 */
class PlayerCountFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_count, container, false)
    }

    var gameType: GameSession.GameType = GameSession.GameType.trivia

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameType = GameSession.GameType.valueOf(PlayerCountFragmentArgs.fromBundle(arguments!!).gameType)
        when (gameType) {
            GameSession.GameType.trivia -> {
                singlePlayerButton.setBackgroundResource(R.drawable.sp_blue)
                multiplayerButton.setBackgroundResource(R.drawable.mp_blue)
            }
            else -> {
                singlePlayerButton.setBackgroundResource(R.drawable.sp_green)
                multiplayerButton.setBackgroundResource(R.drawable.mp_green)
            }
        }

        singlePlayerButton.setOnClickListener { actionToNextFragment(false) }
        multiplayerButton.setOnClickListener { actionToNextFragment(true) }
        (singlePlayerButton.compoundDrawables[3] as AnimationDrawable).start()
        (multiplayerButton.compoundDrawables[3] as AnimationDrawable).start()
        if (!DatabaseFacade.isSignedIn) {
            multiplayerButton.isEnabled = false
            multiplayerButton.alpha = 0.8F
            (multiplayerButton.compoundDrawables[3] as AnimationDrawable).stop()
        }
    }

    private fun actionToNextFragment(isMultiplayer: Boolean) =
            when(gameType) {
                GameSession.GameType.trivia -> findNavController().navigate(
                        PlayerCountFragmentDirections.actionPlayerCountFragmentToTriviaTopicsFragment(isMultiplayer.toString()))
                GameSession.GameType.schema -> findNavController().navigate(
                        PlayerCountFragmentDirections.actionPlayerCountFragmentToSchemaStagesFragment(isMultiplayer.toString()))
            }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
    }
}
