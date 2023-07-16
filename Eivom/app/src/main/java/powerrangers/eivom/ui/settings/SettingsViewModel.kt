package powerrangers.eivom.ui.settings

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.domain.use_case.GoogleAuthClient
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.domain.utility.UserData
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val googleAuthClient: GoogleAuthClient
): ViewModel() {
    var producerState = mutableStateOf(ProducerState())
        private set

    init {
        val userData = googleAuthClient.getSignedInUser()
        if (userData is Resource.Success) {
            producerState.value = ProducerState(
                isSignInSuccess = true,
                userName = userData.data?.userName,
                isSignOutSuccess = false
            )
        } else {
            producerState.value = ProducerState(
                isSignInSuccess = false
            )
        }
    }

    fun onSignInClick(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        viewModelScope.launch {
            val signInIntentSender = googleAuthClient.signIn()
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )
        }
    }

    fun handleIntent(intent: Intent?) {
        viewModelScope.launch {
            val signInResult = googleAuthClient.signInWithIntent(
                intent = intent ?: return@launch
            )
            updateProducerSignInState(signInResult)
        }
    }

    private fun updateProducerSignInState(result: Resource<UserData>) {
        producerState.value = producerState.value.copy(
            isSignInSuccess = result is Resource.Success,
            errorMessage = result.message,
            userName = result.data?.userName,
            isSignOutSuccess = false
        )
    }

    fun onSignOutClick() {
        viewModelScope.launch {
            googleAuthClient.signOut()
            producerState.value = producerState.value.copy(
                isSignInSuccess = false,
                isSignOutSuccess = true
            )
        }
    }
}