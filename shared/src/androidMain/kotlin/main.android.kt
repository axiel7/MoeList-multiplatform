import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.window.layout.WindowMetricsCalculator
import com.moelist.common.BuildConfig
import com.moelist.common.MR
import data.datastore.PreferencesDataStore.dataStoreFileName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.theme.DarkColors
import ui.theme.LightColors
import ui.theme.toAmoled
import kotlin.coroutines.CoroutineContext

actual object Platform {
    actual val type = PlatformType.ANDROID
    @ChecksSdkIntAtLeast(extension = 0)
    actual val androidSdkVersion: Int? = Build.VERSION.SDK_INT
    actual val iosVersion: String? = null
}

actual fun producePreferencePath(): String {
    return App.appContext.filesDir.resolve(dataStoreFileName).absolutePath
}

actual fun getAppVersion() = "1.0" //TODO

@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    amoledColors: Boolean,
): ColorScheme {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context).let {
                return@let if (amoledColors) it.toAmoled() else it
            }
            else dynamicLightColorScheme(context)
        }

        darkTheme -> if (amoledColors) DarkColors.toAmoled() else DarkColors
        else -> LightColors
    }
}

@Composable
actual fun getWindowSize(): DpSize {
    val configuration = LocalConfiguration.current
    val windowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(App.appContext)
    }
    return with(LocalDensity.current) {
        windowMetrics.bounds.toComposeRect().size.toDpSize()
    }
}

actual fun showToast(message: String) {
    Toast.makeText(App.appContext, message, Toast.LENGTH_SHORT).show()
}

actual fun openLoginUrl(
    loginUrl: String,
    useExternalBrowser: Boolean
) {
    App.appContext.apply {
        if (useExternalBrowser) {
            Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl)).apply {
                try {
                    //showToast(getString(MR.strings.login_browser_warning))
                    startActivity(this)
                } catch (e: ActivityNotFoundException) {
                    showToast("No app found for this action")
                }
            }
        }
        //else openCustomTab(loginUrl)
    }
}

actual fun openActionUrl(url: String) {
    Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        App.appContext.startActivity(this)
    }
}

actual fun changeLanguage(locale: String) {
    val appLocale = if (locale == "follow_system") LocaleListCompat.getEmptyLocaleList()
    else LocaleListCompat.forLanguageTags(locale)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

@RequiresApi(Build.VERSION_CODES.S)
actual fun openByDefaultSettings() {
    try {
        // Samsung OneUI 4 bug can't open ACTION_APP_OPEN_BY_DEFAULT_SETTINGS
        val action = if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        } else {
            Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS
        }
        Intent(
            action,
            Uri.parse("package:${App.appContext.packageName}")
        ).apply {
            App.appContext.startActivity(this)
        }
    } catch (e: Exception) {
        showToast(e.message ?: "Error")
    }
}

@Composable fun MainView() = MainApp()
