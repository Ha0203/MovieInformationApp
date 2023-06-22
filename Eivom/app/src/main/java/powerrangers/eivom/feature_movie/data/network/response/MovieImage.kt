package powerrangers.eivom.feature_movie.data.network.response

data class MovieImage(
    val backdrops: List<Backdrop>,
    val id: Int,
    val logos: List<Logo>,
    val posters: List<Poster>
)