package powerrangers.eivom.feature_movie.ui.movie_list

import powerrangers.eivom.feature_movie.domain.utility.TrendingTime

data class FilterState(
    val Trending: TrendingTime? = null,
    val Favorite: Boolean? = null,
    val Watched: Boolean? = null,
    val isUpdated: Boolean = false,
    val AdultContentIncluded: Boolean? = null,
    val Region: String? = null,
    val ReleaseYear: Int? = null,
    val MinimumReleaseDate: String? = null,
    val MaximumReleaseDate: String? = null,
    val MinimumRating: Float? = null,
    val MaximumRating: Float? = null,
    val Genre: List<GenreItems>? = null,
    val OriginCountry: List<Countries>? = null,
    val OriginLanguage: List<Language>? = null,
    val MinimumLength: Int? = null,
    val MaximumLength: Int? = null,
    val WithoutGenre: List<GenreItems>? = null,
)
