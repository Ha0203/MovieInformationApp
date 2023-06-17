package powerrangers.eivom.feature_movie.domain.model

data class MovieListItem(
    val adult: Boolean?,
    val landscapeImageUrl: String?,
    val genres: List<String>?,
    val id: Int,
    val originalLanguage: String?,
    val originalTitle: String?,
    val overview: String?,
    val posterUrl: String?,
//    val release_date: String,
    val title: String?,
    val voteAverage: Double?,
    val voteCount: Int?
)
