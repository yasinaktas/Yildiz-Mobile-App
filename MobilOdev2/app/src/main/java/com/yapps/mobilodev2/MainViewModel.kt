package com.yapps.mobilodev2

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(private val userDao:UserDao,private val sharedPrefs: SharedPrefs):ViewModel() {
    private val _user:MutableLiveData<User> = MutableLiveData()
    val user:LiveData<User>
        get() = _user

    private val _forwardUserActivity:MutableLiveData<Long> = MutableLiveData()
    val forwardUserActivity:LiveData<Long>
        get() = _forwardUserActivity

    private val _forwardAdminActivity:MutableLiveData<Boolean> = MutableLiveData()
    val forwardAdminActivity:LiveData<Boolean>
        get() = _forwardAdminActivity

    private val _forwardStudentActivity:MutableLiveData<Long> = MutableLiveData()
    val forwardStudentActivity:LiveData<Long>
        get() = _forwardStudentActivity

    private val _forwardInstructorActivity:MutableLiveData<Long> = MutableLiveData()
    val forwardInstructorActivity:LiveData<Long>
        get() = _forwardInstructorActivity

    private val _forwardRegisterActivity:MutableLiveData<Boolean> = MutableLiveData()
    val forwardRegisterActivity:LiveData<Boolean>
        get() = _forwardRegisterActivity

    private val _loginMessage:MutableLiveData<String> = MutableLiveData()
    val loginMessage:LiveData<String>
        get() = _loginMessage

    init {
        if(sharedPrefs.getRemember() && sharedPrefs.getRememberedUserType() == "student"){
            _forwardStudentActivity.value = sharedPrefs.getRememberedUserId()
        }else if(sharedPrefs.getRemember() && sharedPrefs.getRememberedUserType() == "instructor"){
            _forwardInstructorActivity.value = sharedPrefs.getRememberedUserId()
        }else if(sharedPrefs.getRemember() && sharedPrefs.getRememberedUserType() == "admin"){
            _forwardAdminActivity.value = true
        }
    }

    fun saveUser(userId:Long,type:String){
        sharedPrefs.setRemember(true)
        sharedPrefs.setRememberedUser(userId)
        sharedPrefs.setRememberedUserType(type)
    }

    fun login(email:String,password:String):LiveData<User?>{
        return userDao.getUser(email,password).asLiveData()
    }


    fun forwardAdminActivity(){
        _loginMessage.value = "Admin Login"
        _forwardAdminActivity.value = true
    }

    fun forwardUserActivity(userId:Long,isRemember: Boolean,isStudent:Boolean){
        _loginMessage.value = "Successful Login"
        sharedPrefs.setRemember(isRemember)
        sharedPrefs.setRememberedUser(userId)
        if(isStudent){
            _forwardStudentActivity.value = userId
        }else{
            _forwardInstructorActivity.value = userId
        }
    }



    fun forwardUserError(){
        _loginMessage.value = "Login Error"
    }

    fun forwardRegisterActivity(){
        _forwardRegisterActivity.value = true
    }

}

class MainViewModelFactory(val context: Context):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(ThisApplication.userDao,ThisApplication.sharedPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}