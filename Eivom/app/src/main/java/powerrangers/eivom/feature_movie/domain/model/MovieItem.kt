package powerrangers.eivom.feature_movie.domain.model

data class MovieItem(
    val adult: Boolean,
    val landscapeImageUrl: String,
    val collection: Collection,
    val budget: Long,
    val genres: List<String>,
    val homepageUrl: String,
    val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val posterUrl: String,
    val productionCompanies: List<Company>,
    val productionCountries: List<String>,
    val regionReleaseDate: String,
    val revenue: Long,
    val length: Int,
    val spokenLanguages: List<String>,
    val status: String,
    val tagline: String,
    val title: String,
    val videos: List<Video>,
    val voteAverage: Double,
    val voteCount: Int
)