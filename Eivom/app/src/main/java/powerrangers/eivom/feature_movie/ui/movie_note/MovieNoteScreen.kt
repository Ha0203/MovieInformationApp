package powerrangers.eivom.feature_movie.ui.movie_note

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar
import powerrangers.eivom.ui.utility.UserPreferences

@Composable
fun MovieNoteScreen(
    modifier: Modifier = Modifier,
    viewModel: MovieNoteViewModel = hiltViewModel(),
    navigateToMenuItem: (String) -> Unit,
    navigateBack: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val userPreferences by remember { viewModel.userPreferences }

    val movieNote by remember { viewModel.movieNote }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name),
                onMenuIconClick = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerContent = {
            DrawerHeader()
            DrawerBody(onItemClick = navigateToMenuItem)
        }
    ) { innerPadding ->
        MovieNoteBody(
            modifier = modifier.padding(innerPadding),
            userPreferences = userPreferences,
            movieNote = movieNote
        )
    }
}

@Composable
fun MovieNoteBody(
    modifier: Modifier = Modifier,
    userPreferences: UserPreferences,
    movieNote: NoteState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (userPreferences.colorMode) userPreferences.movieNoteBackgroundColor else MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .border(2.dp, Color.Black),
            text = movieNote.title,
            textAlign = TextAlign.Center,
            color = if (userPreferences.colorMode) userPreferences.movieNoteTextColor else MaterialTheme.colors.onBackground
        )
        Text(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
                .border(2.dp, Color.Black),
            text = movieNote.content,
            textAlign = TextAlign.Center,
            color = if (userPreferences.colorMode) userPreferences.movieNoteTextColor else MaterialTheme.colors.onBackground
        )
    }
}