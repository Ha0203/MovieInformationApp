package powerrangers.eivom.feature_movie.domain.use_case

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import powerrangers.eivom.feature_movie.domain.model.SponsoredMovie
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

    fun saveSponsoredMovie(movieKey: MovieKey, movie: SponsoredMovie): Boolean {
        return try {
            sponsoredMoviesCollectionReference.document(movie.id).set(movie)
            movieKeysCollectionReference.document(movieKey.id).update(FirebaseConstant.MOVIEKEYADDFEATURE, false)
            true
        } catch (e: Exception) {
            false
        }
    }
}