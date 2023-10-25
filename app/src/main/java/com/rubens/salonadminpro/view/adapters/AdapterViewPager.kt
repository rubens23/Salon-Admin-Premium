package com.rubens.salonadminpro.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterViewPager(fa: FragmentActivity, private val contents: List<Fragment>): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = contents.size
    override fun createFragment(position: Int): Fragment {
        return contents[position]
    }


}