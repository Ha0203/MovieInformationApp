package powerrangers.eivom.ui.settings

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateToMenuItem: (String) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name),
                onMenuIconClick = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerContent = {
            DrawerHeader()
            DrawerBody(onItemClick = navigateToMenuItem)
        }
    ) { innerPadding ->
        SettingsBody(modifier = modifier.padding(innerPadding))
    }
}

@Composable
fun SettingsBody(
    modifier: Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val producerState by remember { viewModel.producerState }

    Column(
        modifier = modifier
    ) {
        ProducerSignInSection(
            modifier = modifier,
            producerState = producerState,
            onSignInClick = {
                viewModel.onSignInClick(it)
            },
            handleIntent = {
                viewModel.handleIntent(it)
            },
            onSignOutClick = {
                viewModel.onSignOutClick()
            },
            deleteErrorMessage = {
                viewModel.deleteErrorMessage()
            }
        )
    }
}

@Composable
fun ProducerSignInSection(
    modifier: Modifier,
    producerState: ProducerState,
    onSignInClick: (ActivityResultLauncher<IntentSenderRequest>) -> Unit,
    handleIntent: (Intent?) -> Unit,
    onSignOutClick: () -> Unit,
    deleteErrorMessage: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleIntent(result.data)
            }
        }
    )
    val context = LocalContext.current

    if (producerState.errorMessage != null) {
        Toast.makeText(
            context,
            producerState.errorMessage,
            Toast.LENGTH_LONG
        ).show()
        deleteErrorMessage()
    }

    LaunchedEffect(key1 = producerState.isSignInSuccess) {
        if (producerState.isSignInSuccess) {
            Toast.makeText(
                context,
                context.getString(R.string.sign_in_success),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    LaunchedEffect(key1 = producerState.isSignOutSuccess) {
        if (producerState.isSignOutSuccess) {
            Toast.makeText(
                context,
                context.getString(R.string.sign_out_success),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text(text = stringResource(id = R.string.producer_sign_in_title))
            if (!producerState.isSignInSuccess) {
                Text(
                    modifier = modifier.clickable { onSignInClick(launcher) },
                    text = stringResource(id = R.string.sign_in_text)
                )
            } else {
                Row {
                    Text(text = producerState.userName ?: stringResource(id = R.string.unknown))
                    Spacer(modifier = modifier.width(8.dp))
                    Text(
                        modifier = modifier.clickable { onSignOutClick() },
                        text = stringResource(id = R.string.sign_out_text)
                    )
                }
            }
        }
    }
}