package ru.netology.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.authentication.AppAuth
import ru.netology.nework.dto.User
import ru.netology.nework.error.AppError
import ru.netology.nework.error.ManipulationError
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val manipulationError: ManipulationError,
    private val appAuth: AppAuth
) : ViewModel() {

    val error: LiveData<Int>
        get() = _error
    private val _error: MutableLiveData<Int> = MutableLiveData()

    private var selectedUser: User? = null

    val selectedUsers: LiveData<List<User>?>
        get() = _selectedUsers
    private val _selectedUsers: MutableLiveData<List<User>?> = MutableLiveData(null)

    val data: LiveData<List<User>> = repository.data
    var filterList: List<User> = listOf()

    val user : LiveData<User?>
        get() = _user
    private val _user = MutableLiveData<User?>(null)

    fun requestUsers() {
        viewModelScope.launch {
            try {
                repository.requestUsers()
            } catch (e: AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }

    fun setupList(): List<User> {
        data.value?.let { usersList ->
            selectedUsers.value?.let { selectedUsersList ->
                selectedUsersList.map { selectedUser ->
                    return usersList.filter { user ->
                        selectedUser.id != user.id
                    }
                }
            }
            return usersList
        }
        return emptyList()
    }

    fun userFilter(newText: String) {
        filterList = setupList()
        filterList = filterList.filter { user ->
            user.name.contains(newText, true)
        }
        selectedUsers.value?.let { selectedUsersList ->
            selectedUsersList.map { user ->
                filterList = filterList.filter {
                    user.id != it.id
                }
            }
        }

    }

    fun addSelectedUser(user: User) {
        selectedUser = user
    }

    fun saveSelectedUser(): Boolean =
        selectedUser?.let { user ->
            val userList = _selectedUsers.value
            if (userList != null) {
                _selectedUsers.postValue(userList + listOf(user))
            } else {
                _selectedUsers.postValue(listOf(user))
            }
            true
        } ?: false


    fun clearSelectedUser() {
        selectedUser = null
    }

    fun deleteSelectedUser(userId: Int) {
        val userList = _selectedUsers.value
        userList?.let {
            _selectedUsers.postValue(userList.filter {
                it.id != userId
            })
        }

    }

    fun clear() {
        _selectedUsers.postValue(null)
    }

    fun getSelectedUserIds() : List<Int> {
        val selectedUsers = selectedUsers.value
        _selectedUsers.postValue(null)
        return if (selectedUsers != null) {
            val selectedIdsList = mutableListOf<Int>()
            selectedUsers.map { user ->
                selectedIdsList.add(user.id)
            }
            selectedIdsList
        } else {
            emptyList()
        }
    }

    fun setupUsersFromSelectedIds(ids : List<Int>) {
        viewModelScope.launch {
            val users = repository.getUsers(ids)
            _selectedUsers.postValue(users)
        }
    }

    fun whosePage(id : Int) : Boolean  = getMyId() == id

    fun getMyId() : Int = appAuth.authStateFlow.value.id

    fun getUser(id : Int) {
        viewModelScope.launch {
            try {
                val user = repository.getUser(id)
                _user.postValue(user)
            } catch (e : AppError) {
                _error.postValue(manipulationError.manipulation(e))
            }
        }
    }

    fun logOut() {
        appAuth.removeAuth()
    }

}