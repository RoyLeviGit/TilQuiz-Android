package com.madortilofficialapps.tilquiz.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schema_step_table")
data class SchemaStep(
        @PrimaryKey var title: String = "",
        var layer: Int = 0)
