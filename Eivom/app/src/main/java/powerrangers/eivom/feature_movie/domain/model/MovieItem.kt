package powerrangers.eivom.feature_movie.domain.model

import powerrangers.eivom.feature_movie.data.network.response.BelongsToCollection
import powerrangers.eivom.feature_movie.data.network.response.ProductionCompany
import powerrangers.eivom.feature_movie.data.network.response.SpokenLanguage

data class MovieItem(
    val adult: Boolean,
    val landscapeImageUrl: String,
    val collection: BelongsToCollection,
    val budget: Long,
    val genres: List<String>,
    val homepageUrl: String,
    val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val posterUrl: String,
    val productionCompanies: List<ProductionCompany>,
    val productionCountries: List<String>,
    val regionReleaseDate: String,
//    val releaseDate: List<ReleaseDates>,
    val revenue: Long,
    val length: Int,
    val spokenLanguages: List<SpokenLanguage>,
    val status: String,
    val tagline: String,
    val title: String,
    // trailers
    val voteAverage: Double,
    val voteCount: Int
)
