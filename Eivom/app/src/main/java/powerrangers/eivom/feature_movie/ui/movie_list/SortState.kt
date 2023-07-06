package powerrangers.eivom.feature_movie.ui.movie_list

data class SortState(
    val isReleaseDate: Boolean = false,
    val isRating: Boolean = false,
    val isVote: Boolean = false,
    val isOriginalTitle: Boolean = false,
    val isTitle: Boolean = false,
)
