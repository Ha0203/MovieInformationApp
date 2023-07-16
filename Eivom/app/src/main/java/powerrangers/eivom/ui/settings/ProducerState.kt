package powerrangers.eivom.ui.settings

data class ProducerState(
    val isSignInSuccess: Boolean = true,
    val isSignOutSuccess: Boolean = false,
    val errorMessage: String? = null,
    val userName: String? = null
)
