package com.mod.moodsong

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class NoteAnimationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val noteBitmap: Bitmap by lazy {
        val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_note)
            ?: throw IllegalArgumentException("ic_note drawable not found")

        val width = 64
        val height = 64
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        bitmap
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val random = Random(System.currentTimeMillis())

    private val upwardNotes = mutableListOf<Note>()
    private val downwardNotes = mutableListOf<Note>()
    private val noteCount = 20

    private val wavePaint = Paint().apply {
        color = Color.parseColor("#26263A") // background açığı
        style = Paint.Style.FILL
    }
    private val wavePath = Path()
    private var waveOffset = 0f

    private var topWaveOffsetY = 0f
    private var bottomWaveOffsetY = 0f

    init {
        generateNotes()

        // Ana animasyon loop'u
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 16L
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                updateNotes()
                waveOffset += 4f
                invalidate()
            }
            start()
        }

        // 5 saniye sonra dalgaları kaydır
        postDelayed({
            animateWaveExit()
        }, 5000)
    }

    private fun animateWaveExit() {
        ValueAnimator.ofFloat(0f, -height.toFloat()).apply {
            duration = 1000
            addUpdateListener {
                topWaveOffsetY = it.animatedValue as Float
                invalidate()
            }
            start()
        }

        ValueAnimator.ofFloat(0f, height.toFloat()).apply {
            duration = 1000
            addUpdateListener {
                bottomWaveOffsetY = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun generateNotes() {
        repeat(noteCount) {
            upwardNotes.add(
                Note(
                    x = random.nextFloat() * width,
                    y = height + random.nextFloat() * (height / 2),
                    scale = random.nextFloat() * 0.6f + 0.4f,
                    speed = random.nextFloat() * 2f + 1f,
                    alpha = random.nextInt(100, 200)
                )
            )
            downwardNotes.add(
                Note(
                    x = random.nextFloat() * width,
                    y = -random.nextFloat() * (height / 2),
                    scale = random.nextFloat() * 0.6f + 0.4f,
                    speed = random.nextFloat() * 2f + 1f,
                    alpha = random.nextInt(100, 200)
                )
            )
        }
    }

    private fun updateNotes() {
        for (note in upwardNotes) {
            note.y -= note.speed
            note.alpha = (note.alpha * 0.985).toInt()

            if (note.y < -64 * note.scale || note.alpha <= 10) {
                note.x = random.nextFloat() * width
                note.y = height + random.nextFloat() * (height / 2)
                note.scale = random.nextFloat() * 0.6f + 0.4f
                note.speed = random.nextFloat() * 2f + 1f
                note.alpha = random.nextInt(100, 200)
            }
        }

        for (note in downwardNotes) {
            note.y += note.speed
            note.alpha = (note.alpha * 0.985).toInt()

            if (note.y > height + 64 * note.scale || note.alpha <= 10) {
                note.x = random.nextFloat() * width
                note.y = -random.nextFloat() * (height / 2)
                note.scale = random.nextFloat() * 0.6f + 0.4f
                note.speed = random.nextFloat() * 2f + 1f
                note.alpha = random.nextInt(100, 200)
            }
        }
    }

    private fun drawBottomWave(canvas: Canvas) {
        wavePath.reset()
        val amplitude = 30f
        val frequency = 1.5f
        val heightMid = height * 0.85f + bottomWaveOffsetY

        wavePath.moveTo(0f, height.toFloat())
        for (x in 0..width step 10) {
            val y = (amplitude * sin((x + waveOffset) * frequency * PI / 180)).toFloat()
            wavePath.lineTo(x.toFloat(), heightMid + y)
        }
        wavePath.lineTo(width.toFloat(), height.toFloat())
        wavePath.close()

        canvas.drawPath(wavePath, wavePaint)
    }

    private fun drawTopWave(canvas: Canvas) {
        wavePath.reset()
        val amplitude = 30f
        val frequency = 1.5f
        val heightMid = height * 0.15f + topWaveOffsetY

        wavePath.moveTo(0f, 0f)
        for (x in 0..width step 10) {
            val y = (amplitude * sin((x + waveOffset) * frequency * PI / 180)).toFloat()
            wavePath.lineTo(x.toFloat(), heightMid + y)
        }
        wavePath.lineTo(width.toFloat(), 0f)
        wavePath.close()

        canvas.drawPath(wavePath, wavePaint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawTopWave(canvas)
        drawBottomWave(canvas)

        val baseSize = 64f

        val allNotes = upwardNotes + downwardNotes
        for (note in allNotes) {
            paint.alpha = note.alpha
            val size = baseSize * note.scale
            val left = note.x - size / 2
            val top = note.y - size / 2
            val rect = RectF(left, top, left + size, top + size)
            canvas.drawBitmap(noteBitmap, null, rect, paint)
        }
    }

    data class Note(
        var x: Float,
        var y: Float,
        var scale: Float,
        var speed: Float,
        var alpha: Int
    )
}
