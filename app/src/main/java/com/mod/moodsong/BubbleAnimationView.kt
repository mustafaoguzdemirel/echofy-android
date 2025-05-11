package com.mod.moodsong

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class BubbleAnimationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.WHITE
        alpha = 50
        isAntiAlias = true
    }

    private val bubbles = mutableListOf<Bubble>()

    init {
        // Rastgele birkaç bubble oluştur
        repeat(20) {
            bubbles.add(
                Bubble(
                    x = Random.nextFloat() * 1000,
                    y = Random.nextFloat() * 2000,
                    radius = Random.nextFloat() * 20 + 10,
                    speed = Random.nextFloat() * 2 + 1
                )
            )
        }

        // Frame updater
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 16L
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                bubbles.forEach { it.y -= it.speed }
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bubbles.forEach {
            if (it.y + it.radius < 0) {
                it.y = height + it.radius
            }
            canvas.drawCircle(it.x, it.y, it.radius, paint)
        }
    }

    data class Bubble(var x: Float, var y: Float, val radius: Float, val speed: Float)
}
