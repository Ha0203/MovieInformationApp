package powerrangers.eivom.feature_movie.ui.movie_management

import powerrangers.eivom.feature_movie.domain.model.Collection
import powerrangers.eivom.feature_movie.domain.model.Company
import powerrangers.eivom.feature_movie.domain.model.Video
import java.time.LocalDate

data class SponsoredMovieState(
    val adult: Boolean? = null,
    val landscapeImageUrl: String? = null,
    val landscapeImageUrls: List<String> = emptyList(),
    val collection: Collection? = null,
    val budget: Long? = null,
    val genres: List<String> = emptyList(),
    val homepageUrl: String? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val posterUrl: String? = null,
    val posterUrls: List<String> = emptyList(),
    val productionCompanies: List<Company> = emptyList(),
    val productionCountries: List<String> = emptyList(),
    val releaseDate: LocalDate? = null,
    val revenue: Long? = null,
    val length: Int? = null,
    val logoImageUrls: List<String> = emptyList(),
    val spokenLanguages: List<String> = emptyList(),
    val status: String? = null,
    val title: String? = null,
    val videos: List<Video> = emptyList()
)

fun SponsoredMovieState.isValid(): Boolean =
    adult != null && genres.isNotEmpty() && !originalLanguage.isNullOrBlank()
            && !originalTitle.isNullOrBlank() && !posterUrl.isNullOrBlank()
            && releaseDate != null && !title.isNullOrBlank()