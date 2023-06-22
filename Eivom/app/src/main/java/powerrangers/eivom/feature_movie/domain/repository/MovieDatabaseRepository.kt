package powerrangers.eivom.feature_movie.domain.repository

import powerrangers.eivom.feature_movie.data.network.response.MovieImage
import powerrangers.eivom.feature_movie.data.network.response.MovieInformation
import powerrangers.eivom.feature_movie.data.network.response.MovieList
import powerrangers.eivom.feature_movie.data.network.response.MovieVideo

interface MovieDatabaseRepository {
    suspend fun getMovieList(apiKey: String, region: String, page: Int): MovieList
    suspend fun getMovieInformation(movieId: Int, apiKey: String, region: String): MovieInformation
    suspend fun getMovieVideo(movieId: Int, apiKey: String, region: String): MovieVideo
    suspend fun getMovieImage(movieId: Int, apiKey: String, region: String): MovieImage
}