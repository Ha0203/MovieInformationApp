package powerrangers.eivom.feature_movie.data.repository

import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import powerrangers.eivom.feature_movie.data.database.LocalMovieItemDao
import powerrangers.eivom.feature_movie.data.utility.LocalMovieItem
import powerrangers.eivom.feature_movie.domain.repository.LocalMovieDatabaseRepository
import javax.inject.Inject

@ActivityScoped
class LocalMovieDatabaseRepositoryImpl @Inject constructor(
    private val localMovieItemDao : LocalMovieItemDao
) : LocalMovieDatabaseRepository{
    override suspend fun insertLocalMovieItem(localMovieItem: LocalMovieItem){
        localMovieItemDao.insert(localMovieItem)
    }

    override suspend fun updateLocalMovieItem(localMovieItem: LocalMovieItem) {
        localMovieItemDao.update(localMovieItem)
    }

    override suspend fun deleteLocalMovieItem(localMovieItem: LocalMovieItem) {
        localMovieItemDao.delete(localMovieItem)
    }

    override fun getLocalMovieItem(movieId: Int): Flow<LocalMovieItem> =
        localMovieItemDao.getLocalMovieItem(id = movieId)

    override fun getLocalMovieListItems(): Flow<List<LocalMovieItem>> =
        localMovieItemDao.getAllItems()
}