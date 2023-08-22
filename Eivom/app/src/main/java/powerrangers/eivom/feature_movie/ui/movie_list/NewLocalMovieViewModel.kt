package powerrangers.eivom.feature_movie.ui.movie_list

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.feature_movie.domain.model.Collection
import powerrangers.eivom.feature_movie.domain.model.Company
import powerrangers.eivom.feature_movie.domain.model.MovieItem
import powerrangers.eivom.feature_movie.domain.use_case.MovieDatabaseUseCase
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import powerrangers.eivom.ui.utility.UserPreferences
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NewLocalMovieViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase,
    private val movieDatabaseUseCase: MovieDatabaseUseCase
) : ViewModel() {
    var userPreferences = mutableStateOf(UserPreferences())
        private set

    var newMovieState = mutableStateOf(NewMovieState())
        private set

    var companies = mutableStateListOf<String>()
        private set

    val genreList = TranslateCode.GENRE.toList()
    val languageList = TranslateCode.ISO_639_1.toList()
    val countryList = TranslateCode.ISO_3166_1.toList()

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

    fun clearNewMovieState() {
        newMovieState.value = NewMovieState()
        companies.clear()
    }

    fun updateMovieState(movieItem: MovieItem) {
        newMovieState.value = NewMovieState(
            adult = movieItem.adult,
            landscapeImageUrl = movieItem.landscapeImageUrl,
            budget = movieItem.budget,
            genres = movieItem.genres,
            homepageUrl = movieItem.homepageUrl,
            originalLanguage = movieItem.originalLanguage,
            originalTitle = movieItem.originalTitle,
            overview = movieItem.overview,
            posterUrl = movieItem.posterUrl,
            regionReleaseDate = LocalDate.parse(movieItem.regionReleaseDate),
            revenue = movieItem.revenue,
            length = movieItem.length,
            spokenLanguages = movieItem.spokenLanguages,
            status = movieItem.status,
            title = movieItem.title,
            voteAverage = movieItem.voteAverage,
            voteCount = movieItem.voteCount,
        )
    }

    suspend fun saveEditedMovie(movieId: Int): Boolean {
        if (isNewMovieValid()) {
            return movieDatabaseUseCase.updateFavoriteMovie(
                MovieItem(
                    editable = true,
                    favorite = true,
                    watched = false,
                    sponsored = false,
                    adult = newMovieState.value.adult ?: true,
                    landscapeImageUrl = newMovieState.value.landscapeImageUrl ?: "",
                    landscapeImageUrls = emptyList(),
                    collection = Collection(
                        landscapeImageUrl = "",
                        id = 0,
                        name = "",
                        posterUrl = ""
                    ),
                    budget = newMovieState.value.budget ?: 0,
                    genres = newMovieState.value.genres,
                    homepageUrl = newMovieState.value.homepageUrl ?: "",
                    id = movieId,
                    originalLanguage = newMovieState.value.originalLanguage ?: "",
                    originalTitle = newMovieState.value.originalTitle ?: "",
                    overview = newMovieState.value.overview ?: "",
                    posterUrl = newMovieState.value.posterUrl ?: "",
                    posterUrls = emptyList(),
                    productionCompanies = companies.mapNotNull { company ->
                        if (company.isNotBlank()) {
                            Company(
                                id = 0,
                                name = company,
                                logoImageUrl = "",
                                originCountry = ""
                            )
                        } else {
                            null
                        }
                    },
                    productionCountries = emptyList(),
                    regionReleaseDate = newMovieState.value.regionReleaseDate?.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    ) ?: "",
                    revenue = newMovieState.value.revenue ?: 0,
                    length = newMovieState.value.length ?: 0,
                    logoImageUrls = emptyList(),
                    spokenLanguages = newMovieState.value.spokenLanguages,
                    status = newMovieState.value.status ?: "",
                    tagline = "",
                    title = newMovieState.value.title ?: "",
                    videos = emptyList(),
                    voteAverage = 0.0,
                    voteCount = 0,
                    note = ""
                )
            )
        }
        return false
    }

    suspend fun saveNewMovie(): Boolean {
        if (isNewMovieValid()) {
            return movieDatabaseUseCase.addFavoriteMovie(
                MovieItem(
                    editable = true,
                    favorite = true,
                    watched = false,
                    sponsored = false,
                    adult = newMovieState.value.adult ?: true,
                    landscapeImageUrl = newMovieState.value.landscapeImageUrl ?: "",
                    landscapeImageUrls = emptyList(),
                    collection = Collection(
                        landscapeImageUrl = "",
                        id = 0,
                        name = "",
                        posterUrl = ""
                    ),
                    budget = newMovieState.value.budget ?: 0,
                    genres = newMovieState.value.genres,
                    homepageUrl = newMovieState.value.homepageUrl ?: "",
                    id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss"))
                        .toInt(),
                    originalLanguage = newMovieState.value.originalLanguage ?: "",
                    originalTitle = newMovieState.value.originalTitle ?: "",
                    overview = newMovieState.value.overview ?: "",
                    posterUrl = newMovieState.value.posterUrl ?: "",
                    posterUrls = emptyList(),
                    productionCompanies = companies.mapNotNull { company ->
                        if (company.isNotBlank()) {
                            Company(
                                id = 0,
                                name = company,
                                logoImageUrl = "",
                                originCountry = ""
                            )
                        } else {
                            null
                        }
                    },
                    productionCountries = emptyList(),
                    regionReleaseDate = newMovieState.value.regionReleaseDate?.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    ) ?: "",
                    revenue = newMovieState.value.revenue ?: 0,
                    length = newMovieState.value.length ?: 0,
                    logoImageUrls = emptyList(),
                    spokenLanguages = newMovieState.value.spokenLanguages,
                    status = newMovieState.value.status ?: "",
                    tagline = "",
                    title = newMovieState.value.title ?: "",
                    videos = emptyList(),
                    voteAverage = 0.0,
                    voteCount = 0,
                    note = ""
                )
            )
        }
        return false
    }

    fun isCompaniesValid(): Boolean {
        if (companies.isEmpty()) {
            return true
        } else {
            for (company in companies) {
                if (company.isBlank()) {
                    return false
                }
            }
            return true
        }
    }

    fun isNewMovieValid(): Boolean = newMovieState.value.isValid() && isCompaniesValid()

    fun addMovieCompany() {
        companies.add("")
    }

    fun updateMovieCompany(index: Int, name: String) {
        companies[index] = name
    }

    fun removeMovieCompany(index: Int) {
        companies.removeAt(index)
    }

    fun updateAdultOfMovie(isAdult: Boolean) {
        newMovieState.value = newMovieState.value.copy(
            adult = isAdult
        )
    }

    fun updateMovieLandscapeImageUrl(url: String) {
        newMovieState.value = newMovieState.value.copy(
            landscapeImageUrl = url
        )
    }

    fun updateMovieBudget(budget: String) {
        try {
            newMovieState.value = newMovieState.value.copy(
                budget = if (budget.isNotBlank()) budget.toLong() else null
            )
        } catch (e: Exception) {
            return
        }
    }

    fun isGenreSelected(genre: String): Boolean = genre in newMovieState.value.genres

    fun addMovieGenre(genre: String) {
        val genres = newMovieState.value.genres + genre
        newMovieState.value = newMovieState.value.copy(
            genres = genres
        )
    }

    fun removeMovieGenre(genre: String) {
        val genres = newMovieState.value.genres - genre
        newMovieState.value = newMovieState.value.copy(
            genres = genres
        )
    }

    fun updateMovieHomepage(url: String) {
        newMovieState.value = newMovieState.value.copy(
            homepageUrl = url
        )
    }

    fun updateMovieOriginalLanguage(language: String) {
        newMovieState.value = newMovieState.value.copy(
            originalLanguage = language
        )
    }

    fun updateMovieOriginalTitle(originalTitle: String) {
        newMovieState.value = newMovieState.value.copy(
            originalTitle = originalTitle
        )
    }

    fun updateMovieOverview(overview: String) {
        newMovieState.value = newMovieState.value.copy(
            overview = overview
        )
    }

    fun updateMoviePosterUrl(url: String) {
        newMovieState.value = newMovieState.value.copy(
            posterUrl = url
        )
    }

    fun updateMovieReleaseDate(year: Int, month: Int, dayOfMonth: Int) {
        newMovieState.value = newMovieState.value.copy(
            regionReleaseDate = LocalDate.of(year, month, dayOfMonth)
        )
    }

    fun updateMovieRevenue(revenue: String) {
        try {
            newMovieState.value = newMovieState.value.copy(
                revenue = if (revenue.isNotBlank()) revenue.toLong() else null
            )
        } catch (e: Exception) {
            return
        }
    }

    fun updateMovieLength(length: String) {
        try {
            newMovieState.value = newMovieState.value.copy(
                length = if (length.isNotBlank()) length.toInt() else null
            )
        } catch (e: Exception) {
            return
        }
    }

    fun addMovieSpokenLanguage(language: String) {
        val languages = newMovieState.value.spokenLanguages + language
        newMovieState.value = newMovieState.value.copy(
            spokenLanguages = languages
        )
    }

    fun removeMovieSpokenLanguage(language: String) {
        val languages = newMovieState.value.spokenLanguages - language
        newMovieState.value = newMovieState.value.copy(
            spokenLanguages = languages
        )
    }

    fun updateMovieStatus(status: String) {
        newMovieState.value = newMovieState.value.copy(
            status = status
        )
    }

    fun updateMovieTitle(title: String) {
        newMovieState.value = newMovieState.value.copy(
            title = title
        )
    }
}