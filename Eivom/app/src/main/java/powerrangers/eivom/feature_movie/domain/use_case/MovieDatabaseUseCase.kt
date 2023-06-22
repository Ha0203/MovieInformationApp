package powerrangers.eivom.feature_movie.domain.use_case

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import powerrangers.eivom.feature_movie.data.network.response.MovieInformation
import powerrangers.eivom.feature_movie.data.network.response.MovieList
import powerrangers.eivom.feature_movie.data.network.response.MovieVideo
import powerrangers.eivom.feature_movie.data.utility.DataSourceRelation
import powerrangers.eivom.feature_movie.data.utility.GenreNotFoundException
import powerrangers.eivom.feature_movie.domain.model.Collection
import powerrangers.eivom.feature_movie.domain.model.Company
import powerrangers.eivom.feature_movie.domain.model.MovieItem
import powerrangers.eivom.feature_movie.domain.model.MovieListItem
import powerrangers.eivom.feature_movie.domain.repository.MovieDatabaseRepository
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import powerrangers.eivom.feature_movie.domain.utility.Resource
import powerrangers.eivom.feature_movie.domain.utility.ResourceErrorMessage
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// If adding use case -> adding use case in app module too
data class MovieDatabaseUseCase(
    // Handler use case
    val handleImageDominantColor: HandleImageDominantColor,

    // Get use case
    val getMovieListItemsResource: GetMovieListItemsResource,
    val getMovieItemResource: GetMovieItemResource
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

// Get use case
class GetMovieListItemsResource(
    private val movieDatabaseRepository: MovieDatabaseRepository
) {
    suspend operator fun invoke(
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        region: String = Locale.getDefault().country,
        page: Int,
        landscapeWidth: Int,
        posterWidth: Int,
        dateFormat: DateTimeFormatter
    ): Resource<List<MovieListItem>> {
        val movieList = getMovieListResource(
            apiKey = apiKey,
            region = region,
            page = page
        )
        if (movieList is Resource.Error) {
            return Resource.Error(
                message = movieList.message
                    ?: ResourceErrorMessage.GET_MOVIELIST
            )
        }

        val getMovieImageUrl = GetMovieImageUrl()

        return try {
            Resource.Success(
                data = movieList.data!!.results.map { movie ->
                    MovieListItem(
                        adult = try {
                            movie.adult
                        } catch (e: Exception) {
                            true
                        },
                        landscapeImageUrl = try {
                            getMovieImageUrl(
                                imageWidth = landscapeWidth,
                                imagePath = movie.backdrop_path
                            )
                        } catch (e: Exception) {
                            ""
                        },
                        genres = movie.genre_ids.map { genreId ->
                            TranslateCode.GENRE[genreId]
                                ?: throw GenreNotFoundException("Genre not found error")
                        },
                        id = movie.id,
                        originalLanguage = TranslateCode.ISO_639_1[movie.original_language]
                            ?: "",
                        originalTitle = try {
                            movie.original_title
                        } catch (e: Exception) {
                            ""
                        },
                        overview = try {
                            movie.overview
                        } catch (e: Exception) {
                            ""
                        },
                        posterUrl = try {
                            getMovieImageUrl(
                                imageWidth = posterWidth,
                                imagePath = movie.poster_path
                            )
                        } catch (e: Exception) {
                            ""
                        },
                        releaseDate = try {
                            LocalDate.parse(
                                movie.release_date,
                                DateTimeFormatter.ofPattern(DefaultValue.DATE_FORMAT)
                            ).format(dateFormat)
                        } catch (e: Exception) {
                            ""
                        },
                        title = try {
                            movie.title
                        } catch (e: Exception) {
                            ""
                        },
                        voteAverage = try {
                            movie.vote_average
                        } catch (e: Exception) {
                            0.0
                        },
                        voteCount = try {
                            movie.vote_count
                        } catch (e: Exception) {
                            0
                        }
                    )
                }
            )
        } catch (e: Exception) {
            return Resource.Error(
                message = e.message
                    ?: ResourceErrorMessage.CONVERT_MOVIELIST_TO_MOVIELISTITEMS
            )
        }
    }

    private suspend fun getMovieListResource(
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        region: String = Locale.getDefault().country,
        page: Int
    ): Resource<MovieList> {
        return try {
            Resource.Success(
                data = movieDatabaseRepository.getMovieList(
                    apiKey = apiKey,
                    region = region,
                    page = page
                )
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_MOVIELIST)
        }
    }
}

class GetMovieItemResource(
    private val movieDatabaseRepository: MovieDatabaseRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        region: String = Locale.getDefault().country,
        landscapeWidth: Int,
        posterWidth: Int,
        dateFormat: DateTimeFormatter
    ): Resource<MovieItem> {
        val information = getMovieInformationResource(
            movieId = movieId,
            apiKey = apiKey,
            region = region
        )
        if (information is Resource.Error) {
            return Resource.Error(
                message = information.message
                    ?: ResourceErrorMessage.GET_MOVIEINFORMATION
            )
        }

        val videos = getMovieVideoResource(
            movieId = movieId,
            apiKey = apiKey,
            region = region
        )
        if (videos is Resource.Error) {
            return Resource.Error(
                message = videos.message
                    ?: ResourceErrorMessage.GET_MOVIETRAILER
            )
        }

        val getMovieImageUrl = GetMovieImageUrl()
        val getYouTubeVideoUrl = GetYouTubeVideoUrl()

        return try {
            Resource.Success(
                data = MovieItem(
                    adult = information.data?.adult ?: true,
                    landscapeImageUrl = getMovieImageUrl(
                        imageWidth = landscapeWidth,
                        imagePath = information.data?.backdrop_path ?: ""
                    ),
                    collection = Collection(
                        landscapeImageUrl = if (information.data?.belongs_to_collection?.backdrop_path != null) {
                            getMovieImageUrl(
                                imageWidth = landscapeWidth,
                                imagePath = information.data.belongs_to_collection.backdrop_path
                            )
                        } else {
                            ""
                        },
                        id = information.data?.belongs_to_collection?.id ?: 0,
                        name = information.data?.belongs_to_collection?.name ?: "",
                        posterUrl = if (information.data?.belongs_to_collection?.poster_path != null) {
                            getMovieImageUrl(
                                imageWidth = posterWidth,
                                imagePath = information.data.belongs_to_collection.poster_path
                            )
                        } else {
                            ""
                        },
                    ),
                    budget = information.data?.budget ?: 0,
                    genres = information.data?.genres?.map { genre ->
                        genre.name
                    } ?: emptyList(),
                    homepageUrl = information.data?.homepage ?: "",
                    id = information.data?.id ?: 0,
                    originalLanguage = information.data?.original_language ?: "",
                    originalTitle = information.data?.original_title ?: "",
                    overview = information.data?.overview ?: "",
                    posterUrl = getMovieImageUrl(
                        imageWidth = posterWidth,
                        imagePath = information.data?.poster_path ?: ""
                    ),
                    productionCompanies = information.data?.production_companies?.map { company ->
                        Company(
                            id = company.id,
                            logoImageUrl = try {
                                getMovieImageUrl(
                                    imageWidth = posterWidth,
                                    imagePath = company.logo_path
                                )
                            } catch (e: Exception) {
                                ""
                            },
                            name = company.name,
                            originCountry = TranslateCode.ISO_639_1[company.origin_country] ?: ""
                        )
                    } ?: emptyList(),
                    productionCountries = information.data?.production_countries?.map { country ->
                        country.name
                    } ?: emptyList(),
                    regionReleaseDate = try {
                        LocalDate.parse(
                            information.data!!.release_date,
                            DateTimeFormatter.ofPattern(DefaultValue.DATE_FORMAT)
                        ).format(dateFormat)
                    } catch (e: Exception) {
                        ""
                    },
                    revenue = information.data?.revenue ?: 0,
                    length = information.data?.runtime ?: 0,
                    spokenLanguages = information.data?.spoken_languages?.map { language ->
                        language.english_name
                    } ?: emptyList(),
                    status = information.data?.status ?: "",
                    tagline = information.data?.tagline ?: "",
                    title = information.data?.title ?: "",
                    videoUrls = videos.data?.results?.mapNotNull { video ->
                        if (video.site == "YouTube")
                            getYouTubeVideoUrl(video.key)
                        else
                            null
                    } ?: emptyList(),
                    voteAverage = information.data?.vote_average ?: 0.0,
                    voteCount = information.data?.vote_count ?: 0
                )
            )
        } catch (e: Exception) {
            return Resource.Error(
                message = e.message
                    ?: ResourceErrorMessage.CONVERT_MOVIEINFORMATION_TO_MOVIEITEM
            )
        }
    }

    private suspend fun getMovieInformationResource(
        movieId: Int,
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        region: String = Locale.getDefault().country
    ): Resource<MovieInformation> {
        return try {
            Resource.Success(
                data = movieDatabaseRepository.getMovieInformation(
                    movieId = movieId,
                    apiKey = apiKey,
                    region = region
                )
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_MOVIEINFORMATION)
        }
    }

    private suspend fun getMovieVideoResource(
        movieId: Int,
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        region: String = Locale.getDefault().country
    ): Resource<MovieVideo> {
        return try {
            Resource.Success(
                data = movieDatabaseRepository.getMovieTrailer(
                    movieId = movieId,
                    apiKey = apiKey,
                    region = region
                )
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_MOVIETRAILER)
        }
    }
}

private class GetMovieImageUrl {
    operator fun invoke(imageWidth: Int, imagePath: String): String =
        DataSourceRelation.MOVIE_DATABASE_IMAGE_URL + "w${imageWidth}${imagePath}"
}

private class GetYouTubeVideoUrl {
    operator fun invoke(key: String): String =
        DataSourceRelation.YOUTUBE_VIDEO_URL + key
}