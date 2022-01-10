package com.madortilofficialapps.tilquiz.view_controllers

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.*
import androidx.navigation.ui.setupWithNavController
import com.madortilofficialapps.tilquiz.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        bottom_nav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.gameTypeFragment, R.id.openGamesFragment, R.id.scoreboardFragment, R.id.mainFragment -> showBottomNavigation()
                else -> hideBottomNavigation()
            }
        }
    }

    private fun hideBottomNavigation() {
        // bottom_navigation is BottomNavigationView
        with(bottom_nav) {
            if (isVisible && alpha == 1f) {
                animate()
                        .alpha(0f)
                        .withEndAction { visibility = View.GONE }
                        .duration = 250
            }
        }
    }

    private fun showBottomNavigation() {
        // bottom_navigation is BottomNavigationView
        with(bottom_nav) {
            isVisible = true
            animate()
                    .alpha(1f)
                    .duration = 250
        }
    }
}
