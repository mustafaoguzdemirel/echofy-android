package com.mod.moodsong

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.mod.moodsong.databinding.ActivityOnboardBinding

class OnboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animateFirstText()

        binding.nextBtn1.setOnClickListener {
            animateQuestion1ExitThenShowQuestion2()
        }

        binding.skipBtn1.setOnClickListener {
            animateQuestion1ExitThenShowQuestion2()
        }

        binding.nextBtn2.setOnClickListener {
            animateQuestion2ExitThenShowQuestion3()
        }

        binding.skipBtn2.setOnClickListener {
            animateQuestion2ExitThenShowQuestion3()
        }

        binding.nextBtn3.setOnClickListener {
            animateQuestion3ExitThenShowOverlay()
        }

        binding.skipBtn3.setOnClickListener {
            animateQuestion3ExitThenShowOverlay()
        }


        val optionList = listOf(
            "Pop",
            "Rock",
            "Hip-Hop",
            "Jazz",
            "Lo-fi",
            "Klasik",
            "Elektronik"
        )

        binding.optionsRV.adapter = OptionAdapter(optionList)
    }


    private fun animateFirstText() {
        binding.firstText.apply {
            translationY = 100f
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(1000)
                .withEndAction {
                    postDelayed({
                        animate()
                            .translationY(-100f)
                            .alpha(0f)
                            .setDuration(1000)
                            .withEndAction {
                                visibility = View.GONE
                                animateQuestionViews()
                            }.start()
                    }, 4000)
                }.start()
        }
    }

    private fun animateQuestionViews() {
        binding.linearProgressIndicator.setProgress(1, true)

        // Question Text - from top
        binding.q1TV.apply {
            translationY = -100f
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .start()
        }

        // EditText - from top (below q1TV)
        binding.editText.apply {
            translationY = -100f
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(100)
                .start()
        }

        // Buttons - from bottom
        binding.nextBtn1.apply {
            translationY = 100f
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(200)
                .start()
        }

        binding.skipBtn1.apply {
            translationY = 100f
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(300)
                .start()
        }
    }

    private fun animateQuestion1ExitThenShowQuestion2() {
        val duration = 500L

        // 1. Yukarı çıkanlar
        binding.q1TV.animate()
            .translationY(-100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                binding.q1TV.visibility = View.GONE
            }.start()

        binding.editText.animate()
            .translationY(-100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                binding.editText.visibility = View.GONE
            }.start()

        // 2. Aşağı inenler
        binding.nextBtn1.animate()
            .translationY(100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                binding.nextBtn1.visibility = View.GONE
            }.start()

        binding.skipBtn1.animate()
            .translationY(100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                binding.skipBtn1.visibility = View.GONE
                // Hepsi kaybolunca yeni soru gelsin
                animateQuestion2Entry()
            }.start()
    }

    private fun animateQuestion2Entry() {
        binding.linearProgressIndicator.setProgress(2, true)

        // Yeni soru text (q2TV)
        binding.q2TV.apply {
            translationY = -100f
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .start()
        }

        // Yeni buton (nextBtn2)
        binding.nextBtn2.apply {
            translationY = 100f
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(100)
                .start()
        }

        binding.skipBtn2.apply {
            translationY = 100f
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(100)
                .start()
        }

        // RecyclerView item'ları animasyonlu göstermek için LayoutAnimation
        binding.optionsRV.apply {
            visibility = View.VISIBLE // <-- bunu EKLE
            layoutAnimation =
                AnimationUtils.loadLayoutAnimation(this@OnboardActivity, R.anim.layout_fall_down)
            scheduleLayoutAnimation()
        }
    }

    private fun animateQuestion2ExitThenShowQuestion3() {
        val duration = 500L

        // Yukarı çıkan: Soru başlığı
        binding.q2TV.animate()
            .translationY(-100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction { binding.q2TV.visibility = View.GONE }
            .start()

        // Yukarı çıkan: RecyclerView
        binding.optionsRV.animate()
            .translationY(-100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                binding.optionsRV.visibility = View.GONE
            }.start()

        // Aşağı inen butonlar
        binding.nextBtn2.animate()
            .translationY(100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction { binding.nextBtn2.visibility = View.GONE }
            .start()

        binding.skipBtn2.animate()
            .translationY(100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                binding.skipBtn2.visibility = View.GONE
                animateQuestion3Entry() // hepsi bitince 3. soruyu getir
            }
            .start()
    }

    private fun animateQuestion3Entry() {
        binding.linearProgressIndicator.setProgress(3, true)

        // Soru başlığı (q3TV)
        binding.q3TV.apply {
            translationY = -100f
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .start()
        }

        // RadioGroup (onboardQ3RadioGroup)
        binding.onboardQ3RadioGroup.apply {
            translationY = -100f
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(100)
                .start()
        }

        // Alttaki butonlar
        binding.nextBtn3.apply {
            translationY = 100f
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(200)
                .start()
        }

        binding.skipBtn3.apply {
            translationY = 100f
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(300)
                .start()
        }
    }

    private fun animateFinalOverlay() {
        binding.linearProgressIndicator.setProgress(4, true)

        binding.linearProgressIndicator.postDelayed({
            binding.overlayView.visibility = View.VISIBLE

            val fullHeight = resources.displayMetrics.heightPixels

            val animator = ValueAnimator.ofInt(1, fullHeight)
            animator.duration = 1000
            animator.addUpdateListener { valueAnimator ->
                val newHeight = valueAnimator.animatedValue as Int
                binding.overlayView.layoutParams.height = newHeight
                binding.overlayView.requestLayout()
            }

            // 🟢 Kepenğin inmesi bitince yazıyı göster
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.thxText.apply {
                        alpha = 0f
                        visibility = View.VISIBLE
                        animate()
                            .alpha(1f)
                            .setDuration(800)
                            .start()
                    }
                }
            })

            animator.start()

        }, 700)
    }

    private fun animateQuestion3ExitThenShowOverlay() {
        val duration = 500L

        // Yukarı çıkanlar
        binding.q3TV.animate()
            .translationY(-100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction { binding.q3TV.visibility = View.GONE }
            .start()

        binding.onboardQ3RadioGroup.animate()
            .translationY(-100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction { binding.onboardQ3RadioGroup.visibility = View.GONE }
            .start()

        // Aşağı inenler
        binding.nextBtn3.animate()
            .translationY(100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction { binding.nextBtn3.visibility = View.GONE }
            .start()

        binding.skipBtn3.animate()
            .translationY(100f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                binding.skipBtn3.visibility = View.GONE
                animateFinalOverlay()
            }
            .start()
    }



}