package powerrangers.eivom.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    fontSize = 50.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h1,
                    color = MaterialTheme.colors.primary,
                    modifier = modifier.padding(bottom = 30.dp)
                )
                Spacer(modifier = Modifier.width(50.dp))
            }

        },
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.background,
        navigationIcon = {
            IconButton(onClick = onMenuIconClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(id = R.string.menu_button_content_description),
                    tint = MaterialTheme.colors.primary
                )
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
            .background(color = Color.Transparent),
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
        ),
        MenuItem(
            route = Route.SETTINGS,
            title = stringResource(id = R.string.settings_title),
            icon = Icons.Filled.Settings,
            contentDescription = stringResource(id = R.string.settings_button_content_description)
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
                    // color = Purple300
                )
            }
        }
    }
}