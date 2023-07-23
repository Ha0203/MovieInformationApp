package powerrangers.eivom.feature_movie.ui.movie_list

import androidx.compose.runtime.mutableStateOf

data class GenreItems(val name: String){
    val isSelected = mutableStateOf(false)
}

