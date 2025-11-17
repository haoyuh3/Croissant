package com.bytedance.crossiantapp.util

import java.util.Calendar

/**
 * Date formatting utility
 */
object DateUtil {
    /**
     */
    fun formatPublishDate(timestamp: Long): String {
        val now = System.currentTimeMillis() / 1000
        val diff = now - timestamp

        return when {
            diff < 24 * 3600 -> { // Within 24h
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = timestamp * 1000
                }
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                String.format("%02d:%02d", hour, minute)
            }
            diff < 48 * 3600 -> { // Yesterday
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = timestamp * 1000
                }
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                "昨天 ${String.format("%02d:%02d", hour, minute)}"
            }
            diff < 7 * 24 * 3600 -> { // Within 7 days
                val days = (diff / (24 * 3600)).toInt()
                "${days}天前"
            }
            else -> { // Others
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = timestamp * 1000
                }
                String.format("%02d-%02d",
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH))
            }
        }
    }
}