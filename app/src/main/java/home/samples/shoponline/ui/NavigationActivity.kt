package home.samples.shoponline.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import home.samples.shoponline.R
import home.samples.shoponline.databinding.ActivityNavigationBinding

@AndroidEntryPoint
class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_navigation) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.apply {
            setupWithNavController(navController)
            setOnItemSelectedListener { item ->
                NavigationUI.onNavDestinationSelected(item, navController)
                navController.popBackStack(item.itemId, inclusive = false)
                true
            }
        }
    }
}
