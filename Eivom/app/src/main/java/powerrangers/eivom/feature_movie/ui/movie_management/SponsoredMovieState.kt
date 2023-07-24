package powerrangers.eivom.feature_movie.ui.movie_management

import java.time.LocalDate

data class SponsoredMovieState(
    val adult: Boolean? = null,
    val landscapeImageUrl: String? = null,
    val budget: Long? = null,
    val genres: List<Int> = emptyList(),
    val homepageUrl: String? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val posterUrl: String? = null,
    val productionCountries: List<String> = emptyList(),
    val releaseDate: LocalDate? = null,
    val revenue: Long? = null,
    val length: Int? = null,
    val spokenLanguages: List<String> = emptyList(),
    val status: String? = null,
    val title: String? = null
)

fun SponsoredMovieState.isValid(): Boolean =
    adult != null && genres.isNotEmpty() && !originalLanguage.isNullOrBlank()
            && !originalTitle.isNullOrBlank() && !posterUrl.isNullOrBlank()
            && releaseDate != null && !title.isNullOrBlank()