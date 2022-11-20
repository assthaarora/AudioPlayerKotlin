package com.shopvite.audioplayerkotlin.viewdmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopvite.audioplayerkotlin.model.AudioModel

class AudioViewModel : ViewModel() {
    var lst = MutableLiveData<ArrayList<AudioModel>>()
    var newlist = arrayListOf<AudioModel>()

    fun add(blog: AudioModel){
        newlist.add(blog)
        lst.value=newlist
    }

    fun remove(blog: AudioModel){
        newlist.remove(blog)
        lst.value=newlist
    }

}