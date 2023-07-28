package ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moelist.common.MR
import data.repository.LoginRepository
import dev.icerock.moko.resources.compose.stringResource
import openLoginUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    modifier: Modifier = Modifier
) {
    var useExternalBrowser by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(MR.strings.please_login_to_use_this_feature),
                modifier = Modifier.padding(8.dp),
            )

            Button(
                onClick = {
                    openLoginUrl(
                        loginUrl = LoginRepository.loginUrl,
                        useExternalBrowser = useExternalBrowser
                    )
                },
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = stringResource(MR.strings.login),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Row(
                modifier = Modifier.clickable {
                    useExternalBrowser = !useExternalBrowser
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = useExternalBrowser,
                    onCheckedChange = { useExternalBrowser = it },
                )
                Text(text = stringResource(MR.strings.use_external_browser))
            }
        }
    }//:Scaffold
}

/*
@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MoeListTheme {
        LoginView()
    }
}
*/
