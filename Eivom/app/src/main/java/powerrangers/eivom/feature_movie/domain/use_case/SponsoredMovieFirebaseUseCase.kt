package powerrangers.eivom.feature_movie.domain.use_case

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.feature_movie.domain.model.MovieItem
import powerrangers.eivom.feature_movie.domain.model.MovieListItem
import powerrangers.eivom.feature_movie.domain.model.SponsoredMovie
import powerrangers.eivom.feature_movie.domain.model.toMovieItem
import powerrangers.eivom.feature_movie.domain.model.toMovieListItem
import powerrangers.eivom.feature_movie.domain.utility.FirebaseConstant
import powerrangers.eivom.feature_movie.domain.utility.MovieKey

class SponsoredMovieFirebaseUseCase {
    private val movieKeysCollectionReference = Firebase.firestore.collection(FirebaseConstant.MOVIEKEYCOLLECTION)
    private val sponsoredMoviesCollectionReference = Firebase.firestore.collection(FirebaseConstant.SPONSOREDMOVIECOLLECTION)

    suspend fun getMovieKey(key: String): MovieKey? {
        try {
            val querySnapshot = movieKeysCollectionReference
                .whereEqualTo(FieldPath.documentId(), key)
                .get()
                .await()
            for (document in querySnapshot.documents) {
                return document.toObject<MovieKey>()
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getSponsoredMovieList(): Resource<List<MovieListItem>> {
        try {
            val querySnapshot = sponsoredMoviesCollectionReference
                .get()
                .await()
            val sponsoredMovieList = mutableListOf<SponsoredMovie>()
            for (document in querySnapshot.documents) {
                val sponsoredMovie = document.toObject<SponsoredMovie>()
                if (sponsoredMovie != null) {
                    sponsoredMovieList.add(sponsoredMovie)
                }
            }
            return Resource.Success(
                data = sponsoredMovieList.map {  sponsoredMovie ->
                    sponsoredMovie.toMovieListItem()
                }
            )
        } catch (e: Exception) {
            return Resource.Error(message = e.message ?: e.toString())
        }
    }

    suspend fun getSponsoredMovieListWithUserId(userId: String): Resource<List<MovieListItem>> {
        try {
            val querySnapshot = sponsoredMoviesCollectionReference
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val sponsoredMovieList = mutableListOf<SponsoredMovie>()
            for (document in querySnapshot.documents) {
                val sponsoredMovie = document.toObject<SponsoredMovie>()
                if (sponsoredMovie != null) {
                    sponsoredMovieList.add(sponsoredMovie)
                }
            }
            return Resource.Success(
                data = sponsoredMovieList.map {  sponsoredMovie ->
                    sponsoredMovie.toMovieListItem()
                }
            )
        } catch (e: Exception) {
            return Resource.Error(message = e.message ?: e.toString())
        }
    }

    suspend fun getSponsoredMovie(id: Int): Resource<MovieItem> {
        try {
            val querySnapshot = sponsoredMoviesCollectionReference
                .whereEqualTo(FieldPath.documentId(), id.toString())
                .get()
                .await()
            for (document in querySnapshot.documents) {
                return Resource.Success(
                    data = document.toObject<SponsoredMovie>()!!.toMovieItem()
                )
            }
            return Resource.Error(message = "Not Found")
        } catch (e: Exception) {
            return Resource.Error(message = e.message ?: e.toString())
        }
    }

    suspend fun getSponsoredMovieKey(id: Int): Resource<String> {
        try {
            val querySnapshot = sponsoredMoviesCollectionReference
                .whereEqualTo(FieldPath.documentId(), id.toString())
                .get()
                .await()
            for (document in querySnapshot.documents) {
                return Resource.Success(
                    data = document.toObject<SponsoredMovie>()!!.keyId
                )
            }
            return Resource.Error(message = "Not Found")
        } catch (e: Exception) {
            return Resource.Error(message = e.message ?: e.toString())
        }
    }

    fun saveSponsoredMovie(movieKey: MovieKey, movie: SponsoredMovie): Boolean {
        return try {
            sponsoredMoviesCollectionReference.document(movie.id.toString()).set(movie)
            movieKeysCollectionReference.document(movieKey.id).update(FirebaseConstant.MOVIEKEYADDFEATURE, false)
            true
        } catch (e: Exception) {
            false
        }
    }
}