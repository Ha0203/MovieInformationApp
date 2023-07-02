package powerrangers.eivom.feature_movie.data.repository

import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun deleteLocalMovieItemById(id: Int) {
        localMovieItemDao.deleteById(id)
    }

    override fun getLocalMovieItem(movieId: Int): Flow<LocalMovieItem> =
        localMovieItemDao.getLocalMovieItem(id = movieId)

    override fun getLocalMovieListItems(): Flow<List<LocalMovieItem>> =
        localMovieItemDao.getAllItems()

    override fun getLocalMovieListItemsAsMap(): Flow<Map<Int, LocalMovieItem>> =
        localMovieItemDao.getAllItems().map { list ->
            list.associateBy { item -> item.id }
        }
}