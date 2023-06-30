package powerrangers.eivom.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import powerrangers.eivom.R
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
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = modifier.fillMaxWidth()

            )
            {
                Text(
                    text = title,
                    
//                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h5,
                    //modifier = modifier.fillMaxWidth()
                ) 
                Spacer(modifier = Modifier.width(30.dp))
            }

        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onMenuIconClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(id = R.string.menu_button_content_description)
                )
            }
        }
    )
}

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colors.primary),
        contentAlignment = Center
    ) {
        Text(text = stringResource(id = R.string.app_name))
    }
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
            icon = Icons.Filled.List,
            contentDescription = stringResource(id = R.string.movie_list_screen_button_content_description)
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
                    contentDescription = item.contentDescription
                )
                Spacer(modifier = modifier.width(16.dp))
                Text(
                    text = item.title,
                    modifier = modifier.weight(1f)
                )
            }
        }
    }
}