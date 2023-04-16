package ru.netology.nework.recyclerView.placeOfWork

import ru.netology.nework.dto.Job

interface PlaceOfWorkListener {
    fun edit(job: Job)
    fun delete (id : Int)
}