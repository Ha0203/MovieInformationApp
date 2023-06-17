package powerrangers.eivom.feature_movie.data.repository

import dagger.hilt.android.scopes.ActivityScoped
import powerrangers.eivom.feature_movie.data.network.MovieDatabaseApi
import powerrangers.eivom.feature_movie.data.network.response.MovieInformation
import powerrangers.eivom.feature_movie.data.network.response.MovieList
import powerrangers.eivom.feature_movie.domain.repository.MovieDatabaseRepository
import javax.inject.Inject

@ActivityScoped
class MovieDatabaseRepositoryImpl @Inject constructor(
    private val movieDatabaseApi: MovieDatabaseApi
) : MovieDatabaseRepository {
    override suspend fun getMovieList(apiKey: String, region: String, page: Int): MovieList =
        movieDatabaseApi.getMovieList(apiKey = apiKey, region = region, page = page)

    override suspend fun getMovieInformation(movieId: Int, apiKey: String, region: String): MovieInformation =
        movieDatabaseApi.getMovieInformation(movieId = movieId, apiKey = apiKey, region = region)
}