package powerrangers.eivom.feature_movie.data.database
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import powerrangers.eivom.feature_movie.data.utility.LocalMovieItem

@Dao
interface LocalMovieItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(localMovieItem: LocalMovieItem)

    @Update
    suspend fun update(localMovieItem: LocalMovieItem)

    @Query("DELETE from LocalMovieItems WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * from LocalMovieItems WHERE id = :id")
    fun getLocalMovieItem(id: Int): Flow<LocalMovieItem>

    @Query("SELECT * from LocalMovieItems ORDER BY regionReleaseDate DESC")
    fun getAllItems(): Flow<List<LocalMovieItem>>
}