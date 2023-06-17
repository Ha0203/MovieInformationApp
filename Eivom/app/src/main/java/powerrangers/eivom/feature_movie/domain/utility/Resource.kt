package powerrangers.eivom.feature_movie.domain.utility

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(data: T? = null, message: String) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

object ResourceErrorMessage {
    const val UNKNOWN = "Unknown error"

    const val RESOURCE_ERROR = "Resource error"

    const val ADD_LIST = "Adding Resource<List> error"

    const val CONVERT_MOVIELIST_TO_MOVIELISTITEMS = "Converting Resource<MovieList> to Resource<MovieListItems> error"
    const val CONVERT_MOVIEINFORMATION_TO_MOVIEITEM = "Converting Resource<MovieInformation> to Resource<MovieItem> error"

    const val GET_MOVIELIST = "Getting Resource<MOVIELIST> error"
    const val GET_MOVIEINFORMATION = "Getting Resource<MOVIEINFORMATION> error"

    const val MOVIELIST_END = "End of movie list error"
    const val LOAD_MOVIELIST = "Loading movie list error"
}

fun <T> Resource<List<T>>.addList(resource: Resource<List<T>>): Resource<List<T>> {
    return try {
        val list = (this.data ?: emptyList()) + (resource.data ?: emptyList())
        if (this is Resource.Error || resource is Resource.Error)
            Resource.Error(data = list, message = ResourceErrorMessage.RESOURCE_ERROR)
        else
            Resource.Success(data = list)
    }
    catch (e: Exception) {
        Resource.Error(data = this.data, message = ResourceErrorMessage.ADD_LIST)
    }
}

fun <T> Resource<T>.toLoading(data: T? = null): Resource.Loading<T> {
    return if (data != null)
        Resource.Loading(data = data)
    else
        Resource.Loading(data = this.data)
}

fun <T> Resource<T>.toError(data: T? = null, message: String): Resource.Error<T> {
    return if (data != null)
        Resource.Error(data = data, message = message)
    else
        Resource.Error(data = this.data, message = message)
}