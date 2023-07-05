package powerrangers.eivom.feature_movie.data.repository

import dagger.hilt.android.scopes.ActivityScoped
import powerrangers.eivom.feature_movie.data.network.MovieDatabaseApi
import powerrangers.eivom.feature_movie.data.network.response.MovieImage
import powerrangers.eivom.feature_movie.data.network.response.MovieInformation
import powerrangers.eivom.feature_movie.data.network.response.MovieList
import powerrangers.eivom.feature_movie.data.network.response.MovieVideo
import powerrangers.eivom.feature_movie.domain.repository.MovieDatabaseRepository
import javax.inject.Inject

@ActivityScoped
class MovieDatabaseRepositoryImpl @Inject constructor(
    private val movieDatabaseApi: MovieDatabaseApi
) : MovieDatabaseRepository {
    override suspend fun getTrendingMovieList(
        time: String,
        apiKey: String,
        page: Int
    ): MovieList =
        movieDatabaseApi.getTrendingMovieList(
            time = time,
            apiKey = apiKey,
            page = page
        )

    override suspend fun getMovieList(
        apiKey: String,
        region: String?,
        includeAdult: Boolean?,
        primaryReleaseYear: String?,
        minimumPrimaryReleaseDate: String?,
        maximumPrimaryReleaseDate: String?,
        minimumRating: Float?,
        maximumRating: Float?,
        minimumVote: Int?,
        maximumVote: Int?,
        genre: String?,
        originCountry: String?,
        originLanguage: String?,
        minimumLength: Int?,
        maximumLength: Int?,
        withoutGenre: String?,
        sortBy: String?,
        page: Int
    ): MovieList =
        movieDatabaseApi.getMovieList(
            apiKey = apiKey,
            region = region,
            includeAdult = includeAdult,
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

    override suspend fun searchMovieList(
        apiKey: String,
        query: String,
        region: String?,
        page: Int
    ): MovieList =
        movieDatabaseApi.searchMovieList(apiKey = apiKey, query = query, region = region, page = page)

    override suspend fun getMovieInformation(
        movieId: Int,
        apiKey: String,
        region: String?
    ): MovieInformation =
        movieDatabaseApi.getMovieInformation(movieId = movieId, apiKey = apiKey, region = region)

    override suspend fun getMovieVideo(
        movieId: Int,
        apiKey: String,
        region: String?
    ): MovieVideo =
        movieDatabaseApi.getMovieVideo(movieId = movieId, apiKey = apiKey, region = region)

    override suspend fun getMovieImage(
        movieId: Int,
        apiKey: String,
        region: String?
    ): MovieImage =
        movieDatabaseApi.getMovieImage(movieId = movieId, apiKey = apiKey, region = region)
}