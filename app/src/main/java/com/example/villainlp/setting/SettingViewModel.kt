package com.example.villainlp.setting

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SettingViewModel : ViewModel() {
    val userImage = MutableLiveData<Uri>()
    val userName = MutableLiveData<String>()
    val userEmail = MutableLiveData<String>()

    fun fetchUserData(auth: FirebaseAuth) {
        userImage.value = auth.currentUser?.photoUrl
        userName.value = auth.currentUser?.displayName ?: ""
        userEmail.value = auth.currentUser?.email ?: ""
    }

    fun signOut(auth: FirebaseAuth) {
        auth.signOut()
    }
}