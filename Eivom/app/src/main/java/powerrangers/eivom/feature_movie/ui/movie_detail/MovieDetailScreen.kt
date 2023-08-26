package powerrangers.eivom.feature_movie.ui.movie_detail

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.domain.utility.Resource
import powerrangers.eivom.domain.utility.ResourceErrorMessage
import powerrangers.eivom.feature_movie.domain.model.MovieItem
import powerrangers.eivom.feature_movie.domain.utility.TranslateCode
import powerrangers.eivom.feature_movie.ui.movie_list.AddOrDeleteButton
import powerrangers.eivom.feature_movie.ui.movie_list.NewLocalMovieViewModel
import powerrangers.eivom.feature_movie.ui.movie_list.SelectButton
import powerrangers.eivom.feature_movie.ui.movie_management.NewMovieDialogViewModel
import powerrangers.eivom.ui.component.BottomOnlyHomeBar
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar
import powerrangers.eivom.ui.theme.PoppinsBold
import powerrangers.eivom.ui.theme.PoppinsItalic
import powerrangers.eivom.ui.theme.PoppinsMedium
import powerrangers.eivom.ui.utility.UserPreferences
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
        bottomBar = {
            BottomOnlyHomeBar()
        },
        drawerContent = {
            DrawerHeader()
            DrawerBody(onItemClick = navigateToMenuItem)
        }
    ) { innerPadding ->
        MovieDetailBody(
            modifier = modifier.padding(innerPadding),
            navigateToMovieNote = {navigateToMovieNote()}
        )
    }
}
fun isColorLight(color: Color): Boolean {
    // Calculate the luminance of the color
    val luminance = color.luminance()

    // You can adjust the threshold value according to your preference
    val threshold = 0.5

    // Compare the luminance with the threshold to determine if the color is light or dark
    return luminance > threshold
}
@Composable
fun MovieDetailBody(
    modifier: Modifier = Modifier,
    navigateToMovieNote: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel(),
) {
    val movie by remember { viewModel.movieInformation }

    val defaultBackgroundColor = MaterialTheme.colors.background
    val userPreferences by remember {
        viewModel.userPreferences
    }

    when (movie) {
        is Resource.Loading -> {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        is Resource.Success -> {

            LazyColumn (
                modifier = Modifier
                    .padding(10.dp)
                    .background(
                        if(userPreferences.colorMode) userPreferences.movieNoteBackgroundColor
                        else Color.White
                    )
            ) {
                //Top Button
                item{
                    TopButton(navigateToMovieNote = {navigateToMovieNote()})
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

                // Space
                item{
                    Spacer(modifier = Modifier.height(30.dp))
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
    navigateToMovieNote: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel(),
){
    val isEditing by remember { viewModel.isEditing }
    val movie by remember { viewModel.movieInformation }
    val isFavorite by remember { viewModel.isFavorite }
    var isWatched = remember { mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()

    if (isEditing) {
        if (movie.data!!.sponsored) {
            AddSponsoredMovieDialog(
                modifier = modifier,
                updateAddingState = {
                    viewModel.updateIsEditing()
                }
            )
        } else {
            AddMovieDialog(
                modifier = modifier,
                userPreferences = viewModel.userPreferences.value,
                updateAddingState = {
                    viewModel.updateIsEditing()
                }
            )
        }
    }

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
        // Note
        if ((movie.data != null && movie.data!!.editable) || viewModel.isSponsoredEdit()) {
            IconButton(onClick = { viewModel.updateIsEditing() }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .scale(-1f, 1f) // Flip horizontally
                        .padding(10.dp),
                    tint = Color.Black
                )
            }
        }
        if (viewModel.isFavoriteMovie()) {
            IconButton(onClick = { navigateToMovieNote() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_note),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .scale(-1f, 1f) // Flip horizontally
                        .padding(10.dp),
                    tint = Color.Black
                )
            }
        }
        // Watched
        IconButton(onClick = { isWatched.value = !isWatched.value }) {
            Icon(
                painter = if (isWatched.value) painterResource(id = R.drawable.ic_visibility) else painterResource(
                    id = R.drawable.ic_eyedown
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .scale(-1f, 1f) // Flip horizontally
                    .padding(6.dp)
                ,
                tint = Color.Black
            )
        }
        // Favorite
        FavoriteMovieButton(
            isFavorite = isFavorite,
            onFavoriteToggle = {
                coroutineScope.launch {
                    if (viewModel.isFavoriteMovie()) {
                        viewModel.deleteFavoriteMovie()
                        viewModel.updateIsFavorite()
                    } else {
                        viewModel.addFavoriteMovie()
                        viewModel.updateIsFavorite()
                    }
                }
            },
            checkedColor = Color.Red,
            uncheckedColor = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun FavoriteMovieButton(
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
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
        onClick = { onFavoriteToggle() },
        modifier = modifier
    ) {
        Icon(
            imageVector = favoriteIcon,
            contentDescription = if (isFavorite) {
                stringResource(R.string.favorite_movie_description)
            } else {
                stringResource(R.string.unfavorite_movie_description)
            },
            tint = if (isFavorite) checkedColor else uncheckedColor,
            modifier = Modifier.size(40.dp)

        )
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
    val posterUrl = movie.data!!.posterUrl
    val onErrorFallbackImageRes = "https://www.globalsign.com/application/files/9516/0389/3750/What_Is_an_SSL_Common_Name_Mismatch_Error_-_Blog_Image.jpg"
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(posterUrl)
            .build()
    )


    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Spacer(modifier = Modifier.width(70.dp))
        // Film Poster
        Image(
            painter = if (painter.state is AsyncImagePainter.State.Error) rememberAsyncImagePainter(
               onErrorFallbackImageRes
            ) else painter,
            contentDescription = null,
            modifier = modifier
                .size(100.dp)
                .padding(13.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
//        SubcomposeAsyncImage(
//            modifier = modifier
//                .size(100.dp)
//                .padding(13.dp)
//                .clip(CircleShape)
//            ,
//            contentScale = ContentScale.Crop,
//            model = ImageRequest.Builder(LocalContext.current)
//                .data(posterUrl)
//                .crossfade(true)
//                .build()
//            ,
//            onSuccess = { image ->
//                viewModel.handleMovieDominantColor(image.result.drawable) { color ->
//                    onFocusColor = color
//                }
//
//            },
//            contentDescription = movie.data!!.title,
//            loading = {
//                CircularProgressIndicator(
//                    modifier = modifier
//                        .scale(0.25f)
//                )
//            }
//        )

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
                .width(if (movie.data!!.status.length > 0) (movie.data!!.status.length + 75).dp else 85.dp)
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

@Composable
fun AddMovieDialog(
    modifier: Modifier,
    userPreferences: UserPreferences,
    newLocalMovieViewModel: NewLocalMovieViewModel = hiltViewModel(),
    updateAddingState: (Boolean) -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val newMovieState by remember { newLocalMovieViewModel.newMovieState }

    val companies = remember { newLocalMovieViewModel.companies }

    newLocalMovieViewModel.updateMovieState(viewModel.movieInformation.value.data!!)

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
                    text = stringResource(id = R.string.add_manual_movie_title),
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = modifier
                        .height(450.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
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
                                newLocalMovieViewModel.updateAdultOfMovie(
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
                                newLocalMovieViewModel.updateAdultOfMovie(
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
                            newLocalMovieViewModel.updateMovieOriginalTitle(it)
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
                            newLocalMovieViewModel.updateMovieTitle(it)
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = modifier.weight(1f),
                            text = stringResource(id = R.string.genre_label)
                        )
                        LazyRow(
                            modifier = modifier.weight(2f)
                        ) {
                            items(newLocalMovieViewModel.genreList) { genre ->
                                val isGenreSelected = newLocalMovieViewModel.isGenreSelected(genre.second)
                                SelectButton(
                                    text = genre.second,
                                    isSelected = isGenreSelected,
                                    onSelect = {
                                        if (isGenreSelected) {
                                            newLocalMovieViewModel.removeMovieGenre(genre.second)
                                        } else {
                                            newLocalMovieViewModel.addMovieGenre(genre.second)
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
                            newLocalMovieViewModel.updateMovieOverview(it)
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
                                newLocalMovieViewModel.languageList.forEach { language ->
                                    DropdownMenuItem(
                                        onClick = {
                                            newLocalMovieViewModel.updateMovieOriginalLanguage(
                                                language.first
                                            )
                                            newLocalMovieViewModel.removeMovieSpokenLanguage(
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
                            newLocalMovieViewModel.languageList.forEach { language ->
                                if (language.first != newMovieState.originalLanguage && language.first !in newMovieState.spokenLanguages) {
                                    DropdownMenuItem(
                                        onClick = {
                                            newLocalMovieViewModel.addMovieSpokenLanguage(
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
                            if (newMovieState.originalLanguage != null) {
                                SelectButton(
                                    modifier = modifier.fillMaxWidth(),
                                    enabled = false,
                                    text = TranslateCode.ISO_639_1[newMovieState.originalLanguage]
                                        ?: stringResource(id = R.string.unknown),
                                    isSelected = true,
                                    onSelect = {}
                                )
                            }
                            for (i in newMovieState.spokenLanguages.lastIndex downTo 0) {
                                SelectButton(
                                    modifier = modifier.fillMaxWidth(),
                                    text = TranslateCode.ISO_639_1[newMovieState.spokenLanguages[i]]
                                        ?: stringResource(
                                            id = R.string.unknown
                                        ),
                                    isSelected = false,
                                    onSelect = {
                                        newLocalMovieViewModel.removeMovieSpokenLanguage(
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
                            newLocalMovieViewModel.updateMovieLength(it)
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
                            text = newMovieState.regionReleaseDate?.format(userPreferences.dateFormat)
                                ?: stringResource(id = R.string.unknown),
                            isSelected = newMovieState.regionReleaseDate != null,
                            onSelect = {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        newLocalMovieViewModel.updateMovieReleaseDate(
                                            year = year,
                                            month = month + 1,
                                            dayOfMonth = dayOfMonth
                                        )
                                    },
                                    newMovieState.regionReleaseDate?.year ?: currentDate.year,
                                    (newMovieState.regionReleaseDate?.monthValue
                                        ?: currentDate.monthValue) - 1,
                                    newMovieState.regionReleaseDate?.dayOfMonth ?: currentDate.dayOfMonth
                                ).show()
                            }
                        )
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
                                    newLocalMovieViewModel.addMovieCompany()
                                }
                            )
                        }
                        for (i in companies.lastIndex downTo 0) {
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = companies[i],
                                onValueChange = {
                                    newLocalMovieViewModel.updateMovieCompany(
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
                                maxLines = 1,
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            newLocalMovieViewModel.removeMovieCompany(i)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = stringResource(id = R.string.remove_company_button)
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
                            newLocalMovieViewModel.updateMoviePosterUrl(
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
                    // Main Backdrop Url
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.landscapeImageUrl ?: "",
                        onValueChange = {
                            newLocalMovieViewModel.updateMovieLandscapeImageUrl(
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
                    // Homepage Url
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.homepageUrl ?: "",
                        onValueChange = {
                            newLocalMovieViewModel.updateMovieHomepage(
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
                    // Status
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.status ?: "",
                        onValueChange = {
                            newLocalMovieViewModel.updateMovieStatus(
                                status = it
                            )
                        },
                        label = {
                            Text(text = stringResource(id = R.string.status_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.status_placeholder))
                        },
                        maxLines = 1
                    )
                    // Budget
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.budget?.toString() ?: "",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        onValueChange = {
                            newLocalMovieViewModel.updateMovieBudget(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.budget_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.budget_placeholder))
                        },
                        trailingIcon = {
                            Text(text = stringResource(id = R.string.currency_unit))
                        },
                        maxLines = 1
                    )
                    // Revenue
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.revenue?.toString() ?: "",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        onValueChange = {
                            newLocalMovieViewModel.updateMovieRevenue(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.revenue_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.revenue_placeholder))
                        },
                        trailingIcon = {
                            Text(text = stringResource(id = R.string.currency_unit))
                        },
                        maxLines = 1
                    )
                }
                // Button
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val context = LocalContext.current
                    val coroutineScope = rememberCoroutineScope()
                    Button(
                        onClick = {
                            updateAddingState(false)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (newLocalMovieViewModel.saveEditedMovie(viewModel.movieId)) {
                                    viewModel.loadMovieInfo()
                                    newLocalMovieViewModel.clearNewMovieState()
                                    updateAddingState(false)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.add_sponsored_movie_success_notification),
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.add_sponsored_movie_error_notification),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        enabled = newLocalMovieViewModel.isNewMovieValid()
                    ) {
                        Text(text = stringResource(id = R.string.save_button_title))
                    }
                }
            }
        }
    }
}

@Composable
fun AddSponsoredMovieDialog(
    modifier: Modifier,
    newMovieDialogViewModel: NewMovieDialogViewModel = hiltViewModel(),
    updateAddingState: (Boolean) -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val userPreferences by remember { newMovieDialogViewModel.userPreferences }

    val isKeyChecked by remember { newMovieDialogViewModel.isKeyChecked }
    val movieKeyField by remember { newMovieDialogViewModel.movieKeyField }
    val newMovieState by remember { newMovieDialogViewModel.newMovieState }
    val collectionState by remember { newMovieDialogViewModel.collectionState }
    val companyStateList = remember { newMovieDialogViewModel.companyStateList }
    val movieLogoUrlList = remember { newMovieDialogViewModel.movieLogoUrlList }
    val moviePosterUrlList = remember { newMovieDialogViewModel.moviePosterUrlList }
    val movieBackdropUrlList = remember { newMovieDialogViewModel.movieBackdropUrlList }
    val videoStateList = remember { newMovieDialogViewModel.videoStateList }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            newMovieDialogViewModel.updateSponsoredMovie(viewModel.movieInformation.value.data!!)
        }
    }

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
                        modifier = modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused) {
                                    coroutineScope.launch {
                                        newMovieDialogViewModel.getMovieKey()
                                    }
                                }
                            },
                        value = movieKeyField,
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieKeyField(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.key_field_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.key_field_placeholder))
                        },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone =
                        {
                            coroutineScope.launch {
                                newMovieDialogViewModel.getMovieKey()
                            }
                        }
                        ),
                        trailingIcon = {
                            if (movieKeyField.isNotBlank() && isKeyChecked) {
                                if (newMovieDialogViewModel.isMovieKeyMatch()) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = stringResource(id = R.string.key_valid)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = stringResource(id = R.string.key_not_valid)
                                    )
                                }
                            }
                        }
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
                        powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
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
                        powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = modifier.weight(1f),
                            text = stringResource(id = R.string.genre_label)
                        )
                        LazyRow(
                            modifier = modifier.weight(2f)
                        ) {
                            items(newMovieDialogViewModel.genreList) { genre ->
                                val isGenreSelected = newMovieDialogViewModel.isGenreSelected(genre.first)
                                powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
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
                            powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
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
                        powerrangers.eivom.feature_movie.ui.movie_management.AddOrDeleteButton(
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
                            if (newMovieState.originalLanguage != null) {
                                powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
                                    modifier = modifier.fillMaxWidth(),
                                    enabled = false,
                                    text = TranslateCode.ISO_639_1[newMovieState.originalLanguage]
                                        ?: stringResource(id = R.string.unknown),
                                    isSelected = true,
                                    onSelect = {}
                                )
                            }
                            for (i in newMovieState.spokenLanguages.lastIndex downTo 0) {
                                powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
                                    modifier = modifier.fillMaxWidth(),
                                    text = TranslateCode.ISO_639_1[newMovieState.spokenLanguages[i]]
                                        ?: stringResource(
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
                        powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
                            modifier = modifier.weight(1f),
                            text = newMovieState.releaseDate?.format(userPreferences.dateFormat)
                                ?: stringResource(id = R.string.unknown),
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
                                    (newMovieState.releaseDate?.monthValue
                                        ?: currentDate.monthValue) - 1,
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
                            powerrangers.eivom.feature_movie.ui.movie_management.AddOrDeleteButton(
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
                            powerrangers.eivom.feature_movie.ui.movie_management.AddOrDeleteButton(
                                modifier = modifier.weight(1f),
                                isAddButton = true,
                                contentDescription = stringResource(id = R.string.add_company_content_description),
                                onClick = {
                                    newMovieDialogViewModel.addMovieCompany()
                                }
                            )
                        }
                        for (i in companyStateList.lastIndex downTo 0) {
                            powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
                                modifier = modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.remove_company_button) + " " + if (companyStateList[i].name.isNullOrBlank()) stringResource(
                                    id = R.string.unknown
                                ) else companyStateList[i].name,
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
                                    powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
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
                        powerrangers.eivom.feature_movie.ui.movie_management.AddOrDeleteButton(
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
                            for (i in newMovieState.productionCountries.lastIndex downTo 0) {
                                powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
                                    modifier = modifier.fillMaxWidth(),
                                    text = TranslateCode.ISO_3166_1[newMovieState.productionCountries[i]]
                                        ?: stringResource(
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
                            powerrangers.eivom.feature_movie.ui.movie_management.AddOrDeleteButton(
                                modifier = modifier.weight(1f),
                                isAddButton = true,
                                contentDescription = stringResource(id = R.string.add_movie_logo_url_content_description),
                                onClick = {
                                    newMovieDialogViewModel.addMovieLogoUrl()
                                }
                            )
                        }
                        for (i in movieLogoUrlList.lastIndex downTo 0) {
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
                            powerrangers.eivom.feature_movie.ui.movie_management.AddOrDeleteButton(
                                modifier = modifier.weight(1f),
                                isAddButton = true,
                                contentDescription = stringResource(id = R.string.add_movie_poster_url_content_description),
                                onClick = {
                                    newMovieDialogViewModel.addMoviePosterUrl()
                                }
                            )
                        }
                        for (i in moviePosterUrlList.lastIndex downTo 0) {
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
                            powerrangers.eivom.feature_movie.ui.movie_management.AddOrDeleteButton(
                                modifier = modifier.weight(1f),
                                isAddButton = true,
                                contentDescription = stringResource(id = R.string.add_movie_backdrop_url_content_description),
                                onClick = {
                                    newMovieDialogViewModel.addMovieLandscapeImageUrl()
                                }
                            )
                        }
                        for (i in movieBackdropUrlList.lastIndex downTo 0) {
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
                                text = stringResource(id = R.string.video_label)
                            )
                            powerrangers.eivom.feature_movie.ui.movie_management.AddOrDeleteButton(
                                modifier = modifier.weight(1f),
                                isAddButton = true,
                                contentDescription = stringResource(id = R.string.add_video_content_description),
                                onClick = {
                                    newMovieDialogViewModel.addMovieVideo()
                                }
                            )
                        }
                        for (i in videoStateList.lastIndex downTo 0) {
                            powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
                                modifier = modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.remove_video_button) + " " + if (videoStateList[i].name.isNullOrBlank()) stringResource(
                                    id = R.string.unknown
                                ) else videoStateList[i].name,
                                isSelected = false,
                                onSelect = {
                                    newMovieDialogViewModel.removeMovieVideo(
                                        index = i
                                    )
                                }
                            )
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = videoStateList[i].name ?: "",
                                onValueChange = {
                                    newMovieDialogViewModel.updateMovieVideoName(
                                        index = i,
                                        name = it
                                    )
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.video_name_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.video_name_placeholder))
                                },
                                maxLines = 2
                            )
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = videoStateList[i].url ?: "",
                                onValueChange = {
                                    newMovieDialogViewModel.updateMovieVideoUrl(
                                        index = i,
                                        url = it
                                    )
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.video_url_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.video_url_placeholder))
                                },
                                maxLines = 1
                            )
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val density = LocalDensity.current

                                var isLanguageDropdownExpanded by remember { mutableStateOf(false) }
                                var dropdownMenuWidth by remember { mutableStateOf(0.dp) }

                                Text(
                                    modifier = modifier.weight(1f),
                                    text = stringResource(id = R.string.video_language_label)
                                )
                                Column(
                                    modifier = modifier.weight(1f)
                                ) {
                                    powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
                                        modifier = modifier
                                            .fillMaxWidth()
                                            .onGloballyPositioned { coordinates ->
                                                dropdownMenuWidth =
                                                    (coordinates.size.width / density.density).dp
                                            },
                                        text = TranslateCode.ISO_639_1[videoStateList[i].language]
                                            ?: stringResource(id = R.string.unknown),
                                        isSelected = videoStateList[i].language != null,
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
                                            DropdownMenuItem(
                                                onClick = {
                                                    newMovieDialogViewModel.updateMovieVideoLanguage(
                                                        index = i,
                                                        language = language.first
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val density = LocalDensity.current

                                var isCountryDropdownExpanded by remember { mutableStateOf(false) }
                                var dropdownMenuWidth by remember { mutableStateOf(0.dp) }

                                Text(
                                    modifier = modifier.weight(1f),
                                    text = stringResource(id = R.string.video_country_label)
                                )
                                Column(
                                    modifier = modifier.weight(1f)
                                ) {
                                    powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
                                        modifier = modifier
                                            .fillMaxWidth()
                                            .onGloballyPositioned { coordinates ->
                                                dropdownMenuWidth =
                                                    (coordinates.size.width / density.density).dp
                                            },
                                        text = TranslateCode.ISO_3166_1[videoStateList[i].country]
                                            ?: stringResource(id = R.string.unknown),
                                        isSelected = videoStateList[i].country != null,
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
                                                    newMovieDialogViewModel.updateMovieVideoCountry(
                                                        index = i,
                                                        country = country.first
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
                            OutlinedTextField(
                                modifier = modifier.fillMaxWidth(),
                                value = videoStateList[i].site ?: "",
                                onValueChange = {
                                    newMovieDialogViewModel.updateMovieVideoSite(
                                        index = i,
                                        site = it
                                    )
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.video_site_label))
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.video_site_placeholder))
                                },
                                maxLines = 1
                            )
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val density = LocalDensity.current

                                var isVideoTypeDropdownExpanded by remember { mutableStateOf(false) }
                                var dropdownMenuWidth by remember { mutableStateOf(0.dp) }

                                Text(
                                    modifier = modifier.weight(1f),
                                    text = stringResource(id = R.string.video_type_label)
                                )
                                Column(
                                    modifier = modifier.weight(1f)
                                ) {
                                    powerrangers.eivom.feature_movie.ui.movie_management.SelectButton(
                                        modifier = modifier
                                            .fillMaxWidth()
                                            .onGloballyPositioned { coordinates ->
                                                dropdownMenuWidth =
                                                    (coordinates.size.width / density.density).dp
                                            },
                                        text = videoStateList[i].type
                                            ?: stringResource(id = R.string.unknown),
                                        isSelected = videoStateList[i].type != null,
                                        onSelect = {
                                            isVideoTypeDropdownExpanded =
                                                !isVideoTypeDropdownExpanded
                                        }
                                    )
                                    DropdownMenu(
                                        modifier = modifier
                                            .height(200.dp)
                                            .width(dropdownMenuWidth),
                                        expanded = isVideoTypeDropdownExpanded,
                                        onDismissRequest = {
                                            isVideoTypeDropdownExpanded = false
                                        }
                                    ) {
                                        newMovieDialogViewModel.videoType.forEach { type ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    newMovieDialogViewModel.updateMovieVideoType(
                                                        index = i,
                                                        type = type
                                                    )
                                                    isVideoTypeDropdownExpanded = false
                                                }
                                            ) {
                                                Text(
                                                    modifier = modifier.fillMaxWidth(),
                                                    text = type,
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
                    // Status
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.status ?: "",
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieStatus(
                                status = it
                            )
                        },
                        label = {
                            Text(text = stringResource(id = R.string.status_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.status_placeholder))
                        },
                        maxLines = 1
                    )
                    // Budget
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.budget?.toString() ?: "",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieBudget(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.budget_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.budget_placeholder))
                        },
                        trailingIcon = {
                            Text(text = stringResource(id = R.string.currency_unit))
                        },
                        maxLines = 1
                    )
                    // Revenue
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = newMovieState.revenue?.toString() ?: "",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        onValueChange = {
                            newMovieDialogViewModel.updateMovieRevenue(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.revenue_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.revenue_placeholder))
                        },
                        trailingIcon = {
                            Text(text = stringResource(id = R.string.currency_unit))
                        },
                        maxLines = 1
                    )
                }
                // Button
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            updateAddingState(false)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }
                    Button(
                        onClick = {
                            if (newMovieDialogViewModel.saveSponsoredMovie(viewModel.movieId)) {
                                updateAddingState(false)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.add_sponsored_movie_success_notification),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.add_sponsored_movie_error_notification),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        enabled = newMovieDialogViewModel.isMovieInformationValid(isMatch = true)
                    ) {
                        Text(text = stringResource(id = R.string.save_button_title))
                    }
                }
            }
        }
    }
}