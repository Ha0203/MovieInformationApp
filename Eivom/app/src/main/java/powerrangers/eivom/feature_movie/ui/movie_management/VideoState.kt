package powerrangers.eivom.feature_movie.ui.movie_management

data class VideoState(
    val name: String? = null,
    val url: String? = null,
    val language: String? = null,
    val country: String? = null,
    val site: String? = null,
    val type: String? = null
)

fun VideoState.isValid(): Boolean = name != null && url != null && site != null