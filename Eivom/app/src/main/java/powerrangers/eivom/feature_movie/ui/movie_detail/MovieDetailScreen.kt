package powerrangers.eivom.feature_movie.ui.movie_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.domain.utility.ResourceErrorMessage
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar
import powerrangers.eivom.ui.theme.PoppinsBold
import powerrangers.eivom.ui.theme.PoppinsMedium

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

            LazyColumn (modifier = Modifier.padding(10.dp)) {
                //Top Button
                item{
                    TopButton()
                }

                // Title
                item{
                    FilmTitle()
                }

                // Trailer
                item {
                    Trailer()
                }

                // Overview
                item {
                    Overview()
                }

                // Genre
                item {
                    GenresList()
                }
            }
        }
        else -> {
            Text(text = movie.message ?: ResourceErrorMessage.UNKNOWN)
        }
    }
}

@Composable
fun TopButton(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    Row(){
        // Exit to Main Screen
        IconButton(onClick = { /*ADD Function*/  }) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .scale(-1f, 1f) // Flip horizontally
                    .padding(10.dp)
                ,
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        // Favorite
        IconButton(onClick = { /*ADD Function*/  }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_unfavorite),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .scale(-1f, 1f) // Flip horizontally
                    .padding(10.dp)
                ,
                tint = Color.Black
            )
        }
    }
}

@Composable
fun FilmTitle(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }
    val defaultBackgroundColor = MaterialTheme.colors.background
    var onFocusColor by remember {
        mutableStateOf(defaultBackgroundColor)
    }

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Spacer(modifier = Modifier.width(70.dp))

        // Film Poster
        SubcomposeAsyncImage(
            modifier = modifier
                .size(100.dp)
                .padding(13.dp)
                .clip(CircleShape)
            ,
            contentScale = ContentScale.Crop,
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(movie.data!!.posterUrl)
                .crossfade(true)
                .build(),
            onSuccess = { image ->
                viewModel.handleMovieDominantColor(image.result.drawable) { color ->
                    onFocusColor = color
                }
            },
            contentDescription = movie.data!!.title,
            loading = {
                CircularProgressIndicator(
                    modifier = modifier
                        .scale(0.25f)
                )
            }
        )

        Column(
            modifier = Modifier
                .padding(1.dp)
                .width(200.dp)
                .height(70.dp)
        ) {
            // Film Name
            val textSize  = movie.data!!.title.length
            Text(
                text = movie.data!!.title,
                color = onFocusColor,
                fontFamily = PoppinsBold,
                fontSize = 26.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
//              modifier = Modifier.width(100.dp)
            )

            // Production Name
            Text(
                text = "by ${movie.data!!.productionCompanies[0].name}",
                color = Color.LightGray,
                fontFamily = PoppinsBold,
                fontSize = 10.sp,
                maxLines = 1, // Limit the text to 1 line
                overflow = TextOverflow.Ellipsis // Show ellipsis if the text overflows
            )
        }
    }
}

@Composable
fun Trailer(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }
    LazyRow(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ){
        item {
            SubcomposeAsyncImage(
                modifier = modifier
                    .padding(top = 5.dp)
                    .height(200.dp)
                    .width(350.dp)
                    .clip(RoundedCornerShape(10.dp))
                ,
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(movie.data!!.landscapeImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = movie.data!!.title,
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator(
                        modifier = modifier
                            .fillMaxWidth()
                            .scale(0.25f)
                    )
                }
            )
        }
    }
}

@Composable
fun Overview(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }

    Column(
        modifier = Modifier.padding(top = 5.dp)
    ) {
        // Overview Tag
        Row(
            modifier = Modifier.padding(
                start = 17.dp,
                end = 17.dp,
                top = 25.dp,
                bottom = 3.dp
            )
        ){
            Text(
                text = "Overview",
                fontSize = 26.sp,
                fontFamily = PoppinsBold,
                color = MaterialTheme.colors.primary,
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color.Yellow,
                modifier = Modifier
                    .size(40.dp)
                    .padding(top = 5.dp)
                ,
            )

            Text(
                text = "${movie.data!!.voteAverage}",
                fontSize = 20.sp,
                fontFamily = PoppinsBold,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 7.dp)
            )

        }

        // Overview Detail
        Text(
            text = "${movie.data!!.overview}",
            fontSize = 14.sp,
            fontFamily = PoppinsMedium,
            textAlign = TextAlign.Start,
            color = Color.Black,
            modifier = Modifier
                .padding(
                start = 18.dp,
                end = 18.dp
                )
        )
    }

}

