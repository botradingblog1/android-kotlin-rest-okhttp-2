package com.example.kotlinrestokhttp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
}