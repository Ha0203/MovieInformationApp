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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Filter
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
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
    val movieListItems by remember { viewModel.movieListItems }
    val isFilterVisible by remember { viewModel.isFilterVisible }
    val isSearchVisible by remember { viewModel.isSearchVisible }
    val isSortVisible by remember { viewModel.isSortVisible }

    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //Text(text = "Menu")
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxHeight()
        ){
                IconButton(onClick = { viewModel.reverseIsFilter() }) {
                    Icon(
                        imageVector = Icons.Filled.Filter,
                        contentDescription = stringResource(id = R.string.filter_button),
                        tint = MaterialTheme.colors.primary
                    )
                    //Create dialog
                }
                if (isFilterVisible){
                    FilterButton(funcToCall = {}, onDismiss = {})
                }
//                AnimatedVisibility(
//                    visible = isFilterVisible,
//                    enter = slideInVertically(
//                        initialOffsetY = { -it },
//                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
//                    ) + fadeIn(),
//                    exit = slideOutVertically(
//                        targetOffsetY = { -it },
//                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
//                    ) + fadeOut(),
//                ) {
//                    // Content of the filter screen
//                    Text(text = "Filter", color = MaterialTheme.colors.primary)
//                }


            IconButton(onClick = { viewModel.reverseIsSort() }) {
                Icon(
                    imageVector = Icons.Filled.Sort,
                    contentDescription = stringResource(id = R.string.sort_button),
                    tint = MaterialTheme.colors.primary,
                )
            }
            if (isSortVisible){
                SortButton(funcToCall = {}, onDismiss = {})
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
            ) {
                // Content of the filter screen
                OutlinedTextField(
                    value = "",
                    singleLine = true,
                    shape = shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 200.dp, height = 50.dp)
                        .padding(start = 10.dp, end = 10.dp)
                    ,
                    onValueChange = { /*To Do*/ },
                    label = { Text(stringResource(R.string.search_label)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { /*To Do*/ }
                    )
                )
            }
            //Box(){}
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
            ){
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
                        if (index >= movieListItems.data!!.size - 1 && movieListItems !is Resource.Loading) {
                            viewModel.loadMoviePaginated()
                        }
                        MovieListEntry(
                            modifier = modifier,
                            navigateToMovieDetailScreen = navigateToMovieDetailScreen,
                            movie = movie,
                            handleMovieDominantColor = { drawable, onFinish ->
                                viewModel.handleMovieDominantColor(drawable = drawable, onFinish = onFinish)
                            }
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
                                error = movieListItems.message?: ResourceErrorMessage.UNKNOWN,
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
    handleMovieDominantColor: (Drawable, (Color) -> Unit) -> Unit
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
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
            .height(300.dp)
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
        }
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
fun FilterButton(
    funcToCall: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
)
{
    val showDialog = remember { mutableStateOf(true)}
    val textList = remember { mutableStateListOf("Action", "Science Fiction", "Horror") }

    if (showDialog.value)
    {
        AlertDialog(
            onDismissRequest = {
//             Dismiss the dialog when the user clicks outside the dialog or on the back
//             button. If you want to disable that functionality, simply use an empty
//             onCloseRequest
                showDialog.value = false
            },
            title = { Text(text = "Select Filter") },
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
                        showDialog.value = false
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Handle Cancel button action
                        showDialog.value = false
                        onDismiss()
                    }
                ) {
                    Text(text = "Cancel")
                }
            }

        )
    }
}
@Composable
fun SortButton(
    funcToCall: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
)
{
    val showDialog = remember { mutableStateOf(true)}
    val textList = remember { mutableStateListOf("Name", "Latest Date", "Star") }

    if (showDialog.value)
    {
        AlertDialog(
            onDismissRequest = {
//             Dismiss the dialog when the user clicks outside the dialog or on the back
//             button. If you want to disable that functionality, simply use an empty
//             onCloseRequest
                showDialog.value = false
            },
            title = { Text(text = "Select Sort") },
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
                        showDialog.value = false
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Handle Cancel button action
                        showDialog.value = false
                        onDismiss()
                    }
                ) {
                    Text(text = "Cancel")
                }
            }

        )
    }
}