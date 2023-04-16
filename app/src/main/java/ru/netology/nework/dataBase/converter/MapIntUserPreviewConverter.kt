package ru.netology.nework.dataBase.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nework.dto.UserPreview

class MapIntUserPreviewConverter {
    @TypeConverter
    fun fromMapIntUserPreview(map : Map<Int, UserPreview>) : String {
        if (map.isEmpty()) {
            return ""
        }
        val gson = Gson()
        val type = object : TypeToken<Map<Int, UserPreview>>() {}.type
        return gson.toJson(map, type)
    }

    @TypeConverter
    fun toMapIntUserPreview(map : String) : Map<Int, UserPreview> {
        if (map.isEmpty()) {
            return emptyMap()
        }
        val gson = Gson()
        val type = object : TypeToken<Map<Int, UserPreview>>() {}.type
        return gson.fromJson(map, type)
    }

}