package com.madortilofficialapps.tilquiz.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.extensions.subLibraryIndexCoorespondingBubbleID
import com.madortilofficialapps.tilquiz.model.SchemaLibrary
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.schema_stage_item.*

class SchemaStagesRecyclerViewAdapter(private val numberOfSchemaStagesItems: Int,
                                      private val onClick: (Int) -> Unit)
    : RecyclerView.Adapter<SchemaStagesRecyclerViewAdapter.SchemaStageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchemaStageViewHolder {
        return SchemaStageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.schema_stage_item, parent, false))
    }

    override fun getItemCount(): Int {
        return numberOfSchemaStagesItems
    }

    override fun onBindViewHolder(holder: SchemaStageViewHolder, position: Int) {
        holder.bind(position, onClick)
    }

    class SchemaStageViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(position: Int, onClick: (Int) -> Unit) {
            containerView.setOnClickListener {
                vImageView.isInvisible = !vImageView.isInvisible
                onClick(position)
            }
            stepBubbleImageView.setImageResource(SchemaLibrary.subLibraryIndexCoorespondingBubbleID(position))
        }
    }
}