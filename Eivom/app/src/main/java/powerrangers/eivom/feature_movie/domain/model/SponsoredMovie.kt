package powerrangers.eivom.feature_movie.domain.model

import powerrangers.eivom.feature_movie.domain.utility.TranslateCode

data class SponsoredMovie(
    val id: Int = 0,
    val keyId: String = "",
    val userId: String = "",
    val adult: Boolean = true,
    val landscapeImageUrl: String? = null,
    val landscapeImageUrls: List<String>? = null,
    val collection: Collection? = null,
    val budget: Long? = null,
    val genres: List<Int> = emptyList(),
    val homepageUrl: String? = null,
    val originalLanguage: String = "",
    val originalTitle: String = "",
    val overview: String? = null,
    val posterUrl: String = "",
    val posterUrls: List<String>? = null,
    val productionCompanies: List<Company>? = null,
    val productionCountries: List<String>? = null,
    val releaseDate: String = "",
    val revenue: Long? = null,
    val length: Int? = null,
    val logoImageUrls: List<String>? = null,
    val spokenLanguages: List<String>? = null,
    val status: String? = null,
    val title: String = "",
    val videos: List<Video>? = null
)

fun SponsoredMovie.toMovieListItem(): MovieListItem =
    MovieListItem(
        favorite = false,
        watched = false,
        sponsored = true,
        adult = this.adult,
        landscapeImageUrl = this.landscapeImageUrl ?: "",
        genres = this.genres.map {  genreId ->
            TranslateCode.GENRE[genreId] ?: "Unknown"
        },
        id = this.id,
        originalLanguage = this.originalLanguage,
        originalTitle = this.originalTitle,
        overview = this.overview ?: "",
        posterUrl = this.posterUrl,
        releaseDate = this.releaseDate,
        title = this.title,
        voteAverage = 0.0,
        voteCount = 0
    )

fun SponsoredMovie.toMovieItem(): MovieItem =
    MovieItem(
        editable = false,
        favorite = false,
        watched = false,
        sponsored = true,
        adult = this.adult,
        landscapeImageUrl = this.landscapeImageUrl ?: "",
        landscapeImageUrls = this.landscapeImageUrls ?: emptyList(),
        collection = this.collection ?: Collection(
            landscapeImageUrl = "",
            id = 0,
            name = "",
            posterUrl = ""
        ),
        budget = this.budget ?: 0,
        genres = this.genres.map {  genreId ->
            TranslateCode.GENRE[genreId] ?: "Unknown"
        },
        homepageUrl = this.homepageUrl ?: "",
        id = this.id,
        originalLanguage = this.originalLanguage,
        originalTitle = this.originalTitle,
        overview = this.overview ?: "",
        posterUrl = this.posterUrl,
        posterUrls = emptyList(),
        productionCompanies = this.productionCompanies ?: emptyList(),
        productionCountries = this.productionCountries?.map { countryCode ->
            TranslateCode.ISO_3166_1[countryCode] ?: "Unknown"
        } ?: emptyList(),
        regionReleaseDate = this.releaseDate,
        revenue = this.revenue ?: 0,
        length = this.length ?: 0,
        logoImageUrls = this.logoImageUrls ?: emptyList(),
        spokenLanguages = this.spokenLanguages?.map { languageCode ->
            TranslateCode.ISO_639_1[languageCode] ?: "Unknown"
        } ?: emptyList(),
        status = this.status ?: "",
        tagline = "",
        title = this.title,
        videos = this.videos ?: emptyList(),
        voteAverage = 0.0,
        voteCount = 0,
        note = ""
    )