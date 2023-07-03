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
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import powerrangers.eivom.feature_movie.domain.utility.Resource
import powerrangers.eivom.feature_movie.domain.utility.ResourceErrorMessage
import powerrangers.eivom.feature_movie.domain.utility.addList
import powerrangers.eivom.feature_movie.domain.utility.toError
import powerrangers.eivom.feature_movie.domain.utility.toLoading
import powerrangers.eivom.feature_movie.ui.utility.UserPreferences
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase,
    private val movieDatabaseUseCase: MovieDatabaseUseCase
) : ViewModel() {
    var userPreferences = mutableStateOf(UserPreferences())
        private set

    private var currentPage = 1
    private var endReached = false

    var movieListItems = mutableStateOf<Resource<List<MovieListItem>>>(Resource.Loading(data = emptyList()))
        private set

    //State of tool
    var isFilterVisible = mutableStateOf(false)
        private set
    var isSearchVisible = mutableStateOf(false)
        private set

    var isSortVisible = mutableStateOf(false)
        private set

    var userSearch by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            userPreferences.value = UserPreferences(
                backgroundColor = userPreferencesUseCase.getBackgroundColor(),
                dateFormat = userPreferencesUseCase.getDateFormat()
            )
        }
        loadMoviePaginated()
    }

    fun loadMoviePaginated(landscapeWidth: Int = 500, posterWidth: Int = 500) {
        viewModelScope.launch {
            if (endReached) {
                movieListItems.value.toError(message = ResourceErrorMessage.MOVIELIST_END)
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

                movieListItems.value = movieListItems.value.addList(
                    movieDatabaseUseCase.getMovieListItemsResource(
                        page = currentPage,
                        landscapeWidth = landscapeWidth,
                        posterWidth = posterWidth,
                        dateFormat = userPreferences.value.dateFormat
                    )
                )

                if (movieListItems.value is Resource.Success) {
                    currentPage++
                }
            } catch (e: Exception) {
                if (movieListItems.value.data == null) {
                    movieListItems.value = movieListItems.value.toError(data = emptyList(), message = e.message ?: ResourceErrorMessage.LOAD_MOVIELIST)
                }
                else {
                    movieListItems.value = movieListItems.value.toError(message = e.message ?: ResourceErrorMessage.LOAD_MOVIELIST)
                }
            }
        }
    }

    fun handleMovieDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        movieDatabaseUseCase.handleImageDominantColor(drawable = drawable, onFinish = onFinish)
    }

    fun reverseIsFilter() {
        isFilterVisible.value = !isFilterVisible.value
    }

    fun reverseIsSearch() {
        isSearchVisible.value = !isSearchVisible.value
    }

    fun reverseIsSort() {
        isSortVisible.value = !isSortVisible.value
    }
    fun updateUserSearch(userWord: String){
        userSearch = userWord
    }

    fun isFavoriteMovie(movieId: Int): Boolean = movieDatabaseUseCase.isFavoriteMovie(movieId)
    fun isWatchedMovie(movieId: Int): Boolean = movieDatabaseUseCase.isWatchedMovie(movieId)
    fun isSponsoredMovie(movieId: Int): Boolean = movieDatabaseUseCase.isSponsoredMovie(movieId)

    suspend fun addFavoriteMovie(movieListItem: MovieListItem): Boolean {
        return movieDatabaseUseCase.addFavoriteMovie(movieListItem)
    }

    suspend fun deleteFavoriteMovie(movieId: Int): Boolean {
        return movieDatabaseUseCase.deleteFavoriteMovie(movieId)
    }
}