@Composable
fun GenresList(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }

    Row(
        modifier = Modifier.padding(
            17.dp
        )
    ){
        Text(
            text = "Genres",
            fontSize = 26.sp,
            fontFamily = PoppinsBold,
            color = MaterialTheme.colors.primary,
        )

        Spacer(modifier = Modifier.weight(1f))
        val genreList = movie.data!!.genres
        LazyRow(
            modifier = Modifier
                .height(50.dp)
                .width(130.dp)
                .padding(5.dp)
            ,
        ){
            items(genreList) {genre ->
                GenreBox(genre)
            }
        }

    }
}

@Composable
fun GenreBox(
    genre: String
){
    Card(
        backgroundColor = Color.Black,
        modifier = Modifier
            .height(30.dp)
            .padding(5.dp)
        ,
        shape = RoundedCornerShape(8.dp)

    ){
        Text(
            text = "${genre}",
            fontSize = 10.sp,
            fontFamily = PoppinsMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp)
        )
    }

}


//Box(modifier = modifier
//.background(
//Brush.verticalGradient(
//listOf(
//backgroundColor,
//defaultBackgroundColor
//)
//)
//)
//.fillMaxSize()
//) {
//    Column(
//        modifier = modifier
//            .verticalScroll(rememberScrollState()),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        SubcomposeAsyncImage(
//            modifier = modifier.fillMaxWidth(),
//            model = ImageRequest.Builder(context = LocalContext.current)
//                .data(movie.data!!.landscapeImageUrl)
//                .crossfade(true)
//                .build(),
//            contentDescription = movie.data!!.title,
//            contentScale = ContentScale.Fit,
//            loading = {
//                CircularProgressIndicator(
//                    modifier = modifier
//                        .fillMaxWidth()
//                        .scale(0.25f)
//                )
//            }
//        )
//        SubcomposeAsyncImage(
//            modifier = modifier.fillMaxWidth(),
//            model = ImageRequest.Builder(context = LocalContext.current)
//                .data(movie.data!!.posterUrl)
//                .crossfade(true)
//                .build(),
//            onSuccess = { image ->
//                viewModel.handleMovieDominantColor(image.result.drawable) { color ->
//                    backgroundColor = color
//                }
//            },
//            contentDescription = movie.data!!.title,
//            contentScale = ContentScale.Fit,
//            loading = {
//                CircularProgressIndicator(
//                    modifier = modifier
//                        .fillMaxWidth()
//                        .scale(0.25f)
//                )
//            }
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Adult: ${movie.data!!.adult} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Budget: ${movie.data!!.budget} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Homepage URL: ${movie.data!!.homepageUrl} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- ID: ${movie.data!!.id} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Original Language: ${movie.data!!.originalLanguage} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Original Title: ${movie.data!!.originalTitle} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Overview: ${movie.data!!.overview} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Revenue: ${movie.data!!.revenue} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Length: ${movie.data!!.length} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Status: ${movie.data!!.status} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Tagline: ${movie.data!!.tagline} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Title: ${movie.data!!.title} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Vote Average: ${movie.data!!.voteAverage} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Vote Count: ${movie.data!!.voteCount} ---")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Collection ---")
//        Text(text = "ID: ${movie.data!!.collection.id}")
//        Text(text = "Name: ${movie.data!!.collection.name}")
//        Text(text = "Backdrop path: ${movie.data!!.collection.landscapeImageUrl}")
//        Text(text = "Poster path: ${movie.data!!.collection.posterUrl}")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "Region release date: ${movie.data!!.regionReleaseDate}")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Genres ---")
//        for (genre in movie.data!!.genres) {
//            Text(text = genre)
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Production Companies ---")
//        for (company in movie.data!!.productionCompanies) {
//            Text(text = "ID: ${company.id}")
//            Text(text = "Name: ${company.name}")
//            Text(text = "Logo path: ${company.logoImageUrl}")
//            Text(text = "Origin country: ${company.originCountry}")
//            Spacer(modifier = Modifier.height(8.dp))
//        }
//        Text(text = "--- Production Countries ---")
//        for (country in movie.data!!.productionCountries) {
//            Text(text = country)
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "--- Spoken Languages ---")
//        for (language in movie.data!!.spokenLanguages) {
//            Text(text = language)
//        }
//    }
//}