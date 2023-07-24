package powerrangers.eivom.feature_movie.domain.use_case

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import powerrangers.eivom.feature_movie.domain.utility.MovieKey

class SponsoredMovieFirebaseUseCase {
    private val movieKeysCollectionRef = Firebase.firestore.collection("movie_keys")

    suspend fun getMovieKey(key: String): MovieKey? {
        try {
            val querySnapshot = movieKeysCollectionRef
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
}