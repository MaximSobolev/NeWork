package ru.netology.nework.dataBase.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nework.dto.EventType

class EventTypeConverter {
    @TypeConverter
    fun fromEventType(eventType : EventType): String {
        val gson = Gson()
        val type = object : TypeToken<EventType>() {}.type
        return gson.toJson(eventType, type)
    }

    @TypeConverter
    fun toIntList(eventType : String) : EventType {
        val gson = Gson()
        val type = object : TypeToken<EventType>() {}.type
        return gson.fromJson(eventType, type)
    }
}