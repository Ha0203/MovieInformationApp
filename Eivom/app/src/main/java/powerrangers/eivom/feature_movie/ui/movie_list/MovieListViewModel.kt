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
import powerrangers.eivom.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.domain.utility.ResourceErrorMessage
import powerrangers.eivom.domain.utility.addList
import powerrangers.eivom.domain.utility.toError
import powerrangers.eivom.domain.utility.toLoading
import powerrangers.eivom.feature_movie.domain.utility.Logic
import powerrangers.eivom.feature_movie.domain.utility.MovieFilter
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import powerrangers.eivom.feature_movie.domain.utility.TrendingTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import powerrangers.eivom.ui.utility.UserPreferences
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
    var minReleaseDate = mutableStateOf<String?>(null)
        private set
    var maxReleaseDate = mutableStateOf<String?>(null)
        private set
    var showDatePicker = mutableStateOf(false)
        private set
    var minRating = mutableStateOf<String?>(null)
        private set
    var maxRating = mutableStateOf<String?>(null)
        private set
    var isGenres = mutableStateOf(false)
        private set
    var isWithoutGenres = mutableStateOf(false)
    private set
    var isCountry = mutableStateOf(false)
        private set
    var isLanguage = mutableStateOf(false)
        private set
    var selectedGenres = mutableListOf<GenreItems>()
        private set
    var selectedWithoutGenres = mutableListOf<GenreItems>()
        private set
    var selectedCountries = mutableListOf<Countries>()
        private set
    var selectedLanguage = mutableListOf<Language>()
        private set
    var minLength = mutableStateOf<String?>(null)
        private set
    var maxLength = mutableStateOf<String?>(null)
        private set
    init {
        viewModelScope.launch {
            userPreferences.value = UserPreferences(
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
                        trending = MovieFilter.Trending(TrendingTime.WEEK),
                        favorite = MovieFilter.Favorite(filterState.value.isFavorite),
                        watched = MovieFilter.Watched(filterState.value.isWatched),
                        region = MovieFilter.Region(filterState.value.Region.toString()),
                        adultContentIncluded = MovieFilter.AdultContentIncluded(filterState.value.AdultContentIncluded),
                        primaryReleaseYear = filterState.value.ReleaseYear?.let {
                            MovieFilter.ReleaseYear(
                                it.toInt())
                        },
                        minimumPrimaryReleaseDate = stringToDate(filterState.value.MinimumReleaseDate.toString())?.let {
                            MovieFilter.MinimumReleaseDate(it)
                        },
                        maximumPrimaryReleaseDate =  stringToDate(filterState.value.MaximumReleaseDate.toString())?.let {
                            MovieFilter.MaximumReleaseDate(it)
                        },
                        genre = filterState.value.Genre?.let {
                            convertGenreValuesToKeys(it)?.let {
                                MovieFilter.Genre(it, logic = Logic.OR)
                            }
                        },
                        originCountry = filterState.value.OriginCountry?.let {
                            convertCountryValuesToKeys(it)?.let {
                                MovieFilter.OriginCountry(it, logic = Logic.OR)
                            }
                        },
                        originLanguage = filterState.value.OriginLanguage?.let {
                            convertLanguageValuesToKeys(it)?.let {
                                MovieFilter.OriginLanguage(it, logic = Logic.OR)
                            }
                        },
                        minimumLength = filterState.value.MinimumLength?.let {
                            MovieFilter.MinimumLength(
                                it
                            )
                        },
                        maximumLength = filterState.value.MaximumLength?.let {
                            MovieFilter.MaximumLength(
                                it
                            )
                        },
                        withoutGenre = filterState.value.WithoutGenre?.let {
                            convertGenreValuesToKeys(it)?.let {
                                MovieFilter.WithoutGenre(it, logic = Logic.OR)
                            }
                        },

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

    fun convertGenreValuesToKeys(genreItemsList: List<GenreItems>): List<Int> {
        val genreKeysList = mutableListOf<Int>()

        for ((key, value) in TranslateCode.GENRE) {
            for (genreItem in genreItemsList) {
                if (genreItem.name == value) {
                    genreKeysList.add(key)
                }
            }
        }
        return genreKeysList
    }
    fun convertCountryValuesToKeys(countryItemsList: List<Countries>): List<String> {
        val countryKeysList = mutableListOf<String>()

        for ((key, value) in TranslateCode.ISO_3166_1) {
            for (countryItem in countryItemsList) {
                if (countryItem.name == value) {
                    countryKeysList.add(key)
                }
            }
        }
        return countryKeysList
    }
    fun convertLanguageValuesToKeys(languageItemsList: List<Language>): List<String> {
        val languageKeysList = mutableListOf<String>()

        for ((key, value) in TranslateCode.ISO_639_1) {
            for (languageItem in languageItemsList) {
                if (languageItem.name == value) {
                    languageKeysList.add(key)
                }
            }
        }
        return languageKeysList
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
    fun reverseIsWatched(){
        filterState.value = filterState.value.copy(isWatched = !filterState.value.isWatched)
    }
    // Adult content
    fun reverseAdultContent(){
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
    fun updateMinReleaseDate(newMin: String){
        minReleaseDate.value = newMin
    }
    fun updateMaxReleaseDate(newMax: String){
        minReleaseDate.value = newMax
    }
    fun reverseDatePicker(){
        showDatePicker.value = !showDatePicker.value
    }
    fun updateMinRating(newVal: String) {
        minRating.value = newVal
    }
    fun updateMaxRating(newVal: String){
        maxRating.value = newVal
    }
    fun updateMinLen(newVal: String) {
        minLength.value = newVal
    }
    fun updateMaxLen(newVal: String){
        maxLength.value = newVal
    }
    fun reverseIsGenres(){
        isGenres.value = !isGenres.value
    }
    fun reverseIsWithoutGenres(){
        isWithoutGenres.value = !isWithoutGenres.value
    }
    fun reverseIsCountry(){
        isCountry.value = !isCountry.value
    }
    fun reverseIsLanguage(){
        isLanguage.value = !isLanguage.value
    }

    fun stringToDate(dateString: String): LocalDate? {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return try {
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            null
        }
    }
    fun updateFilterState(){
        // Except Trending, Favorite, Watched, Adult
        filterState.value = filterState.value.copy(Region = regionSelected.toString())
        filterState.value = filterState.value.copy(ReleaseYear = releaseDate.toString().toInt())
        filterState.value = filterState.value.copy(MinimumReleaseDate = minReleaseDate.toString())
        filterState.value = filterState.value.copy(MaximumReleaseDate = maxReleaseDate.toString())
        filterState.value = filterState.value.copy(MinimumRating = minRating.toString().toFloat())
        filterState.value = filterState.value.copy(MaximumRating = maxRating.toString().toFloat())
        filterState.value = filterState.value.copy(Genre = selectedGenres)
        filterState.value = filterState.value.copy(OriginCountry = selectedCountries)
        filterState.value = filterState.value.copy(OriginLanguage = selectedLanguage)
        filterState.value = filterState.value.copy(MinimumLength = minLength.toString().toInt())
        filterState.value = filterState.value.copy(MaximumLength = maxLength.toString().toInt())
        filterState.value = filterState.value.copy(WithoutGenre = selectedWithoutGenres)
        filterState.value = filterState.value.copy(isUpdated = !filterState.value.isUpdated)
    }
    fun resetUpdateFilter(){
        filterState.value = filterState.value.copy(isUpdated = false)
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