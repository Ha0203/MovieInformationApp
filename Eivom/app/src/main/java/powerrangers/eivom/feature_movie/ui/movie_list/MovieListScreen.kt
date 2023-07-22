package powerrangers.eivom.feature_movie.ui.movie_list

import android.app.DatePickerDialog
import android.graphics.drawable.Drawable
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.feature_movie.domain.model.MovieListItem
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.domain.utility.ResourceErrorMessage
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale


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
                        onDone = {  }
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
                IconButton(onClick = {
                    viewModel.reverseIsTrendingDay()
                    if (filterState.isTrendingWeek) viewModel.reverseIsTrendingWeek()
                } ) {
                    if (!filterState.isTrendingDay)
                        Icon(
                            painter = painterResource(R.drawable.unchecked_ic),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.primary
                        )
                    else    Icon(
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
                IconButton(onClick = {
                    viewModel.reverseIsTrendingWeek()
                    if (filterState.isTrendingDay) viewModel.reverseIsTrendingDay()
                } ) {
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RegionFilter(
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
){
//    val expanded by remember { viewModel.isRegionExpanded }
    val regions = TranslateCode.ISO_3166_1.values.toList()
    val regionSelected by remember { viewModel.regionSelected }
    //var showMenu = remember { mutableStateOf(false)}

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.Region_FilterState),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        // Region selection
        Box(
            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            OutlinedButton(
                onClick = {
                    if (regionSelected.isEmpty())
                    {
                        viewModel.changeRegionSelect(regions[0])
                    }
                    else {
                        viewModel.resetRegionSelect()
                    }
                },

            ) {
                Text(
                    text = regionSelected.ifEmpty { "Select Region" },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .widthIn(min = 60.dp)
                        .height(20.dp),

                )

                DropdownMenu(
                    expanded = regionSelected.isEmpty(),
                    onDismissRequest = {
                        viewModel.resetRegionSelect()
                        //showMenu.value = false
                    },
                    modifier = Modifier.border(2.dp, Color.LightGray)
                ) {
                    Column(
                        modifier = Modifier
                            .width(IntrinsicSize.Min)
                            .heightIn(max = DropdownMenuHeight(visibleItems = 5))
                            .verticalScroll(rememberScrollState())
                    ){
                        regions.forEach() { region ->
                            DropdownMenuItem(onClick = { viewModel.changeRegionSelect(region) }) {
                                Text(text = region)
                            }
                        }
                    }

                }
            }

        }
    }
}

@Composable
fun DropdownMenuHeight(visibleItems: Int): Dp {
    val itemHeight = 48.dp // Height of each item in the dropdown menu
    val padding = 8.dp // Vertical padding between items

    return (visibleItems * itemHeight) + ((visibleItems - 1) * padding)
}

@Composable
fun AdultConTentFilter(
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
)
{
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.AdultContentIncluded_FilterState),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { if (!filterState.isTrending ) viewModel.reverseAdultContet()} ) {
            if (!filterState.AdultContentIncluded )
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
fun ReleaseDateFilter(
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val newRD by remember { viewModel.releaseDate }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.ReleaseYear_FilterState),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = newRD,
            onValueChange = { newValue ->
                val filteredValue = newValue.filter { it.isDigit() }
                if (newValue.length <= 4) {
                    viewModel.updateReleaseDate(filteredValue)
                }
                else {
                    viewModel.updateReleaseDate("")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            visualTransformation = VisualTransformation.None,
            textStyle = TextStyle(
                fontSize = 9.5.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Left,
                //background = Color.White
            ),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_year) , contentDescription = null,
                    modifier = Modifier
                        .size(15.dp),
                    tint = MaterialTheme.colors.primary,
                )
            },
            shape = RoundedCornerShape(5.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor =  Color.Gray,
                placeholderColor = MaterialTheme.colors.primary,
                textColor = MaterialTheme.colors.primary,
                backgroundColor = Color.White,

            ),
            placeholder = {
                Text(
                    text = LocalDate.now().year.toString(),
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        color = Color.LightGray,
                        textAlign = TextAlign.Left
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            },

            modifier = Modifier
                .size(width = 90.dp, height = 45.dp)
                .border(0.5.dp, Color.LightGray, shape = RoundedCornerShape(4.dp)),
        )
    }
}

@Composable
fun MinDateFilter(
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val newMinRD by remember { viewModel.minReleaseDate }
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.MinimumReleaseDate_FilterState),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = newMinRD ?: "",
            onValueChange = { viewModel.updateMinReleaseDate(it) },
            label = { Text("Select a date") },
            visualTransformation = VisualTransformation.None,
            textStyle = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        newMinRD?.let {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            calendar.time = dateFormat.parse(newMinRD) ?: calendar.time
                        }

                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val newcalendar = Calendar.getInstance()
                                newcalendar.set(year, month, dayOfMonth)
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                viewModel.updateMinReleaseDate(dateFormat.format(newcalendar.time))
                                //viewModel.reverseDatePicker()
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                  },
                ) {
                    Icon(
                        Icons.Default.DateRange, contentDescription = "Select a date",
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(5.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor =  Color.Gray,
                placeholderColor = MaterialTheme.colors.primary,
                textColor = MaterialTheme.colors.primary,
                backgroundColor = Color.White,
            ),
            modifier = Modifier
                .size(width = 70.dp, height = 45.dp)
        )
    }
}

