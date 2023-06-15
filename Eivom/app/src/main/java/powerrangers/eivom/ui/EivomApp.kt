package powerrangers.eivom.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import powerrangers.eivom.navigation.EivomNavigationHost

@Composable
fun EivomApp(navController: NavHostController = rememberNavController()) {
    EivomNavigationHost(navController = navController)
}