package powerrangers.eivom.feature_movie.ui.movie_list

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.feature_movie.domain.model.MovieListItem
import powerrangers.eivom.feature_movie.domain.utility.Resource
import powerrangers.eivom.feature_movie.domain.utility.ResourceErrorMessage
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar






@Composable
fun MovieListScreen(
    modifier: Modifier = Modifier,
    navigateToMenuItem: (String) -> Unit,
    navigateToMovieDetailScreen: (Int) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name).uppercase(),
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
        MovieListBody(
            modifier = modifier.padding(innerPadding),
            navigateToMovieDetailScreen = navigateToMovieDetailScreen
        )
    }
}

@Composable
fun MovieListBody(
    modifier: Modifier = Modifier,
    navigateToMovieDetailScreen: (Int) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val movieListItems by remember { viewModel.movieListItems }
    val isFilterVisible by remember { viewModel.isFilterVisible }
    val isSearchVisible by remember { viewModel.isSearchVisible }
    val isSortVisible by remember { viewModel.isSortVisible }

    val filterState by remember { viewModel.filterState  }
    //val textSizeState = remember { mutableStateOf(15.sp)}
    //val userSearch by remember { viewModel.userSearch }

    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //Text(text = "Menu")
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxHeight()
        ) {
            IconButton(onClick = { viewModel.reverseIsFilter() }) {
                Icon(
                    imageVector = Icons.Filled.Filter,
                    contentDescription = stringResource(id = R.string.filter_button),
                    tint = MaterialTheme.colors.primary
                )
                //Create dialog
            }
            if (isFilterVisible) {
                FilterButton(
                    funcToCall = {
                        viewModel.reverseIsFilter()
                    },
                    onDismiss = { viewModel.reverseIsFilter() },
                    filterState = filterState,
                )
            }
//
            IconButton(onClick = { viewModel.reverseIsSort() }) {
                Icon(
                    imageVector = Icons.Filled.Sort,
                    contentDescription = stringResource(id = R.string.sort_button),
                    tint = MaterialTheme.colors.primary,
                )
            }
            if (isSortVisible) {
                SortButton(
                    funcToCall = {
                        viewModel.reverseIsSort()
                    },
                    onDismiss = { viewModel.reverseIsSort() }
                )
            }

            IconButton(onClick = { viewModel.reverseIsSearch() }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.search_button),
                    tint = MaterialTheme.colors.primary
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            AnimatedVisibility(
                visible = isSearchVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                ) + fadeIn(),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                ) + fadeOut(),
                modifier = Modifier.background(MaterialTheme.colors.surface)
            ) {
                // Content of the filter screen
                OutlinedTextField(
                    value = viewModel.userSearch,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 200.dp, height = 55.dp)
                        .padding(horizontal = 10.dp)
                    //.padding(start = 10.dp, end = 10.dp, top = 5.dp,)
                    ,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
                    ),
                    onValueChange = { viewModel.updateUserSearch(it) },
                    label = { Text(stringResource(R.string.search_label)) },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.search_movie_placeholder),
                            style = TextStyle(
                                fontSize = 12.sp, // Adjust the font size as desired
                                color = Color.Gray
                            )
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(id = R.string.search_icon_outlineText),
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable { /*To Do*/ } //Click Search Button
                        )
                    },
                    textStyle = TextStyle(
//                        fontSize = textSizeState.value,
//                        textAlign = TextAlign.Start
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, // Adjust the font weight as needed
                        textAlign = TextAlign.Start
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { /*To Do*/ }
                    )
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    .padding(top = 10.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = modifier
                        .background(MaterialTheme.colors.background)
                        .fillMaxSize()
                        .padding(
                            top = 5.dp,
                            start = 10.dp,
                            end = 10.dp
                        ),

                    //horizontalAlignment = CenterHorizontally
                ) {
                    itemsIndexed(movieListItems.data!!) { index, movie ->
                        if (index >= movieListItems.data!!.size - 1 && movieListItems is Resource.Success) {
                            viewModel.loadMoviePaginated()
                        }
                        MovieListEntry(
                            modifier = modifier,
                            navigateToMovieDetailScreen = navigateToMovieDetailScreen,
                            movie = movie,
                            handleMovieDominantColor = { drawable, onFinish ->
                                viewModel.handleMovieDominantColor(
                                    drawable = drawable,
                                    onFinish = onFinish
                                )
                            },
                            addFavoriteMovie = {
                                val isSuccess = coroutineScope.async {
                                    viewModel.addFavoriteMovie(it)
                                }
                                isSuccess.await()
                            },
                            deleteFavoriteMovie = {
                                val isSuccess = coroutineScope.async {
                                    viewModel.deleteFavoriteMovie(it.id)
                                }
                                isSuccess.await()
                            },
                            isFavoriteMovie = { viewModel.isFavoriteMovie(it) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                }
                Box(
                    contentAlignment = Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (movieListItems) {
                        is Resource.Loading -> {
                            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
                        }
                        is Resource.Error -> {
                            RetrySection(
                                error = movieListItems.message ?: ResourceErrorMessage.UNKNOWN,
                                onRetry = {
                                    viewModel.loadMoviePaginated()
                                }
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }

}

@Composable
fun MovieListEntry(
    modifier: Modifier = Modifier,
    movie: MovieListItem,
    navigateToMovieDetailScreen: (Int) -> Unit,
    handleMovieDominantColor: (Drawable, (Color) -> Unit) -> Unit,
    addFavoriteMovie: suspend (MovieListItem) -> Boolean,
    deleteFavoriteMovie: suspend (MovieListItem) -> Boolean,
    isFavoriteMovie: (Int) -> Boolean
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    var isFavorite by remember {
        mutableStateOf(isFavoriteMovie(movie.id))
    }
    val coroutineScope = rememberCoroutineScope()
    val addError = stringResource(id = R.string.add_favorite_movie_failure)
    val deleteError = stringResource(id = R.string.delete_favorite_movie_failure)
    val showErrorDialog = remember { mutableStateOf(false) }
    val errorDescription = remember { mutableStateOf("") }

    if (showErrorDialog.value) {
        ErrorDialog(
            error = errorDescription.value,
            onRetry = { showErrorDialog.value = false },
            onDismiss = { showErrorDialog.value = false }
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .height(350.dp)
            .width(250.dp)
            .clickable {
                navigateToMovieDetailScreen(movie.id)
            }
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(movie.posterUrl)
                    .crossfade(true)
                    .build(),
                onSuccess = { image ->
                    handleMovieDominantColor(image.result.drawable) { color ->
                        dominantColor = color
                    }
                },
                contentDescription = movie.title,
                contentScale = ContentScale.Fit,
                loading = {
                    CircularProgressIndicator(
                        modifier = modifier
                            .fillMaxWidth()
                            .scale(0.25f)
                    )
                }
            )
            Text(
                text = movie.title,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp
                    )
            )
            FavoriteMovieButton(
                isFavorite = isFavorite,
                onFavoriteToggle = { isChecked ->
                    coroutineScope.launch {
                        val isSuccess = if (isFavorite) {
                            errorDescription.value = deleteError
                            deleteFavoriteMovie(movie)
                        } else {
                            errorDescription.value = addError
                            addFavoriteMovie(movie)
                        }
                        if (isSuccess) {
                            isFavorite = isChecked
                        } else {
                            showErrorDialog.value = true
                        }
                    }
                },
                checkedColor = Color.Red,
                uncheckedColor = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
fun FavoriteMovieButton(
    isFavorite: Boolean,
    onFavoriteToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkedColor: Color = MaterialTheme.colors.primary,
    uncheckedColor: Color = MaterialTheme.colors.onSurface
) {
    val favoriteIcon = if (isFavorite) {
        Icons.Filled.Favorite
    } else {
        Icons.Default.Favorite
    }

    IconButton(
        onClick = { onFavoriteToggle(!isFavorite) },
        modifier = modifier
    ) {
        Icon(
            imageVector = favoriteIcon,
            contentDescription = if (isFavorite) {
                stringResource(R.string.favorite_movie_description)
            } else {
                stringResource(R.string.unfavorite_movie_description)
            },
            tint = if (isFavorite) checkedColor else uncheckedColor
        )
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.retry_button))
        }
    }
}

@Composable
fun TrendingFilter(
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
)
{
    Column(
        modifier = Modifier
        .animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.Trending_FilterState),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {viewModel.reverseIsTrending()} ) {
                if (!filterState.isTrending)
                    Icon(
                        painter = painterResource(R.drawable.unchecked_ic),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colors.primary
                    )
                else Icon(
                    painter = painterResource(R.drawable.checked_ic),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colors.primary
                )
            }
        }

        if (filterState.isTrending)
        {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.TrendingDay_FilterState),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer( modifier = Modifier.weight(0.10f))
                IconButton(onClick = {viewModel.reverseIsTrendingDay()} ) {
                    if (!filterState.isTrendingDay)
                        Icon(
                            painter = painterResource(R.drawable.unchecked_ic),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.primary
                        )
                    else Icon(
                        painter = painterResource(R.drawable.checked_ic),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colors.primary
                    )
                }

                Spacer( modifier = Modifier.weight(0.25f))

                Text(
                    text = stringResource(id = R.string.TrendingWeek_FilterState),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer( modifier = Modifier.weight(0.10f))
                IconButton(onClick = {viewModel.reverseIsTrendingWeek()} ) {
                    if (!filterState.isTrendingWeek)
                        Icon(
                            painter = painterResource(R.drawable.unchecked_ic),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.primary
                        )
                    else Icon(
                        painter = painterResource(R.drawable.checked_ic),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        }
        else viewModel.setAllTrendingDefault()
    }
}

@Composable
fun FavoriteFilter(
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
)
{
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.Favorite_FilterState),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { if (!filterState.isTrending ) viewModel.reverseIsFavorite()} ) {
            if (!filterState.isFavorite )
                Icon(
                    painter = painterResource(R.drawable.unchecked_ic),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colors.primary
                )
            else
                Icon(
                painter = painterResource(R.drawable.checked_ic),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
fun FilterButton(
    funcToCall: () -> Unit,
    onDismiss: () -> Unit,
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(true) }
    val textList = remember { mutableStateListOf("Action", "Science Fiction", "Horror") }


    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
                showDialog.value = false
            },
            modifier = Modifier
                    .height(400.dp),
            //properties = DialogProperties(width = 300.dp, height = 400.dp),
            title = {
                Text(
                    text = stringResource(id = R.string.filter_title),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )
            },
            text = {
                Column() {
//                    item { TrendingFilter(filterState = filterState, viewModel = viewModel) }
//                    item { FavoriteFilter(filterState = filterState, viewModel = viewModel) }
                    // Trending Filter
                    TrendingFilter(filterState = filterState, viewModel = viewModel)
                    // Favorite Filter
                    FavoriteFilter(filterState = filterState, viewModel = viewModel)
                    // Adding a gap to push the button fixed to the bottom
                    Spacer(modifier = Modifier.weight(1f))
                }

            },
            confirmButton = {

                Button(
                    onClick = {
                        funcToCall()
                        showDialog.value = false
                    },
                    //modifier = Modifier.fillMaxHeight()
                ) {
                    Text(text = stringResource(id = R.string.confirm_button))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Handle Cancel button action
                        onDismiss()
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel_button))
                }
            }
        )
    }
}

@Composable
fun SortButton(
    funcToCall: () -> Unit,
    onDismiss: () -> Unit
) {
    val showDialog = remember { mutableStateOf(true) }
    val textList = remember { mutableStateListOf("Name", "Latest Date", "Star") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
                showDialog.value = false
            },
            title = { Text(text = stringResource(id = R.string.sort_title)) },
            text = {
                Column {
                    textList.forEach { text ->
                        Text(
                            text = text,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        funcToCall()
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.confirm_button))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Handle Cancel button action
                        onDismiss()
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel_button))
                }
            },
        )
    }
}

@Composable
fun ErrorDialog(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,

) {
    val showDialog = remember { mutableStateOf(true) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
                showDialog.value = false
            },
            title = { Text(text = stringResource(id = R.string.error_label)) },
            text = {
                Text(text = error)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRetry()
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.retry_button))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Handle Cancel button action
                        onDismiss()
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel_button))
                }
            }

        )
    }
}