package powerrangers.eivom.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import powerrangers.eivom.R
import powerrangers.eivom.feature_movie.ui.movie_list.FilterButton
import powerrangers.eivom.feature_movie.ui.movie_list.MovieListViewModel
import powerrangers.eivom.feature_movie.ui.movie_list.SortButton
import powerrangers.eivom.navigation.Route
import powerrangers.eivom.ui.utility.MenuItem


@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    onMenuIconClick: () -> Unit
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .width(150.dp)
                        .padding(
                            bottom = 15.dp,
                            end = 30.dp
                        )
                    ,
                    text = title,
                    fontSize = 50.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h1,
                    color = MaterialTheme.colors.primary,
                )
            }
        },
        modifier = modifier.height(80.dp),
        backgroundColor = MaterialTheme.colors.background,
        navigationIcon = {
            IconButton(
                onClick = onMenuIconClick,
                modifier = modifier
                    .padding(
                        top = 15.dp,
                        bottom = 10.dp,
                        start = 5.dp,
                        end = 15.dp
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(id = R.string.menu_button_content_description),
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    )

}

@Composable
fun BottomDefaultScreenBar(
    modifier: Modifier = Modifier,
    //onHomeIconClick: () -> Unit,
    //onFavoriteIconClick: () -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val isFilterVisible by remember { viewModel.isFilterVisible }
    val isSearchVisible by remember { viewModel.isSearchVisible }
    val isSortVisible by remember { viewModel.isSortVisible }

    BottomAppBar(
        modifier = Modifier.height(45.dp),
        backgroundColor = Color.Black,
        content = {
            // Home
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    modifier = modifier.padding(
                        top=7.dp,
                        bottom = 7.dp,
                        start = 33.dp,
                        end = 20.dp
                    ),
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            // Filter
            IconButton(onClick = { viewModel.reverseIsFilter() }) {
                Icon(
                    modifier = modifier.padding(
                        top=7.dp,
                        bottom = 7.dp,
                        start = 33.dp,
                        end = 20.dp
                    ),
                    imageVector = Icons.Filled.Filter,
                    contentDescription = null,
                    tint = if (!isFilterVisible) Color.White else MaterialTheme.colors.primary
                )
            }
            // Filter Dialog
                if (isFilterVisible) {
                    FilterButton(
                        funcToCall = {
                            viewModel.reverseIsFilter()
                            viewModel.updateFilterState()
                        },
                        onDismiss = {
                            viewModel.reverseIsFilter()
                        },
                    )
                }


            // Sort
            IconButton(onClick = { if(viewModel.trendingFilter.value == null) viewModel.reverseIsSort() }) {
                Icon(
                    modifier = modifier.padding(
                        top=7.dp,
                        bottom = 7.dp,
                        start = 33.dp,
                        end = 20.dp
                    ),
                    imageVector = Icons.Filled.Sort,
                    contentDescription = null,
                    tint = if (!isSortVisible) Color.White else MaterialTheme.colors.primary
                )
            }
            // Sort Dialog
                if (isSortVisible ) {
                    SortButton(
                        funcToCall = {
                            viewModel.reverseIsSort()
                            viewModel.updateSortState()

                        },
                        onDismiss = {
                            viewModel.reverseIsSort()

                        }
                    )
                }

            // Search
            IconButton(onClick = { viewModel.reverseIsSearch() }) {
                Icon(
                    modifier = modifier.padding(
                        top=7.dp,
                        bottom = 7.dp,
                        start = 33.dp,
                        end = 20.dp
                    ),
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = if (!isSearchVisible) Color.White else MaterialTheme.colors.primary
                )
            }


            // Favorite
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    modifier = modifier.padding(
                        top=7.dp,
                        bottom = 7.dp,
                        start = 33.dp,
                        end = 20.dp
                    ),
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White
                )
            }

        }
    )

}

@Composable
fun BottomOnlyHomeBar(
    modifier: Modifier = Modifier,
    //onHomeIconClick: () -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    BottomAppBar(
        modifier = Modifier.height(45.dp),
        backgroundColor = Color.Black,
        content = {
            // Home
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        modifier = modifier
                            .padding(
                                top = 7.dp,
                                bottom = 7.dp,
                                start = 33.dp,
                                end = 20.dp
                            )
                            .size(50.dp)
                        ,
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

        }
    )

}

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier,
    closeButtonOnClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Transparent)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h1,
            fontSize = 40.sp,
            color = MaterialTheme.colors.primary,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = closeButtonOnClick
        ) {
            Icon(
                imageVector = Icons.Outlined.Cancel,
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
        }

    }
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .background(Color.White),
//        contentAlignment = Alignment.Center
//    ){
//        Text(
//            text = stringResource(id = R.string.app_name),
//            style = MaterialTheme.typography.h1,
//            fontSize = 40.sp,
//            color = MaterialTheme.colors.primary,
//        )
//    }

}


@Composable
fun DrawerBody(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    val menuItems = listOf(
        MenuItem(
            route = Route.MOVIE_LIST_SCREEN,
            title = stringResource(id = R.string.movie_list_screen_title),
            icon = Icons.Filled.Movie,
            contentDescription = stringResource(id = R.string.movie_list_screen_button_content_description)
        ),
        MenuItem(
            route = Route.MOVIE_MANAGEMENT_SCREEN,
            title = stringResource(id = R.string.movie_management_screen_title),
            icon = Icons.Filled.List,
            contentDescription = stringResource(id = R.string.movie_management_screen_button_content_description)
        ),
        MenuItem(
            route = Route.APP_INFORMATION_SCREEN,
            title = stringResource(id = R.string.app_information_title),
            icon = Icons.Filled.Info,
            contentDescription = stringResource(id = R.string.app_information_content_description)
        ),
        MenuItem(
            route = Route.SETTINGS,
            title = stringResource(id = R.string.settings_title),
            icon = Icons.Filled.Settings,
            contentDescription = stringResource(id = R.string.settings_button_content_description)
        ),
        MenuItem(
            route = Route.HELP_SCREEN,
            title = stringResource(id = R.string.help_screen_title),
            icon = Icons.Filled.Help,
            contentDescription = stringResource(id = R.string.help_screen_button_content_description)
        )
    )
    LazyColumn(modifier = modifier) {
        items(menuItems) { item ->
            Row(modifier = modifier
                .fillMaxWidth()
                .clickable {
                    onItemClick(item.route)
                }
                .padding(16.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription,
                )
                Spacer(modifier = modifier.width(16.dp))
                Text(
                    text = item.title,
                    modifier = modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun FloatingAddButton(
    modifier: Modifier = Modifier,
    contentDescription: String,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = contentDescription
        )
    }
}