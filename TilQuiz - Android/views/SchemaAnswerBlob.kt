package com.madortilofficialapps.tilquiz.views

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentActivity
import com.madortilofficialapps.tilquiz.R

class SchemaAnswerBlob @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AnswerView(context, attrs, defStyleAttr) {

    override var activity: FragmentActivity? = null

    override var originalXOrigin: Float? = null
    override var originalYOrigin: Float? = null

    override var inAnimationDuration: Long = resources.getInteger(R.integer.schemaInAnimationDuration).toLong()
    override var outAnimationDuration: Long = resources.getInteger(R.integer.schemaOutAnimationDuration).toLong()
    override var answerShownDelay: Long = resources.getInteger(R.integer.schemaAnswerShownDelay).toLong()
    override var answerColorChangeDuration: Long = resources.getInteger(R.integer.schemaAnswerColorChangeDuration).toLong()
}