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
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.domain.utility.ResourceErrorMessage
import powerrangers.eivom.domain.utility.addList
import powerrangers.eivom.domain.utility.toError
import powerrangers.eivom.domain.utility.toLoading
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
    var filterState = mutableStateOf(FilterState())
        private set
    var sortState = mutableStateOf(SortState())
        private set
    var isFilterVisible = mutableStateOf(false)
        private set
    var isSearchVisible = mutableStateOf(false)
        private set
    var isSortVisible = mutableStateOf(false)
        private set
    var userSearch by mutableStateOf("")
        private set
    var isExpanded = mutableStateOf(false)
        private set
    var regionSelected = mutableStateOf("Select Region")
        private set
    var lastRegionSelected = mutableStateOf("")
        private set
    var releaseDate = mutableStateOf("")
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
                        dateFormat = userPreferences.value.dateFormat,
                        //trending = MovieFilter.Trending(TrendingTime.DAY),
                        //region = MovieFilter.Region("Viet Nam")
                        //genre = MovieFilter.Genre()
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
        //filterState.value = filterState.value.copy(isUpdated = true)
    }
    fun updateUserSearch(userWord: String){
        userSearch = userWord
    }

    // Trending State
    fun reverseIsTrending(){
        filterState.value = filterState.value.copy(isTrending = !filterState.value.isTrending)
    }

    fun reverseIsTrendingDay(){
        filterState.value = filterState.value.copy(isTrendingDay = !filterState.value.isTrendingDay)
    }

    fun reverseIsTrendingWeek(){
        filterState.value = filterState.value.copy(isTrendingWeek = !filterState.value.isTrendingWeek)
    }

    //Favorite State
    fun reverseIsFavorite(){
        filterState.value = filterState.value.copy(isFavorite = !filterState.value.isFavorite)
    }

    // Adult content
    fun reverseAdultContet(){
        filterState.value = filterState.value.copy(AdultContentIncluded = !filterState.value.AdultContentIncluded)
    }

    fun setAllTrendingDefault(){
        filterState.value = filterState.value.copy(isTrendingWeek = false)
        filterState.value = filterState.value.copy(isTrendingDay = false)
    }

    fun changeRegionSelect(region: String){
        lastRegionSelected.value = regionSelected.value
        regionSelected.value = region
    }
    fun resetRegionSelect(){
        lastRegionSelected.value = regionSelected.value
        regionSelected.value = ""
    }

    fun resetLastRegion(){
        lastRegionSelected.value = ""
    }
    fun reverseExpanded(){
        isExpanded.value = !isExpanded.value
    }

    fun updateReleaseDate(newRD: String){
        releaseDate.value = newRD
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