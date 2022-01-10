package com.madortilofficialapps.tilquiz.view_controllers

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.model.DatabaseFacade
import com.madortilofficialapps.tilquiz.model.GameSession
import com.madortilofficialapps.tilquiz.views.SchemaStagesRecyclerViewAdapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_schema_stages.*
import kotlinx.android.synthetic.main.schema_stage_item.view.*
import org.jetbrains.anko.forEachChild

/**
 * A simple [Fragment] subclass.
 *
 */
class SchemaStagesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schema_stages, container, false)
    }

    private var stagesSelected: Array<Boolean> = BooleanArray(6) { true }.toTypedArray()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("wabalabadubdub", stagesSelected.joinToString())
        schemaStagesRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        schemaStagesRecyclerView.adapter = SchemaStagesRecyclerViewAdapter(stagesSelected.size) { position ->
            stagesSelected[position] = !stagesSelected[position]
            Log.d("wabalabadubdub", stagesSelected.contentToString())
        }

        allStagesButton.tag = true
        allStagesButton.setOnClickListener {
            allStagesButton.tag = !(allStagesButton.tag as Boolean)
            schemaStagesRecyclerView.forEachChild { schemaStage ->
                schemaStage.vImageView.isInvisible = !(allStagesButton.tag as Boolean)
            }
            for (index in stagesSelected.indices) {
                stagesSelected[index] = (allStagesButton.tag as Boolean)
            }
            Log.d("wabalabadubdub", stagesSelected.contentToString())
        }

        startButton.setOnClickListener {
            val isMultiplayer = SchemaStagesFragmentArgs.fromBundle(arguments!!).isMultiplayer
            if (isMultiplayer.toBoolean()) DatabaseFacade.createGameSession(
                    GameSession.GameType.schema, withSubLibraries = stagesSelected.toList())
            findNavController().navigate(
                    SchemaStagesFragmentDirections.actionSchemaStagesFragmentToSchemaGameFragment(
                            isMultiplayer, stagesSelected.joinToString()))
        }
        (startButton.background as AnimationDrawable).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
    }

}
