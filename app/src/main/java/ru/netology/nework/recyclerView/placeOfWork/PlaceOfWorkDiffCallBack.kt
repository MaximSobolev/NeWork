package ru.netology.nework.recyclerView.placeOfWork

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nework.dto.Job

class PlaceOfWorkDiffCallBack : DiffUtil.ItemCallback<Job>() {

    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return (oldItem.name == newItem.name
                && oldItem.position == newItem.position
                && oldItem.start == newItem.start
                && oldItem.finish == newItem.finish
                && oldItem.link == newItem.link)
    }
}