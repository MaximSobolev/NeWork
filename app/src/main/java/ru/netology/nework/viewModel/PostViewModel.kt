package ru.netology.nework.viewModel

import androidx.lifecycle.*
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.nework.dto.*
import ru.netology.nework.error.AppError
import ru.netology.nework.error.ManipulationError
import ru.netology.nework.repository.MediaRepository
import ru.netology.nework.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val mediaRepository: MediaRepository,
    private val manipulationError: ManipulationError,
) : ViewModel() {

    val data: Flow<PagingData<Post>> = repository.data

    lateinit var userWallData : Flow<PagingData<Post>>

    val error: LiveData<Int?>
        get() = _error
    private val _error: MutableLiveData<Int?> = MutableLiveData(null)

    private var emptyPost = PostCreate()


    fun likeById(post: Post) {
        viewModelScope.launch {
            try {
                repository.likePost(post)
            } catch (e: AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }

    fun onRemove(id: Int) {
        viewModelScope.launch {
            try {
                repository.onRemove(id)
            } catch (e: AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }


    fun saveId(postId: Int) {
        emptyPost = emptyPost.copy(id = postId)
    }

    fun saveMessage(message: String) {
        emptyPost = emptyPost.copy(content = message)
    }

    fun saveLink(postLink: String?) {
        emptyPost = emptyPost.copy(link = postLink)
    }

    fun saveMentionIds(usersIds: List<Int>) {
        emptyPost = emptyPost.copy(mentionIds = usersIds)
    }


    fun savePost() {
        viewModelScope.launch {
            try {
                emptyPost = emptyPost.copy(attachment = mediaRepository.getAttach())
                repository.save(emptyPost)
                mediaRepository.deleteAudioList()
                mediaRepository.deleteMedia()
                emptyPost = PostCreate()
            } catch (e: AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }

    fun setPagingSource(userId : Int) {
        repository.setNewPager(userId)
        userWallData = repository.userWallData
    }
}