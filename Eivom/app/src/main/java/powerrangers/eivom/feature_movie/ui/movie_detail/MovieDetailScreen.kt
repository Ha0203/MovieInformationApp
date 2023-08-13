package powerrangers.eivom.feature_movie.ui.movie_detail

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
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
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.domain.utility.ResourceErrorMessage
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar
import powerrangers.eivom.ui.theme.PoppinsBold
import powerrangers.eivom.ui.theme.PoppinsItalic
import powerrangers.eivom.ui.theme.PoppinsMedium
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    navigateToMenuItem: (String) -> Unit,
    navigateToMovieNote: () -> Unit,
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

                // Tag Line
                item {
                    TagLine()
                }

                // Duration
                item {
                    Duration()
                }

                // Genre
                item {
                    GenresList()
                }


                // Status
                item {
                    Status()
                }

                // Release Date
                item {
                    ReleaseDate()
                }

                // Budget
                item {
                    Budget()
                }

                // Revenue
                item {
                    Revenue()
                }

                // Language
                item {
                    LanguageList()
                }

                // Country
                item {
                    CountryList()
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
        mutableStateOf(Color.Black)
    }
    val posterUrl = movie.data?.posterUrl
    val onErrorFallbackImageRes = "https://upload.wikimedia.org/wikipedia/vi/d/d7/Main_1_fa_1080x1350.jpg"
    // Check if posterUrl is valid
    val imageRequest = if (!posterUrl.isNullOrEmpty()) {
        ImageRequest.Builder(LocalContext.current)
            .data(posterUrl)
            .crossfade(true)
            .build()
        Log.d("Detail", "success + ${posterUrl.toString()}")
    } else {
        // Use a fallback image request for invalid posterUrl
        ImageRequest.Builder(LocalContext.current)
            .data(onErrorFallbackImageRes)
            .build()
        Log.d("Detail", "fail + ${posterUrl.toString()}")
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
            model = imageRequest
            ,
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
            text = if (movie.data!!.overview.length > 0) "${movie.data!!.overview}" else "Comming Soon",
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
        if (genreList.size > 1)
        {
            LazyRow(
                modifier = Modifier
                    .height(45.dp)
                    .width(130.dp)
                    .padding(6.dp)
                ,
            ){
                items(genreList) {genre ->
                    GenreBox(genre)
                }
            }
        }
        else if (genreList.size == 1){
            GenreCard(genre = genreList[0])
        }
        else {
            GenreBox(genre = "Comming Soon")
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
            .height(40.dp)
            .width((genre.length + 60).dp)
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

@Composable
fun GenreCard(
    genre: String
){
    Card(
        backgroundColor = Color.Black,
        modifier = Modifier
            .height(40.dp)
            .width(80.dp)
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
            modifier = Modifier.padding(
                6.dp
            )
        )
    }

}

@Composable
fun TagLine(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }

    Column(
        modifier = Modifier.padding(
            start = 17.dp,
            end = 17.dp,
            bottom = 5.dp
        )
    ) {
        Text(
            text =  "Tagline",
            fontSize = 26.sp,
            fontFamily = PoppinsBold,
            color = MaterialTheme.colors.primary,
        )

        Text(
            text = if (movie.data!!.tagline.length > 0) "${movie.data!!.tagline}" else "Comming Soon",
            fontSize = 15.sp,
            fontFamily = PoppinsMedium,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Status(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }

    Row(
        modifier = Modifier.padding(
            start = 17.dp,
            end = 17.dp,
        )
    ) {
        Text(
            text = "Status",
            fontSize = 26.sp,
            fontFamily = PoppinsBold,
            color = MaterialTheme.colors.primary,
        )

        Spacer(modifier = Modifier.weight(1f))

        Card(
            backgroundColor = if (movie.data!!.status == "Released") MaterialTheme.colors.primary else Color.LightGray,
            modifier = Modifier
                .height(40.dp)
                .width( if (movie.data!!.status.length > 0) (movie.data!!.status.length + 75).dp else 85.dp)
                .padding(5.dp)
            ,
            shape = RoundedCornerShape(8.dp)

        ){
            Text(
                text = if (movie.data!!.status.length > 0) "${movie.data!!.status}" else "Comming Soon",
                fontSize = if (movie.data!!.status.length > 0) (12 / movie.data!!.status.length + 9).sp else 10.sp,
                fontFamily = if (movie.data!!.status == "Released") PoppinsMedium else PoppinsItalic,
                color = if (movie.data!!.status == "Released") Color.White else Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    top = 7.dp,
                    bottom = 7.dp,
                    start = 5.dp,
                    end = 5.dp
                )
            )
        }
    }
}

@Composable
fun Duration(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }

    Row(
        modifier = Modifier.padding(
            start = 17.dp,
            end = 17.dp,
            top = 17.dp
        )
    ) {
        Text(
            text = "Duration",
            fontSize = 26.sp,
            fontFamily = PoppinsBold,
            color = MaterialTheme.colors.primary,
        )

        Spacer(modifier = Modifier.weight(1f))

        Card(
            backgroundColor = Color.Black,
            modifier = Modifier
                .height(if (movie.data!!.length > 0) 38.dp else 44.dp)
                .width(80.dp)
                .padding(5.dp)
            ,
            shape = RoundedCornerShape(8.dp)

        ){
            Text(
                text = if (movie.data!!.length > 0) "${movie.data!!.length / 60} h ${movie.data!!.length % 60} p" else "Comming Soon",
                fontSize = if (movie.data!!.length > 0) 11.sp else 8.sp,
                fontFamily = PoppinsMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    6.dp
                )
            )
        }
    }
}

