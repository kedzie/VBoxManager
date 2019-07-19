package com.kedzie.vbox.machine.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.kedzie.vbox.R
import kotlinx.android.synthetic.main.pager_tabs.*

class SystemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pager_tabs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager.adapter = object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount() = 3

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> SystemMotherboardFragment(arguments!!)
                    1 -> SystemProcessorsFragment(arguments!!)
                    else -> SystemAccelerationFragment(arguments!!)
                }
            }

            override fun getPageTitle(position: Int): CharSequence {
                return when (position) {
                    0 -> "Motherboard"
                    1 -> "Processors"
                    else -> "Acceleration"
                }
            }
        }
        pager_tabs.setupWithViewPager(pager)
    }
}

