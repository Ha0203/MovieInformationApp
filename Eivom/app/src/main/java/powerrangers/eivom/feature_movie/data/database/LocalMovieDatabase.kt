package powerrangers.eivom.feature_movie.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import powerrangers.eivom.feature_movie.data.utility.LocalMovieItem

@Database(
    entities = [LocalMovieItem::class],
    version = 2
)
abstract class LocalMovieDatabase: RoomDatabase() {

    abstract val localMovieItemDao: LocalMovieItemDao

}