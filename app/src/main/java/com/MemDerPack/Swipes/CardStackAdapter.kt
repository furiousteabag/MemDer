package com.MemDerPack.Swipes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.MemDerPack.Logic.PictureLogic
import com.MemDerPack.R
import com.bumptech.glide.Glide
import java.lang.Exception

class CardStackAdapter(
        private var pictures: List<PictureLogic.Picture> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picture = pictures[position]
//        holder.name.text = "${spot.id}. ${spot.name}"
//        holder.city.text = spot.city

        try {
            Glide.with(holder.image)
                    .load(picture.Image.imagePath)
                    .into(holder.image)

        }
        catch (e: Exception) {
            println(e.message);
        }


    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    fun setSpots(pictures: List<PictureLogic.Picture>) {
        this.pictures = pictures
    }

    fun getSpots(): List<PictureLogic.Picture> {
        return pictures
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //        val name: TextView = view.findViewById(R.id.item_name)
//        var city: TextView = view.findViewById(R.id.item_city)
        var image: ImageView = view.findViewById(R.id.item_image)
    }

}