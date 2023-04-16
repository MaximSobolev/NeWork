package ru.netology.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.authentication.AppAuth
import ru.netology.nework.error.AppError
import ru.netology.nework.error.ManipulationError
import ru.netology.nework.model.State
import ru.netology.nework.repository.SignInRepository
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository : SignInRepository,
    private val appAuth: AppAuth,
    private val manipulationError: ManipulationError
): ViewModel() {

    val state : LiveData<State>
    get() = _state
    private val _state = MutableLiveData<State>()
    val error : LiveData<Int>
        get() = _error
    private val _error = MutableLiveData<Int>()

    fun signIn(login : String, password : String) {
        _state.postValue(State(loading = true))
        viewModelScope.launch {
            try {
                val authState = repository.signIn(login, password)
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