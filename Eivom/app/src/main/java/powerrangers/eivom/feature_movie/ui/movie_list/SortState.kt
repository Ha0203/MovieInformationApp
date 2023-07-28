package powerrangers.eivom.feature_movie.ui.movie_list

import powerrangers.eivom.feature_movie.domain.utility.Order

data class SortState(
    val ReleaseDate: Order? = null,
    val Rating: Order? = null,
    val Vote: Order? = null,
    val OriginalTitle: Order? = null,
    val Title: Order? = null,
)
