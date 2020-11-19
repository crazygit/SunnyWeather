package com.github.crazygit.demo.sunnyweather.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.crazygit.demo.sunnyweather.R
import com.github.crazygit.demo.sunnyweather.logic.model.Place
import com.github.crazygit.demo.sunnyweather.ui.weather.WeatherActivity

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val placeName: TextView = view.findViewById(R.id.placeName)
        private val placeAddress: TextView = view.findViewById(R.id.placeAddress)

        fun bind(place: Place) {
            placeName.text = place.name
            placeAddress.text = place.address
            val activity = fragment.activity
            itemView.setOnClickListener {
                if (activity is WeatherActivity) {
                    activity.binding.drawerLayout.closeDrawers()
                    activity.viewModel.locationLat = place.location.lat
                    activity.viewModel.locationLng = place.location.lng
                    activity.viewModel.placeName = place.name
                    activity.refreshWeather()

                } else {
                    val intent = Intent(it.context, WeatherActivity::class.java).apply {
                        putExtra("location_lng", place.location.lng)
                        putExtra("location_lat", place.location.lat)
                        putExtra("place_name", place.name)
                    }
                    fragment.viewModel.savePlace(place)
                    fragment.startActivity(intent)
                    activity?.finish()

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.bind(place)
    }

    override fun getItemCount(): Int = placeList.size

}