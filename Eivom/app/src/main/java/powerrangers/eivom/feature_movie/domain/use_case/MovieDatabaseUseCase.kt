package powerrangers.eivom.feature_movie.domain.use_case

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import powerrangers.eivom.feature_movie.data.network.response.BelongsToCollection
import powerrangers.eivom.feature_movie.data.network.response.MovieInformation
import powerrangers.eivom.feature_movie.data.network.response.MovieList
import powerrangers.eivom.feature_movie.data.utility.DataSourceRelation
import powerrangers.eivom.feature_movie.domain.model.MovieItem
import powerrangers.eivom.feature_movie.domain.model.MovieListItem
import powerrangers.eivom.feature_movie.domain.repository.MovieDatabaseRepository
import powerrangers.eivom.feature_movie.domain.utility.Resource

// If adding use case -> adding use case in app module too
data class MovieDatabaseUseCase(
    // Handler use case
    val handleImageDominantColor: HandleImageDominantColor,
    val convertMovieListResourceToMovieListItemsResource: ConvertMovieListResourceToMovieListItemsResource,
    val convertMovieInformationResourceToMovieItemResource: ConvertMovieInformationResourceToMovieItemResource,

    // Get use case
    val getMovieList: GetMovieList,
    val getMovieInformation: GetMovieInformation,
    val getMovieImageUrl: GetMovieImageUrl
)

// Handler use case
class HandleImageDominantColor {
    operator fun invoke(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}

class ConvertMovieListResourceToMovieListItemsResource {
    operator fun invoke(
        movieList: Resource<MovieList>,
        movieImageWidth: Int
    ): Resource<List<MovieListItem>> {
        when (movieList) {
            is Resource.Success -> {
                return try {
                    Resource.Success(
                        data = movieList.data!!.results.map { movie ->
                            MovieListItem(
                                movieId = movie.id,
                                movieName = movie.title,
                                imageUrl = GetMovieImageUrl()(
                                    imageWidth = movieImageWidth,
                                    imagePath = movie.poster_path
                                )
                            )
                        }
                    )
                }
                catch (e: Exception) {
                    return Resource.Error(message = "Can't convert Resource<MovieList> to Resource<MovieListItems>")
                }
            }

            else -> {
                return Resource.Error(message = "Can't convert Resource<MovieList> to Resource<MovieListItems>")
            }
        }
    }
}

class ConvertMovieInformationResourceToMovieItemResource {
    operator fun invoke(
        movieInformation: Resource<MovieInformation>,
        landscapeWidth: Int,
        posterWidth: Int
    ): Resource<MovieItem> {
        when (movieInformation) {
            is Resource.Success -> {
                return try {
                    Resource.Success(
                        data = MovieItem(
                            adult = movieInformation.data?.adult ?: false,
                            landscapeImageUrl = GetMovieImageUrl()(
                                imageWidth = landscapeWidth,
                                imagePath = movieInformation.data?.backdrop_path ?: ""
                            ),
                            collection = movieInformation.data?.belongs_to_collection
                                ?: BelongsToCollection("", 0, "", ""),
                            budget = movieInformation.data?.budget ?: 0,
                            genres = movieInformation.data?.genres?.map { genre ->
                                genre.name
                            } ?: listOf(),
                            homepageUrl = movieInformation.data?.homepage ?: "",
                            id = movieInformation.data?.id ?: 0,
                            originalLanguage = movieInformation.data?.original_language ?: "",
                            originalTitle = movieInformation.data?.original_title ?: "",
                            overview = movieInformation.data?.overview ?: "",
                            posterUrl = GetMovieImageUrl()(
                                imageWidth = posterWidth,
                                imagePath = movieInformation.data?.poster_path ?: ""
                            ),
                            productionCompanies = movieInformation.data?.production_companies
                                ?: listOf(),
                            productionCountries = movieInformation.data?.production_countries?.map { country ->
                                country.name
                            } ?: listOf(),
                            revenue = movieInformation.data?.revenue ?: 0,
                            length = movieInformation.data?.runtime ?: 0,
                            spokenLanguages = movieInformation.data?.spoken_languages ?: listOf(),
                            status = movieInformation.data?.status ?: "",
                            tagline = movieInformation.data?.tagline ?: "",
                            title = movieInformation.data?.title ?: "",
                            voteAverage = movieInformation.data?.vote_average ?: 0.0,
                            voteCount = movieInformation.data?.vote_count ?: 0
                        )
                    )
                } catch (e: Exception) {
                    return Resource.Error(message = "Can't convert Resource<MovieInformation> to Resource<MovieItem>")
                }
            }

            else -> {
                return Resource.Error(message = "Can't convert Resource<MovieInformation> to Resource<MovieItem>")
            }
        }
    }
}

// Get use case
class GetMovieList(
    private val movieDatabaseRepository: MovieDatabaseRepository
) {
    suspend operator fun invoke(
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        page: Int
    ): Resource<MovieList> {
        return try {
            Resource.Success(
                data = movieDatabaseRepository.getMovieList(
                    apiKey = apiKey,
                    page = page
                )
            )
        } catch (e: Exception) {
            Resource.Error(message = "Can't get movie list")
        }
    }
}

class GetMovieInformation(
    private val movieDatabaseRepository: MovieDatabaseRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        apiKey: String = DataSourceRelation.TMDB_API_KEY
    ): Resource<MovieInformation> {
        return try {
            Resource.Success(
                data = movieDatabaseRepository.getMovieInformation(
                    movieId = movieId,
                    apiKey = apiKey
                )
            )
        } catch (e: Exception) {
            Resource.Error(message = "Can't get movie information")
        }
    }
}

class GetMovieImageUrl {
    operator fun invoke(imageWidth: Int, imagePath: String): String =
        DataSourceRelation.MOVIE_DATABASE_IMAGE_URL + "w${imageWidth}${imagePath}"
}