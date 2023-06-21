package powerrangers.eivom.dependency_injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import powerrangers.eivom.feature_movie.data.network.MovieDatabaseApi
import powerrangers.eivom.feature_movie.data.repository.MovieDatabaseRepositoryImpl
import powerrangers.eivom.feature_movie.data.repository.UserPreferencesRepositoryImpl
import powerrangers.eivom.feature_movie.data.utility.DataSourceRelation
import powerrangers.eivom.feature_movie.domain.repository.MovieDatabaseRepository
import powerrangers.eivom.feature_movie.domain.repository.UserPreferencesRepository
import powerrangers.eivom.feature_movie.domain.use_case.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DataSourceRelation.USER_PREFERENCES_NAME)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(userPreferencesDataStore: DataStore<Preferences>): UserPreferencesRepository =
        UserPreferencesRepositoryImpl(dataStore = userPreferencesDataStore)

    @Provides
    @Singleton
    fun provideUserPreferencesUseCase(userPreferencesRepository: UserPreferencesRepository): UserPreferencesUseCase =
        UserPreferencesUseCase(
            saveBackgroundColor = SaveBackgroundColor(userPreferencesRepository),
            saveDateFormat = SaveDateFormat(userPreferencesRepository),
            getBackgroundColor = GetBackgroundColor(userPreferencesRepository),
            getDateFormat = GetDateFormat(userPreferencesRepository)
        )

    @Provides
    @Singleton
    fun provideMovieDatabaseApi(): MovieDatabaseApi =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(DataSourceRelation.MOVIE_DATABASE_URL)
            .build()
            .create(MovieDatabaseApi::class.java)

    @Provides
    @Singleton
    fun provideMovieDatabaseRepository(movieDatabaseApi: MovieDatabaseApi): MovieDatabaseRepository =
        MovieDatabaseRepositoryImpl(movieDatabaseApi)

    @Provides
    @Singleton
    fun provideMovieDatabaseUseCase(movieDatabaseRepository: MovieDatabaseRepository): MovieDatabaseUseCase =
        MovieDatabaseUseCase(
            handleImageDominantColor = HandleImageDominantColor(),
            convertMovieListResourceToMovieListItemsResource = ConvertMovieListResourceToMovieListItemsResource(),
            convertMovieInformationResourceToMovieItemResource = ConvertMovieInformationResourceToMovieItemResource(movieDatabaseRepository),
            getMovieListResource = GetMovieListResource(movieDatabaseRepository),
            getMovieInformationResource = GetMovieInformationResource(movieDatabaseRepository),
            getMovieVideoResource = GetMovieVideoResource(movieDatabaseRepository),
            getMovieImageUrl = GetMovieImageUrl(),
            getYouTubeVideoUrl = GetYouTubeVideoUrl()
        )
}