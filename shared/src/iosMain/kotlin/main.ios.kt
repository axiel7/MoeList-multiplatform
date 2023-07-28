import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import data.datastore.PreferencesDataStore.dataStoreFileName
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import ui.theme.DarkColors
import ui.theme.LightColors
import ui.theme.toAmoled

actual object Platform {
    actual val type = PlatformType.IOS
    actual val androidSdkVersion: Int? = null
    actual val iosVersion: String? = UIDevice.currentDevice.systemVersion
}

actual fun getAppVersion() = "1.0" //TODO

actual fun producePreferencePath(): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory).path + "/$dataStoreFileName"
}

@Composable
actual fun getWindowSize(): DpSize {
    return DpSize(width = 300.dp, height = 500.dp)
}

@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    amoledColors: Boolean,
): ColorScheme {
    return when {
        darkTheme -> if (amoledColors) DarkColors.toAmoled() else DarkColors
        else -> LightColors
    }
}

actual fun showToast(message: String) {
    TODO()
}

actual fun openLoginUrl(
    loginUrl: String,
    useExternalBrowser: Boolean
) {
    //TODO
    UIApplication.sharedApplication.openURL(NSURL(string = loginUrl))
}

actual fun openActionUrl(url: String) {
    UIApplication.sharedApplication.openURL(NSURL(string = url))
}

actual fun changeLanguage(locale: String) {
    TODO()
}

actual fun openByDefaultSettings() { /* Not applicable on iOS */ }

fun MainViewController() = ComposeUIViewController { MainApp() }