package eu.tutorials.carpoolingapp.models

@kotlinx.parcelize.Parcelize
data class Passenger(
    val email: String,
    val name: String,
    val address: String?,
    val rating: Float
) : android.os.Parcelable
