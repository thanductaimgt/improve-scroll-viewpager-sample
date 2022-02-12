package mgt.android.performance.scroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var firstTimeLowLimit = true
    private var lastPosition = 0
    private var positionChanged = true
    private var improvementEnabled = false
    private lateinit var pagerAdapter: ItemPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            pagerAdapter = ItemPagerAdapter(this@MainActivity)
            adapter = pagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    val isScrolling = state != ViewPager2.SCROLL_STATE_IDLE
                    if (!isScrolling && positionChanged) {
                        viewPager.post {
                            checkSetHighPageLimit()
                        }
                        positionChanged = false
                    }
                }

                override fun onPageSelected(position: Int) {
                    if (lastPosition != position) {
                        lastPosition = position
                        positionChanged = true
                    }
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    if (position >= pagerAdapter.itemCount - 1) {
                        loadMoreItems()
                    }
                }
            })
            setHighPageLimit()
        }

        toggleButton.setOnClickListener {
            if (improvementEnabled) {
                improvementEnabled = false
                toggleButton.text = "Turn on improvement"
            } else {
                improvementEnabled = true
                toggleButton.text = "Turn off improvement"
            }
        }

        loadMoreItems()
    }

    private fun loadMoreItems() {
        val next = pagerAdapter.itemCount
        val newItems = arrayListOf<Int>()
        for (i in next until next + 10) {
            newItems.add(i)
        }
        pagerAdapter.items.addAll(newItems)
        pagerAdapter.notifyItemRangeInserted(next, newItems.size)
    }

    /**
     * Preloading next page(s) is essential to improving UI performance. However, it
     * usually happens when user start scrolling, which including inflating
     * the next page immediately and subsequently causing a very janky user experience.
     *
     * The idea is to trigger preloading when viewpager is in idle state. To achieve this,
     * we increase viewpager.offscreenPageLimit, which will trigger a preload,
     * then reset it when preload is already triggered.
     */

    fun checkSetHighPageLimit() {
        if (!improvementEnabled) return
        // tag format: "fN", N: 0,1,2,...
        val fragmentTags = supportFragmentManager.fragments.map { it.tag?.drop(1)?.toInt() ?: 0 }
        val lowerBound = fragmentTags.minOrNull() ?: 0
        val upperBound = fragmentTags.maxOrNull() ?: 0
        if (lastPosition + viewPager.offscreenPageLimit >= upperBound ||
            (lastPosition - viewPager.offscreenPageLimit in 1..lowerBound)
        ) {
            setPageLimit(2)
        }
    }

    private fun setHighPageLimit() {
        setPageLimit(2)
    }

    fun checkSetLowPageLimit() {
        if (!improvementEnabled) return

        if (firstTimeLowLimit) {
            firstTimeLowLimit = false
            return
        }
        setPageLimit(1)
    }

    private fun setPageLimit(limit: Int) {
        if (viewPager.offscreenPageLimit != limit) {
            viewPager.offscreenPageLimit = limit
        }
    }
}
