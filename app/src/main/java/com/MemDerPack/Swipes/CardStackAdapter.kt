package com.MemDerPack.Swipes

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.MemDerPack.Logic.PictureLogic
import com.MemDerPack.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import java.lang.Exception
import java.net.URL

public class CardStackAdapter(

        public var pictures: List<PictureLogic.Picture> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picture = pictures[position]

        try {
            Glide.with(holder.image)
                    .load(URL(picture.ImagePath))
                    .into(holder.image)
            Log.d("URL", picture.ImagePath);
        } catch (e: Exception){
            Log.d("EXEPTION", e.message);
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