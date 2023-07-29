package cris.twoper.spookymatchy

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import cris.twoper.spookymatchy.databinding.ActivitySpookyMatchBinding
import java.util.Locale
import kotlin.math.abs

class SpookyMatch : AppCompatActivity() {

    private lateinit var binding: ActivitySpookyMatchBinding

    private val GAME_ACTIVITY_TAG = "GAME_ACTIVITY_TAG"

    private var mWebView: WebView? = null
    private var mLastBackPress: Long = 0
    private val mBackPressThreshold: Long = 3500
    private val IS_FULLSCREEN_PREF = "is_fullscreen_pref"
    private var mLastTouch: Long = 0
    private val mTouchThreshold: Long = 2000
    private var pressBackToast: Toast? = null

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivitySpookyMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mWebView = binding.mainWebView

        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        applyFullScreen(isFullScreen())

        var isOrientationEnabled = false
        try {
            isOrientationEnabled = Settings.System.getInt(
                contentResolver,
                Settings.System.ACCELEROMETER_ROTATION
            ) == 1
        } catch (e: Settings.SettingNotFoundException) {
            Log.d(GAME_ACTIVITY_TAG, "Settings could not be loaded")
        }

        val screenLayout = (resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)

        if ((screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE
                    || screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            && isOrientationEnabled
        ) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }

        val settings = mWebView?.settings
        if (settings != null) {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            settings.databasePath = filesDir.parentFile!!.path + "/databases"
            settings.allowFileAccess = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
        }

        if (savedInstanceState != null) {
            mWebView?.restoreState(savedInstanceState)
        } else {
            mWebView?.loadUrl("file:///android_asset/spooky-match/dist/index.html?lang=" + Locale.getDefault().language)
        }

        Toast.makeText(application, R.string.toggle_fullscreen, Toast.LENGTH_SHORT).show()
        mWebView?.setOnTouchListener { _: View?, event: MotionEvent ->
            val currentTime = System.currentTimeMillis()
            if (event.action == MotionEvent.ACTION_UP
                && abs(currentTime - mLastTouch) > mTouchThreshold
            ) {
                val toggledFullScreen = !isFullScreen()
                saveFullScreen(toggledFullScreen)
                applyFullScreen(toggledFullScreen)
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                mLastTouch = currentTime
            }
            false
        }
        pressBackToast = Toast.makeText(
            applicationContext, R.string.press_back_again_to_exit,
            Toast.LENGTH_SHORT
        )
    }

    override fun onResume() {
        super.onResume()
        mWebView!!.loadUrl("file:///android_asset/spooky-match/dist/index.html?lang=" + Locale.getDefault().language)
    }

    private fun saveFullScreen(isFullScreen: Boolean) {
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putBoolean(IS_FULLSCREEN_PREF, isFullScreen)
        editor.apply()
    }

    private fun isFullScreen(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
            IS_FULLSCREEN_PREF,
            true
        )
    }

    private fun applyFullScreen(isFullScreen: Boolean) {
        if (isFullScreen) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (abs(currentTime - mLastBackPress) > mBackPressThreshold) {
            pressBackToast!!.show()
            mLastBackPress = currentTime
        } else {
            pressBackToast!!.cancel()
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mainWebView.destroy()
    }
}