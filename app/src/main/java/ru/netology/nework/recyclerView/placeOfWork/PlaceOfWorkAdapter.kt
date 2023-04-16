package ru.netology.nework.recyclerView.placeOfWork

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nework.databinding.CardJobBinding
import ru.netology.nework.dto.Job

class PlaceOfWorkAdapter(
    private val placeOfWorkListener: PlaceOfWorkListener
) : ListAdapter<Job, PlaceOfWorkViewHolder>(PlaceOfWorkDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceOfWorkViewHolder {
        val cardBinding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceOfWorkViewHolder(cardBinding, placeOfWorkListener)
    }

    override fun onBindViewHolder(holder: PlaceOfWorkViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job)
    }


}