package ru.netology.nework.dataBase.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListIntConverter {
    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.toJson(list, type)
    }

    @TypeConverter
    fun toIntList(list : String) : List<Int> {
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(list, type)
    }

}