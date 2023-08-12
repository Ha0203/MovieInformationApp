package powerrangers.eivom.feature_movie.ui.movie_management

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.domain.use_case.GoogleAuthClient
import powerrangers.eivom.domain.use_case.UserPreferencesUseCase
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.feature_movie.domain.model.MovieListItem
import powerrangers.eivom.feature_movie.domain.use_case.SponsoredMovieFirebaseUseCase
import javax.inject.Inject

@HiltViewModel
class MovieManagementViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase,
    private val googleAuthClient: GoogleAuthClient,
    private val sponsoredMovieFirebaseUseCase: SponsoredMovieFirebaseUseCase
): ViewModel() {
    var user = mutableStateOf(googleAuthClient.getSignedInUser())
        private set

    // New movie state
    var isAddingMovie = mutableStateOf(false)

    // Movie List
    var sponsoredMovieList = mutableStateOf<Resource<List<MovieListItem>>>(Resource.Loading(data = emptyList()))
        private set

    init {
        viewModelScope.launch {
            loadSponsoredMovie()
        }
    }

    // Get user
    fun getUser() {
        user.value = googleAuthClient.getSignedInUser()
    }

    // Sponsored Movie List
    private suspend fun loadSponsoredMovie() {
        if (user.value.data != null) {
            sponsoredMovieList.value =
                sponsoredMovieFirebaseUseCase.getSponsoredMovieListWithUserId(user.value.data!!.userId)
        }
    }

    // Update new movie state functions
    fun updateAddingState(isAdding: Boolean) {
        isAddingMovie.value = isAdding
    }
}