@Composable
fun ReleaseDate(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }
    val localDate = LocalDate.parse(movie.data!!.regionReleaseDate)
    val formattedDate = localDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))

    Row(
        modifier = Modifier.padding(
            start = 17.dp,
            end = 17.dp,
            top = 17.dp
        )
    ) {
        Text(
            text = "Release Date",
            fontSize = 26.sp,
            fontFamily = PoppinsBold,
            color = MaterialTheme.colors.primary,
        )

        Spacer(modifier = Modifier.weight(1f))

        Card(
            backgroundColor = if (movie.data!!.regionReleaseDate.length > 0) MaterialTheme.colors.primary else Color.LightGray,
            modifier = Modifier
                .height(if (formattedDate.length <= 12) 40.dp else 50.dp)
                .width(90.dp)
                .padding(6.dp)
            ,
            shape = RoundedCornerShape(8.dp)

        ){
            Text(
                text = if (movie.data!!.regionReleaseDate.length > 0) "${formattedDate}" else "Comming Soon",
                fontSize = 10.5.sp,
                fontFamily = if (movie.data!!.regionReleaseDate.length > 0) PoppinsMedium else PoppinsItalic,
                color = if (movie.data!!.regionReleaseDate.length > 0) Color.White else Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    6.dp
                )
            )
        }
    }
}

@Composable
fun Budget(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }
    val budget =(movie.data!!.budget.toDouble() / 1000000).toDouble()
    Row(
        modifier = Modifier.padding(
            start = 17.dp,
            end = 17.dp,
            top = 17.dp
        )
    ) {
        Text(
            text = "Budget",
            fontSize = 26.sp,
            fontFamily = PoppinsBold,
            color = MaterialTheme.colors.primary,
        )

        Spacer(modifier = Modifier.weight(1f))

        Card(
            backgroundColor = Color.Black,
            modifier = Modifier
                .height(40.dp)
                .width(80.dp)
                .padding(5.dp)
            ,
            shape = RoundedCornerShape(8.dp)

        ){
            Text(
                text = "$${budget}M",
                fontSize = 12.sp,
                fontFamily = PoppinsMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    6.dp
                )
            )
        }
    }
}

