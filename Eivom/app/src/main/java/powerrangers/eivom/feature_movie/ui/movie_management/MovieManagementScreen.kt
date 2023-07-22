package powerrangers.eivom.feature_movie.ui.movie_management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.FloatingAddButton
import powerrangers.eivom.ui.component.TopBar
import kotlin.math.roundToInt

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

    LaunchedEffect(Unit) {
        movieManagementViewModel.getUser()
    }

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
            key = movieManagementViewModel.movieKey.value,
            movieState = movieManagementViewModel.newMovieState.value,
            updateAddingState = {
                movieManagementViewModel.updateAddingState(
                    isAdding = it
                )
            },
            updateNewMovieState = { key, movieState ->
                movieManagementViewModel.updateNewMovieState(
                    key = key,
                    movieState = movieState
                )
            }
        )
    }
}

@Composable
fun AddMovieDialog(
    modifier: Modifier,
    newMovieDialogViewModel: NewMovieDialogViewModel = hiltViewModel(),
    key: String,
    movieState: SponsoredMovieState,
    updateAddingState: (Boolean) -> Unit,
    updateNewMovieState: (String, SponsoredMovieState) -> Unit
) {
    newMovieDialogViewModel.updateNewMovieState(
        key = key,
        movieState = movieState
    )
    val movieKey by remember { newMovieDialogViewModel.movieKey }
    val newMovieState by remember { newMovieDialogViewModel.newMovieState }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var scrollToPosition by remember { mutableStateOf(0F) }

    Dialog(
        onDismissRequest = {
            updateAddingState(false)
            updateNewMovieState(movieKey, newMovieState)
        }
    ) {
        Card(
            elevation = 8.dp,
            modifier = modifier
                .padding(8.dp)
        ) {
            Column(
                modifier = modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Dialog title
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.add_sponsored_movie_title),
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = modifier
                        .height(450.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Movie Key Field
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = movieKey,
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieKey(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.key_field_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.key_field_placeholder))
                        },
                        maxLines = 1
                    )
                    // Adult
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = modifier.weight(1f),
                            text = stringResource(id = R.string.adult_label)
                        )
                        SelectButton(
                            modifier = modifier.weight(0.45f),
                            text = stringResource(id = R.string.adult_yes),
                            isSelected = newMovieState.adult == true,
                            onSelect = {
                                newMovieDialogViewModel.updateAdultOfMovie(
                                    isAdult = true
                                )
                            }
                        )
                        Spacer(modifier = modifier.weight(0.1f))
                        SelectButton(
                            modifier = modifier.weight(0.45f),
                            text = stringResource(id = R.string.adult_no),
                            isSelected = newMovieState.adult == false,
                            onSelect = {
                                newMovieDialogViewModel.updateAdultOfMovie(
                                    isAdult = false
                                )
                            }
                        )
                    }
                    // Original Title
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.originalTitle ?: "",
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieOriginalTitle(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.original_title_field_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.original_title_field_placeholder))
                        },
                        maxLines = 2
                    )
                    // Title
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.title ?: "",
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieTitle(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.title_field_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.title_field_placeholder))
                        },
                        maxLines = 2
                    )
                    // Genres
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.genre_label))
                        Spacer(modifier = modifier.width(8.dp))
                        LazyRow {
                            items(newMovieDialogViewModel.genreList) { genre ->
                                SelectButton(
                                    text = genre.second,
                                    isSelected = newMovieDialogViewModel.isGenreSelected(genre.first),
                                    onSelect = {
                                        newMovieDialogViewModel.addMovieGenre(genre.first)
                                    }
                                )
                            }
                        }
                    }
                    // Overview
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.overview ?: "",
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieOverview(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.overview_field_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.overview_field_placeholder))
                        },
                        maxLines = 8
                    )
                    // Original Language
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val density = LocalDensity.current

                        var isOriginalLanguageDropdownExpanded by remember { mutableStateOf(false) }
                        var dropdownMenuWidth by remember { mutableStateOf(0.dp) }

                        Text(
                            modifier = modifier.weight(1f),
                            text = stringResource(id = R.string.original_language_label)
                        )
                        Column(
                            modifier = modifier.weight(1f)
                        ) {
                            SelectButton(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { coordinates ->
                                        dropdownMenuWidth =
                                            (coordinates.size.width / density.density).dp
                                    },
                                text = TranslateCode.ISO_639_1[newMovieState.originalLanguage]
                                    ?: stringResource(id = R.string.unknown),
                                isSelected = newMovieState.originalLanguage != null,
                                onSelect = {
                                    isOriginalLanguageDropdownExpanded =
                                        !isOriginalLanguageDropdownExpanded
                                }
                            )
                            DropdownMenu(
                                modifier = modifier
                                    .height(200.dp)
                                    .width(dropdownMenuWidth),
                                expanded = isOriginalLanguageDropdownExpanded,
                                onDismissRequest = {
                                    isOriginalLanguageDropdownExpanded = false
                                }
                            ) {
                                newMovieDialogViewModel.languageList.forEach { language ->
                                    DropdownMenuItem(
                                        onClick = {
                                            newMovieDialogViewModel.updateMovieOriginalLanguage(
                                                language.first
                                            )
                                            newMovieDialogViewModel.removeMovieSpokenLanguage(
                                                language.first
                                            )
                                            isOriginalLanguageDropdownExpanded = false
                                        }
                                    ) {
                                        Text(
                                            modifier = modifier.fillMaxWidth(),
                                            text = language.second,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Spoken Language
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = modifier.weight(1f),
                            text = stringResource(id = R.string.spoken_language_label)
                        )
                        SelectButton(
                            modifier = modifier.weight(1f),
                            enabled = false,
                            text = TranslateCode.ISO_639_1[newMovieState.originalLanguage]
                                ?: stringResource(id = R.string.unknown),
                            isSelected = newMovieState.originalLanguage != null,
                            onSelect = {}
                        )
                    }
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        val density = LocalDensity.current

                        var isLanguageDropdownExpanded by remember { mutableStateOf(false) }
                        var dropdownMenuWidth by remember { mutableStateOf(0.dp) }

                        Spacer(modifier = modifier.weight(1f))
                        
                        Column(
                            modifier = modifier.weight(1f)
                        ) {
                            newMovieState.spokenLanguages.forEach { language ->
                                SelectButton(
                                    modifier = modifier.fillMaxWidth(),
                                    text = TranslateCode.ISO_639_1[language] ?: stringResource(id = R.string.unknown),
                                    isSelected = false,
                                    onSelect = {
                                        newMovieDialogViewModel.removeMovieSpokenLanguage(language)
                                    }
                                )
                            }
                            AddButton(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { coordinates ->
                                        dropdownMenuWidth =
                                            (coordinates.size.width / density.density).dp
                                    },
                                contentDescription = stringResource(id = R.string.add_spoken_language_content_description),
                                onSelect = {
                                    isLanguageDropdownExpanded =
                                        !isLanguageDropdownExpanded
                                }
                            )
                            DropdownMenu(
                                modifier = modifier
                                    .height(200.dp)
                                    .width(dropdownMenuWidth),
                                expanded = isLanguageDropdownExpanded,
                                onDismissRequest = {
                                    isLanguageDropdownExpanded = false
                                }
                            ) {
                                newMovieDialogViewModel.languageList.forEach { language ->
                                    if (language.first != newMovieState.originalLanguage && language.first !in newMovieState.spokenLanguages) {
                                        DropdownMenuItem(
                                            onClick = {
                                                coroutineScope.launch {
                                                    newMovieDialogViewModel.addMovieSpokenLanguage(
                                                        language.first
                                                    )
                                                    isLanguageDropdownExpanded = false
                                                    scrollState.animateScrollTo(scrollToPosition.roundToInt())
                                                }
                                            }
                                        ) {
                                            Text(
                                                modifier = modifier.fillMaxWidth(),
                                                text = language.second,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Length
                    OutlinedTextField(
                        modifier = modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                scrollToPosition = coordinates.positionInParent().y
                            },
                        value = newMovieState.length?.toString() ?: "",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieLength(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.length_field_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.length_field_placeholder))
                        },
                        trailingIcon = {
                            Text(text = stringResource(id = R.string.length_unit))
                        },
                        maxLines = 1
                    )
                    // Release Date
                    // Collection
                    // Company
                    // Country
                    // Logo Urls
                    // Main Poster Url
                    // Poster Urls
                    // Main Backdrop Url
                    // Backdrop Urls
                    // Homepage Url
                    // Video
                    // Status
                    // Budget
                    // Revenue
                    // Button
                }
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            updateAddingState(false)
                            updateNewMovieState(movieKey, newMovieState)
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
fun SelectButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onSelect,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
            contentColor = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
        )
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String,
    onSelect: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onSelect
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = contentDescription
        )
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