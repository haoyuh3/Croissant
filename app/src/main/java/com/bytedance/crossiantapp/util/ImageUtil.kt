package com.bytedance.crossiantapp.util

import kotlin.math.max
import kotlin.math.min

/**
 * Image processing utility
 */
object ImageUtil {
    /**
     * Calculate aspect ratio for post card cover image
     * Constrained between 3:4 and 4:3
     */
    fun calculateAspectRatio(width: Int, height: Int): Float {
        if (width <= 0 || height <= 0) return 1f
        val ratio = width.toFloat() / height.toFloat()
        return ratio.coerceIn(0.75f, 1.33f) // 3:4 ~ 4:3
    }

    /**
     * Calculate aspect ratio for detail page first image
     * Constrained between 3:4 and 16:9
     */
    fun calculateFirstImageRatio(width: Int, height: Int): Float {
        if (width <= 0 || height <= 0) return 1f
        val ratio = width.toFloat() / height.toFloat()
        return ratio.coerceIn(0.75f, 1.78f) // 3:4 ~ 16:9
    }

    /**
     * Calculate image dimensions to fit within max bounds while maintaining aspect ratio
     */
    fun calculateFitDimensions(
        imageWidth: Int,
        imageHeight: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Pair<Int, Int> {
        if (imageWidth <= 0 || imageHeight <= 0) return Pair(maxWidth, maxHeight)

        val aspectRatio = imageWidth.toFloat() / imageHeight.toFloat()
        val maxAspectRatio = maxWidth.toFloat() / maxHeight.toFloat()

        return if (aspectRatio > maxAspectRatio) {
            // Width is limiting factor
            Pair(maxWidth, (maxWidth / aspectRatio).toInt())
        } else {
            // Height is limiting factor
            Pair((maxHeight * aspectRatio).toInt(), maxHeight)
        }
    }
}