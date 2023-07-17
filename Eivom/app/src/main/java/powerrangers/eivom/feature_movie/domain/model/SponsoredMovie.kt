package powerrangers.eivom.feature_movie.domain.model

data class SponsoredMovie(
    val adult: Boolean,
    val landscapeImageUrl: String?,
    val landscapeImageUrls: List<String>?,
    val collection: Collection?,
    val budget: Long?,
    val genres: List<String>,
    val homepageUrl: String?,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String?,
    val posterUrl: String,
    val posterUrls: List<String>?,
    val productionCompanies: List<Company>?,
    val productionCountries: List<String>?,
    val releaseDate: String,
    val revenue: Long?,
    val length: Int?,
    val logoImageUrls: List<String>?,
    val spokenLanguages: List<String>?,
    val status: String?,
    val title: String,
    val videos: List<Video>?
)