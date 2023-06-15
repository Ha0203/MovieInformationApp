package powerrangers.eivom.feature_movie.data.network.response

data class ReleaseDate(
    val certification: String,
    val descriptors: List<String>,
    val iso_639_1: String,
    val note: String,
    val release_date: String,
    val type: Int
)