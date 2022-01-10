package com.madortilofficialapps.tilquiz.model

import com.madortilofficialapps.tilquiz.extensions.random

class SchemaLibrary {

    constructor(fullLibrary: List<SchemaStep>, usingSubLibraries: Array<Boolean>) {
        this.usingSubLibraries = usingSubLibraries
        this.fullLibrary = fullLibrary
        this.library = mutableListOf()
        initiateSubLibraries()
    }
    companion object

    // The PHTLS Scheme separated into different platous and in each one the steps are arranged. it is initialised as to have it available offline
    var fullLibrary: List<SchemaStep>
        set(fullLibrary) {
            field = fullLibrary
            initiateSubLibraries()
        }

    var library: MutableList<SchemaStep>
        private set(library) {
            field = library
        }


    var subLibraries: MutableList<MutableList<SchemaStep>> = mutableListOf()
        private set(subLibraries) {
            field = subLibraries
        }

    var usingSubLibraries: Array<Boolean>
        set(usingSubLibraries) {
            field = usingSubLibraries
            if (subLibraries.size == usingSubLibraries.size) {
                // reset the library according to usingSubSequence
                library = mutableListOf()
                for (index in usingSubLibraries.indices) {
                    if (usingSubLibraries[index]) {
                        library.addAll(subLibraries[index])
                    }
                }
            }
        }

    private fun initiateSubLibraries() {
        // Initiate the subLibraries var to separate arrays from each first layer SchemaStep
        subLibraries = mutableListOf()
        var currentSubSequence = -1
        fullLibrary.forEach {
                if (it.layer == 0) {
                    currentSubSequence += 1
                    subLibraries.add(mutableListOf())
                }
            subLibraries[currentSubSequence].add(it)
        }

        // Initiates a Bool Array with the same amount of indices as the amount of subLibraries
        val oldUsingSubSequence = usingSubLibraries
        usingSubLibraries = emptyArray()
        usingSubLibraries = oldUsingSubSequence
    }

    fun resetSchemeLibrary() {
        initiateSubLibraries()
        currentStep = -1
        currentOptions = emptyArray()
    }

    var currentStep = -1
        set(currentStep) {
            field = currentStep
            if (currentStep == library.size) { isFinished = true }
            // if Options are generated and SchemeLibrary is finished then there will be an "Out Of Bounds" exception
            if (!isFinished) { generateOptions() }
        }

    val currentStepTitle: String get() = library[currentStep].title
    val currentStepLayer: Int get() = library[currentStep].layer
    var isFinished = false
        private set(isFinished) {
            field = isFinished
        }

    var numOfOptions = 4 // Default is 4
    var currentCorrectOption: Int = numOfOptions.random()
        private set(currentCorrectOption) {
            field = currentCorrectOption
        }
    var currentOptions: Array<String> = emptyArray()
        private set(currentOptions) {
            field = currentOptions
        }

    private val randomSchemaStepInCurrentStepLayer: SchemaStep
        get() {
            // Returns a random SchemaStep out of an Array that contains all of the SchemaSteps in the currentStepLayer except the current SchemaStep itself
            val schemaStepsInCurrentLayer = fullLibrary.filter {
                schemaStep -> schemaStep.layer == currentStepLayer && schemaStep != library[currentStep]
            }
            return schemaStepsInCurrentLayer[schemaStepsInCurrentLayer.size.random()]
        }

    private fun generateOptions() {
        currentOptions = Array(numOfOptions) { currentStepTitle }
        currentCorrectOption = numOfOptions.random()
        for (index in 0 until numOfOptions) {
            if (index != currentCorrectOption) {
                var randomTitle: String
                do {
                    randomTitle = randomSchemaStepInCurrentStepLayer.title
                } while (currentOptions.contains(randomTitle))
                currentOptions[index] = randomTitle
            }
        }
    }
}