@Composable
fun MaxDateFilter(
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val newMaxRD by remember { viewModel.maxReleaseDate }
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.MaximumReleaseDate_FilterState),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = newMaxRD ?: "",
            onValueChange = { viewModel.updateMinReleaseDate(it) },
            label = { Text("Select a date") },
            visualTransformation = VisualTransformation.None,
            textStyle = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        newMaxRD?.let {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            calendar.time = dateFormat.parse(newMaxRD) ?: calendar.time
                        }

                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val newcalendar = Calendar.getInstance()
                                newcalendar.set(year, month, dayOfMonth)
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                viewModel.updateMinReleaseDate(dateFormat.format(newcalendar.time))
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                ) {
                    Icon(
                        Icons.Default.DateRange, contentDescription = "Select a date",
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(5.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor =  Color.Gray,
                placeholderColor = MaterialTheme.colors.primary,
                textColor = MaterialTheme.colors.primary,
                backgroundColor = Color.White,
            ),
            modifier = Modifier
                .size(width = 70.dp, height = 45.dp)
        )
    }
}

@Composable
fun MinRating(
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val newFloat by remember { viewModel.minRating }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.MinimumRating_FilterState),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = newFloat?: "",
            onValueChange = { newValue ->
                // Validate the input to ensure it falls within the desired range
                if ( newValue.length < 3 ||
                    (
                            newValue.length == 3 && newValue.toFloat() in 1.0f..5.0f
                    )
                ){
                    viewModel.updateMinRating(newValue)
                } else {
                    // If the input is not a valid float within the range, do not update the value
                    viewModel.updateMinRating("")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            visualTransformation = VisualTransformation.None,
            textStyle = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Left,

                //background = Color.White
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Star, contentDescription = null,
                    modifier = Modifier.size(15.dp), tint = MaterialTheme.colors.primary
                )
            },
            shape = RoundedCornerShape(5.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor =  Color.Gray,
                placeholderColor = MaterialTheme.colors.primary,
                textColor = MaterialTheme.colors.primary,
                backgroundColor = Color.White,

                ),
            placeholder = {
                Text(
                    text = "1.0",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        color = Color.LightGray,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            },
            modifier = Modifier
                .size(width = 90.dp, height = 45.dp)
                .border(0.5.dp, Color.LightGray, shape = RoundedCornerShape(4.dp)),
        )
    }
}

@Composable
fun MaxRating(
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val newFloat by remember { viewModel.maxRating }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.MaximumRating_FilterState),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = newFloat?: "",
            onValueChange = { newValue ->
                // Validate the input to ensure it falls within the desired range
                if ( newValue.length < 3 ||
                    (
                            newValue.length == 3 && newValue.toFloat() in 1.0f..5.0f
                    )
                ){
                    viewModel.updateMaxRating(newValue)
                } else {
                    // If the input is not a valid float within the range, do not update the value
                    viewModel.updateMaxRating("")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            visualTransformation = VisualTransformation.None,
            textStyle = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Left,

                //background = Color.White
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Star, contentDescription = null,
                    modifier = Modifier.size(15.dp), tint = MaterialTheme.colors.primary
                )
            },
            shape = RoundedCornerShape(5.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor =  Color.Gray,
                placeholderColor = MaterialTheme.colors.primary,
                textColor = MaterialTheme.colors.primary,
                backgroundColor = Color.White,

                ),
            placeholder = {
                Text(
                    text = "5.0",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        color = Color.LightGray,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            },
            modifier = Modifier
                .size(width = 90.dp, height = 45.dp)
                .border(0.5.dp, Color.LightGray, shape = RoundedCornerShape(4.dp)),
        )
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FilterDialog(
    funcToCall: () -> Unit,
    onDismiss: () -> Unit,
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = true),
        content = {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .background(MaterialTheme.colors.surface)
                    .fillMaxWidth()
                    .width(300.dp)
                    .height(400.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.filter_title),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                        .weight(0.2f)
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    item{
                        TrendingFilter(filterState = filterState, viewModel = viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(5.dp)) }
                    // Favorite Filter
                    item{
                        FavoriteFilter(filterState = filterState, viewModel = viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(5.dp)) }
                    // Adult Content Filter
                    item{
                        AdultConTentFilter(filterState = filterState, viewModel = viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(5.dp)) }
                    // Region Filter
                    item{
                        RegionFilter(filterState = filterState, viewModel = viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                    // Release Date Filter
                    item{
                        ReleaseDateFilter(filterState = filterState, viewModel = viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                    // Min release Date
                    item{
                        MinDateFilter(filterState = filterState, viewModel = viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                    // Max release Date
                    item{
                        MaxDateFilter(filterState = filterState, viewModel = viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                    // Min Rating Point
                    item{
                        MinRating(filterState = filterState, viewModel = viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                    // Max Rating Point
                    item{
                        MaxRating(filterState = filterState, viewModel = viewModel)
                    }
                    // Add other filter items here
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .weight(0.2f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            // Handle Cancel button action
                            onDismiss()
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }

                    Button(
                        onClick = {
                            funcToCall()
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.confirm_button))
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FilterButton(
    funcToCall: () -> Unit,
    onDismiss: () -> Unit,
    filterState: FilterState,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = showDialog.value,
        enter = scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(),
        exit = scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut()
    ) {
        FilterDialog(funcToCall, onDismiss, filterState, viewModel)
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