package com.shopvite.audioplayerkotlin.viewdmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import androidx.lifecycle.viewModelScope
import com.shopvite.audioplayerkotlin.model.SplashModel
import kotlinx.coroutines.launch

class SplashViewModel (application: Application) : AndroidViewModel(application) {

    var liveData: MutableLiveData<SplashModel> = MutableLiveData()

    fun initSplashScreen() {
        viewModelScope.launch {
            delay(2000)
            updateLiveData()
        }
    }

    private fun updateLiveData() {
        val splashModel = SplashModel()
        liveData.value = splashModel
    }
}