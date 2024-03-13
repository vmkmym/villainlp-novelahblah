package com.example.villainlp.setting.blockManage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BlockManageViewModel: ViewModel() {
    private val blockManageModel = BlockManageModel()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser?.uid?: "ERROR"

    private val _blockList = MutableStateFlow<List<BlockedData>>(emptyList())
    val blockedList: StateFlow<List<BlockedData>> = _blockList

    fun loadBlockList(){
        viewModelScope.launch {
            _blockList.value = blockManageModel.getBlackedIDs(user)
        }
    }

    fun unblock(blockID: String){
        viewModelScope.launch {
            blockManageModel.deleteBlockID(user, blockID)
            loadBlockList()
        }
    }
}