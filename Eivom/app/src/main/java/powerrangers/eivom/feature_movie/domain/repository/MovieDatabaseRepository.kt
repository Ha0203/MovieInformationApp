package powerrangers.eivom.feature_movie.domain.repository

import powerrangers.eivom.feature_movie.data.network.response.MovieInformation
import powerrangers.eivom.feature_movie.data.network.response.MovieList
import powerrangers.eivom.feature_movie.data.network.response.MovieVideo

interface MovieDatabaseRepository {
    suspend fun getMovieList(apiKey: String, region: String, page: Int): MovieList
    suspend fun getMovieInformation(movieId: Int, apiKey: String, region: String): MovieInformation
    suspend fun getMovieTrailer(movieId: Int, apiKey: String, region: String): MovieVideo
}