package com.madortilofficialapps.tilquiz.view_controllers


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.madortilofficialapps.tilquiz.model.DatabaseFacade
import com.madortilofficialapps.tilquiz.model.DatabaseFacadeDelegate

import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.views.ScoreboardRecyclerViewAdapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_scoreboard.*
import kotlinx.android.synthetic.main.score_item.*
import kotlinx.android.synthetic.main.score_item.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ScoreboardFragment : Fragment(), DatabaseFacadeDelegate {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scoreboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scoresRecyclerView.layoutManager = LinearLayoutManager(activity)
        myScoreItem.isInvisible = true
        if (!DatabaseFacade.scoreboard.isNullOrEmpty()) scoreboardChanged()
        DatabaseFacade.delegate = this
        DatabaseFacade.fetchScoreboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (DatabaseFacade.delegate == this) DatabaseFacade.delegate = null
        clearFindViewByIdCache()
    }

    override fun scoreboardChanged() {
        if (DatabaseFacade.scoreboard != null) {
            val sortedScoreboard = DatabaseFacade.scoreboard!!.sortedByDescending { it.score }
            scoresRecyclerView.adapter = ScoreboardRecyclerViewAdapter(sortedScoreboard)

            if (DatabaseFacade.isSignedIn) {
                myScoreItem.isInvisible = false
                myScoreItem.nameTextView.text = DatabaseFacade.userDisplayName
                scoreTextView.text = DatabaseFacade.myScore?.score.toString()
                val placement = sortedScoreboard.indexOf(DatabaseFacade.myScore)
                if (placement != 0) myScoreItem.placementTextView.text = placement.toString()
                myScoreItem.placementImageView.setImageResource(ScoreboardRecyclerViewAdapter.placementImageID(placement))
            } else myScoreItem.isInvisible = true
        }
    }
}
