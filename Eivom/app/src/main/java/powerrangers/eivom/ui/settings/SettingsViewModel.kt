package powerrangers.eivom.ui.settings

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.domain.use_case.GoogleAuthClient
import powerrangers.eivom.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.domain.utility.ResourceErrorMessage
import powerrangers.eivom.domain.utility.UserData
import powerrangers.eivom.ui.utility.UserPreferences
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase,
    private val googleAuthClient: GoogleAuthClient
) : ViewModel() {
    var userPreferences = mutableStateOf(UserPreferences())
        private set
    var producerState = mutableStateOf(ProducerState())
        private set
    var dateFormat = mutableStateOf("")
        private set

    var colorPickerFunction = mutableStateOf<((Long) -> Unit)?>(null)
        private set

    init {
        viewModelScope.launch {
            userPreferences.value =
                UserPreferences(
                    colorMode = userPreferencesUseCase.getColorMode(),
                    topbarBackgroundColor = userPreferencesUseCase.getTopbarBackgroundColor(),
                    sidebarBackgroundColor = userPreferencesUseCase.getSidebarBackgroundColor(),
                    screenBackgroundColor = userPreferencesUseCase.getScreenBackgroundColor(),
                    movieNoteBackgroundColor = userPreferencesUseCase.getMovieNoteBackgroundColor(),
                    dialogBackgroundColor = userPreferencesUseCase.getDialogBackgroundColor(),
                    topbarTextColor = userPreferencesUseCase.getTopbarTextColor(),
                    sidebarTextColor = userPreferencesUseCase.getSidebarTextColor(),
                    screenTextColor = userPreferencesUseCase.getScreenTextColor(),
                    movieNoteTextColor = userPreferencesUseCase.getMovieNoteTextColor(),
                    dialogTextColor = userPreferencesUseCase.getDialogTextColor(),
                    originalTitleDisplay = userPreferencesUseCase.getOriginalTitleDisplay(),
                    dateFormat = userPreferencesUseCase.getDateFormat(),
                    notificationBeforeMonth = userPreferencesUseCase.getNotificationBeforeMonth(),
                    notificationBeforeWeek = userPreferencesUseCase.getNotificationBeforeWeek(),
                    notificationBeforeDay = userPreferencesUseCase.getNotificationBeforeDay(),
                    notificationOnDate = userPreferencesUseCase.getNotificationOnDate(),
                )
            dateFormat.value = userPreferencesUseCase.getDateFormatString()
        }
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

    // Save Settings
    suspend fun saveSettings(): Boolean {
        return userPreferencesUseCase.saveColorMode(userPreferences.value.colorMode) &&
                userPreferencesUseCase.saveTopbarBackgroundColor(userPreferences.value.topbarBackgroundColor.toArgb().toLong()) &&
                userPreferencesUseCase.saveSidebarBackgroundColor(userPreferences.value.sidebarBackgroundColor.toArgb().toLong()) &&
                userPreferencesUseCase.saveScreenBackgroundColor(userPreferences.value.screenBackgroundColor.toArgb().toLong()) &&
                userPreferencesUseCase.saveMovieNoteBackgroundColor(userPreferences.value.movieNoteBackgroundColor.toArgb().toLong()) &&
                userPreferencesUseCase.saveDialogBackgroundColor(userPreferences.value.dialogBackgroundColor.toArgb().toLong()) &&
                userPreferencesUseCase.saveTopbarTextColor(userPreferences.value.topbarTextColor.toArgb().toLong()) &&
                userPreferencesUseCase.saveSidebarTextColor(userPreferences.value.sidebarTextColor.toArgb().toLong()) &&
                userPreferencesUseCase.saveScreenTextColor(userPreferences.value.screenTextColor.toArgb().toLong()) &&
                userPreferencesUseCase.saveMovieNoteTextColor(userPreferences.value.movieNoteTextColor.toArgb().toLong()) &&
                userPreferencesUseCase.saveDialogTextColor(userPreferences.value.dialogTextColor.toArgb().toLong()) &&
                userPreferencesUseCase.saveOriginalTitleDisplay(userPreferences.value.originalTitleDisplay) &&
                userPreferencesUseCase.saveDateFormat(dateFormat.value) &&
                userPreferencesUseCase.saveNotificationBeforeMonth(userPreferences.value.notificationBeforeMonth) &&
                userPreferencesUseCase.saveNotificationBeforeWeek(userPreferences.value.notificationBeforeWeek) &&
                userPreferencesUseCase.saveNotificationBeforeDay(userPreferences.value.notificationBeforeDay) &&
                userPreferencesUseCase.saveNotificationOnDate(userPreferences.value.notificationOnDate)
    }

    // Color Mode
    fun updateColorMode(isCustom: Boolean) {
        userPreferences.value = userPreferences.value.copy(
            colorMode = isCustom
        )
    }

    // Custom Color
    fun clearColorPickerFunction() {
        colorPickerFunction.value = null
    }
    fun updateTopbarBackground() {
        colorPickerFunction.value = {
            userPreferences.value = userPreferences.value.copy(
                topbarBackgroundColor = Color(it)
            )
        }
    }
    fun updateSidebarBackground() {
        colorPickerFunction.value = {
            userPreferences.value = userPreferences.value.copy(
                sidebarBackgroundColor = Color(it)
            )
        }
    }
    fun updateScreenBackground() {
        colorPickerFunction.value = {
            userPreferences.value = userPreferences.value.copy(
                screenBackgroundColor = Color(it)
            )
        }
    }
    fun updateMovieNoteBackground() {
        colorPickerFunction.value = {
            userPreferences.value = userPreferences.value.copy(
                movieNoteBackgroundColor = Color(it)
            )
        }
    }
    fun updateDialogBackground() {
        colorPickerFunction.value = {
            userPreferences.value = userPreferences.value.copy(
                dialogBackgroundColor = Color(it)
            )
        }
    }
    fun updateTopbarText() {
        colorPickerFunction.value = {
            userPreferences.value = userPreferences.value.copy(
                topbarTextColor = Color(it)
            )
        }
    }
    fun updateSidebarText() {
        colorPickerFunction.value = {
            userPreferences.value = userPreferences.value.copy(
                sidebarTextColor = Color(it)
            )
        }
    }
    fun updateScreenText() {
        colorPickerFunction.value = {
            userPreferences.value = userPreferences.value.copy(
                screenTextColor = Color(it)
            )
        }
    }
    fun updateMovieNoteText() {
        colorPickerFunction.value = {
            userPreferences.value = userPreferences.value.copy(
                movieNoteTextColor = Color(it)
            )
        }
    }
    fun updateDialogText() {
        colorPickerFunction.value = {
            userPreferences.value = userPreferences.value.copy(
                dialogTextColor = Color(it)
            )
        }
    }

    // Original Title Display
    fun updateOriginalTitleDisplay(isDisplay: Boolean) {
        userPreferences.value = userPreferences.value.copy(
            originalTitleDisplay = isDisplay
        )
    }

    // Date Format
    fun updateDateFormat(format: String) {
        dateFormat.value = format
    }

    // Notification
    fun updateNotificationBeforeMonth(isNotification: Boolean) {
        userPreferences.value = userPreferences.value.copy(
            notificationBeforeMonth = isNotification
        )
    }
    fun updateNotificationBeforeWeek(isNotification: Boolean) {
        userPreferences.value = userPreferences.value.copy(
            notificationBeforeWeek = isNotification
        )
    }
    fun updateNotificationBeforeDay(isNotification: Boolean) {
        userPreferences.value = userPreferences.value.copy(
            notificationBeforeDay = isNotification
        )
    }
    fun updateNotificationOnDate(isNotification: Boolean) {
        userPreferences.value = userPreferences.value.copy(
            notificationOnDate = isNotification
        )
    }

    // Sign In
    fun onSignInClick(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        viewModelScope.launch {
            val signInIntentSender = googleAuthClient.signIn()
            if (signInIntentSender == null) {
                producerState.value =
                    producerState.value.copy(errorMessage = ResourceErrorMessage.SIGN_IN)
                return@launch
            }
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender
                ).build()
            )
        }
    }

    fun handleIntent(intent: Intent?) {
        viewModelScope.launch {
            if (intent == null) {
                producerState.value =
                    producerState.value.copy(errorMessage = ResourceErrorMessage.SIGN_IN)
                return@launch
            }
            val signInResult = googleAuthClient.signInWithIntent(
                intent = intent
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

    fun deleteErrorMessage() {
        producerState.value = producerState.value.copy(
            errorMessage = null
        )
    }
}