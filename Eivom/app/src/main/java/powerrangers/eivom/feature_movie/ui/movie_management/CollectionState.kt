package powerrangers.eivom.feature_movie.ui.movie_management

data class CollectionState(
    val name: String? = null,
    val posterUrl: String? = null,
    val backdropUrl: String? = null
)

fun CollectionState.isValid(): Boolean = !name.isNullOrBlank()