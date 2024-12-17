package eu.tutorials.carpoolingapp.models

data class Rating(
    val driverEmail: String,
    val passengerEmail: String,
    val ratingValue: Float
)
