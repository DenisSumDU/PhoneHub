package com.example.phonehub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PhoneViewModel(private val repository: PhoneRepository) : ViewModel() {

    enum class SortOrder {
        DEFAULT, PRICE_ASC, PRICE_DESC
    }

    private val allPhonesLiveData = repository.phones.asLiveData()
    private val searchQuery = MutableLiveData("")
    private val _sortOrder = MutableLiveData(SortOrder.DEFAULT)

    // Список телефонів для порівняння (зберігає до 2 пристроїв)
    private val _comparedPhones = MutableLiveData<List<PhoneEntity>>(emptyList())
    val comparedPhones: LiveData<List<PhoneEntity>> get() = _comparedPhones

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    // Каталог із підтримкою пошуку та сортування за ціною
    val phones: LiveData<List<PhoneEntity>> = MediatorLiveData<List<PhoneEntity>>().apply {
        var currentQuery = ""
        var currentList = emptyList<PhoneEntity>()
        var currentSort = SortOrder.DEFAULT

        fun updateList() {
            var result = if (currentQuery.isBlank()) {
                currentList
            } else {
                currentList.filter {
                    it.brand.contains(currentQuery, ignoreCase = true) ||
                            it.model.contains(currentQuery, ignoreCase = true)
                }
            }

            result = when (currentSort) {
                SortOrder.PRICE_ASC -> result.sortedBy { it.price }
                SortOrder.PRICE_DESC -> result.sortedByDescending { it.price }
                SortOrder.DEFAULT -> result
            }

            value = result
        }

        addSource(allPhonesLiveData) { list ->
            currentList = list ?: emptyList()
            updateList()
        }

        addSource(searchQuery) { query ->
            currentQuery = query ?: ""
            updateList()
        }

        addSource(_sortOrder) { order ->
            currentSort = order
            updateList()
        }
    }


    val favoritePhones: LiveData<List<PhoneEntity>> = MediatorLiveData<List<PhoneEntity>>().apply {
        var currentQuery = ""
        var currentList = emptyList<PhoneEntity>()

        fun updateList() {
            val favorites = currentList.filter { it.isFavorite }
            value = if (currentQuery.isBlank()) {
                favorites
            } else {
                favorites.filter {
                    it.brand.contains(currentQuery, ignoreCase = true) ||
                            it.model.contains(currentQuery, ignoreCase = true)
                }
            }
        }

        addSource(allPhonesLiveData) { list ->
            currentList = list ?: emptyList()
            updateList()
        }

        addSource(searchQuery) { query ->
            currentQuery = query ?: ""
            updateList()
        }
    }

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshPhones()
            _isRefreshing.value = false
        }
    }

    fun toggleFavorite(phone: PhoneEntity) {
        viewModelScope.launch {
            val updated = phone.copy(isFavorite = !phone.isFavorite)
            repository.update(updated)
        }
    }

    fun toggleFavorite(phoneId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(phoneId, currentStatus)
        }
    }

    // Методи для порівняння телефонів
    fun toggleCompare(phone: PhoneEntity) {
        val currentList = _comparedPhones.value.orEmpty().toMutableList()
        if (currentList.any { it.id == phone.id }) {
            currentList.removeAll { it.id == phone.id }
        } else {
            if (currentList.size >= 2) {
                currentList.removeAt(0) // Залишаємо максимум 2 пристрої для порівняння
            }
            currentList.add(phone)
        }
        _comparedPhones.value = currentList
    }
}

class PhoneViewModelFactory(private val repository: PhoneRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhoneViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhoneViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}