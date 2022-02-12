package mgt.android.performance.scroll

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ItemPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    val items = arrayListOf<Int>()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return ItemFragment.newInstance(position)
    }
}
