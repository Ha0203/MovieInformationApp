package powerrangers.eivom.feature_movie.ui.movie_management

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.feature_movie.domain.model.Collection
import powerrangers.eivom.feature_movie.domain.model.Company
import powerrangers.eivom.feature_movie.domain.model.Video
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import powerrangers.eivom.feature_movie.ui.utility.UserPreferences
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NewMovieDialogViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase
): ViewModel() {
    var userPreferences = mutableStateOf(UserPreferences())
        private set

    var movieKey = mutableStateOf("")
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

    val genreList = TranslateCode.GENRE.toList()
    val languageList = TranslateCode.ISO_639_1.toList()
    val countryList = TranslateCode.ISO_3166_1.toList()

    init {
        viewModelScope.launch {
            userPreferences.value =
                UserPreferences(
                    backgroundColor = userPreferencesUseCase.getBackgroundColor(),
                    dateFormat = userPreferencesUseCase.getDateFormat()
                )
        }
    }

    fun clearNewMovieState() {
        movieKey.value = ""
        newMovieState.value = SponsoredMovieState()
        collectionState.value = null
        companyStateList.clear()
        movieLogoUrlList.clear()
        moviePosterUrlList.clear()
        movieBackdropUrlList.clear()
    }

    // Update new movie state functions
    fun updateMovieKey(key: String) {
        movieKey.value = key.uppercase()
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

    fun updateMovieBudget(budget: Long) {
        newMovieState.value = newMovieState.value.copy(
            budget = budget
        )
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

    fun isMovieCompanyListValid(): Boolean {
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

    fun updateMovieRevenue(revenue: Long) {
        newMovieState.value = newMovieState.value.copy(
            revenue = revenue
        )
    }

    fun updateMovieLength(length: String) {
        newMovieState.value = newMovieState.value.copy(
            length = if (length.isNotBlank()) if (length.length <= 6) length.toInt() else 999999 else null
        )
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

    fun addMovieVideo(video: Video) {
        val videos = newMovieState.value.videos + video
        newMovieState.value = newMovieState.value.copy(
            videos = videos
        )
    }

    fun removeMovieVideo(video: Video) {
        val videos = newMovieState.value.videos - video
        newMovieState.value = newMovieState.value.copy(
            videos = videos
        )
    }
}