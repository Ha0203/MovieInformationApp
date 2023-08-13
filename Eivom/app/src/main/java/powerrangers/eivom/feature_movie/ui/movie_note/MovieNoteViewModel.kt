package powerrangers.eivom.feature_movie.ui.movie_note

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.feature_movie.domain.use_case.MovieDatabaseUseCase
import powerrangers.eivom.navigation.Route
import powerrangers.eivom.ui.utility.UserPreferences
import javax.inject.Inject

@HiltViewModel
class MovieNoteViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase,
    private val movieDatabaseUseCase: MovieDatabaseUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var userPreferences = mutableStateOf(UserPreferences())
        private set

    private val movieId: Int = checkNotNull(savedStateHandle[Route.MOVIE_NOTE_SCREEN_MOVIE_ID])

    var movieNote = mutableStateOf(NoteState())
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
            val movieNoteResource = movieDatabaseUseCase.getMovieNoteResource(movieId = movieId, userPreferences.value.originalTitleDisplay)
            if (movieNoteResource is Resource.Success) {
                movieNote.value = NoteState(
                    title = movieNoteResource.data!!.first,
                    content = movieNoteResource.data.second
                )
            }
        }
    }
}