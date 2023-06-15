package powerrangers.eivom.feature_movie.domain.utility

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(data: T? = null, message: String) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

fun <T> Resource<List<T>>.add(resource: Resource<List<T>>): Resource<List<T>> {
    return try {
        val list = (this.data ?: emptyList()) + (resource.data ?: emptyList())
        Resource.Success(data = list)
    }
    catch (e: Exception) {
        Resource.Error(data = this.data, message = "Adding resource error")
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