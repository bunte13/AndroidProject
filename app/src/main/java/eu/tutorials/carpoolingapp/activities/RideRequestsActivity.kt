package eu.tutorials.carpoolingapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.tutorials.carpoolingapp.R
import eu.tutorials.carpoolingapp.data.DatabaseHelper

class RideRequestsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_requests)

        dbHelper = DatabaseHelper(this)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_requests)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val requests = fetchRideRequests()
        recyclerView.adapter = RideRequestAdapter(requests)
    }

    @SuppressLint("Range")
    private fun fetchRideRequests(): List<RideRequest> {
        val cursor = dbHelper.getPassengersInNeed()
        val requests = mutableListOf<RideRequest>()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EMAIL))
                val name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NAME))
                val address = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ADDRESS))
                requests.add(RideRequest(email, name, address))
            } while (cursor.moveToNext())
            cursor.close()
        }

        return requests.toList()
    }
}

data class RideRequest(val email: String, val name: String, val address: String)

class RideRequestAdapter(private val requests: List<RideRequest>) :
    RecyclerView.Adapter<RideRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_ride_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)

        fun bind(request: RideRequest) {
            emailTextView.text = request.email
            nameTextView.text = request.name
            addressTextView.text = request.address
        }
    }
}
