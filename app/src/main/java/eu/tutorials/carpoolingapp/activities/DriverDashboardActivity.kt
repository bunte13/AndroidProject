package eu.tutorials.carpoolingapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import eu.tutorials.carpoolingapp.R
import eu.tutorials.carpoolingapp.data.DatabaseHelper

class DriverDashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_dashboard)

        dbHelper = DatabaseHelper(this)

        val etPassengerEmail: EditText = findViewById(R.id.et_passenger_email)
        val btnViewRating: Button = findViewById(R.id.btn_view_rating)

        btnViewRating.setOnClickListener {
            val passengerEmail = etPassengerEmail.text.toString().trim()

            if (passengerEmail.isNotEmpty()) {
                val averageRating = dbHelper.getPassengerAverageRating(passengerEmail)
                Toast.makeText(this, "Passenger's Average Rating: $averageRating", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
