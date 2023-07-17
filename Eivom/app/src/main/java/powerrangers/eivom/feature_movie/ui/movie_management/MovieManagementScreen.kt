package powerrangers.eivom.feature_movie.ui.movie_management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.FloatingAddButton
import powerrangers.eivom.ui.component.TopBar

@Composable
fun MovieManagementScreen(
    modifier: Modifier = Modifier,
    movieManagementViewModel: MovieManagementViewModel = hiltViewModel(),
    navigateToMenuItem: (String) -> Unit,
    navigateToSettingsScreen: () -> Unit,
    navigateToMovieDetailScreen: (Int) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

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
        },
        floatingActionButton = {
            FloatingAddButton(
                modifier = modifier.padding(20.dp),
                contentDescription = stringResource(id = R.string.add_sponsored_movie_button_content_description),
                onClick = {
                    movieManagementViewModel.updateAddingState(
                        isAdding = true
                    )
                }
            )
        }
    ) { innerPadding ->
        MovieManagementBody(
            modifier = modifier.padding(innerPadding),
            movieManagementViewModel = movieManagementViewModel,
            navigateToSettingsScreen = navigateToSettingsScreen,
            navigateToMovieDetailScreen = navigateToMovieDetailScreen
        )
    }
}

@Composable
fun MovieManagementBody(
    modifier: Modifier,
    movieManagementViewModel: MovieManagementViewModel,
    navigateToSettingsScreen: () -> Unit,
    navigateToMovieDetailScreen: (Int) -> Unit
) {
    if (movieManagementViewModel.user.value is Resource.Error) {
        ErrorDialog(
            error = stringResource(id = R.string.user_error),
            onRetry = {
                movieManagementViewModel.getUser()
            },
            onConfirm = {
                navigateToSettingsScreen()
            }
        )
    }

    val isAddingMovie by remember { movieManagementViewModel.isAddingMovie }

    if (isAddingMovie) {
        AddMovieDialog(
            modifier = modifier,
            movieManagementViewModel = movieManagementViewModel
        )
    }
}

@Composable
fun AddMovieDialog(
    modifier: Modifier,
    movieManagementViewModel: MovieManagementViewModel,
) {
    val movieKey by remember { movieManagementViewModel.movieKey }
    val newMovieState by remember { movieManagementViewModel.newMovieState }

    Dialog(
        onDismissRequest = {
            movieManagementViewModel.updateAddingState(
                isAdding = false
            )
        }
    ) {
        Card(
            elevation = 8.dp,
            modifier = modifier
                .padding(8.dp)
        ) {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.add_sponsored_movie_title),
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    modifier = modifier.fillMaxWidth(),
                    value = movieKey,
                    onValueChange = {
                        movieManagementViewModel.updateMovieKey(it)
                    },
                    label = {
                        Text(text = stringResource(id = R.string.key_field_label))
                    },
                    placeholder = {
                        Text(text = stringResource(id = R.string.key_field_placeholder))
                    },
                    maxLines = 1
                )
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            movieManagementViewModel.updateAddingState(
                                isAdding = false
                            )
                        }
                    ) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }
                    Button(
                        onClick = {},
                        enabled = newMovieState.isValid()
                    ) {
                        Text(text = stringResource(id = R.string.save_button_title))
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(
    error: String,
    onRetry: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onRetry()
        },
        title = { Text(text = stringResource(id = R.string.error_label)) },
        text = {
            Text(text = error)
        },
        dismissButton = {
            Button(
                onClick = {
                    onRetry()
                }
            ) {
                Text(text = stringResource(id = R.string.retry_button))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(text = stringResource(id = R.string.navigate_to_settings))
            }
        }
    )
}