package eu.tutorials.carpoolingapp.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import eu.tutorials.carpoolingapp.R
import eu.tutorials.carpoolingapp.data.DatabaseHelper

class PassengerActivity : AppCompatActivity() {

    private lateinit var driverListAdapter: DriverListAdapter
    private lateinit var driversRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var requestRideButton: Button
    private lateinit var searchDriversButton: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passanger)

        // Initialize Views
        driversRecyclerView = findViewById(R.id.driversRecyclerView)
        progressBar = findViewById(R.id.loadingProgressBar)
        requestRideButton = findViewById(R.id.requestRideButton)
        searchDriversButton = findViewById(R.id.searchDriversButton)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Setup RecyclerView with Adapter
        driverListAdapter = DriverListAdapter()
        driversRecyclerView.layoutManager = LinearLayoutManager(this)
        driversRecyclerView.adapter = driverListAdapter

        // Handle search for drivers button click
        searchDriversButton.setOnClickListener {
            showLoading(true)

            // Fetch drivers from the database
            val drivers = dbHelper.getDrivers()

            // Simulate a delay while loading drivers
            Handler(Looper.getMainLooper()).postDelayed({
                driverListAdapter.submitList(drivers)
                showLoading(false)
            }, 2000)
        }

        // Handle request ride button click
        requestRideButton.setOnClickListener {
            Toast.makeText(this, "Ride request functionality under development.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        driversRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    class DriverListAdapter : Adapter<DriverListAdapter.DriverViewHolder>() {

        private val driverList = mutableListOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_driver, parent, false)
            return DriverViewHolder(view)
        }

        override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
            holder.bind(driverList[position])
        }

        override fun getItemCount(): Int {
            return driverList.size
        }

        fun submitList(drivers: List<String>) {
            driverList.clear()
            driverList.addAll(drivers)
            notifyDataSetChanged()
        }

        class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val driverNameTextView: TextView = itemView.findViewById(R.id.driverNameTextView)

            fun bind(driverName: String) {
                driverNameTextView.text = driverName
            }
        }
    }
}