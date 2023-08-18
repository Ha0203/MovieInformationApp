package powerrangers.eivom.feature_movie.ui.movie_note

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar
import powerrangers.eivom.ui.theme.PoppinsBold
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

    val isEditing by remember { viewModel.isEditing }

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
            movieNote = movieNote,
            isEditing = isEditing,
            navigateBack = {
                navigateBack()
            },
            updateIsEditing = {
                viewModel.updateIsEditing()
            },
            updateNoteContent = {
                viewModel.updateNoteContent(it)
            },
            saveMovieNote = {
                viewModel.saveMovieNote()
            }
        )
    }
}

@Composable
fun MovieNoteBody(
    modifier: Modifier = Modifier,
    userPreferences: UserPreferences,
    movieNote: NoteState,
    isEditing: Boolean,
    navigateBack: () -> Unit,
    updateIsEditing: () -> Unit,
    updateNoteContent: (String) -> Unit,
    saveMovieNote: suspend () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(if (userPreferences.colorMode) userPreferences.movieNoteBackgroundColor else MaterialTheme.colors.background)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .border(2.dp, Color.Black)
        ) {
            IconButton(onClick = { navigateBack() }) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .scale(-1f, 1f) // Flip horizontally
                        .padding(10.dp),
                    tint = Color.Black
                )
            }
            Spacer(modifier = modifier.weight(1f))
            if (!isEditing) {
                IconButton(onClick = { updateIsEditing() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(10.dp),
                        tint = Color.Black
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            saveMovieNote()
                            updateIsEditing()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(10.dp),
                        tint = Color.Black
                    )
                }
            }
        }
        Text(
            modifier = modifier
                .fillMaxWidth()
                .border(2.dp, Color.Black),
            text = movieNote.title,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontFamily = PoppinsBold,
            color = if (userPreferences.colorMode) userPreferences.movieNoteTextColor else MaterialTheme.colors.onBackground
        )
        if (!isEditing) {
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(2.dp, Color.Black),
                text = movieNote.content,
                fontFamily = PoppinsBold,
                color = if (userPreferences.colorMode) userPreferences.movieNoteTextColor else MaterialTheme.colors.onBackground
            )
        } else {
            OutlinedTextField(
                modifier = modifier.fillMaxSize(),
                value = movieNote.content,
                onValueChange = {
                    updateNoteContent(it)
                },
                placeholder = {
                    Text(text = stringResource(id = R.string.note_placeholder))
                }
            )
        }
    }
}