@Composable
fun Revenue(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }
    val revenue = if (movie.data!!.revenue < 1000000000) (movie.data!!.revenue.toDouble() / 1000000).toDouble() else (movie.data!!.revenue.toDouble() / 1000000000).toDouble()
    val revenueReformat = formatDoubleDecimalPlacesWithNumberFormat(revenue, 2)
    Row(
        modifier = Modifier.padding(
            start = 17.dp,
            end = 17.dp,
            top = 17.dp
        )
    ) {
        Text(
            text = "Revenue",
            fontSize = 26.sp,
            fontFamily = PoppinsBold,
            color = MaterialTheme.colors.primary,
        )

        Spacer(modifier = Modifier.weight(1f))

        Card(
            backgroundColor = Color.Black,
            modifier = Modifier
                .height(40.dp)
                .width(80.dp)
                .padding(5.dp)
            ,
            shape = RoundedCornerShape(8.dp)

        ){
            Text(
                text = if (movie.data!!.revenue < 1000000000) "$${revenueReformat}M" else "$${revenueReformat}B",
                fontSize = 12.sp,
                fontFamily = PoppinsMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    6.dp
                )
            )
        }
    }
}

fun formatDoubleDecimalPlacesWithNumberFormat(value: Double, decimalPlaces: Int): String {
    val numberFormat = NumberFormat.getNumberInstance()
    numberFormat.maximumFractionDigits = decimalPlaces
    return numberFormat.format(value)
}

@Composable
fun LanguageList(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val movie by remember { viewModel.movieInformation }

    Row(
        modifier = Modifier.padding(
            top = 17.dp,
            start = 17.dp,
            end = 17.dp,
        )
    ){
        Text(
            text = "Language",
            fontSize = 26.sp,
            fontFamily = PoppinsBold,
            color = MaterialTheme.colors.primary,
        )

        Spacer(modifier = Modifier.weight(1f))
        val languageList = movie.data!!.spokenLanguages
        
        if (languageList.size > 1) {
            LazyRow(
                modifier = Modifier
                    .height(45.dp)
                    .width(130.dp)
                    .padding(6.dp)
                ,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                items(languageList) {genre ->
                    LanguageBox(genre)
                }
            }
        }
        else if (languageList.size == 1){
            LanguageCard(language = languageList[0])
        }
        else {
            LanguageCard(language = "Comming Soon")
        }
    }
}

@Composable
fun LanguageCard(
    language: String
){
    Card(
        backgroundColor = Color.Black,
        modifier = Modifier
            .height(40.dp)
            .width(80.dp)
            .padding(5.dp)
        ,
        shape = RoundedCornerShape(8.dp)

    ){
        Text(
            text = "${language}",
            fontSize = 10.sp,
            fontFamily = PoppinsMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                6.dp
            )
        )
    }

}

@Composable
fun LanguageBox(
    language: String
){
    Card(
        backgroundColor = Color.Black,
        modifier = Modifier
            .height(40.dp)
            .width((language.length + 60).dp)
            .padding(5.dp)
        ,
        shape = RoundedCornerShape(8.dp)

    ){
        Text(
            text = "${language}",
            fontSize = 10.sp,
            fontFamily = PoppinsMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp)
        )
    }

}


@Composable
fun CountryList(
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
            text = "Country",
            fontSize = 26.sp,
            fontFamily = PoppinsBold,
            color = MaterialTheme.colors.primary,
        )

        Spacer(modifier = Modifier.weight(1f))
        val CountryList = movie.data!!.productionCountries

        if (CountryList.size > 1) {
            LazyRow(
                modifier = Modifier
                    .height(45.dp)
                    .width(130.dp)
                    .padding(6.dp)
                ,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                items(CountryList) {country ->
                    CountryBox(country)
                }
            }
        }
        else if (CountryList.size == 1){
            CountryCard(Country = CountryList[0])
        }
        else {
            CountryBox(Country = "Comming Soon")
        }
    }
}

@Composable
fun CountryCard(
    Country: String
){
    Card(
        backgroundColor = Color.Black,
        modifier = Modifier
            .height(45.dp)
            .width(80.dp)
            .padding(5.dp)
        ,
        shape = RoundedCornerShape(8.dp)

    ){
        Text(
            text = "${Country}",
            fontSize = 8.sp,
            fontFamily = PoppinsMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                6.dp
            )
        )
    }

}

@Composable
fun CountryBox(
    Country: String
){
    Card(
        backgroundColor = Color.Black,
        modifier = Modifier
            .height(45.dp)
            .width((Country.length + 60).dp)
            .padding(5.dp)
        ,
        shape = RoundedCornerShape(8.dp)

    ){
        Text(
            text = "${Country}",
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