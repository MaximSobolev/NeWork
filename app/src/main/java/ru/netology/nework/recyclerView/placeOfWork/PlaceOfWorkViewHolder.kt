package ru.netology.nework.recyclerView.placeOfWork

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.CardJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.utils.AppUtils

class PlaceOfWorkViewHolder(
    private val binding: CardJobBinding,
    private val placeOfWorkListener: PlaceOfWorkListener
) : RecyclerView.ViewHolder(binding.root) {

    private var itemJob: Job? = null
    private val editOnClickListener : View.OnClickListener =
        View.OnClickListener { itemJob?.let { placeOfWorkListener.edit(it) } }
    private val deleteOnClickListener : View.OnClickListener =
        View.OnClickListener { itemJob?.let { placeOfWorkListener.delete(it.id) } }

    init {
        binding.apply {
            editButton.setOnClickListener(editOnClickListener)
            deleteButton.setOnClickListener(deleteOnClickListener)
        }
    }


    fun bind(job: Job) {
        binding.apply {
            val nameWithPosition = "${job.name} - ${job.position}"

            val startWithFinish =
                if (job.finish == null) "${AppUtils.parseDateTimeForJobs(job.start)} - Now"
                else "${AppUtils.parseDateTimeForJobs(job.start)} - ${AppUtils.parseDateTimeForJobs(job.finish)}"
            nameAndPosition.text = nameWithPosition
            startAndFinish.text = startWithFinish
            if (job.link != null) {
                link.visibility = View.VISIBLE
                link.text = job.link
            } else {
                link.visibility = View.GONE
            }

            itemJob = job
        }
    }

}