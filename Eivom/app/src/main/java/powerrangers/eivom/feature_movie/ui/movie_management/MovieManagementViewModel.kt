package powerrangers.eivom.feature_movie.ui.movie_management

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import powerrangers.eivom.domain.use_case.GoogleAuthClient
import javax.inject.Inject

@HiltViewModel
class MovieManagementViewModel @Inject constructor(
    private val googleAuthClient: GoogleAuthClient
): ViewModel() {
    var user = mutableStateOf(googleAuthClient.getSignedInUser())
        private set

    // New movie state
    var isAddingMovie = mutableStateOf(false)
        private set
    var movieKey = mutableStateOf("")
        private set
    var newMovieState = mutableStateOf(SponsoredMovieState())
        private set
    var collectionState = mutableStateOf<CollectionState?>(null)
        private set
    var companyStateList = mutableStateListOf<CompanyState>()
        private set

    // Get user
    fun getUser() {
        user.value = googleAuthClient.getSignedInUser()
    }

    // Update new movie state functions
    fun updateAddingState(isAdding: Boolean) {
        isAddingMovie.value = isAdding
    }

    fun updateNewMovieState(
        key: String,
        movieState: SponsoredMovieState,
        movieCollectionState: CollectionState?,
        movieCompanyStateList: List<CompanyState>
    ) {
        movieKey.value = key
        newMovieState.value = movieState
        collectionState.value = movieCollectionState
        companyStateList.clear()
        companyStateList.addAll(movieCompanyStateList)
    }
}