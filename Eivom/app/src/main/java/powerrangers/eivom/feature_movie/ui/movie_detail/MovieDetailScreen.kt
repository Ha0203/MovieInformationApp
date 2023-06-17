package powerrangers.eivom.feature_movie.ui.movie_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.feature_movie.domain.utility.Resource
import powerrangers.eivom.feature_movie.domain.utility.ResourceErrorMessage
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar

@Composable
fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    navigateToMenuItem: (String) -> Unit,
    navigateBack: () -> Unit
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
        MovieDetailBody(modifier = modifier.padding(innerPadding))
    }
}

@Composable
fun MovieDetailBody(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
) {
    val movie by remember { viewModel.movieInformation }

    val defaultBackgroundColor = MaterialTheme.colors.background
    var backgroundColor by remember {
        mutableStateOf(defaultBackgroundColor)
    }

    when (movie) {
        is Resource.Loading -> {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        is Resource.Success -> {
            Box(modifier = modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            backgroundColor,
                            defaultBackgroundColor
                        )
                    )
                )
                .fillMaxSize()
            ) {
                Column(
                    modifier = modifier
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SubcomposeAsyncImage(
                        modifier = modifier.fillMaxWidth(),
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(movie.data!!.landscapeImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = movie.data!!.title,
                        contentScale = ContentScale.Fit,
                        loading = {
                            CircularProgressIndicator(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .scale(0.25f)
                            )
                        }
                    )
                    SubcomposeAsyncImage(
                        modifier = modifier.fillMaxWidth(),
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(movie.data!!.posterUrl)
                            .crossfade(true)
                            .build(),
                        onSuccess = { image ->
                            viewModel.handleMovieDominantColor(image.result.drawable) { color ->
                                backgroundColor = color
                            }
                        },
                        contentDescription = movie.data!!.title,
                        contentScale = ContentScale.Fit,
                        loading = {
                            CircularProgressIndicator(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .scale(0.25f)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Adult: ${movie.data!!.adult} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Budget: ${movie.data!!.budget} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Homepage URL: ${movie.data!!.homepageUrl} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- ID: ${movie.data!!.id} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Original Language: ${movie.data!!.originalLanguage} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Original Title: ${movie.data!!.originalTitle} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Overview: ${movie.data!!.overview} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Revenue: ${movie.data!!.revenue} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Length: ${movie.data!!.length} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Status: ${movie.data!!.status} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Tagline: ${movie.data!!.tagline} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Title: ${movie.data!!.title} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Vote Average: ${movie.data!!.voteAverage} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Vote Count: ${movie.data!!.voteCount} ---")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Collection ---")
                    Text(text = "ID: ${movie.data!!.collection.id}")
                    Text(text = "Name: ${movie.data!!.collection.name}")
                    Text(text = "Backdrop path: ${movie.data!!.collection.backdrop_path}")
                    Text(text = "Poster path: ${movie.data!!.collection.poster_path}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Genres ---")
                    for (genre in movie.data!!.genres) {
                        Text(text = genre)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Production Companies ---")
                    for (company in movie.data!!.productionCompanies) {
                        Text(text = "ID: ${company.id}")
                        Text(text = "Name: ${company.name}")
                        Text(text = "Logo path: ${company.logo_path}")
                        Text(text = "Origin country: ${company.origin_country}")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(text = "--- Production Countries ---")
                    for (country in movie.data!!.productionCountries) {
                        Text(text = country)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "--- Spoken Languages ---")
                    for (language in movie.data!!.spokenLanguages) {
                        Text(text = "${language.name} - ${language.english_name} - ${language.iso_639_1}")
                    }
                }
            }
        }
        else -> {
            Text(text = movie.message ?: ResourceErrorMessage.UNKNOWN)
        }
    }
}