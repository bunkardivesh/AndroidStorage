package com.divesh.androidstorage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.divesh.androidstorage.databinding.LayoutPhotoBinding

class InternalPhotoAdapter : ListAdapter<PhotoModel,InternalPhotoAdapter.PhotoViewHolder>(Companion) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = LayoutPhotoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

        val photo = getItem(position)

        holder.photoBinding.apply {
                imageView.setImageBitmap(photo.bmp)

            val aspectRatio = photo.bmp.width.toFloat() / photo.bmp.height.toFloat()
            ConstraintSet().apply {
                clone(root)
                setDimensionRatio(imageView.id, aspectRatio.toString())
                applyTo(root)
            }
        }
    }

    inner class PhotoViewHolder(val photoBinding: LayoutPhotoBinding): RecyclerView.ViewHolder(photoBinding.root)

    companion object: DiffUtil.ItemCallback<PhotoModel>(){
        override fun areItemsTheSame(oldItem: PhotoModel, newItem: PhotoModel) =
            oldItem.fileName == newItem.fileName


        override fun areContentsTheSame(oldItem: PhotoModel, newItem: PhotoModel) =
            oldItem.fileName == newItem.fileName && oldItem.bmp.sameAs(newItem.bmp)

    }



}