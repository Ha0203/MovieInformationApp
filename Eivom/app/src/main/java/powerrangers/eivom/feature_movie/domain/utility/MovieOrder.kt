package powerrangers.eivom.feature_movie.domain.utility

sealed class MovieOrder(val order: Order) {
    class ReleaseDate(order: Order): MovieOrder(order)
    class Rating(order: Order): MovieOrder(order)
    class Vote(order: Order): MovieOrder(order)
    class OriginalTitle(order: Order): MovieOrder(order)
    class Title(order: Order): MovieOrder(order)
}

enum class Order {
    ASCENDING, DESCENDING
}