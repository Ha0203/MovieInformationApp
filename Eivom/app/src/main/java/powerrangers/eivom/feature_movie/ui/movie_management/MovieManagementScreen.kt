package powerrangers.eivom.feature_movie.ui.movie_management

import android.app.DatePickerDialog
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.platform.LocalContext
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
import java.time.LocalDate

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
            updateAddingState = {
                movieManagementViewModel.updateAddingState(
                    isAdding = it
                )
            }
        )
    }
}

@Composable
fun AddMovieDialog(
    modifier: Modifier,
    newMovieDialogViewModel: NewMovieDialogViewModel = hiltViewModel(),
    updateAddingState: (Boolean) -> Unit
) {
    val userPreferences by remember { newMovieDialogViewModel.userPreferences }
    val movieKey by remember { newMovieDialogViewModel.movieKey }
    val newMovieState by remember { newMovieDialogViewModel.newMovieState }
    val collectionState by remember { newMovieDialogViewModel.collectionState }
    val companyStateList = remember { newMovieDialogViewModel.companyStateList }
    val movieLogoUrlList = remember { newMovieDialogViewModel.movieLogoUrlList }
    val moviePosterUrlList = remember { newMovieDialogViewModel.moviePosterUrlList }
    val movieBackdropUrlList = remember { newMovieDialogViewModel.movieBackdropUrlList }

    Dialog(
        onDismissRequest = {
            updateAddingState(false)
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
                        .verticalScroll(rememberScrollState()),
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
                                val isGenreSelected = newMovieDialogViewModel.isGenreSelected(genre.first)
                                SelectButton(
                                    text = genre.second,
                                    isSelected = isGenreSelected,
                                    onSelect = {
                                        if (isGenreSelected) {
                                            newMovieDialogViewModel.removeMovieGenre(genre.first)
                                        } else {
                                            newMovieDialogViewModel.addMovieGenre(genre.first)
                                        }
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
                        val density = LocalDensity.current
                        var dropdownMenuWidth by remember { mutableStateOf(0.dp) }
                        var isLanguageDropdownExpanded by remember { mutableStateOf(false) }

                        Text(
                            modifier = modifier.weight(1f),
                            text = stringResource(id = R.string.spoken_language_label)
                        )
                        AddOrDeleteButton(
                            modifier = modifier
                                .weight(1f)
                                .onGloballyPositioned { coordinates ->
                                    dropdownMenuWidth =
                                        (coordinates.size.width / density.density).dp
                                },
                            isAddButton = true,
                            contentDescription = stringResource(id = R.string.add_spoken_language_content_description),
                            onClick = {
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
                                            newMovieDialogViewModel.addMovieSpokenLanguage(
                                                language.first
                                            )
                                            isLanguageDropdownExpanded = false
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
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Spacer(modifier = modifier.weight(1f))

                        Column(
                            modifier = modifier
                                .weight(1f)
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                        ) {
                            SelectButton(
                                modifier = modifier.fillMaxWidth(),
                                enabled = false,
                                text = TranslateCode.ISO_639_1[newMovieState.originalLanguage]
                                    ?: stringResource(id = R.string.unknown),
                                isSelected = newMovieState.originalLanguage != null,
                                onSelect = {}
                            )
                            for (i in newMovieState.spokenLanguages.size - 1 downTo 0) {
                                SelectButton(
                                    modifier = modifier.fillMaxWidth(),
                                    text = TranslateCode.ISO_639_1[newMovieState.spokenLanguages[i]] ?: stringResource(
                                        id = R.string.unknown
                                    ),
                                    isSelected = false,
                                    onSelect = {
                                        newMovieDialogViewModel.removeMovieSpokenLanguage(
                                            newMovieState.spokenLanguages[i]
                                        )
                                    }
                                )
                            }
                        }
                    }
                    // Length
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
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
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val context = LocalContext.current
                        val currentDate = LocalDate.now()

                        Text(
                            modifier = modifier.weight(1f),
                            text = stringResource(id = R.string.release_date_label)
                        )
                        SelectButton(
                            modifier = modifier.weight(1f),
                            text = newMovieState.releaseDate?.format(userPreferences.dateFormat) ?: stringResource(id = R.string.unknown),
                            isSelected = newMovieState.releaseDate != null,
                            onSelect = {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        newMovieDialogViewModel.updateMovieReleaseDate(
                                            year = year,
                                            month = month + 1,
                                            dayOfMonth = dayOfMonth
                                        )
                                    },
                                    newMovieState.releaseDate?.year ?: currentDate.year,
                                    (newMovieState.releaseDate?.monthValue ?: currentDate.monthValue) - 1,
                                    newMovieState.releaseDate?.dayOfMonth ?: currentDate.dayOfMonth
                                ).show()
                            }
                        )
                    }
                    // Collection
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                    ) {
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = modifier.weight(1f),
                                text = stringResource(id = R.string.collection_label)
                            )
                            AddOrDeleteButton(
                                modifier = modifier.weight(1f),
                                isAddButton = collectionState == null,
                                contentDescription = stringResource(id = R.string.collection_content_description),
                                onClick = {
                                    if (collectionState == null) {
                                        newMovieDialogViewModel.addMovieCollection()
                                    } else {
                                        newMovieDialogViewModel.deleteMovieCollection()
                                    }
                                }
                            )
                        }
                        if (collectionState != null) {
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = collectionState?.name ?: "",
                                onValueChange = {
                                    newMovieDialogViewModel.updateMovieCollectionName(it)
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.collection_name_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.collection_name_placeholder))
                                },
                                maxLines = 2
                            )
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = collectionState?.posterUrl ?: "",
                                onValueChange = {
                                    newMovieDialogViewModel.updateMovieCollectionPosterUrl(it)
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.collection_poster_url_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.collection_poster_url_placeholder))
                                },
                                maxLines = 1
                            )
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = collectionState?.backdropUrl ?: "",
                                onValueChange = {
                                    newMovieDialogViewModel.updateMovieCollectionBackdropUrl(it)
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.collection_backdrop_url_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.collection_backdrop_url_placeholder))
                                },
                                maxLines = 1
                            )
                        }
                    }
                    // Company
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                    ) {
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = modifier.weight(1f),
                                text = stringResource(id = R.string.company_label)
                            )
                            AddOrDeleteButton(
                                modifier = modifier.weight(1f),
                                isAddButton = true,
                                contentDescription = stringResource(id = R.string.add_company_content_description),
                                onClick = {
                                    newMovieDialogViewModel.addMovieCompany()
                                }
                            )
                        }
                        for (i in companyStateList.size - 1 downTo 0) {
                            SelectButton(
                                modifier = modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.remove_company_button) + " " + if (companyStateList[i].name.isNullOrBlank()) stringResource(id = R.string.unknown) else companyStateList[i].name,
                                isSelected = false,
                                onSelect = {
                                    newMovieDialogViewModel.deleteMovieCompany(
                                        index = i
                                    )
                                }
                            )
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = companyStateList[i].name ?: "",
                                onValueChange = {
                                    newMovieDialogViewModel.updateMovieCompanyName(
                                        index = i,
                                        name = it
                                    )
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.company_name_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.company_name_placeholder))
                                },
                                maxLines = 2
                            )
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = companyStateList[i].logoUrl ?: "",
                                onValueChange = {
                                    newMovieDialogViewModel.updateMovieCompanyLogoUrl(
                                        index = i,
                                        logoUrl = it
                                    )
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.company_logo_url_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.company_logo_url_placeholder))
                                },
                                maxLines = 1
                            )
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val density = LocalDensity.current

                                var isCountryDropdownExpanded by remember { mutableStateOf(false) }
                                var dropdownMenuWidth by remember { mutableStateOf(0.dp) }

                                Text(
                                    modifier = modifier.weight(1f),
                                    text = stringResource(id = R.string.company_country_label)
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
                                        text = TranslateCode.ISO_3166_1[companyStateList[i].originCountry]
                                            ?: stringResource(id = R.string.unknown),
                                        isSelected = companyStateList[i].originCountry != null,
                                        onSelect = {
                                            isCountryDropdownExpanded =
                                                !isCountryDropdownExpanded
                                        }
                                    )
                                    DropdownMenu(
                                        modifier = modifier
                                            .height(200.dp)
                                            .width(dropdownMenuWidth),
                                        expanded = isCountryDropdownExpanded,
                                        onDismissRequest = {
                                            isCountryDropdownExpanded = false
                                        }
                                    ) {
                                        newMovieDialogViewModel.countryList.forEach { country ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    newMovieDialogViewModel.updateMovieCompanyOriginCountry(
                                                        index = i,
                                                        originCountry = country.first
                                                    )
                                                    isCountryDropdownExpanded = false
                                                }
                                            ) {
                                                Text(
                                                    modifier = modifier.fillMaxWidth(),
                                                    text = country.second,
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
                    }
                    // Country
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val density = LocalDensity.current
                        var dropdownMenuWidth by remember { mutableStateOf(0.dp) }
                        var isCountryDropdownExpanded by remember { mutableStateOf(false) }

                        Text(
                            modifier = modifier.weight(1f),
                            text = stringResource(id = R.string.production_country_label)
                        )
                        AddOrDeleteButton(
                            modifier = modifier
                                .weight(1f)
                                .onGloballyPositioned { coordinates ->
                                    dropdownMenuWidth =
                                        (coordinates.size.width / density.density).dp
                                },
                            isAddButton = true,
                            contentDescription = stringResource(id = R.string.add_production_country_content_description),
                            onClick = {
                                isCountryDropdownExpanded =
                                    !isCountryDropdownExpanded
                            }
                        )
                        DropdownMenu(
                            modifier = modifier
                                .height(200.dp)
                                .width(dropdownMenuWidth),
                            expanded = isCountryDropdownExpanded,
                            onDismissRequest = {
                                isCountryDropdownExpanded = false
                            }
                        ) {
                            newMovieDialogViewModel.countryList.forEach { country ->
                                DropdownMenuItem(
                                    onClick = {
                                        newMovieDialogViewModel.addMovieProductionCountry(
                                            country.first
                                        )
                                        isCountryDropdownExpanded = false
                                    }
                                ) {
                                    Text(
                                        modifier = modifier.fillMaxWidth(),
                                        text = country.second,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Spacer(modifier = modifier.weight(1f))

                        Column(
                            modifier = modifier
                                .weight(1f)
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                        ) {
                            for (i in newMovieState.productionCountries.size - 1 downTo 0) {
                                SelectButton(
                                    modifier = modifier.fillMaxWidth(),
                                    text = TranslateCode.ISO_3166_1[newMovieState.productionCountries[i]] ?: stringResource(
                                        id = R.string.unknown
                                    ),
                                    isSelected = false,
                                    onSelect = {
                                        newMovieDialogViewModel.removeMovieProductionCountry(
                                            newMovieState.productionCountries[i]
                                        )
                                    }
                                )
                            }
                        }
                    }
                    // Logo Urls
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                    ) {
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = modifier.weight(1f),
                                text = stringResource(id = R.string.movie_logo_url_label)
                            )
                            AddOrDeleteButton(
                                modifier = modifier.weight(1f),
                                isAddButton = true,
                                contentDescription = stringResource(id = R.string.add_movie_logo_url_content_description),
                                onClick = {
                                    newMovieDialogViewModel.addMovieLogoUrl()
                                }
                            )
                        }
                        for (i in movieLogoUrlList.size - 1 downTo 0) {
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = movieLogoUrlList[i],
                                onValueChange = {
                                    newMovieDialogViewModel.updateMovieLogoUrl(
                                        index = i,
                                        url = it
                                    )
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.movie_logo_url_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.movie_logo_url_placeholder))
                                },
                                maxLines = 1,
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            newMovieDialogViewModel.removeMovieLogoUrl(i)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = stringResource(id = R.string.delete_movie_logo_url_content_description)
                                        )
                                    }
                                }
                            )
                        }
                    }
                    // Main Poster Url
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.posterUrl ?: "",
                        onValueChange = {
                            newMovieDialogViewModel.updateMoviePosterUrl(
                                url = it
                            )
                        },
                        label = {
                            Text(text = stringResource(id = R.string.movie_main_poster_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.movie_main_poster_placeholder))
                        },
                        maxLines = 1
                    )
                    // Poster Urls
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                    ) {
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = modifier.weight(1f),
                                text = stringResource(id = R.string.movie_poster_url_label)
                            )
                            AddOrDeleteButton(
                                modifier = modifier.weight(1f),
                                isAddButton = true,
                                contentDescription = stringResource(id = R.string.add_movie_poster_url_content_description),
                                onClick = {
                                    newMovieDialogViewModel.addMoviePosterUrl()
                                }
                            )
                        }
                        for (i in moviePosterUrlList.size - 1 downTo 0) {
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = moviePosterUrlList[i],
                                onValueChange = {
                                    newMovieDialogViewModel.updateMoviePosterUrl(
                                        index = i,
                                        url = it
                                    )
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.movie_poster_url_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.movie_poster_url_placeholder))
                                },
                                maxLines = 1,
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            newMovieDialogViewModel.removeMoviePosterUrl(i)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = stringResource(id = R.string.delete_movie_poster_url_content_description)
                                        )
                                    }
                                }
                            )
                        }
                    }
                    // Main Backdrop Url
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.landscapeImageUrl ?: "",
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieLandscapeImageUrl(
                                url = it
                            )
                        },
                        label = {
                            Text(text = stringResource(id = R.string.movie_main_backdrop_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.movie_main_backdrop_placeholder))
                        },
                        maxLines = 1
                    )
                    // Backdrop Urls
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                    ) {
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = modifier.weight(1f),
                                text = stringResource(id = R.string.movie_backdrop_url_label)
                            )
                            AddOrDeleteButton(
                                modifier = modifier.weight(1f),
                                isAddButton = true,
                                contentDescription = stringResource(id = R.string.add_movie_backdrop_url_content_description),
                                onClick = {
                                    newMovieDialogViewModel.addMovieLandscapeImageUrl()
                                }
                            )
                        }
                        for (i in movieBackdropUrlList.size - 1 downTo 0) {
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = movieBackdropUrlList[i],
                                onValueChange = {
                                    newMovieDialogViewModel.updateMovieLandscapeImageUrl(
                                        index = i,
                                        url = it
                                    )
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.movie_backdrop_url_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.movie_backdrop_url_placeholder))
                                },
                                maxLines = 1,
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            newMovieDialogViewModel.removeMovieLandscapeImageUrl(i)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = stringResource(id = R.string.delete_movie_backdrop_url_content_description)
                                        )
                                    }
                                }
                            )
                        }
                    }
                    // Homepage Url
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.homepageUrl ?: "",
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieHomepage(
                                url = it
                            )
                        },
                        label = {
                            Text(text = stringResource(id = R.string.movie_homepage_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.movie_homepage_placeholder))
                        },
                        maxLines = 1
                    )
                    // Video
                    // Status
                    // Budget
                    // Revenue
                }
                // Button
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            updateAddingState(false)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }
                    Button(
                        onClick = {
                            newMovieDialogViewModel.clearNewMovieState()
                            updateAddingState(false)
                        },
                        enabled = movieKey.isNotBlank() && newMovieState.isValid() && (collectionState?.isValid() ?: true) && newMovieDialogViewModel.isMovieCompanyListValid()
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
fun AddOrDeleteButton(
    modifier: Modifier = Modifier,
    isAddButton: Boolean,
    contentDescription: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isAddButton) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
            contentColor = if (isAddButton) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
        )
    ) {
        Icon(
            imageVector = if (isAddButton) Icons.Filled.Add else Icons.Filled.Delete,
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