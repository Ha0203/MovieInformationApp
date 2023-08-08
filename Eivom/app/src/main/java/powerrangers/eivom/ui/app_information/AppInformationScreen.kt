package powerrangers.eivom.ui.app_information

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar


@Composable
fun AppInformationScreen(
    modifier: Modifier = Modifier,
    navigateToMenuItem: (String) -> Unit,
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
        AppInformationBody(modifier = modifier.padding(innerPadding))
    }
}

@Composable
fun AppInformation(
    modifier : Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.height(200.dp),
    ) {
        Text(
            modifier = modifier
                .fillMaxSize()
                .padding(start = 50.dp, top = 50.dp, end = 50.dp, bottom = 0.dp),
            text = " Not to miss any blockbuster," +
                    " we will have notification for your up-coming favorite movie," +
                    " display a short overview, critics scores, previews, and even trailers. " +
                    "Create your own favorite list, jot down movie notes for  easier comparison" +
                    " and summary \n",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
        )
    }
}
@Composable
fun AppMember(
    modifier :  Modifier = Modifier,
)
{
    val members = listOf(
        Members(
            name = "Nguyen Minh Quan",
            image = R.drawable.member1
        ),
        Members(
            name = " Pham Dang Son Ha ",
            image = R.drawable.member2
        ),
        Members(
            name = " Nguyen Minh Duc ",
            image = R.drawable.member3
        ),
    )
    val members2 = listOf(
        Members(
            name = "Nguyen Phu Minh Bao",
            image = R.drawable.member4
        ),
        Members(
            name = "Tran Thu Minh",
            image = R.drawable.member5
        )
    )
    Column(modifier = modifier.fillMaxHeight()){
        Row(modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (member in members){
                Column(
                    modifier = modifier.width(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = member.image),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(120.dp)
                            .width(120.dp)
                        ,
                        contentScale = ContentScale.Crop
                    )
                    Spacer ( modifier = Modifier.height(10.dp))
                    Text(
                        text = member.name,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Row(modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (member in members2){
                Column(
                    modifier = modifier
                        .width(130.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = member.image),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(120.dp)
                            .width(120.dp)
                        ,
                        contentScale = ContentScale.Crop
                    )
                    Spacer ( modifier = Modifier.height(10.dp))
                    Text(
                        text = member.name,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun Reference (
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(50.dp)
            .background(MaterialTheme.colors.primary)
            .fillMaxSize()
    ) {
        Image(
            painterResource(id = R.drawable.firebase),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(20.dp))
        Image(
            painterResource(id = R.drawable.ic_tmdb),
            contentDescription = null
        )
    }
}

@Composable
fun AppInformationBody(
    modifier: Modifier
){
   Column(
       modifier = modifier
   ) {
       AppInformation()
       AppMember()
       Spacer(modifier = Modifier. height(15.dp))
       Reference()
   }
}