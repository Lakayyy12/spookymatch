package cris.twoper.spookymatchy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import cris.twoper.spookymatchy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var exit = false

    private val currentFragment by lazy {
        findNavController(R.id.nav_host_fragment_content_simon_memory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_simon_memory)
        appBarConfiguration = AppBarConfiguration(navController.graph)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (currentFragment.currentDestination?.id == R.id.FirstFragment) {
            if (exit) {
                finishAffinity()
                return
            }
            exit = true
            Toast.makeText(this, "PRESS AGAIN TO EXIT", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ exit = false }, 2000)
        } else {
            findNavController(R.id.nav_host_fragment_content_simon_memory).navigateUp()
        }
    }
}