package powerrangers.eivom.feature_movie.domain.model

import powerrangers.eivom.feature_movie.data.utility.LocalMovieItem

data class MovieItem(
    val favorite: Boolean,
    val watched: Boolean,
    val sponsored: Boolean,
    val adult: Boolean,
    val landscapeImageUrl: String,
    val landscapeImageUrls: List<String>,
    val collection: Collection,
    val budget: Long,
    val genres: List<String>,
    val homepageUrl: String,
    val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val posterUrl: String,
    val posterUrls: List<String>,
    val productionCompanies: List<Company>,
    val productionCountries: List<String>,
    val regionReleaseDate: String,
    val revenue: Long,
    val length: Int,
    val logoImageUrls: List<String>,
    val spokenLanguages: List<String>,
    val status: String,
    val tagline: String,
    val title: String,
    val videos: List<Video>,
    val voteAverage: Double,
    val voteCount: Int
)

fun MovieItem.toLocalMovieItem(): LocalMovieItem{
    return LocalMovieItem(
        favorite = this.favorite,
        watched = this.watched,
        sponsored = this.sponsored,
        adult = this.adult,
        budget = this.budget,
        genres = this.genres,
        homepageUrl = this.homepageUrl,
        id = this.id,
        originalLanguage = this.originalLanguage,
        originalTitle = this.originalTitle,
        overview = this.overview,
        productionCompanies = this.productionCompanies.map { company ->
            company.name
        },
        productionCountries = this.productionCountries,
        regionReleaseDate = this.regionReleaseDate,
        revenue = this.revenue,
        length = this.length,
        spokenLanguages = this.spokenLanguages,
        status = this.status,
        tagline = this.tagline,
        title = this.title,
        voteAverage = this.voteAverage,
        voteCount = this.voteCount
    )
}