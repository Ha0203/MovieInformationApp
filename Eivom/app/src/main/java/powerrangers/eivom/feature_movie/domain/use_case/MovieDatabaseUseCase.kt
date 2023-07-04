package powerrangers.eivom.feature_movie.domain.use_case

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import kotlinx.coroutines.flow.first
import powerrangers.eivom.feature_movie.data.network.response.MovieImage
import powerrangers.eivom.feature_movie.data.network.response.MovieInformation
import powerrangers.eivom.feature_movie.data.network.response.MovieList
import powerrangers.eivom.feature_movie.data.network.response.MovieVideo
import powerrangers.eivom.feature_movie.data.utility.AddingLocalMovieItemException
import powerrangers.eivom.feature_movie.data.utility.DataSourceRelation
import powerrangers.eivom.feature_movie.data.utility.GenreNotFoundException
import powerrangers.eivom.feature_movie.data.utility.LocalMovieItem
import powerrangers.eivom.feature_movie.data.utility.toMovieItem
import powerrangers.eivom.feature_movie.domain.model.Collection
import powerrangers.eivom.feature_movie.domain.model.Company
import powerrangers.eivom.feature_movie.domain.model.MovieItem
import powerrangers.eivom.feature_movie.domain.model.MovieListItem
import powerrangers.eivom.feature_movie.domain.model.Video
import powerrangers.eivom.feature_movie.domain.model.toLocalMovieItem
import powerrangers.eivom.feature_movie.domain.repository.LocalMovieDatabaseRepository
import powerrangers.eivom.feature_movie.domain.repository.MovieDatabaseRepository
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import powerrangers.eivom.feature_movie.domain.utility.Logic
import powerrangers.eivom.feature_movie.domain.utility.MovieFilter
import powerrangers.eivom.feature_movie.domain.utility.Resource
import powerrangers.eivom.feature_movie.domain.utility.ResourceErrorMessage
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import powerrangers.eivom.feature_movie.domain.utility.TrendingTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// If adding use case -> adding use case in app module too
class MovieDatabaseUseCase(
    private val movieDatabaseRepository: MovieDatabaseRepository,
    private val localMovieDatabaseRepository: LocalMovieDatabaseRepository
) {
    private var localMovieMap: Resource<MutableMap<Int, LocalMovieItem>>? = null

    // Handler
    fun handleImageDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }

    fun isFavoriteMovie(movieId: Int): Boolean =
        localMovieMap?.data?.get(movieId)?.favorite ?: false

    fun isWatchedMovie(movieId: Int): Boolean = localMovieMap?.data?.get(movieId)?.watched ?: false
    fun isSponsoredMovie(movieId: Int): Boolean =
        localMovieMap?.data?.get(movieId)?.sponsored ?: false

    // Get movie
    suspend fun getMovieListItemsResource(
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        region: MovieFilter.Region? = MovieFilter.Region(Locale.getDefault().country),
        trending: MovieFilter.Trending? = MovieFilter.Trending(TrendingTime.DAY),
        adultContent: MovieFilter.AdultContent? = null,
        primaryReleaseYear: MovieFilter.ReleaseYear? = null,
        minimumPrimaryReleaseDate: MovieFilter.MinimumReleaseDate? = null,
        maximumPrimaryReleaseDate: MovieFilter.MaximumReleaseDate? = null,
        minimumRating: MovieFilter.MinimumRating? = null,
        maximumRating: MovieFilter.MaximumRating? = null,
        minimumVote: MovieFilter.MinimumVote? = null,
        maximumVote: MovieFilter.MaximumVote? = null,
        genre: MovieFilter.Genre? = null,
        originCountry: MovieFilter.OriginCountry? = null,
        originLanguage: MovieFilter.OriginLanguage? = null,
        minimumLength: MovieFilter.MinimumLength? = null,
        maximumLength: MovieFilter.MaximumLength? = null,
        withoutGenre: MovieFilter.WithoutGenre? = null,
        page: Int,
        landscapeWidth: Int,
        posterWidth: Int,
        dateFormat: DateTimeFormatter
    ): Resource<List<MovieListItem>> {
        val movieList = getMovieListResource(
            apiKey = apiKey,
            region = region,
            trending = trending,
            adultContent = adultContent,
            primaryReleaseYear = primaryReleaseYear,
            minimumPrimaryReleaseDate = minimumPrimaryReleaseDate,
            maximumPrimaryReleaseDate = maximumPrimaryReleaseDate,
            minimumRating = minimumRating,
            maximumRating = maximumRating,
            minimumVote = minimumVote,
            maximumVote = maximumVote,
            genre = genre,
            originCountry = originCountry,
            originLanguage = originLanguage,
            minimumLength = minimumLength,
            maximumLength = maximumLength,
            withoutGenre = withoutGenre,
            page = page
        )
        if (movieList is Resource.Error) {
            return Resource.Error(
                message = movieList.message
                    ?: ResourceErrorMessage.GET_MOVIELIST
            )
        }

        if (localMovieMap == null) {
            localMovieMap = getLocalMovieListItemsAsMap()
        }
        if (localMovieMap is Resource.Error) {
            return Resource.Error(
                message = (localMovieMap as Resource.Error).message
                    ?: ResourceErrorMessage.GET_LOCALMOVIEMAP
            )
        }

        return try {
            Resource.Success(
                data = movieList.data!!.results.map { movie ->
                    MovieListItem(
                        favorite = localMovieMap?.data?.get(movie.id)?.favorite ?: false,
                        watched = localMovieMap?.data?.get(movie.id)?.watched ?: false,
                        sponsored = localMovieMap?.data?.get(movie.id)?.sponsored ?: false,
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

    suspend fun getMovieItemResource(
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
            return try {
                Resource.Success(
                    data = getLocalMovieItem(movieId).data?.toMovieItem()!!
                )
            } catch (e: Exception) {
                return Resource.Error(
                    message = e.message
                        ?: ResourceErrorMessage.GET_MOVIEINFORMATION
                )
            }
        }

        val videos = getMovieVideoResource(
            movieId = movieId,
            apiKey = apiKey,
            region = region
        )
        if (videos is Resource.Error) {
            return try {
                Resource.Success(
                    data = getLocalMovieItem(movieId).data?.toMovieItem()!!
                )
            } catch (e: Exception) {
                return Resource.Error(
                    message = e.message
                        ?: ResourceErrorMessage.GET_MOVIEVIDEO
                )
            }
        }

        val images = getMovieImageResource(
            movieId = movieId,
            apiKey = apiKey,
            region = region
        )
        if (images is Resource.Error) {
            return try {
                Resource.Success(
                    data = getLocalMovieItem(movieId).data?.toMovieItem()!!
                )
            } catch (e: Exception) {
                return Resource.Error(
                    message = e.message
                        ?: ResourceErrorMessage.GET_MOVIEIMAGE
                )
            }
        }

        if (localMovieMap == null) {
            localMovieMap = getLocalMovieListItemsAsMap()
        }
        if (localMovieMap is Resource.Error) {
            return Resource.Error(
                message = (localMovieMap as Resource.Error).message
                    ?: ResourceErrorMessage.GET_LOCALMOVIEMAP
            )
        }

        return try {
            Resource.Success(
                data = MovieItem(
                    favorite = localMovieMap?.data?.get(information.data?.id)?.favorite ?: false,
                    watched = localMovieMap?.data?.get(information.data?.id)?.watched ?: false,
                    sponsored = localMovieMap?.data?.get(information.data?.id)?.sponsored ?: false,
                    adult = information.data?.adult ?: true,
                    landscapeImageUrl = getMovieImageUrl(
                        imageWidth = landscapeWidth,
                        imagePath = information.data?.backdrop_path ?: ""
                    ),
                    landscapeImageUrls = images.data?.backdrops?.map { backdrop ->
                        getMovieImageUrl(
                            imageWidth = landscapeWidth,
                            imagePath = backdrop.file_path
                        )
                    } ?: emptyList(),
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
                    originalLanguage = TranslateCode.ISO_639_1[information.data?.original_language
                        ?: ""] ?: "",
                    originalTitle = information.data?.original_title ?: "",
                    overview = information.data?.overview ?: "",
                    posterUrl = getMovieImageUrl(
                        imageWidth = posterWidth,
                        imagePath = information.data?.poster_path ?: ""
                    ),
                    posterUrls = images.data?.posters?.map { poster ->
                        getMovieImageUrl(
                            imageWidth = posterWidth,
                            imagePath = poster.file_path
                        )
                    } ?: emptyList(),
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
                            originCountry = TranslateCode.ISO_3166_1[company.origin_country] ?: ""
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
                    logoImageUrls = images.data?.logos?.map { logo ->
                        getMovieImageUrl(
                            imageWidth = posterWidth,
                            imagePath = logo.file_path
                        )
                    } ?: emptyList(),
                    spokenLanguages = information.data?.spoken_languages?.map { language ->
                        language.english_name
                    } ?: emptyList(),
                    status = information.data?.status ?: "",
                    tagline = information.data?.tagline ?: "",
                    title = information.data?.title ?: "",
                    videos = videos.data?.results?.mapNotNull { video ->
                        if (video.site == "YouTube") {
                            Video(
                                id = video.id,
                                country = TranslateCode.ISO_3166_1[video.iso_3166_1] ?: "",
                                language = TranslateCode.ISO_639_1[video.iso_639_1] ?: "",
                                url = getYouTubeVideoUrl(video.key),
                                name = video.name,
                                official = video.official,
                                publishedDateTime = video.published_at,
                                site = video.site,
                                size = video.size,
                                type = video.type,
                            )
                        } else
                            null
                    } ?: emptyList(),
                    voteAverage = information.data?.vote_average ?: 0.0,
                    voteCount = information.data?.vote_count ?: 0
                )
            )
        } catch (e: Exception) {
            return try {
                Resource.Success(
                    data = getLocalMovieItem(movieId).data?.toMovieItem()!!
                )
            } catch (e: Exception) {
                return Resource.Error(
                    message = e.message
                        ?: ResourceErrorMessage.CONVERT_MOVIEINFORMATION_TO_MOVIEITEM
                )
            }
        }
    }

    // Add movie
    suspend fun addFavoriteMovie(
        movieListItem: MovieListItem,
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        region: String = Locale.getDefault().country
    ): Boolean {
        try {
            val information = getMovieInformationResource(
                movieId = movieListItem.id,
                apiKey = apiKey,
                region = region
            )
            if (information is Resource.Error) {
                throw AddingLocalMovieItemException(
                    message = information.message
                        ?: ResourceErrorMessage.GET_MOVIEINFORMATION
                )
            }

            val localMovieItem = LocalMovieItem(
                favorite = true,
                watched = movieListItem.watched,
                sponsored = movieListItem.sponsored,
                adult = movieListItem.adult,
                budget = information.data?.budget ?: 0,
                genres = movieListItem.genres,
                homepageUrl = information.data?.homepage ?: "",
                id = movieListItem.id,
                originalLanguage = movieListItem.originalLanguage,
                originalTitle = movieListItem.originalTitle,
                overview = movieListItem.overview,
                productionCompanies = information.data?.production_companies?.map { company ->
                    company.name
                } ?: emptyList(),
                productionCountries = information.data?.production_countries?.map { country ->
                    country.name
                } ?: emptyList(),
                regionReleaseDate = movieListItem.releaseDate,
                revenue = information.data?.revenue ?: 0,
                length = information.data?.runtime ?: 0,
                spokenLanguages = information.data?.spoken_languages?.map { language ->
                    language.english_name
                } ?: emptyList(),
                status = information.data?.status ?: "",
                tagline = information.data?.tagline ?: "",
                title = movieListItem.title,
                voteAverage = movieListItem.voteAverage,
                voteCount = movieListItem.voteCount
            )

            localMovieDatabaseRepository.insertLocalMovieItem(localMovieItem)
            localMovieMap?.data?.put(localMovieItem.id, localMovieItem)

            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun addFavoriteMovie(
        movieItem: MovieItem
    ): Boolean {
        return try {
            val localMovieItem = movieItem.copy(favorite = true).toLocalMovieItem()
            localMovieDatabaseRepository.insertLocalMovieItem(localMovieItem)
            localMovieMap?.data?.put(localMovieItem.id, localMovieItem)

            true
        } catch (e: Exception) {
            false
        }
    }

    // Delete movie
    suspend fun deleteFavoriteMovie(
        movieId: Int
    ): Boolean {
        return try {
            localMovieDatabaseRepository.deleteLocalMovieItemById(movieId)
            localMovieMap?.data?.remove(movieId)

            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun getMovieListResource(
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        region: MovieFilter.Region?,
        trending: MovieFilter.Trending?,
        adultContent: MovieFilter.AdultContent?,
        primaryReleaseYear: MovieFilter.ReleaseYear?,
        minimumPrimaryReleaseDate: MovieFilter.MinimumReleaseDate?,
        maximumPrimaryReleaseDate: MovieFilter.MaximumReleaseDate?,
        minimumRating: MovieFilter.MinimumRating?,
        maximumRating: MovieFilter.MaximumRating?,
        minimumVote: MovieFilter.MinimumVote?,
        maximumVote: MovieFilter.MaximumVote?,
        genre: MovieFilter.Genre?,
        originCountry: MovieFilter.OriginCountry?,
        originLanguage: MovieFilter.OriginLanguage?,
        minimumLength: MovieFilter.MinimumLength?,
        maximumLength: MovieFilter.MaximumLength?,
        withoutGenre: MovieFilter.WithoutGenre?,
        page: Int
    ): Resource<MovieList> {
        return try {
            val formatter = DateTimeFormatter.ofPattern(DefaultValue.DATE_FORMAT)

            if (trending == null) {
                Resource.Success(
                    data = movieDatabaseRepository.getMovieList(
                        apiKey = apiKey,
                        region = region?.region ?: "",
                        includeAdult = adultContent?.isAdult ?: true,
                        primaryReleaseYear = primaryReleaseYear?.year.toString(),
                        minimumPrimaryReleaseDate = (minimumPrimaryReleaseDate?.releaseDate
                            ?: LocalDate.of(1, 1, 1)).format(formatter),
                        maximumPrimaryReleaseDate = (maximumPrimaryReleaseDate?.releaseDate
                            ?: LocalDate.of(9999, 12, 31)).format(formatter),
                        minimumRating = minimumRating?.rating ?: 0f,
                        maximumRating = maximumRating?.rating ?: Float.MAX_VALUE,
                        minimumVote = minimumVote?.voteCount ?: 0,
                        maximumVote = maximumVote?.voteCount ?: Int.MAX_VALUE,
                        genre = if ((genre?.logic ?: Logic.AND) == Logic.AND) (genre?.genres ?: emptyList()).joinToString(
                            separator = ","
                        )
                        else (genre?.genres ?: emptyList()).joinToString(separator = "|"),
                        originCountry = if ((originCountry?.logic ?: Logic.AND) == Logic.AND) (originCountry?.countries ?: emptyList()).joinToString(
                            separator = ","
                        )
                        else (originCountry?.countries ?: emptyList()).joinToString(separator = "|"),
                        originLanguage = if ((originLanguage?.logic ?: Logic.AND) == Logic.AND) (originLanguage?.languages ?: emptyList()).joinToString(
                            separator = ","
                        )
                        else (originLanguage?.languages ?: emptyList()).joinToString(separator = "|"),
                        minimumLength = minimumLength?.movieLength ?: 0,
                        maximumLength = maximumLength?.movieLength ?: Int.MAX_VALUE,
                        withoutGenre = if ((withoutGenre?.logic ?: Logic.AND) == Logic.AND) (withoutGenre?.withoutGenres ?: emptyList()).joinToString(
                            separator = ","
                        )
                        else (withoutGenre?.withoutGenres ?: emptyList()).joinToString(separator = "|"),
                        page = page
                    )
                )
            } else {
                Resource.Success(
                    data = movieDatabaseRepository.getTrendingMovieList(
                        apiKey = apiKey,
                        region = region?.region ?: "",
                        time = if (trending.trendingTime == TrendingTime.DAY) "day" else "week",
                        includeAdult = adultContent?.isAdult ?: true,
                        primaryReleaseYear = primaryReleaseYear?.year.toString(),
                        minimumPrimaryReleaseDate = (minimumPrimaryReleaseDate?.releaseDate
                            ?: LocalDate.of(1, 1, 1)).format(formatter),
                        maximumPrimaryReleaseDate = (maximumPrimaryReleaseDate?.releaseDate
                            ?: LocalDate.of(9999, 12, 31)).format(formatter),
                        minimumRating = minimumRating?.rating ?: 0f,
                        maximumRating = maximumRating?.rating ?: Float.MAX_VALUE,
                        minimumVote = minimumVote?.voteCount ?: 0,
                        maximumVote = maximumVote?.voteCount ?: Int.MAX_VALUE,
                        genre = if ((genre?.logic ?: Logic.AND) == Logic.AND) (genre?.genres ?: emptyList()).joinToString(
                            separator = ","
                        )
                        else (genre?.genres ?: emptyList()).joinToString(separator = "|"),
                        originCountry = if ((originCountry?.logic ?: Logic.AND) == Logic.AND) (originCountry?.countries ?: emptyList()).joinToString(
                            separator = ","
                        )
                        else (originCountry?.countries ?: emptyList()).joinToString(separator = "|"),
                        originLanguage = if ((originLanguage?.logic ?: Logic.AND) == Logic.AND) (originLanguage?.languages ?: emptyList()).joinToString(
                            separator = ","
                        )
                        else (originLanguage?.languages ?: emptyList()).joinToString(separator = "|"),
                        minimumLength = minimumLength?.movieLength ?: 0,
                        maximumLength = maximumLength?.movieLength ?: Int.MAX_VALUE,
                        withoutGenre = if ((withoutGenre?.logic ?: Logic.AND) == Logic.AND) (withoutGenre?.withoutGenres ?: emptyList()).joinToString(
                            separator = ","
                        )
                        else (withoutGenre?.withoutGenres ?: emptyList()).joinToString(separator = "|"),
                        page = page
                    )
                )
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_MOVIELIST)
        }
    }

    private suspend fun getLocalMovieListItems(): Resource<List<LocalMovieItem>> {
        return try {
            Resource.Success(
                data = localMovieDatabaseRepository.getLocalMovieListItems().first()
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_LOCALMOVIELIST)
        }
    }

    private suspend fun getLocalMovieListItemsAsMap(): Resource<MutableMap<Int, LocalMovieItem>> {
        return try {
            Resource.Success(
                data = localMovieDatabaseRepository.getLocalMovieListItemsAsMap().first()
                    .toMutableMap()
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_LOCALMOVIEMAP)
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

    private suspend fun getLocalMovieItem(movieId: Int): Resource<LocalMovieItem> {
        return try {
            Resource.Success(
                data = localMovieDatabaseRepository.getLocalMovieItem(movieId).first()
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_LOCALMOVIEITEM)
        }
    }

    private suspend fun getMovieVideoResource(
        movieId: Int,
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        region: String = Locale.getDefault().country
    ): Resource<MovieVideo> {
        return try {
            Resource.Success(
                data = movieDatabaseRepository.getMovieVideo(
                    movieId = movieId,
                    apiKey = apiKey,
                    region = region
                )
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_MOVIEVIDEO)
        }
    }

    private suspend fun getMovieImageResource(
        movieId: Int,
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        region: String = Locale.getDefault().country
    ): Resource<MovieImage> {
        return try {
            Resource.Success(
                data = movieDatabaseRepository.getMovieImage(
                    movieId = movieId,
                    apiKey = apiKey,
                    region = region
                )
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_MOVIEIMAGE)
        }
    }

    private fun getMovieImageUrl(imageWidth: Int, imagePath: String): String =
        DataSourceRelation.MOVIE_DATABASE_IMAGE_URL + "w${imageWidth}${imagePath}"

    private fun getYouTubeVideoUrl(key: String): String =
        DataSourceRelation.YOUTUBE_VIDEO_URL + key
}