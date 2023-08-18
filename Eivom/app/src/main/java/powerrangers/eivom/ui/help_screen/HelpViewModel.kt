package powerrangers.eivom.ui.help_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.ui.utility.UserPreferences
import javax.inject.Inject

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase
) : ViewModel() {
    var userPreferences = mutableStateOf(UserPreferences())
        private set

    var helpState = mutableStateOf(0)
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
        }
    }

    fun updateHelpState(state: Int) {
        helpState.value = state
    }
}