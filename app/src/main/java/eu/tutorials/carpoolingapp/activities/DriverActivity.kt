package eu.tutorials.carpoolingapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import eu.tutorials.carpoolingapp.R
import eu.tutorials.carpoolingapp.data.DatabaseHelper

class DriverActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        // Initialize the database helper
        dbHelper = DatabaseHelper(this)

        // Set up the ListView for showing passengers needing rides
        val listView: ListView = findViewById(R.id.listViewPassengers)

        // Fetch the passengers who need rides from the database
        val passengersInNeed = getPassengersInNeed()

        if (passengersInNeed.isEmpty()) {
            Toast.makeText(this, "No passengers in need of a ride", Toast.LENGTH_SHORT).show()
        }

        // Set the adapter for the ListView to display the passengers
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, passengersInNeed)
        listView.adapter = adapter

        // Handle item click to accept a passenger's request
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedPassenger = passengersInNeed[position]
            acceptPassengerRequest(selectedPassenger)
        }

        // Set up Google Maps
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) // This triggers the onMapReady callback
    }

    // Fetch the list of passengers from the database who need rides
    @SuppressLint("Range")
    private fun getPassengersInNeed(): List<String> {
        val passengers = mutableListOf<String>()
        val cursor = dbHelper.getPassengersInNeed()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val passengerName = cursor.getString(cursor.getColumnIndex("name"))
                passengers.add(passengerName)
            }
            cursor.close()
        }
        return passengers
    }

    // Handle accepting the passenger's request
    private fun acceptPassengerRequest(passenger: String) {
        Toast.makeText(this, "Accepted ride request from $passenger", Toast.LENGTH_SHORT).show()
        // Implement further logic if needed
    }

    // This is where we correctly override onMapReady
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Example: Add markers for passengers' locations (replace with actual data from the database)
        val passengersLocation = getPassengersLocations()

        for (location in passengersLocation) {
            val latLng = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(location.name))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
        }
    }

    // Example function to fetch passengers' locations (replace with actual data from your database)
    private fun getPassengersLocations(): List<PassengerLocation> {
        // This should fetch data from your database, for now, we use static locations
        return listOf(
            PassengerLocation("Passenger 1", 37.7749, -122.4194), // Example: San Francisco
            PassengerLocation("Passenger 2", 34.0522, -118.2437), // Example: Los Angeles
            PassengerLocation("Passenger 3", 40.7128, -74.0060)  // Example: New York
        )
    }

    data class PassengerLocation(val name: String, val latitude: Double, val longitude: Double)
}
