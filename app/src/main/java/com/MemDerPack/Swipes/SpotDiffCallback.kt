package com.MemDerPack.Swipes

import android.support.v7.util.DiffUtil
import com.MemDerPack.Logic.PictureLogic

class SpotDiffCallback(
        private val old: List<PictureLogic.Picture>,
        private val new: List<PictureLogic.Picture>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
       return old[oldPosition].id == new[newPosition].id

    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}