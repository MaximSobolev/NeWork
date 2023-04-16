package ru.netology.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventCreate
import ru.netology.nework.dto.EventType
import ru.netology.nework.error.AppError
import ru.netology.nework.error.ManipulationError
import ru.netology.nework.repository.EventRepository
import ru.netology.nework.repository.MediaRepository
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val manipulationError: ManipulationError,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    val error: LiveData<Int?>
        get() = _error
    private val _error = MutableLiveData<Int?>(null)

    private var emptyEvent = EventCreate()

    val data: Flow<PagingData<Event>> = repository.data

    fun likeEvent(event: Event) {
        viewModelScope.launch {
            try {
                repository.likeEvent(event)
            } catch (e: AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }

    fun participateEvent(event: Event) {
        viewModelScope.launch {
            try {
                repository.participateEvent(event)
            } catch (e: AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }

    fun removeEventById(id: Int) {
        viewModelScope.launch {
            try {
                repository.removeEventById(id)
            } catch (e : AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }

    fun saveId(id : Int) {
        emptyEvent = emptyEvent.copy(id = id)
    }

    fun saveContent(content : String) {
        emptyEvent = emptyEvent.copy(content = content)
    }

    fun saveType(type : EventType) {
        emptyEvent = emptyEvent.copy(type = type)
    }

    fun saveDatetime(datetime : String) {
        emptyEvent = emptyEvent.copy(datetime = datetime)
    }

    fun saveLink(link : String?) {
        emptyEvent = emptyEvent.copy(link = link)
    }

    fun saveSpeakersIds(ids : List<Int>) {
        emptyEvent = emptyEvent.copy(speakerIds = ids)
    }

    fun saveEvent() {
        viewModelScope.launch {
            try {
                emptyEvent = emptyEvent.copy(attachment = mediaRepository.getAttach())
                repository.save(emptyEvent)
                mediaRepository.deleteAudioList()
                mediaRepository.deleteMedia()
                emptyEvent = EventCreate()
            } catch (e : AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }

}