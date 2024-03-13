package com.example.villainlp.novel.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel: ViewModel() {
    private val reportModel = ReportModel()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser?.uid?:"ERROR"

    private val _selectedOption = MutableStateFlow("")
    val selectedOption : StateFlow<String> = _selectedOption

    private val _text = MutableStateFlow("")
    val text : StateFlow<String> = _text

    fun submitReport(blackedID: String, blackedName: String){
        val blackList = BlackList(
            user = user,
            blackedID = blackedID,
            blackedName = blackedName
        )
        viewModelScope.launch {
            reportModel.blackList(blackList)
        }
    }

    fun onSeleceted(newOption: String){
        _selectedOption.value = newOption
    }

    fun onTextChange(newText: String){
        _text.value = newText
    }

}