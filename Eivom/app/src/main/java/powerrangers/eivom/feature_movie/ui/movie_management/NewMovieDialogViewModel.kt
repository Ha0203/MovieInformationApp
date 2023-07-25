package powerrangers.eivom.feature_movie.ui.movie_management

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.domain.use_case.GoogleAuthClient
import powerrangers.eivom.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.feature_movie.domain.use_case.SponsoredMovieFirebaseUseCase
import powerrangers.eivom.feature_movie.domain.utility.MovieKey
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import powerrangers.eivom.ui.utility.UserPreferences
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NewMovieDialogViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase,
    private val googleAuthClient: GoogleAuthClient,
    private val sponsoredMovieFirebaseUseCase: SponsoredMovieFirebaseUseCase
): ViewModel() {
    var userPreferences = mutableStateOf(UserPreferences())
        private set

    private var movieKey = mutableStateOf<MovieKey?>(null)

    var isKeyChecked = mutableStateOf(false)
        private set
    var movieKeyField = mutableStateOf("")
        private set
    var newMovieState = mutableStateOf(SponsoredMovieState())
        private set
    var collectionState = mutableStateOf<CollectionState?>(null)
        private set
    var companyStateList = mutableStateListOf<CompanyState>()
        private set
    var movieLogoUrlList = mutableStateListOf<String>()
        private set
    var moviePosterUrlList = mutableStateListOf<String>()
        private set
    var movieBackdropUrlList = mutableStateListOf<String>()
        private set
    var videoStateList = mutableStateListOf<VideoState>()
        private set

    val genreList = TranslateCode.GENRE.toList()
    val languageList = TranslateCode.ISO_639_1.toList()
    val countryList = TranslateCode.ISO_3166_1.toList()
    val videoType = listOf("Teaser", "Trailer", "Featurette", "Clip", "Behind the Scenes", "Other")

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

    // Interact to database
    suspend fun getMovieKey() {
        movieKey.value = sponsoredMovieFirebaseUseCase.getMovieKey(movieKeyField.value)
        isKeyChecked.value = true
    }

    fun saveSponsoredMovie(): Boolean {
        return try {
            if (!isMovieInformationValid()) {
                return false
            }
            sponsoredMovieFirebaseUseCase.saveSponsoredMovie(
                movieKey = movieKey.value!!,
                movie = newMovieState.value.toSponsoredMovie(
                    userId = googleAuthClient.getSignedInUser().data!!.userId,
                    keyId = movieKey.value!!.id,
                    collectionState = collectionState.value,
                    companyStateList = companyStateList,
                    landscapeImageUrls = movieBackdropUrlList,
                    posterUrls = moviePosterUrlList,
                    logoUrls = movieLogoUrlList,
                    videoStateList = videoStateList
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    fun clearNewMovieState() {
        movieKeyField.value = ""
        newMovieState.value = SponsoredMovieState()
        collectionState.value = null
        companyStateList.clear()
        movieLogoUrlList.clear()
        moviePosterUrlList.clear()
        movieBackdropUrlList.clear()
        videoStateList.clear()
    }

    // Validate functions
    fun isMovieKeyValid(): Boolean = movieKey.value?.addEnabled ?: false

    private fun isMovieCompanyListValid(): Boolean {
        if (companyStateList.isEmpty()) {
            return true
        } else {
            for (company in companyStateList) {
                if (!company.isValid()) {
                    return false
                }
            }
            return true
        }
    }

    private fun isMovieVideoListValid(): Boolean {
        if (videoStateList.isEmpty()) {
            return true
        } else {
            for (video in videoStateList) {
                if (!video.isValid()) {
                    return false
                }
            }
            return true
        }
    }

    fun isMovieInformationValid(): Boolean = isKeyChecked.value && isMovieKeyValid() && newMovieState.value.isValid() && (collectionState.value?.isValid() ?: true) && isMovieCompanyListValid() && isMovieVideoListValid()

    // Update new movie state functions
    fun updateMovieKeyField(key: String) {
        movieKeyField.value = key.uppercase()
        isKeyChecked.value = false
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

    fun addMovieLandscapeImageUrl() {
        movieBackdropUrlList.add("")
    }

    fun updateMovieLandscapeImageUrl(index: Int, url: String) {
        movieBackdropUrlList[index] = url
    }

    fun removeMovieLandscapeImageUrl(index: Int) {
        movieBackdropUrlList.removeAt(index)
    }

    fun addMovieCollection() {
        collectionState.value = CollectionState()
    }

    fun updateMovieCollectionName(name: String) {
        collectionState.value = collectionState.value?.copy(
            name = name
        ) ?: CollectionState(
            name = name
        )
    }

    fun updateMovieCollectionPosterUrl(posterUrl: String) {
        collectionState.value = collectionState.value?.copy(
            posterUrl = posterUrl
        ) ?: CollectionState(
            posterUrl = posterUrl
        )
    }

    fun updateMovieCollectionBackdropUrl(backdropUrl: String) {
        collectionState.value = collectionState.value?.copy(
            backdropUrl = backdropUrl
        ) ?: CollectionState(
            backdropUrl = backdropUrl
        )
    }

    fun deleteMovieCollection() {
        collectionState.value = null
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

    fun isGenreSelected(genre: Int): Boolean = genre in newMovieState.value.genres

    fun addMovieGenre(genre: Int) {
        val genres = newMovieState.value.genres + genre
        newMovieState.value = newMovieState.value.copy(
            genres = genres
        )
    }

    fun removeMovieGenre(genre: Int) {
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

    fun addMoviePosterUrl() {
        moviePosterUrlList.add("")
    }

    fun updateMoviePosterUrl(index: Int, url: String) {
        moviePosterUrlList[index] = url
    }

    fun removeMoviePosterUrl(index: Int) {
        moviePosterUrlList.removeAt(index)
    }

    fun addMovieCompany() {
        companyStateList.add(CompanyState())
    }

    fun updateMovieCompanyName(index: Int, name: String) {
        companyStateList[index] = companyStateList[index].copy(
            name = name
        )
    }

    fun updateMovieCompanyLogoUrl(index: Int, logoUrl: String) {
        companyStateList[index] = companyStateList[index].copy(
            logoUrl = logoUrl
        )
    }

    fun updateMovieCompanyOriginCountry(index: Int, originCountry: String) {
        companyStateList[index] = companyStateList[index].copy(
            originCountry = originCountry
        )
    }

    fun deleteMovieCompany(index: Int) {
        companyStateList.removeAt(index)
    }

    fun addMovieProductionCountry(country: String) {
        val countries = newMovieState.value.productionCountries + country
        newMovieState.value = newMovieState.value.copy(
            productionCountries = countries
        )
    }

    fun removeMovieProductionCountry(country: String) {
        val countries = newMovieState.value.productionCountries - country
        newMovieState.value = newMovieState.value.copy(
            productionCountries = countries
        )
    }

    fun updateMovieReleaseDate(year: Int, month: Int, dayOfMonth: Int) {
        newMovieState.value = newMovieState.value.copy(
            releaseDate = LocalDate.of(year, month, dayOfMonth)
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

    fun addMovieLogoUrl() {
        movieLogoUrlList.add("")
    }

    fun updateMovieLogoUrl(index: Int, url: String) {
        movieLogoUrlList[index] = url
    }

    fun removeMovieLogoUrl(index: Int) {
        movieLogoUrlList.removeAt(index)
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

    fun addMovieVideo() {
        videoStateList.add(VideoState())
    }

    fun updateMovieVideoName(index: Int, name: String) {
        videoStateList[index] = videoStateList[index].copy(
            name = name
        )
    }

    fun updateMovieVideoUrl(index: Int, url: String) {
        videoStateList[index] = videoStateList[index].copy(
            url = url
        )
    }

    fun updateMovieVideoLanguage(index: Int, language: String) {
        videoStateList[index] = videoStateList[index].copy(
            language = language
        )
    }

    fun updateMovieVideoCountry(index: Int, country: String) {
        videoStateList[index] = videoStateList[index].copy(
            country = country
        )
    }

    fun updateMovieVideoSite(index: Int, site: String) {
        videoStateList[index] = videoStateList[index].copy(
            site = site
        )
    }

    fun updateMovieVideoType(index: Int, type: String) {
        videoStateList[index] = videoStateList[index].copy(
            type = type
        )
    }

    fun removeMovieVideo(index: Int) {
        videoStateList.removeAt(index)
    }
}