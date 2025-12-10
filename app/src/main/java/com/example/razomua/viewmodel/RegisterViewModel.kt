package com.example.razomua.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.razomua.data.local.DatabaseProvider
import com.example.razomua.data.local.entity.UserEntity
import com.example.razomua.model.User
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

//class RegisterViewModel(application: Application) : AndroidViewModel(application) {
//
//    // DAO для роботи з Room
//    private val userDao = DatabaseProvider.getDatabase(application).userDao()
//
//    // Список користувачів для UI
//    private val _users = mutableStateListOf<User>()
//    val users: List<User> get() = _users
//
//    // Тимчасове збереження даних під час реєстрації (між екранами)
//    var tempUser: User? = null
//
//    /** Початок реєстрації: зберігаємо email та пароль тимчасово */
//    fun startRegistration(email: String, password: String) {
//        tempUser = User(
//            id = 0, // Room згенерує id
//            email = email,
//            password = password
//        )
//    }
//
//    /** Завершення реєстрації: додаємо ім'я, гендер, день народження і зберігаємо в Room */
//    fun completeRegistration(name: String, gender: String?, birthday: String?) {
//        tempUser?.let { user ->
//            val fullUser = user.copy(
//                name = name,
//                gender = gender,
//                birthday = birthday
//            )
//
//            // Зберігаємо в Room
//            viewModelScope.launch {
//                val userEntity = UserEntity(
//                    id = 0, // Room автоматично згенерує id
//                    name = fullUser.name,
//                    gender = fullUser.gender,
//                    birthday = fullUser.birthday,
//                    email = fullUser.email,
//                    password = fullUser.password
//                )
//                userDao.insert(userEntity)
//
//                // Оновлюємо локальний список користувачів для UI
//                loadAllUsers()
//            }
//
//            tempUser = null
//        }
//    }
//
//    /** Завантаження всіх користувачів із бази даних */
//    fun loadAllUsers() {
//        viewModelScope.launch {
//            val allUsers = userDao.getAllUsers()
//            _users.clear()
//            _users.addAll(allUsers.map { userEntity ->
//                User(
//                    id = userEntity.id,
//                    name = userEntity.name,
//                    gender = userEntity.gender,
//                    birthday = userEntity.birthday,
//                    email = userEntity.email,
//                    password = userEntity.password
//                )
//            })
//        }
//    }
//
//    fun logAllUsers() {
//        viewModelScope.launch {
//            val users = userDao.getAllUsers()
//            users.forEach { Log.d("DB_USERS", it.toString()) }
//        }
//    }
//
//
//}


//class RegisterViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val auth = FirebaseAuth.getInstance()
//    private val userDao = DatabaseProvider.getDatabase(application).userDao()
//
//    private val _registerState = MutableLiveData<Boolean>()
//    val registerState: LiveData<Boolean> = _registerState
//
//    private val _error = MutableLiveData<String>()
//    val error: LiveData<String> = _error
//
//    fun register(email: String, password: String, name: String) {
//
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnSuccessListener {
//
//                viewModelScope.launch {
//                    userDao.insert(
//                        UserEntity(
//                            id = 0,
//                            email = email,
//                            password = password,
//                            name = name,
//                        )
//                    )
//                }
//
//                _registerState.value = true
//            }
//            .addOnFailureListener {
//                _error.value = it.localizedMessage
//            }
//    }
//}

package com.example.razomua.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.razomua.data.local.DatabaseProvider
import com.example.razomua.data.local.entity.UserEntity
import com.example.razomua.repository.FirebaseChatRepository // ДОДАТИ ІМПОРТ
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val userDao = DatabaseProvider.getDatabase(application).userDao()
    private val chatRepository = FirebaseChatRepository() // ДОДАТИ

    private val _registerState = MutableLiveData<Boolean>()
    val registerState: LiveData<Boolean> = _registerState

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun register(email: String, password: String, name: String) {

        Log.d("REGISTER", "User tries to register: $email")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("REGISTER", "Registration successful: $email")
                viewModelScope.launch {
                    userDao.insert(
                        UserEntity(
                            id = 0,
                            email = email,
                            password = password,
                            name = name,
                        )
                    )

                    // ДОДАТИ: Ініціалізувати користувача в Firebase Realtime Database
                    chatRepository.initializeCurrentUser().onSuccess {
                        Log.d("RegisterViewModel", "User initialized in Firebase Database")
                        _registerState.postValue(true)
                    }.onFailure { error ->
                        Log.e("RegisterViewModel", "Failed to initialize user", error)
                        _error.postValue("Помилка ініціалізації: ${error.localizedMessage}")
                    }
                }

                _registerState.value = true
            }
            .addOnFailureListener {exception ->
                Log.e("REGISTER", "Registration failed: ${exception.localizedMessage}")
                _error.value = exception.localizedMessage
            }
    }
}

