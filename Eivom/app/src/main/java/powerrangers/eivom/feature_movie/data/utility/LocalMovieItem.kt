package powerrangers.eivom.feature_movie.data.utility

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import powerrangers.eivom.feature_movie.domain.model.Collection
import powerrangers.eivom.feature_movie.domain.model.Company
import powerrangers.eivom.feature_movie.domain.model.MovieItem
import powerrangers.eivom.feature_movie.domain.model.MovieListItem

@Entity(tableName = DataSourceRelation.LOCAL_MOVIE_ITEM_NAME)
@TypeConverters(StringListConverter::class)
data class LocalMovieItem(
    val editable: Boolean, // Make true
    val favorite: Boolean,
    val watched: Boolean,
    val sponsored: Boolean,
    // From Adult to voteCount that allow user to change value except the ID
    val adult: Boolean,
    val landscapeImageUrl: String,
    val budget: Long,
    val genres: List<String>,
    val homepageUrl: String,
    @PrimaryKey val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val posterUrl: String,
    val productionCompanies: List<String>,
    val productionCountries: List<String>,
    val regionReleaseDate: String,
    val revenue: Long,
    val length: Int,
    val spokenLanguages: List<String>,
    val status: String,
    val tagline: String,
    val title: String,
    val voteAverage: Double,
    val voteCount: Int,
    val note: String
)

fun LocalMovieItem.toMovieItem(): MovieItem {
    return MovieItem(
        editable = this.editable,
        favorite = this.favorite,
        watched = this.watched,
        sponsored = this.sponsored,
        adult = this.adult,
        landscapeImageUrl = "",
        landscapeImageUrls = emptyList(),
        collection = Collection(
            landscapeImageUrl = "",
            id = 0,
            name = "",
            posterUrl = ""
        ),
        budget = this.budget,
        genres = this.genres,
        homepageUrl = this.homepageUrl,
        id = this.id,
        originalLanguage = this.originalLanguage,
        originalTitle = this.originalTitle,
        overview = this.overview,
        posterUrl = "",
        posterUrls = emptyList(),
        productionCompanies = this.productionCompanies.map {  company ->
            Company(
                id = 0,
                name = company,
                logoImageUrl = "",
                originCountry = ""
            )
        },
        productionCountries = this.productionCountries,
        regionReleaseDate = this.regionReleaseDate,
        revenue = this.revenue,
        length = this.length,
        logoImageUrls = emptyList(),
        spokenLanguages = this.spokenLanguages,
        status = this.status,
        tagline = this.tagline,
        title = this.title,
        videos = emptyList(),
        voteAverage = this.voteAverage,
        voteCount = this.voteCount,
        note = this.note
    )
}

fun LocalMovieItem.toMovieListItem(): MovieListItem {
    return MovieListItem(
        favorite = this.favorite,
        watched = this.watched,
        sponsored = this.sponsored,
        adult = this.adult,
        landscapeImageUrl = this.landscapeImageUrl,
        genres = this.genres,
        id = this.id,
        originalLanguage = this.originalLanguage,
        originalTitle = this.originalTitle,
        overview = this.overview,
        posterUrl = this.posterUrl,
        releaseDate = this.regionReleaseDate,
        title = this.title,
        voteAverage = this.voteAverage,
        voteCount = this.voteCount
    )
}

object StringListConverter {
    @TypeConverter
    @JvmStatic
    fun fromString(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    @JvmStatic
    fun toString(value: List<String>?): String? {
        return value?.joinToString(",")
    }
}
