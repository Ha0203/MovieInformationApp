package powerrangers.eivom.feature_movie.ui.movie_list

import java.time.LocalDate

data class NewMovieState(
    val adult: Boolean? = null,
    val landscapeImageUrl: String? = null,
    val budget: Long? = null,
    val genres: List<String> = emptyList(),
    val homepageUrl: String? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val posterUrl: String? = null,
    val regionReleaseDate: LocalDate? = null,
    val revenue: Long? = null,
    val length: Int? = null,
    val spokenLanguages: List<String> = emptyList(),
    val status: String? = null,
    val title: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
)

fun NewMovieState.isValid(): Boolean =
    adult != null && genres.isNotEmpty() && !originalLanguage.isNullOrBlank()
            && !originalTitle.isNullOrBlank() && !posterUrl.isNullOrBlank()
            && regionReleaseDate != null && !title.isNullOrBlank()