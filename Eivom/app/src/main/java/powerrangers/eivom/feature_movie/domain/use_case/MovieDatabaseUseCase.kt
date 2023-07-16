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
import powerrangers.eivom.feature_movie.data.utility.toMovieListItem
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
import powerrangers.eivom.feature_movie.domain.utility.MovieOrder
import powerrangers.eivom.feature_movie.domain.utility.Order
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.domain.utility.ResourceErrorMessage
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
        searchQuery: String? = null,    // No filter, no sort
        trending: MovieFilter.Trending? = MovieFilter.Trending(TrendingTime.DAY),   // No filter, no sort
        favorite: MovieFilter.Favorite? = null, // No region filter
        watched: MovieFilter.Watched? = null,   // No region filter
        region: MovieFilter.Region? = MovieFilter.Region(Locale.getDefault().country),  // No favorite and watched filter
        adultContentIncluded: MovieFilter.AdultContentIncluded? = null,
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
        sortBy: MovieOrder? = null,
        page: Int,
        landscapeWidth: Int,
        posterWidth: Int,
        dateFormat: DateTimeFormatter
    ): Resource<List<MovieListItem>> {
        if (localMovieMap == null) {
            localMovieMap = getLocalMovieListItemsAsMap()
        }
        if (localMovieMap is Resource.Error) {
            return Resource.Error(
                message = (localMovieMap as Resource.Error).message
                    ?: ResourceErrorMessage.GET_LOCALMOVIEMAP
            )
        }

        if (favorite?.isFavorite != true && watched?.isWatched != true) {
            if (!searchQuery.isNullOrBlank()) {
                val movieList = searchMovieListResource(
                    apiKey = apiKey,
                    query = searchQuery,
                    region = region,
                    page = page
                )
                if (movieList is Resource.Error) {
                    return Resource.Error(
                        message = movieList.message
                            ?: ResourceErrorMessage.GET_MOVIELIST
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

            val movieList = getMovieListResource(
                apiKey = apiKey,
                region = region,
                trending = trending,
                adultContentIncluded = adultContentIncluded,
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
                sortBy = sortBy,
                page = page
            )
            if (movieList is Resource.Error) {
                return Resource.Error(
                    message = movieList.message
                        ?: ResourceErrorMessage.GET_MOVIELIST
                )
            }

            return try {
                if (favorite == null && watched == null) {
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
                } else if (favorite?.isFavorite == false && watched == null) {
                    Resource.Success(
                        data = movieList.data!!.results.mapNotNull { movie ->
                            if (localMovieMap?.data?.get(movie.id)?.favorite == true) {
                                null
                            } else {
                                MovieListItem(
                                    favorite = localMovieMap?.data?.get(movie.id)?.favorite
                                        ?: false,
                                    watched = localMovieMap?.data?.get(movie.id)?.watched ?: false,
                                    sponsored = localMovieMap?.data?.get(movie.id)?.sponsored
                                        ?: false,
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
                        }
                    )
                } else if (watched?.isWatched == false && favorite == null) {
                    Resource.Success(
                        data = movieList.data!!.results.mapNotNull { movie ->
                            if (localMovieMap?.data?.get(movie.id)?.watched == true) {
                                null
                            } else {
                                MovieListItem(
                                    favorite = localMovieMap?.data?.get(movie.id)?.favorite
                                        ?: false,
                                    watched = localMovieMap?.data?.get(movie.id)?.watched ?: false,
                                    sponsored = localMovieMap?.data?.get(movie.id)?.sponsored
                                        ?: false,
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
                        }
                    )
                } else {
                    Resource.Success(
                        data = movieList.data!!.results.mapNotNull { movie ->
                            if (
                                localMovieMap?.data?.get(movie.id)?.favorite == true &&
                                localMovieMap?.data?.get(movie.id)?.watched == true
                            ) {
                                null
                            } else {
                                MovieListItem(
                                    favorite = localMovieMap?.data?.get(movie.id)?.favorite
                                        ?: false,
                                    watched = localMovieMap?.data?.get(movie.id)?.watched ?: false,
                                    sponsored = localMovieMap?.data?.get(movie.id)?.sponsored
                                        ?: false,
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
                        }
                    )
                }
            } catch (e: Exception) {
                return Resource.Error(
                    message = e.message
                        ?: ResourceErrorMessage.CONVERT_MOVIELIST_TO_MOVIELISTITEMS
                )
            }
        } else {
            val localMovieListItems = getLocalMovieListItems(
                searchQuery = searchQuery,    // No filter, no sort
                favorite = favorite, // No region filter
                watched = watched,   // No region filter
                adultContentIncluded = adultContentIncluded,
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
                sortBy = sortBy,
            )
            if (localMovieListItems is Resource.Error) {
                return Resource.Error(
                    message = localMovieListItems.message
                        ?: ResourceErrorMessage.GET_LOCALMOVIELIST
                )
            }

            return try {
                Resource.Success(
                    data = localMovieListItems.data!!.map { localMovieItem ->
                        localMovieItem.toMovieListItem()
                    }
                )
            } catch (e: Exception) {
                return Resource.Error(
                    message = e.message
                        ?: ResourceErrorMessage.CONVERT_LOCALMOVIEITEMS_TO_MOVIELISTITEMS
                )
            }
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
        if (localMovieMap == null) {
            localMovieMap = getLocalMovieListItemsAsMap()
        }
        if (localMovieMap is Resource.Error) {
            return Resource.Error(
                message = (localMovieMap as Resource.Error).message
                    ?: ResourceErrorMessage.GET_LOCALMOVIEMAP
            )
        }

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

        return try {
            Resource.Success(
                data = MovieItem(
                    editable = false,
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
                    voteCount = information.data?.vote_count ?: 0,
                    note = localMovieMap?.data?.get(information.data?.id)?.note ?: ""
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
                editable = false,
                favorite = true,
                watched = movieListItem.watched,
                sponsored = movieListItem.sponsored,
                adult = movieListItem.adult,
                landscapeImageUrl = movieListItem.landscapeImageUrl,
                budget = information.data?.budget ?: 0,
                genres = movieListItem.genres,
                homepageUrl = information.data?.homepage ?: "",
                id = movieListItem.id,
                originalLanguage = movieListItem.originalLanguage,
                originalTitle = movieListItem.originalTitle,
                overview = movieListItem.overview,
                posterUrl = movieListItem.posterUrl,
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
                voteCount = movieListItem.voteCount,
                note = ""
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
        adultContentIncluded: MovieFilter.AdultContentIncluded?,
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
        sortBy: MovieOrder?,
        page: Int
    ): Resource<MovieList> {
        return try {
            val formatter = DateTimeFormatter.ofPattern(DefaultValue.DATE_FORMAT)

            if (trending == null) {
                Resource.Success(
                    data = movieDatabaseRepository.getMovieList(
                        apiKey = apiKey,
                        region = region?.region,
                        includeAdult = adultContentIncluded?.isIncluded,
                        primaryReleaseYear = primaryReleaseYear?.year?.toString(),
                        minimumPrimaryReleaseDate = (minimumPrimaryReleaseDate?.releaseDate)?.format(
                            formatter
                        ),
                        maximumPrimaryReleaseDate = (maximumPrimaryReleaseDate?.releaseDate)?.format(
                            formatter
                        ),
                        minimumRating = minimumRating?.rating,
                        maximumRating = maximumRating?.rating,
                        minimumVote = minimumVote?.voteCount,
                        maximumVote = maximumVote?.voteCount,
                        genre = if (genre == null) null else if (genre.logic == Logic.AND) (genre.genres).joinToString(
                            separator = ","
                        )
                        else (genre.genres).joinToString(separator = "|"),
                        originCountry = if (originCountry == null) null else if (originCountry.logic == Logic.AND) (originCountry.countries).joinToString(
                            separator = ","
                        )
                        else (originCountry.countries).joinToString(separator = "|"),
                        originLanguage = if (originLanguage == null) null else if ((originLanguage.logic) == Logic.AND) (originLanguage.languages).joinToString(
                            separator = ","
                        )
                        else (originLanguage.languages).joinToString(separator = "|"),
                        minimumLength = minimumLength?.movieLength ?: 0,
                        maximumLength = maximumLength?.movieLength ?: Int.MAX_VALUE,
                        withoutGenre = if (withoutGenre == null) null else if ((withoutGenre.logic) == Logic.AND) (withoutGenre.withoutGenres).joinToString(
                            separator = ","
                        )
                        else (withoutGenre.withoutGenres).joinToString(separator = "|"),
                        sortBy = when (sortBy) {
                            is MovieOrder.ReleaseDate -> {
                                if (sortBy.order == Order.ASCENDING) {
                                    "primary_release_date.asc"
                                } else {
                                    "primary_release_date.desc"
                                }
                            }

                            is MovieOrder.Rating -> {
                                if (sortBy.order == Order.ASCENDING) {
                                    "vote_average.asc"
                                } else {
                                    "vote_average.desc"
                                }
                            }

                            is MovieOrder.Vote -> {
                                if (sortBy.order == Order.ASCENDING) {
                                    "vote_count.asc"
                                } else {
                                    "vote_count.desc"
                                }
                            }

                            is MovieOrder.OriginalTitle -> {
                                if (sortBy.order == Order.ASCENDING) {
                                    "original_title.asc"
                                } else {
                                    "original_title.desc"
                                }
                            }

                            is MovieOrder.Title -> {
                                if (sortBy.order == Order.ASCENDING) {
                                    "title.asc"
                                } else {
                                    "title.desc"
                                }
                            }

                            else -> {
                                null
                            }
                        },
                        page = page
                    )
                )
            } else {
                Resource.Success(
                    data = movieDatabaseRepository.getTrendingMovieList(
                        apiKey = apiKey,
                        time = if (trending.trendingTime == TrendingTime.DAY) "day" else "week",
                        page = page
                    )
                )
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_MOVIELIST)
        }
    }

    private suspend fun searchMovieListResource(
        apiKey: String = DataSourceRelation.TMDB_API_KEY,
        query: String,
        region: MovieFilter.Region?,
        page: Int
    ): Resource<MovieList> {
        return try {
            Resource.Success(
                data = movieDatabaseRepository.searchMovieList(
                    apiKey = apiKey,
                    query = query,
                    region = region?.region,
                    page = page
                )
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_MOVIELIST)
        }
    }

    private suspend fun getLocalMovieListItems(
        searchQuery: String?,// No filter, no sort
        favorite: MovieFilter.Favorite?,// No region filter
        watched: MovieFilter.Watched?,// No region filter
        adultContentIncluded: MovieFilter.AdultContentIncluded?,
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
        sortBy: MovieOrder?,
    ): Resource<List<LocalMovieItem>> {
        val localMovieItemList = localMovieDatabaseRepository.getLocalMovieListItems().first()
        val formatter = DateTimeFormatter.ofPattern(DefaultValue.DATE_FORMAT)

        val filterList = mutableListOf<LocalMovieItem>()
        if (searchQuery.isNullOrBlank()) {
            for (movie in localMovieItemList) {
                if (
                    (if (favorite == null) true else {movie.favorite == favorite.isFavorite}) &&
                    (if (watched == null) true else {movie.watched == watched.isWatched}) &&
                    (if (adultContentIncluded?.isIncluded == false) !movie.adult else true) &&
                    (if (primaryReleaseYear == null) true else {LocalDate.parse(movie.regionReleaseDate, formatter).year == primaryReleaseYear.year}) &&
                    (LocalDate.parse(movie.regionReleaseDate, formatter) >= (minimumPrimaryReleaseDate?.releaseDate ?: LocalDate.of(1,1,1))) &&
                    (LocalDate.parse(movie.regionReleaseDate, formatter) <= (maximumPrimaryReleaseDate?.releaseDate ?: LocalDate.of(9999,12,31))) &&
                    (movie.voteAverage >= (minimumRating?.rating ?: 0f)) &&
                    (movie.voteAverage <= (maximumRating?.rating ?: Float.MAX_VALUE)) &&
                    (movie.voteCount >= (minimumVote?.voteCount ?: 0)) &&
                    (movie.voteCount <= (maximumVote?.voteCount ?: Int.MAX_VALUE)) &&
                    (movie.length >= (minimumLength?.movieLength ?: 0)) &&
                    (movie.length <= (maximumLength?.movieLength ?: Int.MAX_VALUE)) &&
                    (if (genre == null) true else {if (genre.logic == Logic.AND) movie.genres.containsAll(genre.genres.map { TranslateCode.GENRE[it] }) else genre.genres.any{TranslateCode.GENRE[it] in movie.genres}}) &&
                    (if (originCountry == null) true else {if (originCountry.logic == Logic.AND) movie.productionCountries.containsAll(originCountry.countries.map { TranslateCode.ISO_3166_1[it] }) else originCountry.countries.any{TranslateCode.ISO_3166_1[it] in movie.productionCountries}}) &&
                    (if (originLanguage == null) true else movie.originalLanguage in originLanguage.languages) &&
                    (if (withoutGenre == null) true else {!(if (withoutGenre.logic == Logic.AND) movie.genres.containsAll(withoutGenre.withoutGenres.map { TranslateCode.GENRE[it] }) else withoutGenre.withoutGenres.any{TranslateCode.GENRE[it] in movie.genres})})
                ) {
                    filterList.add(movie)
                }
            }
        }

        val resultList = if (sortBy != null) {
            sortLocalMovieItems(filterList, sortBy)
        } else {
            filterList
        }

        return try {
            Resource.Success(
                data = resultList
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: ResourceErrorMessage.GET_LOCALMOVIELIST)
        }
    }

    private fun sortLocalMovieItems(
        localMovieItemList: List<LocalMovieItem>,
        sortBy: MovieOrder
    ): List<LocalMovieItem>
    {
        val list: List<LocalMovieItem>

        if(sortBy.order == Order.ASCENDING) {
            list = when (sortBy) {
                is MovieOrder.ReleaseDate -> localMovieItemList.sortedBy { it.regionReleaseDate }
                is MovieOrder.Rating -> localMovieItemList.sortedBy { it.voteAverage }
                is MovieOrder.Vote -> localMovieItemList.sortedBy { it.voteCount }
                is MovieOrder.OriginalTitle -> localMovieItemList.sortedBy { it.originalTitle }
                else -> localMovieItemList.sortedBy { it.title}
            }
        }
        else{
            list = when (sortBy) {
                is MovieOrder.ReleaseDate -> localMovieItemList.sortedByDescending { it.regionReleaseDate }
                is MovieOrder.Rating -> localMovieItemList.sortedByDescending { it.voteAverage }
                is MovieOrder.Vote -> localMovieItemList.sortedByDescending { it.voteCount }
                is MovieOrder.OriginalTitle -> localMovieItemList.sortedByDescending { it.originalTitle }
                else -> localMovieItemList.sortedByDescending { it.title}
            }
        }

        return list
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
        region: String?
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
        region: String?
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
        region: String?
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