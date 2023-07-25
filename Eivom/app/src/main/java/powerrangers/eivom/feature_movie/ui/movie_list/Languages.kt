package powerrangers.eivom.feature_movie.ui.movie_list

import androidx.compose.runtime.mutableStateOf

data class Language(val name: String){
    val isSelected = mutableStateOf(false)
}
