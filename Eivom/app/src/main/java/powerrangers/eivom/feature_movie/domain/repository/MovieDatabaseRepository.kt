package powerrangers.eivom.feature_movie.domain.repository

import powerrangers.eivom.feature_movie.data.network.response.MovieInformation
import powerrangers.eivom.feature_movie.data.network.response.MovieList

interface MovieDatabaseRepository {
    suspend fun getMovieList(apiKey: String, page: Int): MovieList
    suspend fun getMovieInformation(movieId: Int, apiKey: String): MovieInformation
}