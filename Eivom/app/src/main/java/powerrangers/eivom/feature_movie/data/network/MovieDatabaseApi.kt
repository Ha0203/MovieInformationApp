package powerrangers.eivom.feature_movie.data.network

import powerrangers.eivom.feature_movie.data.network.response.MovieImage
import powerrangers.eivom.feature_movie.data.network.response.MovieInformation
import powerrangers.eivom.feature_movie.data.network.response.MovieList
import powerrangers.eivom.feature_movie.data.network.response.MovieVideo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDatabaseApi {
    // Using version 3 of TMDB API

    // Get movie list
    @GET("trending/movie/{time}")
    suspend fun getTrendingMovieList(
        @Path("time") time: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieList

    @GET("discover/movie")
    suspend fun getMovieList(
        @Query("api_key") apiKey: String,
        @Query("region") region: String?,
        @Query("include_adult") includeAdult: Boolean?,
        @Query("primary_release_year") primaryReleaseYear: String?,
        @Query("primary_release_date.gte") minimumPrimaryReleaseDate: String?,
        @Query("primary_release_date.lte") maximumPrimaryReleaseDate: String?,
        @Query("vote_average.gte") minimumRating: Float?,
        @Query("vote_average.lte") maximumRating: Float?,
        @Query("vote_count.gte") minimumVote: Int?,
        @Query("vote_count.lte") maximumVote: Int?,
        @Query("with_genres") genre: String?,
        @Query("with_origin_country") originCountry: String?,
        @Query("with_original_language") originLanguage: String?,
        @Query("with_runtime.gte") minimumLength: Int?,
        @Query("with_runtime.lte") maximumLength: Int?,
        @Query("without_genres") withoutGenre: String?,
        @Query("sort_by") sortBy: String?,
        @Query("page") page: Int
    ): MovieList

    // Get movie information
    @GET("movie/{movie_id}")
    suspend fun getMovieInformation(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("region") region: String?
    ): MovieInformation

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideo(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("region") region: String?
    ): MovieVideo

    @GET("movie/{movie_id}/images")
    suspend fun getMovieImage(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("region") region: String?
    ): MovieImage
}