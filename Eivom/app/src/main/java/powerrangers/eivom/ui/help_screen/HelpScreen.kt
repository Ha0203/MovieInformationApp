package powerrangers.eivom.ui.help_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar
import powerrangers.eivom.ui.settings.SettingsBody

@Composable
fun HelpScreen(
    modifier: Modifier = Modifier,
    navigateToMenuItem: (String) -> Unit
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
        HelpScreenBody(modifier = modifier.padding(innerPadding))
    }
}

@Composable
fun HelpScreenBody(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
@Composable
fun AddSponsoredMovieScreenBody(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.help_add_sponsored_movie_s1),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_add_sponsored_movie_s2),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_add_sponsored_movie_s3),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_add_sponsored_movie_s4),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_add_sponsored_movie_s5),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_add_sponsored_movie_s6),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_add_sponsored_movie_s7),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_add_sponsored_movie_s8),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_add_sponsored_movie_s9),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SignInAsProducerScreenBody(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.help_sign_in_as_producer_s1),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_sign_in_as_producer_s2),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_sign_in_as_producer_s3),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AddFavoriteScreenBody(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.help_add_favorite_s1),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_add_favorite_s2),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_add_favorite_s3),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TakeANoteScreenBody(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.help_take_note_s1),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_take_note_s2),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_take_note_s3),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FilterScreenBody(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.help_filter_s1),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_filter_s2),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_filter_s3),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SortScreenBody(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.help_sort_s1),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_sort_s2),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(9.dp))
        Text(
            text = stringResource(id = R.string.help_sort_s3),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}