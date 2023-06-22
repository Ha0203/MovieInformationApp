package powerrangers.eivom.feature_movie.domain.model

data class Video(
    val id: String,
    val country: String,
    val language: String,
    val url: String,
    val name: String,
    val official: Boolean,
    val publishedDateTime: String,
    val site: String,
    val size: Int,
    val type: String
)