package eu.tutorials.carpoolingapp.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.tutorials.carpoolingapp.R
import eu.tutorials.carpoolingapp.models.Driver

class DriverAdapter(
    private val drivers: List<Driver>,
    private val onClick: (Driver) -> Unit // Callback for handling clicks on drivers
) : RecyclerView.Adapter<DriverAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_driver, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(drivers[position], onClick)
    }

    override fun getItemCount(): Int = drivers.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        private val ratingTextView: TextView = itemView.findViewById(R.id.ratingTextView)

        fun bind(driver: Driver, onClick: (Driver) -> Unit) {
            nameTextView.text = driver.name
            addressTextView.text = driver.address
            ratingTextView.text = "Rating: ${driver.rating}"
            itemView.setOnClickListener { onClick(driver) }
        }
    }
}

