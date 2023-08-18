package powerrangers.eivom.ui.help_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar
import powerrangers.eivom.ui.theme.PoppinsBold
import powerrangers.eivom.ui.theme.PoppinsItalic
import powerrangers.eivom.ui.utility.UserPreferences

@Composable
fun HelpScreen(
    modifier: Modifier = Modifier,
    viewModel: HelpViewModel = hiltViewModel(),
    navigateToMenuItem: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val helpState by remember { viewModel.helpState }

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
        when (helpState) {
            0 -> {
                HelpScreenBody(
                    modifier = modifier.padding(innerPadding),
                    userPreferences = viewModel.userPreferences.value,
                    updateHelpState = {
                        viewModel.updateHelpState(it)
                    }
                )
            }
            1 -> {
                AddSponsoredMovieScreenBody()
            }
            2 -> {
                SignInAsProducerScreenBody()
            }
            3 -> {
                AddFavoriteScreenBody()
            }
            4 -> {
                TakeANoteScreenBody()
            }
            5 -> {
                FilterScreenBody()
            }
            6 -> {
                SortScreenBody()
            }
        }
    }
}

@Composable
fun HelpScreenBody(
    modifier: Modifier = Modifier,
    userPreferences: UserPreferences,
    updateHelpState: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (userPreferences.colorMode) userPreferences.screenBackgroundColor else MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(id = R.string.help_introduction),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (userPreferences.colorMode) userPreferences.screenTextColor else MaterialTheme.colors.onBackground
        )
        Spacer(modifier = modifier.height(10.dp))
        OutlinedTextField(
            value = "",
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .size(width = 310.dp, height = 60.dp)
                .padding(horizontal = 10.dp)
            //.padding(start = 10.dp, end = 10.dp, top = 5.dp,)
            ,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
            ),
            onValueChange = { },
            label = {
                Text(
                    text = stringResource(R.string.help_search_title),
                    fontFamily = PoppinsItalic,
                    fontSize = 15.sp,
                    color = MaterialTheme.colors.primary
                )
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.help_search_recommend_title),
                    style = TextStyle(
                        fontSize = 14.sp, // Adjust the font size as desired
                        color = Color.Gray
                    ),

                    )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.search_icon_outlineText),
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.clickable {
                    } //Click Search Button
                )
            },
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = PoppinsBold,
                textAlign = TextAlign.Start
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                }
            )
        )
        Spacer(modifier = modifier.height(25.dp))
        Text(
            text = stringResource(id = R.string.help_FAQ_title),
            fontSize = 20.sp,
            color = if (userPreferences.colorMode) userPreferences.screenTextColor else MaterialTheme.colors.onBackground
        )
        Spacer(modifier = modifier.height(25.dp))
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .height(130.dp)
                        .background(color = if (userPreferences.colorMode) userPreferences.dialogBackgroundColor else MaterialTheme.colors.secondary)
                        .weight(1f)
                        .clickable {
                            updateHelpState(1)
                        }
                ) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How to add sponsored movies",
                            textAlign = TextAlign.Center,
                            color = if (userPreferences.colorMode) userPreferences.dialogTextColor else MaterialTheme.colors.onBackground
                        )
                    }
                }
                Spacer(modifier = modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .height(130.dp)
                        .background(color = if (userPreferences.colorMode) userPreferences.dialogBackgroundColor else MaterialTheme.colors.secondary)
                        .weight(1f)
                        .clickable {
                            updateHelpState(2)
                        }
                ) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How to sign in as producer",
                            textAlign = TextAlign.Center,
                            color = if (userPreferences.colorMode) userPreferences.dialogTextColor else MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
            Row(modifier = modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .height(130.dp)
                        .background(color = if (userPreferences.colorMode) userPreferences.dialogBackgroundColor else MaterialTheme.colors.secondary)
                        .weight(1f)
                        .clickable {
                            updateHelpState(3)
                        }
                ) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How to add favorite movies",
                            textAlign = TextAlign.Center,
                            color = if (userPreferences.colorMode) userPreferences.dialogTextColor else MaterialTheme.colors.onBackground
                        )
                    }
                }
                Spacer(modifier = modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .height(130.dp)
                        .background(color = if (userPreferences.colorMode) userPreferences.dialogBackgroundColor else MaterialTheme.colors.secondary)
                        .weight(1f)
                        .clickable {
                            updateHelpState(4)
                        }
                ) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How to take a note",
                            textAlign = TextAlign.Center,
                            color = if (userPreferences.colorMode) userPreferences.dialogTextColor else MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
            Row(modifier = modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .height(130.dp)
                        .background(color = if (userPreferences.colorMode) userPreferences.dialogBackgroundColor else MaterialTheme.colors.secondary)
                        .weight(1f)
                        .clickable {
                            updateHelpState(5)
                        }
                ) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How to filter movies",
                            textAlign = TextAlign.Center,
                            color = if (userPreferences.colorMode) userPreferences.dialogTextColor else MaterialTheme.colors.onBackground
                        )
                    }
                }
                Spacer(modifier = modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .height(130.dp)
                        .background(color = if (userPreferences.colorMode) userPreferences.dialogBackgroundColor else MaterialTheme.colors.secondary)
                        .weight(1f)
                        .clickable {
                            updateHelpState(6)
                        }
                ) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How to sort movies",
                            textAlign = TextAlign.Center,
                            color = if (userPreferences.colorMode) userPreferences.dialogTextColor else MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
        }
    }
}
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