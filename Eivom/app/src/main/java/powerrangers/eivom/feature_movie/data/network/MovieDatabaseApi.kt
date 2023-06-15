package powerrangers.eivom.feature_movie.data.network

import powerrangers.eivom.feature_movie.data.network.response.MovieInformation
import powerrangers.eivom.feature_movie.data.network.response.MovieList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDatabaseApi {
    // Using version 3 of TMDB API

    // Get movie list
    @GET("discover/movie")
    suspend fun getMovieList(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieList

    // Get movie information
    @GET("movie/{movie_id}")
    suspend fun getMovieInformation(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieInformation
}