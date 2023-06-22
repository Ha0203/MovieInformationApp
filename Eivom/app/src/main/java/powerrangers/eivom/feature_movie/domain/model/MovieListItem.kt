package powerrangers.eivom.feature_movie.domain.model

import powerrangers.eivom.feature_movie.data.utility.LocalMovieItem
import powerrangers.eivom.feature_movie.data.utility.toMovieListItem

data class MovieListItem(
    val adult: Boolean,
    val landscapeImageUrl: String,
    val genres: List<String>,
    val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val posterUrl: String,
    val releaseDate: String,
    val title: String,
    val voteAverage: Double,
    val voteCount: Int
)