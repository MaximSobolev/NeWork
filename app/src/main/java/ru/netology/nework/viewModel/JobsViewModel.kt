package ru.netology.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Job
import ru.netology.nework.error.AppError
import ru.netology.nework.error.ManipulationError
import ru.netology.nework.model.State
import ru.netology.nework.repository.JobsRepository
import ru.netology.nework.utils.AppUtils
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val repository: JobsRepository, private val manipulationError: ManipulationError
) : ViewModel() {

    val state: LiveData<State>
        get() = _state
    private val _state = MutableLiveData<State>()
    val error: LiveData<Int>
        get() = _error
    private val _error = MutableLiveData<Int>()


    val userJobs: LiveData<List<Job>?>
        get() = _userJobs
    private val _userJobs = MutableLiveData<List<Job>?>(null)

    fun delete(id : Int) {
        viewModelScope.launch {
            try {
                repository.deleteJob(id)
                val works = userJobs.value
                _userJobs.postValue(works?.filter {
                    it.id != id
                })
            } catch (e : AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }

    fun sendJob(job : Job) {
        viewModelScope.launch {
            _state.postValue(State(loading = true))
            try {
                val responseJob = repository.save(job)
                addToList(responseJob)
                _state.postValue(State(idle = true))
            } catch (e : AppError) {
                _state.postValue(State(error = true))
                _error.postValue(manipulationError.manipulation(e))
            }
        }

    }

    private fun addToList(job : Job) {
        val oldJobList = userJobs.value
        if (oldJobList != null) {
            _userJobs.postValue(oldJobList + listOf(job))
        } else {
            _userJobs.postValue(listOf(job))
        }
    }

    fun getEditedJob (oldJob: Job, newJob: Job): Job {
        val newName = if (oldJob.name != newJob.name) newJob.name else oldJob.name
        val newPosition = if (oldJob.position != newJob.position) newJob.position else oldJob.position
        val newStart = if (oldJob.start != newJob.start) newJob.start else oldJob.start
        val newFinish = if (oldJob.finish != newJob.finish) newJob.finish else oldJob.finish
        val newLink = if (oldJob.link != newJob.link) newJob.link else oldJob.link
        return Job(oldJob.id, newName, newPosition, newStart, newFinish, newLink)
    }

    fun getUserJobs(userId: Int) {
        viewModelScope.launch {
            try {
                val jobs = repository.getUserJob(userId)
                _userJobs.postValue(jobs)
            } catch (e: AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }

    fun getFilteredList(jobList : List<Job>) : List<Job> {
        val newList = mutableListOf<Job>()
        jobList.map {job ->
            if (newList.isEmpty()) {
                newList.add(job)
            } else {
                val listSize = newList.size

                for ((index, filteredJob) in newList.withIndex()) {
                    if (AppUtils.compareDateTime(filteredJob.start, job.start) > 0) {
                        newList.add(index, job)
                        break
                    }
                }

                if (newList.size == listSize) {
                    newList.add(job)
                } else {
                    job
                }
            }
        }
        return newList
    }

}