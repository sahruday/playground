package com.sahu.playground

import androidx.lifecycle.ViewModel
import com.sahu.playground.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo : Repository
) : ViewModel() {

}