package com.mhmdjalal.newsapp.ui.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmdjalal.newsapp.data.models.Category
import com.mhmdjalal.newsapp.repositories.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @author Created by Muhamad Jalaludin on 22/10/2022
 */
class CategoryViewModel(private val repository: MainRepository): ViewModel() {

    private var _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    fun getCategories() {
        viewModelScope.launch {
            repository.getCategories {
                _categories.value = it
            }
        }
    }

}