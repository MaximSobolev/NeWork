package ru.netology.nework.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class PermissionViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context
) : ViewModel() {

    data class UiState(
        val hasReadMediaAudioAccess: Boolean,
        val hasReadExternalStorageAccess: Boolean
    )

    val uiState: LiveData<UiState>
        get() = _uiState
    private val _uiState: MutableLiveData<UiState> = MutableLiveData(
        UiState(
            hasReadMediaAudioAccess = if (Build.VERSION.SDK_INT >= 33) {
                hasPermission(android.Manifest.permission.READ_MEDIA_AUDIO)
            } else false,
            hasReadExternalStorageAccess = hasPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        )
    )

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun onPermissionChange(permission: String, isGranted: Boolean) {
        when (permission) {
            android.Manifest.permission.READ_MEDIA_AUDIO -> {
                _uiState.value?.let { state ->
                    _uiState.postValue(state.copy(hasReadMediaAudioAccess = isGranted))
                }
            }
            android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                _uiState.value?.let { state ->
                    _uiState.postValue(state.copy(hasReadExternalStorageAccess = isGranted))
                }
            }
        }
    }

    fun canAddAudio(): Boolean {
        uiState.value?.let { state ->
            if (Build.VERSION.SDK_INT >= 33) {
                return state.hasReadMediaAudioAccess
            } else {
                return state.hasReadExternalStorageAccess
            }
        } ?: return false
    }

    fun getPermission(): String =
        if (Build.VERSION.SDK_INT >= 33) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else android.Manifest.permission.READ_EXTERNAL_STORAGE


}