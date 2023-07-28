package com.moelist

import MainView
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import data.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import utils.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // login intent
        if (intent.data?.toString()?.startsWith(Constants.MOELIST_PAGELINK) == true) {
            intent.data?.let { parseIntentData(it) }
        }

        setContent {
            MainView()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.data?.toString()?.startsWith(Constants.MOELIST_PAGELINK) == true) {
            intent.data?.let { parseIntentData(it) }
        }
    }

    private fun parseIntentData(uri: Uri) {
        val code = uri.getQueryParameter("code")
        val receivedState = uri.getQueryParameter("state")
        if (code != null && receivedState == LoginRepository.STATE) {
            lifecycleScope.launch(Dispatchers.IO) { LoginRepository.getAccessToken(code) }
        }
    }
}