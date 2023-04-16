package ru.netology.nework.utils

import java.time.Month
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor

object AppUtils {
    private const val LOCAL_DATETIME_FORMAT = "YYYY-MM-01T00:00:43.690Z"
    private const val MONTH_DIFFERENCE_CHAR = 5
    private const val DAY_DIFFERENCE_CHAR = 8
    private const val HOUR_DIFFERENCE_CHAR = 11
    private const val MINUTE_DIFFERENCE_CHAR = 14

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun largeNumberDisplay(number: Int): String {
        val firstBorder = 1_000
        val secondBorder = 10_000
        val thirdBorder = 1_000_000
        val numberOfSings = 10.0
        val output = when {
            (number >= firstBorder) and (number < secondBorder) ->
                "${
                    String.format(
                        "%.1f",
                        floor((number / firstBorder.toDouble()) * numberOfSings) /
                                numberOfSings
                    )
                }K"
            (number >= secondBorder) and (number < thirdBorder) ->
                "${number / firstBorder}K"
            number >= thirdBorder -> "${
                String.format(
                    "%.1f",
                    floor((number / thirdBorder.toDouble()) * numberOfSings) /
                            numberOfSings
                )
            }M"
            else -> number.toString()
        }
        return output
    }

    fun getMonth(dateTime: String): String {
        return dateTime.subSequence(5, 7).toString()

    }

    fun getYear(dateTime: String): String {
        return dateTime.subSequence(0, 4).toString()
    }

    fun compareDateTime(firstDateTime: String, secondDateTime : String) : Int {
        val parseFirst = OffsetDateTime.parse(firstDateTime)
        val parseSecond = OffsetDateTime.parse(secondDateTime)
        return parseFirst.compareTo(parseSecond)
    }

    fun setMonthAndYear(month: String, year: String, dateTime: String?) : String? {
        val parseDateTime = if (dateTime== null || dateTime.isEmpty()) LOCAL_DATETIME_FORMAT else dateTime
        val chars = parseDateTime.toCharArray()
        for (i in 0..3) {
            try {
                if (i <= 1) {
                    chars[i + MONTH_DIFFERENCE_CHAR] = month[i]
                }
                chars[i] = year[i]
            } catch (e : StringIndexOutOfBoundsException) {
                return null
            }
        }
        return String(chars)
    }

    fun setDate(date : String, dateTime : String) : String {
        val parseDateTime = dateTime.ifEmpty { LOCAL_DATETIME_FORMAT }
        val chars = parseDateTime.toCharArray()
        val dateParse = OffsetDateTime.parse(date)
        val year = dateParse.year.toString()
        var month = dateParse.monthValue.toString()
        if (month.length == 1) {
            month = "0$month"
        }
        var day = dateParse.dayOfMonth.toString()
        if (day.length == 1) {
            day = "0$day"
        }

        for (i in 0..3) {
            try {
                if (i <= 1) {
                    chars[i + MONTH_DIFFERENCE_CHAR] = month[i]
                    chars[i + DAY_DIFFERENCE_CHAR] = day[i]
                }
                chars[i] = year[i]
            } catch (e: StringIndexOutOfBoundsException) {
                return ""
            }
        }
        return String(chars)
    }

    fun setTime(hour : String, minute : String, dateTime: String) : String {
        val parseDateTime = dateTime.ifEmpty { LOCAL_DATETIME_FORMAT }
        val parseHour = if (hour.length == 1) "0$hour" else hour
        val parseMinute = if (minute.length == 1) "0$minute" else minute
        val chars = parseDateTime.toCharArray()
        for (i in 0..1) {
            try {
                chars[i + HOUR_DIFFERENCE_CHAR] = parseHour[i]
                chars[i + MINUTE_DIFFERENCE_CHAR] = parseMinute[i]
            } catch (e: StringIndexOutOfBoundsException) {
                return ""
            }
        }
        return String(chars)
    }

    fun getDate(dateTime : String) : String {
        if (dateTime.isEmpty()) {
            return ""
        }
        val parseDateTime = OffsetDateTime.parse(dateTime)
        return parseDateTime.format(dateFormatter)
    }

    fun getTime(dateTime: String) : String {
        if (dateTime.isEmpty()) {
            return ""
        }
        val parseDateTime = OffsetDateTime.parse(dateTime)
        return parseDateTime.format(timeFormatter)
    }

    fun parseDateTime(dateTime: String): String {
        val parseDateTime = OffsetDateTime.parse(dateTime).toLocalDateTime()
        val year = parseDateTime.year
        var month = parseDateTime.month.toString()
        val day = parseDateTime.dayOfMonth
        var hour = parseDateTime.toLocalTime().hour.toString()
        var minute = parseDateTime.minute.toString()
        month = when(month) {
            Month.JANUARY.toString() -> "January"
            Month.FEBRUARY.toString() -> "February"
            Month.MARCH.toString() -> "March"
            Month.APRIL.toString() -> "April"
            Month.MAY.toString() -> "May"
            Month.JUNE.toString() -> "June"
            Month.JULY.toString() -> "July"
            Month.AUGUST.toString() -> "August"
            Month.SEPTEMBER.toString() -> "September"
            Month.OCTOBER.toString() -> "October"
            Month.NOVEMBER.toString() -> "November"
            Month.DECEMBER.toString() -> "December"
            else -> month
        }
        hour = if (hour.length == 1) "0$hour" else hour
        minute = if (minute.length == 1) "0$minute" else minute
        return "$day $month $year, $hour:$minute"
    }

    fun parseDateTimeForJobs(dateTime: String) : String {
        val year = dateTime.subSequence(0, 4).toString()
        var month = dateTime.subSequence(5, 7).toString()
        month = when (month) {
            "01" -> "January"
            "02" -> "February"
            "03" -> "March"
            "04" -> "April"
            "05" -> "May"
            "06" -> "June"
            "07" -> "July"
            "08" -> "August"
            "09" -> "September"
            "10" -> "October"
            "11" -> "November"
            "12" -> "December"
            else -> return ""
        }
        return "$month $year"
    }
}