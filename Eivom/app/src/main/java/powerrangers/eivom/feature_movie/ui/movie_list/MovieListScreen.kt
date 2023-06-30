package powerrangers.eivom.feature_movie.ui.movie_list

import android.graphics.drawable.Drawable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
//        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
//        modifier = modifier
//    )
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        //Text(text = "Menu")
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.primary)
                .fillMaxHeight()
        ){
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Filter,
                    contentDescription = stringResource(id = R.string.filter_button),
                    tint = Color.White
                )
            }

            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Sort,
                    contentDescription = stringResource(id = R.string.sort_button),
                    tint = Color.White
                )
            }

            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.search_button),
                    tint = Color.White
                )
            }
        }

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
                    CircularProgressIndicator(color = MaterialTheme.colors.primary)
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
fun FilterButton()
{

}
