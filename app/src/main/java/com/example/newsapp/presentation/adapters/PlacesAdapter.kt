package com.example.newsapp.presentation.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.data.model.Place
import com.squareup.picasso.Picasso

class PlacesAdapter(private var places: List<Place>) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.place_title)
        val description: TextView = view.findViewById(R.id.place_description)
        val distance: TextView = view.findViewById(R.id.place_distance)
        val thumbnail: ImageView = view.findViewById(R.id.place_thumbnail)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val place = places[position]
                    val url = place.articleUrl
                    if (url.isNotBlank()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.title.text = place.title

        place.description?.let { description ->
            holder.description.apply {
                visibility = View.VISIBLE
                text = description
            }
        } ?: run {
            holder.description.visibility = View.GONE
        }

        place.distance.let { distance ->
            holder.distance.apply {
                visibility = View.VISIBLE
                text = "$distance miles"
            }
        }

        val imageUrl = place.thumbnail ?: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/Gnome-image-missing.svg/200px-Gnome-image-missing.svg.png"
        Picasso.get().load(imageUrl).into(holder.thumbnail)
    }


    override fun getItemCount() = places.size

    fun submitList(placeItems: List<Place>) {
        places = placeItems
        notifyDataSetChanged()
    }
}
