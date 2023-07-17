package powerrangers.eivom.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import powerrangers.eivom.feature_movie.ui.movie_detail.MovieDetailScreen
import powerrangers.eivom.feature_movie.ui.movie_list.MovieListScreen
import powerrangers.eivom.feature_movie.ui.movie_management.MovieManagementScreen
import powerrangers.eivom.ui.settings.SettingsScreen

@Composable
fun EivomNavigationHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.MOVIE_LIST_SCREEN
    ) {
        composable(
            route = Route.MOVIE_LIST_SCREEN
        ) {
            MovieListScreen(
                navigateToMenuItem = {
                    navController.navigate(it)
                },
                navigateToMovieDetailScreen = { movieId ->
                    navController.navigate(Route.MOVIE_DETAIL_SCREEN + "/${movieId}")
                }
            )
        }
        composable(
            route = Route.MOVIE_MANAGEMENT_SCREEN
        ) {
            MovieManagementScreen(
                navigateToMenuItem = {
                    navController.navigate(it)
                },
                navigateToSettingsScreen = {
                    navController.navigate(Route.SETTINGS)
                },
                navigateToMovieDetailScreen = { movieId ->
                    navController.navigate(Route.MOVIE_DETAIL_SCREEN + "/${movieId}")
                }
            )
        }
        composable(
            route = Route.MOVIE_DETAIL_SCREEN +
                    "/{${Route.MOVIE_DETAIL_SCREEN_MOVIE_ID}}",
            arguments = listOf(
                navArgument(Route.MOVIE_DETAIL_SCREEN_MOVIE_ID) {
                    type = NavType.IntType
                }
            )
        ) {
            MovieDetailScreen(
                navigateToMenuItem = {
                    navController.navigate(it)
                },
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable(
            route = Route.SETTINGS
        ) {
            SettingsScreen(
                navigateToMenuItem = {
                    navController.navigate(it)
                }
            )
        }
    }
}