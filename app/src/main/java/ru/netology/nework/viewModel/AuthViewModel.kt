package ru.netology.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.netology.nework.authentication.AppAuth
import ru.netology.nework.model.AuthState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {
    val data : LiveData<AuthState> = appAuth.authStateFlow.asLiveData(Dispatchers.Default)
    val authenticated : Boolean
        get() = appAuth.authStateFlow.value.id != 0
}