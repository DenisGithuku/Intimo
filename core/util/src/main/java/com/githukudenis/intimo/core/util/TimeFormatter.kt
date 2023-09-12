package com.githukudenis.intimo.core.util

class TimeFormatter {
    companion object {

        fun getTimeFromMillis(timeInMillis: Long): String {
            return if (timeInMillis / 1000 / 60 / 60 >= 1) {
                "${timeInMillis / 1000 / 60 / 60}hr ${timeInMillis / 1000 / 60 % 60}min"
            } else if (timeInMillis / 1000 / 60 >= 1) {
                "${timeInMillis / 1000 / 60}min"
            } else if (timeInMillis / 1000 >= 1) {
                "Less than a minute"
            } else {
                "0 min"
            }
        }

        fun getHoursAndMinutes(timeInMillis: Long): String {
            return if (timeInMillis / 1000 / 60 / 60 > 0 && timeInMillis / 1000 / 60 % 60 > 0) {
                "${timeInMillis / 1000 / 60 / 60}h ${timeInMillis / 1000 / 60 % 60}m"
            } else if (timeInMillis / 1000 / 60 / 60 > 0 && timeInMillis / 1000 / 60 % 60 <= 0) {
                "${timeInMillis / 1000 / 60 / 60}h"
            } else {
                "${timeInMillis / 1000 / 60}m"
            }
        }
    }
}