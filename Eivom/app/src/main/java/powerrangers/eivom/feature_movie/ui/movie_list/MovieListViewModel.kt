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
import powerrangers.eivom.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.domain.utility.ResourceErrorMessage
import powerrangers.eivom.domain.utility.addList
import powerrangers.eivom.domain.utility.toError
import powerrangers.eivom.domain.utility.toLoading
import powerrangers.eivom.feature_movie.domain.model.MovieListItem
import powerrangers.eivom.feature_movie.domain.use_case.MovieDatabaseUseCase
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import powerrangers.eivom.feature_movie.domain.utility.Logic
import powerrangers.eivom.feature_movie.domain.utility.MovieFilter
import powerrangers.eivom.feature_movie.domain.utility.MovieOrder
import powerrangers.eivom.feature_movie.domain.utility.Order
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import powerrangers.eivom.feature_movie.domain.utility.TrendingTime
import powerrangers.eivom.ui.utility.UserPreferences
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    // States
    var isAddingMovie = mutableStateOf(false)
        private set
    var isSearchVisible = mutableStateOf(false)
        private set
    var isSortVisible = mutableStateOf(false)
        private set
    var userSearch by mutableStateOf("")
        private set
    private var query = mutableStateOf("")
    var isExpanded = mutableStateOf(false)
        private set
    var isSearchUpdated = mutableStateOf(true)
        private set

    //State of Filter
    var filterState = mutableStateOf(FilterState())
        private set
    var isFilterVisible = mutableStateOf(false)
        private set
    var trendingFilter = mutableStateOf(FilterState().Trending)
        private set
    var favoriteFilter = mutableStateOf(FilterState().Favorite)
        private set
    var watchedFilter = mutableStateOf(FilterState().Watched)
        private set
    var adultFilter = mutableStateOf(FilterState().AdultContentIncluded)
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

    // State of Sort
    var sortState = mutableStateOf(SortState())
        private set
    var releaseDateSort = mutableStateOf(SortState().ReleaseDate)
        private set
    var ratingSort  = mutableStateOf(SortState().Rating)
        private set
    var voteSort = mutableStateOf(SortState().Vote)
        private set
    var originalTitleSort = mutableStateOf(SortState().OriginalTitle)
        private set
    var titleSort = mutableStateOf(SortState().Title)
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

    fun updateAddingState(isAdding: Boolean) {
        isAddingMovie.value = isAdding
    }

    fun updateMovieListBySearch() {
        movieListItems.value = Resource.Loading(data = emptyList())
        currentPage = 1
        isSearchUpdated.value = true
        query.value = userSearch
        loadMoviePaginated()
    }

    fun resetMovieList() {
        movieListItems.value = Resource.Loading(data = emptyList())
        currentPage = 1
        endReached = false
        query.value = ""
        userSearch = ""
        filterState.value = filterState.value.copy(isUpdated = false)
        sortState.value = sortState.value.copy(isUpdate = false)
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
                        trending = filterState.value.Trending?.let { MovieFilter.Trending(it) },
                        favorite = filterState.value.Favorite?.let { MovieFilter.Favorite(it) },
                        watched = filterState.value.Watched?.let { MovieFilter.Watched(it) },
                        region =
                            if (filterState.value.Region.isNullOrBlank()) null
                                else filterState.value.Region?.let{it ->
                                    convertRegionValuesToKeys(it).let {
                                        if (it.isBlank()) null
                                            else MovieFilter.Region(it)
                                    }
                            },
                        adultContentIncluded = filterState.value.AdultContentIncluded?.let { MovieFilter.AdultContentIncluded(it) },
                        primaryReleaseYear =
                            if (filterState.value.ReleaseYear == null) null
                                else MovieFilter.ReleaseYear(filterState.value.ReleaseYear!!),

                        minimumPrimaryReleaseDate =
                            if (filterState.value.MinimumReleaseDate == null) {
                                null
                            }
                                else stringToDate(filterState.value.MinimumReleaseDate.toString())?.let {
                                MovieFilter.MinimumReleaseDate(it)
                                },

                        maximumPrimaryReleaseDate =
                            if (filterState.value.MaximumReleaseDate == null) null
                                else stringToDate(filterState.value.MaximumReleaseDate.toString())?.let {
                                    MovieFilter.MaximumReleaseDate(it)
                                },

                        genre =
                            if (filterState.value.Genre.isNullOrEmpty()) null
                                else filterState.value.Genre?.let { it ->
                                    convertGenreValuesToKeys(it).let {
                                        MovieFilter.Genre(it, logic = Logic.OR)
                                    }
                            },

                        minimumRating =
                            if (filterState.value.MinimumRating == null) null
                                else filterState.value.MinimumRating?.let {
                                MovieFilter.MinimumRating(it)
                            },
                        maximumRating =
                            if (filterState.value.MaximumRating == null) null
                                else filterState.value.MaximumRating?.let {
                                    MovieFilter.MaximumRating(it)
                            },
                        originCountry =
                            if (filterState.value.OriginCountry.isNullOrEmpty()) null
                                else filterState.value.OriginCountry?.let { it ->
                                    convertCountryValuesToKeys(it).let {
                                        MovieFilter.OriginCountry(it, logic = Logic.OR)
                                    }
                            },

                        originLanguage =
                            if (filterState.value.OriginLanguage.isNullOrEmpty()) null
                                 else filterState.value.OriginLanguage?.let { it ->
                                    convertLanguageValuesToKeys(it).let {
                                        MovieFilter.OriginLanguage(it, logic = Logic.OR)
                                    }
                            },
                        minimumLength =
                            if (filterState.value.MinimumLength == null) null
                                else filterState.value.MinimumLength?.let {
                                    MovieFilter.MinimumLength(it)
                            },

                        maximumLength =
                            if (filterState.value.MaximumLength == null) null
                                else filterState.value.MaximumLength?.let {
                                    MovieFilter.MaximumLength(it)
                            },

                        withoutGenre =
                            if (filterState.value.WithoutGenre.isNullOrEmpty()) null
                                else filterState.value.WithoutGenre?.let { it ->
                                    convertGenreValuesToKeys(it).let {
                                        MovieFilter.WithoutGenre(it, logic = Logic.OR)
                                }
                            },
                        searchQuery = query.value.ifBlank { null },
                        sortBy = if (sortState.value.OriginalTitle != null) {
                            MovieOrder.OriginalTitle(sortState.value.OriginalTitle!!)
                        } else if (sortState.value.Rating != null) {
                            MovieOrder.Rating(sortState.value.Rating!!)
                        } else if (sortState.value.Title != null) {
                            MovieOrder.Title(sortState.value.Title!!)
                        } else if (sortState.value.ReleaseDate != null) {
                            MovieOrder.ReleaseDate(sortState.value.ReleaseDate!!)
                        } else if (sortState.value.Vote != null) {
                            MovieOrder.Vote(sortState.value.Vote!!)
                        } else null
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

    private fun convertGenreValuesToKeys(genreItemsList: List<GenreItems>): List<Int> {
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

    private fun convertRegionValuesToKeys(region: String): String {
        for ((key, value) in TranslateCode.ISO_3166_1) {
            if (value == region) return key
        }
        return ""
    }

    private fun convertCountryValuesToKeys(countryItemsList: List<Countries>): List<String> {
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
    private fun convertLanguageValuesToKeys(languageItemsList: List<Language>): List<String> {
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
        isSearchUpdated.value = false
    }

    // Trending State
    fun reverseIsTrending(){
        if (trendingFilter.value != null){
            trendingFilter.value = null
        } else trendingFilter.value = TrendingTime.DAY
    }

    fun reverseTrendingDayWeek(){
        if (trendingFilter.value == TrendingTime.DAY) trendingFilter.value = TrendingTime.WEEK
        else trendingFilter.value = TrendingTime.DAY
    }

    //Favorite State
    fun reverseIsFavorite(){
        if (favoriteFilter.value == null){
            favoriteFilter.value = true
        } else favoriteFilter.value = null
    }
    fun reverseIsWatched(){
        if (watchedFilter.value == null){
            watchedFilter.value = true
        } else watchedFilter.value = null
    }
    // Adult content
    fun reverseAdultContent(){
        if (adultFilter.value == null){
            adultFilter.value = true
        } else adultFilter.value = null
    }

    fun resetAllFilterDefault(){
        filterState.value = filterState.value.copy(Trending = null)
        filterState.value = filterState.value.copy(Favorite = null)
        filterState.value = filterState.value.copy(Watched = null)
        filterState.value = filterState.value.copy(isUpdated = false)
        filterState.value = filterState.value.copy(AdultContentIncluded = false)
        filterState.value = filterState.value.copy(Region = null)
        filterState.value = filterState.value.copy(ReleaseYear = null)
        filterState.value = filterState.value.copy(MinimumReleaseDate = null)
        filterState.value = filterState.value.copy(MaximumReleaseDate = null)
        filterState.value = filterState.value.copy(MinimumRating = null)
        filterState.value = filterState.value.copy(MaximumRating = null)
        filterState.value = filterState.value.copy(Genre = null)
        filterState.value = filterState.value.copy(OriginLanguage = null)
        filterState.value = filterState.value.copy(OriginCountry = null)
        filterState.value = filterState.value.copy(MinimumLength = null)
        filterState.value = filterState.value.copy(MaximumLength = null)
        filterState.value = filterState.value.copy(WithoutGenre = null)
    }

    fun changeRegionSelect(region: String){
        lastRegionSelected.value = regionSelected.value
        regionSelected.value = region
    }
    fun resetRegionSelect(){
        lastRegionSelected.value = regionSelected.value
        regionSelected.value = ""
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
        maxReleaseDate.value = newMax
    }
    fun reverseDatePicker(){
        showDatePicker.value = !showDatePicker.value
    }
    fun updateMinRating(newVal: String) {
        minRating.value = newVal.ifBlank { null }
    }
    fun updateMaxRating(newVal: String){
        maxRating.value = newVal.ifBlank { null }
    }
    fun updateMinLen(newVal: String) {
        minLength.value = newVal.ifBlank { null }
    }
    fun updateMaxLen(newVal: String){
        maxLength.value = if (newVal.isBlank()) null else newVal
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

    private fun stringToDate(dateString: String): LocalDate? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return try {
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            null
        }
    }
    fun updateFilterState() {

        if (trendingFilter.value != null){
            filterState.value = filterState.value.copy(Trending = trendingFilter.value)
        } else filterState.value = filterState.value.copy(Trending = null)

        if (favoriteFilter.value != null){
            filterState.value = filterState.value.copy(Favorite = favoriteFilter.value)
        } else filterState.value = filterState.value.copy(Favorite = null)

        if (watchedFilter.value != null){
            filterState.value = filterState.value.copy(Watched = watchedFilter.value)
        } else filterState.value = filterState.value.copy(Watched = null)

        if (adultFilter.value != null){
            filterState.value = filterState.value.copy(AdultContentIncluded = adultFilter.value)
        } else filterState.value = filterState.value.copy(AdultContentIncluded = null)

        if (regionSelected.value != "Select Region" && regionSelected.value != "")
        {
            filterState.value = filterState.value.copy(Region = regionSelected.value)
        } else filterState.value = filterState.value.copy(Region = null)

        if (releaseDate.value != "")
        {
            filterState.value = filterState.value.copy(ReleaseYear = releaseDate.value.toInt())
        } else filterState.value = filterState.value.copy(ReleaseYear = null)

        if (minReleaseDate.value != null){
            filterState.value = filterState.value.copy(MinimumReleaseDate = minReleaseDate.value)
        } else filterState.value = filterState.value.copy(MinimumReleaseDate = null)

        if (maxReleaseDate.value != null){
            filterState.value = filterState.value.copy(MaximumReleaseDate = maxReleaseDate.value)
        } else filterState.value = filterState.value.copy(MaximumReleaseDate = null)

        if (minRating.value != null){
            filterState.value = filterState.value.copy(MinimumRating = minRating.value?.toFloat())
        } else filterState.value = filterState.value.copy(MinimumRating = null)

        if (maxRating.value != null){
            filterState.value = filterState.value.copy(MaximumRating = maxRating.value?.toFloat())
        } else  filterState.value = filterState.value.copy(MaximumRating = null)

        filterState.value = filterState.value.copy(Genre = selectedGenres)
        filterState.value = filterState.value.copy(OriginCountry = selectedCountries)
        filterState.value = filterState.value.copy(OriginLanguage = selectedLanguage)

        if (minLength.value != null){
            filterState.value = filterState.value.copy(MinimumLength = minLength.value?.toInt())
        } else filterState.value = filterState.value.copy(MinimumLength = null)

        if (maxLength.value != null){
            filterState.value = filterState.value.copy(MaximumLength = maxLength.value?.toInt())
        } else filterState.value = filterState.value.copy(MaximumLength = null)

        filterState.value = filterState.value.copy(WithoutGenre = selectedWithoutGenres)
        filterState.value = filterState.value.copy(isUpdated = !filterState.value.isUpdated)
    }
    fun resetUpdateFilter(){
        filterState.value = filterState.value.copy(isUpdated = false)
    }
    fun updateFilterViewModel(){
        trendingFilter.value = filterState.value.Trending
        favoriteFilter.value = filterState.value.Favorite
        watchedFilter.value = filterState.value.Watched
        adultFilter.value = filterState.value.AdultContentIncluded
        regionSelected.value = (if (filterState.value.Region == null) "Select Region" else filterState.value.Region.toString())
        releaseDate.value = (if (filterState.value.ReleaseYear == null) "" else filterState.value.ReleaseYear.toString())
        minReleaseDate.value = (if (filterState.value.MinimumReleaseDate == null) null else filterState.value.MinimumReleaseDate)
        maxReleaseDate.value = (if (filterState.value.MaximumReleaseDate == null) null else filterState.value.MaximumReleaseDate)
        minRating.value = (if (filterState.value.MinimumRating == null) null else filterState.value.MinimumRating.toString())
        maxRating.value = (if (filterState.value.MaximumRating == null) null else filterState.value.MaximumRating.toString())
        selectedGenres = filterState.value.Genre?.toMutableList()?: mutableListOf()
        selectedWithoutGenres = filterState.value.WithoutGenre?.toMutableList()?: mutableListOf()
        selectedLanguage = filterState.value.OriginLanguage?.toMutableList()?: mutableListOf()
        selectedCountries = filterState.value.OriginCountry?.toMutableList()?: mutableListOf()
        minLength.value = (if (filterState.value.MinimumLength == null) null else filterState.value.MinimumLength.toString())
        maxRating.value = (if (filterState.value.MaximumLength == null) null else filterState.value.MaximumLength.toString())

    }
    // Sort Func
    fun updateSortState(){
        if (releaseDateSort == null) {
            sortState.value = sortState.value.copy(ReleaseDate = null)
        } else sortState.value = sortState.value.copy(ReleaseDate = releaseDateSort.value)

        if (ratingSort == null){
            sortState.value = sortState.value.copy(Rating = null)
        } else sortState.value = sortState.value.copy(Rating = ratingSort.value)

        if (voteSort == null){
            sortState.value = sortState.value.copy(Vote = null)
        } else sortState.value = sortState.value.copy(Vote = voteSort.value)

        if (originalTitleSort == null){
            sortState.value = sortState.value.copy(OriginalTitle = null)
        } else sortState.value = sortState.value.copy(OriginalTitle = originalTitleSort.value)

        if (titleSort == null){
            sortState.value = sortState.value.copy(Title = null)
        } else sortState.value = sortState.value.copy(Title = titleSort.value)

        sortState.value = sortState.value.copy(isUpdate = !sortState.value.isUpdate)
    }
    fun resetUpdateSort(){
        sortState.value = sortState.value.copy(isUpdate = false)
    }
    fun resetAllSortDefault(){
        sortState.value = sortState.value.copy(ReleaseDate = null)
        sortState.value = sortState.value.copy(Rating = null)
        sortState.value = sortState.value.copy(Vote = null)
        sortState.value = sortState.value.copy(OriginalTitle = null)
        sortState.value = sortState.value.copy(Title = null)
    }
    fun reverseReleaseDateSort(){
        if (releaseDateSort == null) {
            releaseDateSort.value = Order.ASCENDING
        }
        else if (releaseDateSort.value == Order.ASCENDING){
            resetAllSortDefault()
            updateSortViewModel()
            releaseDateSort.value = Order.DESCENDING
        }
        else {
            resetAllSortDefault()
            updateSortViewModel()
            releaseDateSort.value = Order.ASCENDING
        }
    }
    fun reverseRatingSort(){
        if (ratingSort == null) {
            ratingSort.value = Order.ASCENDING
        }
        else if (ratingSort.value == Order.ASCENDING){
            resetAllSortDefault()
            updateSortViewModel()
            ratingSort.value = Order.DESCENDING
        }
        else {
            resetAllSortDefault()
            updateSortViewModel()
            ratingSort.value = Order.ASCENDING
        }
    }
    fun reverseVoteSort(){
        if (voteSort == null) {
            voteSort.value = Order.ASCENDING
        }
        else if (voteSort.value == Order.ASCENDING){
            resetAllSortDefault()
            updateSortViewModel()
            voteSort.value = Order.DESCENDING
        }
        else {
            resetAllSortDefault()
            updateSortViewModel()
            voteSort.value = Order.ASCENDING
        }
    }

    fun reverseOTitleSort(){
        if (originalTitleSort == null) {
            originalTitleSort.value = Order.ASCENDING
        }
        else if (originalTitleSort.value == Order.ASCENDING){
            resetAllSortDefault()
            updateSortViewModel()
            originalTitleSort.value = Order.DESCENDING
        }
        else {
            resetAllSortDefault()
            updateSortViewModel()
            originalTitleSort.value = Order.ASCENDING
        }
    }

    fun reverseTitleSort(){
        if (titleSort == null) {
            titleSort.value = Order.ASCENDING
        }
        else if (titleSort.value == Order.ASCENDING){
            resetAllSortDefault()
            updateSortViewModel()
            titleSort.value = Order.DESCENDING
        }
        else {
            resetAllSortDefault()
            updateSortViewModel()
            titleSort.value = Order.ASCENDING
        }
    }

    fun updateSortViewModel(){
        releaseDateSort.value = sortState.value.ReleaseDate
        ratingSort.value = sortState.value.Rating
        voteSort.value = sortState.value.Vote
        originalTitleSort.value = sortState.value.OriginalTitle
        titleSort.value = sortState.value.Title
    }
    fun isSortStateEmpty(sortState: SortState): Boolean {
        return sortState.ReleaseDate == null &&
                sortState.Rating == null &&
                sortState.Vote == null &&
                sortState.OriginalTitle == null &&
                sortState.Title == null
    }

    fun updateMovieOrder(){
        if (sortState.value.ReleaseDate != null) MovieOrder.ReleaseDate(sortState.value.ReleaseDate!!)
        if (sortState.value.Rating != null) MovieOrder.ReleaseDate(sortState.value.Rating!!)
        if (sortState.value.Vote != null) MovieOrder.ReleaseDate(sortState.value.Vote!!)
        if (sortState.value.OriginalTitle != null) MovieOrder.ReleaseDate(sortState.value.OriginalTitle!!)
        if (sortState.value.Title != null) MovieOrder.ReleaseDate(sortState.value.Title!!)
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