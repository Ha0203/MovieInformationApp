package powerrangers.eivom.feature_movie.data.network.response

data class ReleaseDates(
    val iso_3166_1: String,
    val release_dates: List<ReleaseDate>
)