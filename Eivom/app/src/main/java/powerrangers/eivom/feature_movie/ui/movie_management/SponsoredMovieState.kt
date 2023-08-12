package powerrangers.eivom.feature_movie.ui.movie_management

import powerrangers.eivom.feature_movie.domain.model.Collection
import powerrangers.eivom.feature_movie.domain.model.Company
import powerrangers.eivom.feature_movie.domain.model.SponsoredMovie
import powerrangers.eivom.feature_movie.domain.model.Video
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SponsoredMovieState(
    val adult: Boolean? = null,
    val landscapeImageUrl: String? = null,
    val budget: Long? = null,
    val genres: List<Int> = emptyList(),
    val homepageUrl: String? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val posterUrl: String? = null,
    val productionCountries: List<String> = emptyList(),
    val releaseDate: LocalDate? = null,
    val revenue: Long? = null,
    val length: Int? = null,
    val spokenLanguages: List<String> = emptyList(),
    val status: String? = null,
    val title: String? = null
)

fun SponsoredMovieState.isValid(): Boolean =
    adult != null && genres.isNotEmpty() && !originalLanguage.isNullOrBlank()
            && !originalTitle.isNullOrBlank() && !posterUrl.isNullOrBlank()
            && releaseDate != null && !title.isNullOrBlank()

fun SponsoredMovieState.toSponsoredMovie(
    userId: String,
    keyId: String,
    collectionState: CollectionState?,
    companyStateList: List<CompanyState>,
    landscapeImageUrls: List<String>,
    posterUrls: List<String>,
    logoUrls: List<String>,
    videoStateList: List<VideoState>
): SponsoredMovie {
    val defaultFormatter = DateTimeFormatter.ofPattern(DefaultValue.DATE_FORMAT)
    return SponsoredMovie(
        id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss")).toInt(),
        keyId = keyId,
        userId = userId,
        adult = this.adult!!,
        landscapeImageUrl = this.landscapeImageUrl,
        landscapeImageUrls = landscapeImageUrls,
        collection = if (collectionState == null) null else Collection(
            id = -1,
            landscapeImageUrl = collectionState.backdropUrl ?: "",
            name = collectionState.name!!,
            posterUrl = collectionState.posterUrl ?: ""
        ),
        budget = this.budget,
        genres = this.genres,
        homepageUrl = this.homepageUrl,
        originalLanguage = this.originalLanguage!!,
        originalTitle = this.originalTitle!!,
        overview = this.overview,
        posterUrl = this.posterUrl!!,
        posterUrls = posterUrls,
        productionCompanies = companyStateList.map { companyState ->
            Company(
                id = -1,
                logoImageUrl = companyState.logoUrl ?: "",
                name = companyState.name!!,
                originCountry = companyState.originCountry ?: ""
            )
        },
        productionCountries = this.productionCountries,
        releaseDate = this.releaseDate!!.format(defaultFormatter),
        revenue = this.revenue,
        length = this.length,
        logoImageUrls = logoUrls,
        spokenLanguages = this.spokenLanguages,
        status = this.status,
        title = this.title!!,
        videos = videoStateList.map { videoState ->
            Video(
                id = "",
                country = videoState.country ?: "",
                language = videoState.language ?: "",
                url = videoState.url!!,
                name = videoState.name!!,
                official = true,
                publishedDateTime = "",
                site = videoState.site!!,
                size = -1,
                type = videoState.type ?: ""
            )
        }
    )
}