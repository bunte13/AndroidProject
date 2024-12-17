package eu.tutorials.carpoolingapp.models

@kotlinx.parcelize.Parcelize
data class Driver(
    val email: String,
    val name: String,
    val address: String,
    val rating: Float
) : android.os.Parcelable
