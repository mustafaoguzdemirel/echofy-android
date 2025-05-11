package com.mod.moodsong

import android.animation.*
import android.content.Context
import android.os.*
import android.view.*
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.mod.moodsong.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: SongViewModel by viewModels()

    // --- değişiklik ---
    private var animationsAllowed = true             // sonsuz döngü kontrolü
    private val allFakeCards = mutableListOf<View>() // kart referansları
    // -------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            startFakeCoverAnimation(binding.fakeContainer)
        }

        // fancy intro
        Handler(Looper.getMainLooper()).postDelayed({
            animateEntrance(binding)
        }, 6000)

        binding.editText.addTextChangedListener {
            spawnNote(binding.editContainer)
        }
    }

    /* -------------------------------------------------- *
     *                    1. Loader                       *
     * -------------------------------------------------- */

    fun startFakeCoverAnimation(container: ViewGroup) {
        scatterFakeCards(container)

        // 15 sn sonra fake’leri toparla
        container.postDelayed({
            showSelectedCardsCentered(container)
        }, 5_000)
    }

    private fun scatterFakeCards(container: ViewGroup) {
        repeat(50) {
            val card = createFakeSongCard(container.context, container)
            container.addView(card)
            allFakeCards.add(card)

            card.animate()
                .translationXBy((-400..400).random().toFloat())
                .translationYBy((-800..800).random().toFloat())
                .rotation((-30..30).random().toFloat())
                .alpha(0.9f)
                .setDuration((500..800).random().toLong())
                .start()

            startEndlessFloating(card)
        }
    }

    // --- değişiklik ---
    private fun startEndlessFloating(view: View) {
        if (!animationsAllowed) return                // bayrak kapalıysa çık

        val parent = view.parent as? ViewGroup ?: return
        val set = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, View.TRANSLATION_X,
                    (-600..parent.width + 600).random().toFloat()),
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Y,
                    (-1000..parent.height + 1000).random().toFloat()),
                ObjectAnimator.ofFloat(view, View.ROTATION, (-25..25).random().toFloat())
            )
            duration = (800..1400).random().toLong()
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    startEndlessFloating(view)       // sonsuz döngü
                }
            })
        }
        view.setTag(R.id.tag_animator, set)          // referansı tut
        set.start()
    }
    // -------------------

    // --- değişiklik ---
    private fun stopAllFloating() {
        animationsAllowed = false                    // yeni döngüleri engelle

        allFakeCards.forEach { card ->
            (card.getTag(R.id.tag_animator) as? Animator)?.cancel()
            card.animate().setListener(null).cancel()
        }
    }
    // -------------------

    private fun showSelectedCardsCentered(container: ViewGroup) {
        stopAllFloating()                       // sonsuz animasyonları durdur

        val selected = allFakeCards.shuffled().take(4)

        // ‑‑ kart ölçüleri (scale dâhil) + aralık ‑‑
        val scale        = 1.1f                 // istediğin büyütme
        val gapPx        = 24 * resources.displayMetrics.density   // 24 dp boşluk
        val cardW        = selected.first().width  * scale + gapPx
        val cardH        = selected.first().height * scale + gapPx

        val centerX = container.width  / 2f
        val centerY = container.height / 2f

        val positions = listOf(
            Pair(centerX - cardW / 2, centerY - cardH / 2), // sol‑üst
            Pair(centerX + cardW / 2, centerY - cardH / 2), // sağ‑üst
            Pair(centerX - cardW / 2, centerY + cardH / 2), // sol‑alt
            Pair(centerX + cardW / 2, centerY + cardH / 2)  // sağ‑alt
        )

        selected.forEachIndexed { i, card ->
            card.animate()
                .translationX(positions[i].first  - card.width  / 2)
                .translationY(positions[i].second - card.height / 2)
                .rotation(0f)
                .scaleX(scale).scaleY(scale)
                .alpha(1f)
                .setDuration(800)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        // seçilmeyenleri dışarı fırlat
        scatterOutAndRemove(allFakeCards.filterNot { it in selected }, container)
    }



    private fun scatterOutAndRemove(cards: List<View>, parent: ViewGroup) {
        if (cards.isEmpty()) return

        val w = parent.width
        val h = parent.height

        cards.forEach { card ->
            // Rastgele kenar seç: 0‑sol,1‑üst,2‑sağ,3‑alt
            when ((0..3).random()) {
                0 -> {                           // sola uç
                    card.animate()
                        .translationX(-w.toFloat() - 200)
                        .alpha(0f)
                }
                1 -> {                           // yukarı uç
                    card.animate()
                        .translationY(-h.toFloat() - 200)
                        .alpha(0f)
                }
                2 -> {                           // sağa uç
                    card.animate()
                        .translationX(w.toFloat() + 200)
                        .alpha(0f)
                }
                else -> {                        // aşağı uç
                    card.animate()
                        .translationY(h.toFloat() + 200)
                        .alpha(0f)
                }
            }.apply {
                duration = (400..700).random().toLong()
                interpolator = AccelerateDecelerateInterpolator()
                withEndAction {
                    parent.removeView(card)      // bellek kaçaklarını önle
                    allFakeCards.remove(card)
                }
                start()
            }
        }
    }

    /* -------------------------------------------------- *
     *          2. API cevabı → gerçek kartlar            *
     * -------------------------------------------------- */

//    fun onApiResponseReceived(container: ViewGroup) {
//        stopAllFloating()                            // --- değişiklik ---
//
//        // Önce fake kartları sil
//        container.children.forEach { fake ->
//            fake.animate()
//                .alpha(0f).scaleX(0.8f).scaleY(0.8f)
//                .setDuration(500)
//                .withEndAction { container.removeView(fake) }
//                .start()
//        }
//
//        // Sonra gerçek önerileri göster
//        showRecommendationCards(binding)
//    }

    /* -------------------------------------------------- *
     *            3. Geri kalan kod aynen                 *
     * -------------------------------------------------- */

    private fun createFakeSongCard(context: Context, parent: ViewGroup): View {
        val v = layoutInflater.inflate(R.layout.child_item_song, parent, false)
        v.translationX = parent.width / 2f
        v.translationY = parent.height / 2f
        applyFloatingEffect(v)
        return v
    }

    private fun applyFloatingEffect(view: View) {
        // ilk “nefes alma” efekti (sonsuz değil)
        val pulse = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.05f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.05f, 1f)
        ).apply {
            duration = 4000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }
        pulse.start()
    }

    private fun spawnNote(container: FrameLayout) {
        val note = ImageView(container.context).apply {
            setImageResource(R.drawable.ic_note)
            layoutParams = FrameLayout.LayoutParams(32, 32).apply {
                leftMargin = (20..container.width - 60).random()
                topMargin = container.height - 40
            }
        }
        container.addView(note)

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(note, View.TRANSLATION_Y, 0f, -150f),
                ObjectAnimator.ofFloat(note, View.ALPHA, 1f, 0f)
            )
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    container.removeView(note)
                }
            })
            start()
        }
    }

    /* giriş – underline animasyonu bölümü aynen kalabilir */

    // --------------------------------------------------
//  Giriş animasyonu + underline çizgisi
// --------------------------------------------------
    private fun animateEntrance(binding: ActivityMainBinding) {
        // Title yukarı kaydır
        val targetY = -binding.titleTV.y + 100f
        binding.titleTV.animate()
            .translationY(targetY)
            .setDuration(800)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // EditText + underline container’ı içeren LinearLayout
        binding.inputLL.apply {
            alpha = 0f
            translationY = 40f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(300)
                .setDuration(800)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction { animateUnderline(binding) }   // underline tamamlayıcı
                .start()
        }

        // “Get a song” butonu
        binding.button.apply {
            alpha = 0f
            translationY = 40f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(300)
                .setDuration(800)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    private fun animateUnderline(binding: ActivityMainBinding) {
        binding.underlineView.alpha = 1f
        ValueAnimator.ofInt(0, binding.editText.width).apply {
            duration = 800
            addUpdateListener { anim ->
                val params = binding.underlineView.layoutParams
                params.width = anim.animatedValue as Int
                binding.underlineView.layoutParams = params
            }
            start()
        }
    }

}
