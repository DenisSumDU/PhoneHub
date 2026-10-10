@file:Suppress("unused")

package com.example.phonehub

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PhoneRepository(
    private val phoneDao: PhoneDao?,
    private val apiService: PhoneApiService?
) {
    private val initialPhones = listOf(
        PhoneEntity("1", "Samsung", "Galaxy A05", 5500.0, 4, 64, 5000, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500", false),
        PhoneEntity("2", "Xiaomi", "Redmi A2", 3200.0, 3, 32, 5000, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("3", "Motorola", "Moto G14", 4800.0, 4, 128, 5000, "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=500", false),
        PhoneEntity("4", "Nokia", "G22", 6100.0, 4, 64, 5050, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", false),
        PhoneEntity("5", "Tecno", "Spark Go 2024", 3900.0, 4, 64, 5000, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", false),
        PhoneEntity("6", "Infinix", "Hot 40i", 5200.0, 8, 128, 5000, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", false),
        PhoneEntity("7", "Poco", "C65", 4600.0, 6, 128, 5000, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("8", "Samsung", "Galaxy A54 5G", 16000.0, 8, 128, 5000, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500", false),
        PhoneEntity("9", "Xiaomi", "Redmi Note 13 Pro", 14500.0, 8, 256, 5100, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", true),
        PhoneEntity("10", "Google", "Pixel 7a", 19500.0, 8, 128, 4385, "https://images.unsplash.com/photo-1591337676887-a217a6970a8a?w=500", false),
        PhoneEntity("11", "Motorola", "Edge 40 Neo", 13800.0, 12, 256, 5000, "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=500", false),
        PhoneEntity("12", "Realme", "11 Pro", 15200.0, 8, 256, 5000, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", false),
        PhoneEntity("13", "Honor", "90 Lite", 11900.0, 8, 256, 4500, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", false),
        PhoneEntity("14", "OnePlus", "Nord CE 3 Lite", 12500.0, 8, 128, 5000, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", false),
        PhoneEntity("15", "Samsung", "Galaxy A34", 13200.0, 6, 128, 5000, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500", false),
        PhoneEntity("16", "Xiaomi", "Poco X6 Pro", 16800.0, 12, 512, 5000, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("17", "Nothing", "Phone (2a)", 14900.0, 8, 128, 5000, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", false),
        PhoneEntity("18", "Samsung", "Galaxy S23 FE", 27000.0, 8, 128, 4500, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500", false),
        PhoneEntity("19", "Google", "Pixel 8", 31500.0, 8, 128, 4575, "https://images.unsplash.com/photo-1591337676887-a217a6970a8a?w=500", true),
        PhoneEntity("20", "Apple", "iPhone 13", 29000.0, 4, 128, 3227, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", false),
        PhoneEntity("21", "Xiaomi", "13T", 26500.0, 12, 256, 5000, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("22", "OnePlus", "12R", 34000.0, 16, 256, 5500, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", false),
        PhoneEntity("23", "Asus", "Zenfone 10", 36000.0, 8, 256, 4300, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", false),
        PhoneEntity("24", "Samsung", "Galaxy S22 Ultra", 39000.0, 12, 256, 5000, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500", false),
        PhoneEntity("25", "Motorola", "Edge 50 Pro", 33300.0, 12, 512, 4500, "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=500", false),
        PhoneEntity("26", "Apple", "iPhone 15 Pro Max", 59000.0, 8, 256, 4422, "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=500", true),
        PhoneEntity("27", "Samsung", "Galaxy S24 Ultra", 56000.0, 12, 256, 5000, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500", true),
        PhoneEntity("28", "Google", "Pixel 8 Pro", 43000.0, 12, 256, 5050, "https://images.unsplash.com/photo-1591337676887-a217a6970a8a?w=500", true),
        PhoneEntity("29", "Xiaomi", "14 Ultra", 54000.0, 16, 512, 5000, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("30", "Sony", "Xperia 1 V", 49500.0, 12, 256, 5000, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", false),
        PhoneEntity("31", "Asus", "ROG Phone 8 Pro", 51000.0, 16, 512, 5500, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", false),
        PhoneEntity("32", "OnePlus", "12", 45000.0, 16, 512, 5400, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", false),
        PhoneEntity("33", "Apple", "iPhone 15 Plus", 46000.0, 6, 256, 4383, "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=500", false),
        PhoneEntity("34", "Samsung", "Galaxy Z Fold 5", 68000.0, 12, 512, 4400, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500", false),
        PhoneEntity("35", "Honor", "Magic 6 Pro", 47000.0, 12, 512, 5600, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", false),
        PhoneEntity("36", "Xiaomi", "Redmi Note 13", 8900.0, 6, 128, 5000, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("37", "Samsung", "Galaxy A25", 9800.0, 6, 128, 5000, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500", false),
        PhoneEntity("38", "Poco", "X6", 12500.0, 8, 256, 5100, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("39", "Motorola", "Moto G24", 4500.0, 4, 128, 5000, "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=500", false),
        PhoneEntity("40", "Realme", "C67", 6700.0, 8, 256, 5000, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", false),
        PhoneEntity("41", "Oppo", "Reno 11 Pro", 19900.0, 12, 512, 4600, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", false),
        PhoneEntity("42", "Vivo", "V30", 18500.0, 12, 256, 5000, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", false),
        PhoneEntity("43", "Huawei", "Pura 70 Ultra", 51000.0, 16, 512, 5200, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", true),
        PhoneEntity("44", "ZTE", "Nubia Z60 Ultra", 31000.0, 12, 256, 6000, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", false),
        PhoneEntity("45", "Infinix", "Note 40 Pro", 9900.0, 8, 256, 5000, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("46", "Tecno", "Camon 30 Pro", 11500.0, 12, 256, 5000, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", false),
        PhoneEntity("47", "Apple", "iPhone 14", 32000.0, 6, 128, 3279, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", false),
        PhoneEntity("48", "Samsung", "Galaxy S24+", 43000.0, 12, 256, 4900, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500", true),
        PhoneEntity("49", "Xiaomi", "14", 39900.0, 12, 256, 4610, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("50", "Google", "Pixel 7", 23000.0, 8, 128, 4355, "https://images.unsplash.com/photo-1591337676887-a217a6970a8a?w=500", false),
        PhoneEntity("51", "OnePlus", "Open", 72000.0, 16, 512, 4805, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", true),
        PhoneEntity("52", "Honor", "Magic V2", 65000.0, 16, 512, 5000, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", false),
        PhoneEntity("53", "Motorola", "Razr 40 Ultra", 35000.0, 8, 256, 3800, "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=500", false),
        PhoneEntity("54", "Asus", "ZenFone 9", 25000.0, 8, 128, 4300, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", false),
        PhoneEntity("55", "Sony", "Xperia 10 V", 14000.0, 6, 128, 5000, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", false),
        PhoneEntity("56", "Realme", "GT 5 Pro", 27500.0, 16, 512, 5400, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", false),
        PhoneEntity("57", "Oppo", "Find X7 Ultra", 48000.0, 16, 512, 5000, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", true),
        PhoneEntity("58", "Vivo", "X100 Pro", 42000.0, 16, 512, 5400, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", true),
        PhoneEntity("59", "Xiaomi", "Redmi A3", 3500.0, 3, 64, 5000, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("60", "Samsung", "Galaxy A15", 6800.0, 4, 128, 5000, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500", false),
        PhoneEntity("61", "Poco", "M6 Pro", 7900.0, 8, 256, 5000, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500", false),
        PhoneEntity("62", "Infinix", "Smart 8", 3100.0, 3, 64, 5000, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500", false),
        PhoneEntity("63", "Tecno", "Pop 8", 3300.0, 3, 64, 5000, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", false),
        PhoneEntity("64", "Motorola", "Moto G54", 7500.0, 8, 256, 6000, "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=500", false),
        PhoneEntity("65", "Nokia", "X30 5G", 15500.0, 6, 128, 4200, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500", false)
    )

    private val _phonesFlow = MutableStateFlow(initialPhones)
    val phones: Flow<List<PhoneEntity>> = _phonesFlow.asStateFlow()

    suspend fun update(phone: PhoneEntity) {
        _phonesFlow.update { currentList ->
            currentList.map { if (it.id == phone.id) phone else it }
        }
    }

    suspend fun updatePhone(phone: PhoneEntity) {
        update(phone)
    }

    fun insertInitialDataIfNeeded() {}
    fun refreshPhones() {}

    fun toggleFavorite(phoneId: String, currentFav: Boolean) {
        _phonesFlow.update { currentList ->
            currentList.map { if (it.id == phoneId) it.copy(isFavorite = !currentFav) else it }
        }
    }
}