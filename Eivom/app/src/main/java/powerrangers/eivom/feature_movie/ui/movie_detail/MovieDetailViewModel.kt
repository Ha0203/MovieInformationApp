package powerrangers.eivom.feature_movie.ui.movie_detail

import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.feature_movie.domain.model.MovieItem
import powerrangers.eivom.feature_movie.domain.use_case.MovieDatabaseUseCase
import powerrangers.eivom.feature_movie.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.feature_movie.domain.utility.Resource
import powerrangers.eivom.feature_movie.ui.utility.UserPreferences
import powerrangers.eivom.navigation.Route
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase,
    private val movieDatabaseUseCase: MovieDatabaseUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var userPreferences = mutableStateOf(UserPreferences())
        private set

    private val movieId: Int = checkNotNull(savedStateHandle[Route.MOVIE_DETAIL_SCREEN_MOVIE_ID])

    var movieInformation = mutableStateOf<Resource<MovieItem>>(Resource.Loading())
        private set

    init {
        viewModelScope.launch {
            userPreferences.value =
                UserPreferences(
                    backgroundColor = userPreferencesUseCase.getBackgroundColor(),
                    dateFormat = userPreferencesUseCase.getDateFormat()
                )
            movieInformation.value =
                movieDatabaseUseCase.convertMovieInformationResourceToMovieItemResource(
                    movieDatabaseUseCase.getMovieInformationResource(movieId = movieId),
                    landscapeWidth = 500,
                    posterWidth = 500
                )
        }
    }

    fun handleMovieDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        movieDatabaseUseCase.handleImageDominantColor(drawable = drawable, onFinish = onFinish)
    }
}