package powerrangers.eivom.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import powerrangers.eivom.feature_movie.ui.movie_detail.MovieDetailScreen
import powerrangers.eivom.feature_movie.ui.movie_list.MovieListScreen

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
    }
}