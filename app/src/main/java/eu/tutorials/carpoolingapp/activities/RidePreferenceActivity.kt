package eu.tutorials.carpoolingapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import eu.tutorials.carpoolingapp.data.DatabaseHelper
import eu.tutorials.carpoolingapp.R

class RidePreferenceActivity : AppCompatActivity() {

    private lateinit var rgRidePreference: RadioGroup
    private lateinit var btnSavePreference: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_preference)

        // Initialize views
        rgRidePreference = findViewById(R.id.rg_ride_preference)
        btnSavePreference = findViewById(R.id.btn_save_preference)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Get the user email from the intent
        val userEmail = intent.getStringExtra("USER_EMAIL")

        if (userEmail != null) {
            // Fetch the user type (Driver or Passenger) from the database
            val userType = dbHelper.getUserTypeByEmail(userEmail)

            if (userType == "Driver") {
                // If the user is a driver, skip ride preference screen and go directly to the Driver Dashboard
                val intent = Intent(this, DriverDashboardActivity::class.java)
                intent.putExtra("USER_EMAIL", userEmail)  // Pass the email to the Driver Dashboard
                startActivity(intent)
                finish()
                return  // Stop further execution
            }
        }

        // Handle the save preference button click for passengers
        btnSavePreference.setOnClickListener {
            val isSeekingRide = when (rgRidePreference.checkedRadioButtonId) {
                R.id.rb_need_ride -> true
                else -> false
            }

            if (userEmail != null) {
                val result = dbHelper.updateRidePreference(userEmail, isSeekingRide)

                if (result > 0) {
                    Toast.makeText(this, "Preference saved", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, PassengerActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save preference", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
