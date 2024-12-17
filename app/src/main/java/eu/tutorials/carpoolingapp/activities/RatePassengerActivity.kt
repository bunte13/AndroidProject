package eu.tutorials.carpoolingapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import eu.tutorials.carpoolingapp.R
import eu.tutorials.carpoolingapp.data.DatabaseHelper

class RatePassengerActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_passenger)

        dbHelper = DatabaseHelper(this)

        val etPassengerEmail: EditText = findViewById(R.id.et_passenger_email)
        val btnViewRating: Button = findViewById(R.id.btn_view_rating)
        val btnSubmitRating: Button = findViewById(R.id.btn_submit_rating)
        val ratingBar: RatingBar = findViewById(R.id.rating_bar)

        btnViewRating.setOnClickListener {
            val passengerEmail = etPassengerEmail.text.toString().trim()

            if (passengerEmail.isNotEmpty()) {
                val averageRating = dbHelper.getPassengerAverageRating(passengerEmail)
                Toast.makeText(this, "Passenger's Average Rating: $averageRating", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show()
            }
        }

        btnSubmitRating.setOnClickListener {
            val passengerEmail = etPassengerEmail.text.toString().trim()
            val rating = ratingBar.rating

            if (passengerEmail.isNotEmpty() && rating > 0) {
                val sharedPreferences = getSharedPreferences("carpooling_prefs", MODE_PRIVATE)
                val driverEmail = sharedPreferences.getString("current_user_email", null)

                if (driverEmail != null) {
                    val success = dbHelper.addRating(driverEmail, passengerEmail, rating)
                    if (success) {
                        Toast.makeText(this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to submit rating.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No logged-in driver found.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid email and rating.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
