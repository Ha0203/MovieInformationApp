package powerrangers.eivom.feature_movie.ui.movie_management

data class CompanyState(
    val name: String? = null,
    val logoUrl: String? = null,
    val originCountry: String? = null
)

fun CompanyState.isValid(): Boolean = !name.isNullOrBlank()