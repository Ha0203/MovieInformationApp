package powerrangers.eivom.feature_movie.ui.movie_list

import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.feature_movie.domain.model.MovieListItem
import powerrangers.eivom.feature_movie.domain.use_case.MovieDatabaseUseCase
import powerrangers.eivom.feature_movie.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.feature_movie.domain.utility.Resource
import powerrangers.eivom.feature_movie.domain.utility.add
import powerrangers.eivom.feature_movie.domain.utility.toError
import powerrangers.eivom.feature_movie.domain.utility.toLoading
import powerrangers.eivom.feature_movie.ui.utility.UserPreferences
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase,
    private val movieDatabaseUseCase: MovieDatabaseUseCase
) : ViewModel() {
    var userPreferences: UserPreferences by mutableStateOf(UserPreferences())
        private set

    private var currentPage = 1
    private var endReached = false

    var movieListItems = mutableStateOf<Resource<List<MovieListItem>>>(Resource.Loading(data = emptyList()))
        private set

    init {
        viewModelScope.launch {
            userPreferences = UserPreferences(
                backgroundColor = userPreferencesUseCase.getBackgroundColor(),
                dateFormat = userPreferencesUseCase.getDateFormat()
            )
        }
        loadMoviePaginated()
    }

    fun loadMoviePaginated(movieImageWidth: Int = 500) {
        viewModelScope.launch {
            if (endReached) {
                movieListItems.value.toError(message = "End of movie list error")
                return@launch
            }

            if (movieListItems.value.data == null) {
                movieListItems.value = movieListItems.value.toLoading(data = emptyList())
            }
            else {
                movieListItems.value = movieListItems.value.toLoading()
            }

            try {
                endReached = currentPage >= DefaultValue.TMDB_API_TOTAL_PAGES

                movieListItems.value = movieListItems.value.add(
                    movieDatabaseUseCase.convertMovieListResourceToMovieListItemsResource(
                        movieList = movieDatabaseUseCase.getMovieList(page = currentPage),
                        movieImageWidth = movieImageWidth
                    )
                )

                currentPage++
            } catch (e: Exception) {
                if (movieListItems.value.data == null) {
                    movieListItems.value = movieListItems.value.toError(data = emptyList(), message = "Load movie list error")
                }
                else {
                    movieListItems.value = movieListItems.value.toError(message = "Load movie list error")
                }
            }
        }
    }

    fun handleMovieDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        movieDatabaseUseCase.handleImageDominantColor(drawable = drawable, onFinish = onFinish)
    }
}