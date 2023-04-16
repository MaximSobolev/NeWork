package ru.netology.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.authentication.AppAuth
import ru.netology.nework.dto.UnknownUser
import ru.netology.nework.error.AppError
import ru.netology.nework.error.ManipulationError
import ru.netology.nework.model.State
import ru.netology.nework.repository.MediaRepository
import ru.netology.nework.repository.SignUpRepository
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: SignUpRepository,
    private val mediaRepository: MediaRepository,
    private val appAuth: AppAuth,
    private val manipulationError: ManipulationError
) : ViewModel() {

    private var user = UnknownUser()

    val state: LiveData<State>
        get() = _state
    private val _state = MutableLiveData<State>()

    val error : LiveData<Int>
        get() = _error
    private val _error = MutableLiveData<Int>()

    fun saveLoginAndPassword(login: String, password: String) {
        user = UnknownUser(login = login, password = password)
    }

    fun saveName(name: String) {
        _state.postValue(State(loading = true))
        user = user.copy(name = name, avatar = mediaRepository.photo.value?.file)
        signUp(user)
    }

    private fun signUp(user: UnknownUser) {
        viewModelScope.launch {
            try {
                val authState = mediaRepository.photo.value?.let { photoModel ->
                   repository.signUpWithAvatar(user.login, user.password, user.name, photoModel.file)
                } ?: repository.signUp(user.login, user.password, user.name)

                if (authState.id != 0 && authState.token != null) {
                    appAuth.setAuth(authState.id, authState.token)
                }
                _state.postValue(State(idle = true))
            } catch (e : AppError) {
                _state.postValue(State(error = true))
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }
}