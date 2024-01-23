package com.example.villainlp.setting

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingViewModel(private val auth: FirebaseAuth) : ViewModel() {
    private val _userImage = MutableStateFlow<Uri?>(null)
    val userImage: StateFlow<Uri?> = _userImage.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _userImage.value = auth.currentUser?.photoUrl
            _userName.value = auth.currentUser?.displayName
            _userEmail.value = auth.currentUser?.email
        }
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
        }
    }
}