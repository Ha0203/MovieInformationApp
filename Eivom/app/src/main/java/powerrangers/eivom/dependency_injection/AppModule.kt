package powerrangers.eivom.dependency_injection

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.google.android.gms.auth.api.identity.Identity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import powerrangers.eivom.data.repository.UserPreferencesRepositoryImpl
import powerrangers.eivom.domain.repository.UserPreferencesRepository
import powerrangers.eivom.domain.use_case.GetColorMode
import powerrangers.eivom.domain.use_case.GetDateFormat
import powerrangers.eivom.domain.use_case.GetDialogBackgroundColor
import powerrangers.eivom.domain.use_case.GetDialogTextColor
import powerrangers.eivom.domain.use_case.GetMovieNoteBackgroundColor
import powerrangers.eivom.domain.use_case.GetMovieNoteTextColor
import powerrangers.eivom.domain.use_case.GetNotificationBeforeDay
import powerrangers.eivom.domain.use_case.GetNotificationBeforeMonth
import powerrangers.eivom.domain.use_case.GetNotificationBeforeWeek
import powerrangers.eivom.domain.use_case.GetNotificationOnDate
import powerrangers.eivom.domain.use_case.GetOriginalTitleDisplay
import powerrangers.eivom.domain.use_case.GetScreenBackgroundColor
import powerrangers.eivom.domain.use_case.GetScreenTextColor
import powerrangers.eivom.domain.use_case.GetSidebarBackgroundColor
import powerrangers.eivom.domain.use_case.GetSidebarTextColor
import powerrangers.eivom.domain.use_case.GetTopbarBackgroundColor
import powerrangers.eivom.domain.use_case.GetTopbarTextColor
import powerrangers.eivom.domain.use_case.GoogleAuthClient
import powerrangers.eivom.domain.use_case.SaveColorMode
import powerrangers.eivom.domain.use_case.SaveDateFormat
import powerrangers.eivom.domain.use_case.SaveDialogBackgroundColor
import powerrangers.eivom.domain.use_case.SaveDialogTextColor
import powerrangers.eivom.domain.use_case.SaveMovieNoteBackgroundColor
import powerrangers.eivom.domain.use_case.SaveMovieNoteTextColor
import powerrangers.eivom.domain.use_case.SaveNotificationBeforeDay
import powerrangers.eivom.domain.use_case.SaveNotificationBeforeMonth
import powerrangers.eivom.domain.use_case.SaveNotificationBeforeWeek
import powerrangers.eivom.domain.use_case.SaveNotificationOnDate
import powerrangers.eivom.domain.use_case.SaveOriginalTitleDisplay
import powerrangers.eivom.domain.use_case.SaveScreenBackgroundColor
import powerrangers.eivom.domain.use_case.SaveScreenTextColor
import powerrangers.eivom.domain.use_case.SaveSidebarBackgroundColor
import powerrangers.eivom.domain.use_case.SaveSidebarTextColor
import powerrangers.eivom.domain.use_case.SaveTopbarBackgroundColor
import powerrangers.eivom.domain.use_case.SaveTopbarTextColor
import powerrangers.eivom.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.feature_movie.data.database.LocalMovieDatabase
import powerrangers.eivom.feature_movie.data.database.LocalMovieItemDao
import powerrangers.eivom.feature_movie.data.network.MovieDatabaseApi
import powerrangers.eivom.feature_movie.data.repository.LocalMovieDatabaseRepositoryImpl
import powerrangers.eivom.feature_movie.data.repository.MovieDatabaseRepositoryImpl
import powerrangers.eivom.feature_movie.data.utility.DataSourceRelation
import powerrangers.eivom.feature_movie.domain.repository.LocalMovieDatabaseRepository
import powerrangers.eivom.feature_movie.domain.repository.MovieDatabaseRepository
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
            saveColorMode = SaveColorMode(userPreferencesRepository),
            saveTopbarBackgroundColor = SaveTopbarBackgroundColor(userPreferencesRepository),
            saveSidebarBackgroundColor = SaveSidebarBackgroundColor(userPreferencesRepository),
            saveScreenBackgroundColor = SaveScreenBackgroundColor(userPreferencesRepository),
            saveMovieNoteBackgroundColor = SaveMovieNoteBackgroundColor(userPreferencesRepository),
            saveDialogBackgroundColor = SaveDialogBackgroundColor(userPreferencesRepository),
            saveTopbarTextColor = SaveTopbarTextColor(userPreferencesRepository),
            saveSidebarTextColor = SaveSidebarTextColor(userPreferencesRepository),
            saveScreenTextColor = SaveScreenTextColor(userPreferencesRepository),
            saveMovieNoteTextColor = SaveMovieNoteTextColor(userPreferencesRepository),
            saveDialogTextColor = SaveDialogTextColor(userPreferencesRepository),
            saveOriginalTitleDisplay = SaveOriginalTitleDisplay(userPreferencesRepository),
            saveDateFormat = SaveDateFormat(userPreferencesRepository),
            saveNotificationBeforeMonth = SaveNotificationBeforeMonth(userPreferencesRepository),
            saveNotificationBeforeWeek = SaveNotificationBeforeWeek(userPreferencesRepository),
            saveNotificationBeforeDay = SaveNotificationBeforeDay(userPreferencesRepository),
            saveNotificationOnDate = SaveNotificationOnDate(userPreferencesRepository),
            getColorMode = GetColorMode(userPreferencesRepository),
            getTopbarBackgroundColor = GetTopbarBackgroundColor(userPreferencesRepository),
            getSidebarBackgroundColor = GetSidebarBackgroundColor(userPreferencesRepository),
            getScreenBackgroundColor = GetScreenBackgroundColor(userPreferencesRepository),
            getMovieNoteBackgroundColor = GetMovieNoteBackgroundColor(userPreferencesRepository),
            getDialogBackgroundColor = GetDialogBackgroundColor(userPreferencesRepository),
            getTopbarTextColor = GetTopbarTextColor(userPreferencesRepository),
            getSidebarTextColor = GetSidebarTextColor(userPreferencesRepository),
            getScreenTextColor = GetScreenTextColor(userPreferencesRepository),
            getMovieNoteTextColor = GetMovieNoteTextColor(userPreferencesRepository),
            getDialogTextColor = GetDialogTextColor(userPreferencesRepository),
            getOriginalTitleDisplay = GetOriginalTitleDisplay(userPreferencesRepository),
            getDateFormat = GetDateFormat(userPreferencesRepository),
            getNotificationBeforeMonth = GetNotificationBeforeMonth(userPreferencesRepository),
            getNotificationBeforeWeek = GetNotificationBeforeWeek(userPreferencesRepository),
            getNotificationBeforeDay = GetNotificationBeforeDay(userPreferencesRepository),
            getNotificationOnDate = GetNotificationOnDate(userPreferencesRepository)
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
    fun provideMovieDatabaseUseCase(movieDatabaseRepository: MovieDatabaseRepository, localMovieDatabaseRepository: LocalMovieDatabaseRepository): MovieDatabaseUseCase =
        MovieDatabaseUseCase(
            movieDatabaseRepository = movieDatabaseRepository,
            localMovieDatabaseRepository = localMovieDatabaseRepository
        )

    @Provides
    @Singleton
    fun provideLocalMovieDatabase(app: Application): LocalMovieDatabase {
        return Room.databaseBuilder(
            app,
            LocalMovieDatabase::class.java,
            DataSourceRelation.LOCAL_MOVIE_ITEM_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideLocalMovieItemDao(localMovieDatabase: LocalMovieDatabase): LocalMovieItemDao =
        localMovieDatabase.localMovieItemDao

    @Provides
    @Singleton
    fun provideLocalMovieDatabaseRepository(localMovieItemDao: LocalMovieItemDao): LocalMovieDatabaseRepository =
        LocalMovieDatabaseRepositoryImpl(localMovieItemDao)

    @Provides
    @Singleton
    fun provideGoogleAuthClient(@ApplicationContext context: Context): GoogleAuthClient = GoogleAuthClient(
        context = context,
        oneTapClient = Identity.getSignInClient(context)
    )

    @Provides
    @Singleton
    fun provideSponsoredMovieFirebaseUseCase(): SponsoredMovieFirebaseUseCase = SponsoredMovieFirebaseUseCase()
}