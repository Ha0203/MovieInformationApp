package powerrangers.eivom.feature_movie.ui.movie_management

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import powerrangers.eivom.domain.use_case.GoogleAuthClient
import powerrangers.eivom.feature_movie.domain.model.Collection
import powerrangers.eivom.feature_movie.domain.model.Company
import powerrangers.eivom.feature_movie.domain.model.Video
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NewMovieDialogViewModel @Inject constructor(
    private val googleAuthClient: GoogleAuthClient
): ViewModel() {
    var movieKey = mutableStateOf("")
        private set
    var newMovieState = mutableStateOf(SponsoredMovieState())
        private set

    val genreList = TranslateCode.GENRE.toList()
    val languageList = TranslateCode.ISO_639_1.toList()

    // Update new movie state functions
    fun updateNewMovieState(key: String, movieState: SponsoredMovieState) {
        movieKey.value = key
        newMovieState.value = movieState
    }

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

    fun addMovieLandscapeImageUrl(url: String) {
        val urls = newMovieState.value.landscapeImageUrls + url
        newMovieState.value = newMovieState.value.copy(
            landscapeImageUrls = urls
        )
    }

    fun removeMovieLandscapeImageUrl(url: String) {
        val urls = newMovieState.value.landscapeImageUrls - url
        newMovieState.value = newMovieState.value.copy(
            landscapeImageUrls = urls
        )
    }

    fun updateMovieCollection(collection: Collection) {
        newMovieState.value = newMovieState.value.copy(
            collection = collection
        )
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

    fun addMoviePosterUrl(url: String) {
        val urls = newMovieState.value.posterUrls + url
        newMovieState.value = newMovieState.value.copy(
            posterUrls = urls
        )
    }

    fun removeMoviePosterUrl(url: String) {
        val urls = newMovieState.value.posterUrls - url
        newMovieState.value = newMovieState.value.copy(
            posterUrls = urls
        )
    }

    fun addMovieProductionCompany(company: Company) {
        val companies = newMovieState.value.productionCompanies + company
        newMovieState.value = newMovieState.value.copy(
            productionCompanies = companies
        )
    }

    fun removeMovieProductionCompany(company: Company) {
        val companies = newMovieState.value.productionCompanies - company
        newMovieState.value = newMovieState.value.copy(
            productionCompanies = companies
        )
    }

    fun addMovieProductionCountries(country: String) {
        val countries = newMovieState.value.productionCountries + country
        newMovieState.value = newMovieState.value.copy(
            productionCountries = countries
        )
    }

    fun removeMovieProductionCountries(country: String) {
        val countries = newMovieState.value.productionCountries - country
        newMovieState.value = newMovieState.value.copy(
            productionCountries = countries
        )
    }

    fun updateMovieReleaseDate(date: LocalDate) {
        newMovieState.value = newMovieState.value.copy(
            releaseDate = date
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

    fun addMovieLogoUrl(url: String) {
        val urls = newMovieState.value.logoImageUrls + url
        newMovieState.value = newMovieState.value.copy(
            logoImageUrls = urls
        )
    }

    fun removeMovieLogoUrl(url: String) {
        val urls = newMovieState.value.logoImageUrls - url
        newMovieState.value = newMovieState.value.copy(
            logoImageUrls = urls
        )
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