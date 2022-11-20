package com.shopvite.audioplayerkotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shopvite.audioplayerkotlin.viewdmodel.AudioViewModel

class AudioViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AudioViewModel::class.java)){
            return AudioViewModel() as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }

}