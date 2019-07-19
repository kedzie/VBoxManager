package com.kedzie.vbox.machine.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.INetworkAdapter
import com.kedzie.vbox.machine.ActionsFragment
import com.kedzie.vbox.machine.InfoFragment
import com.kedzie.vbox.machine.LogFragment
import com.kedzie.vbox.machine.SnapshotFragment
import kotlinx.android.synthetic.main.pager_tabs.*

class NetworkFragment : Fragment()  {

    private lateinit var machine: IMachine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        machine = arguments!!.getParcelable(IMachine.BUNDLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pager_tabs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager.adapter = object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount() = 4

            override fun getItem(position: Int): Fragment {
                val args = Bundle().apply {
                    putAll(arguments)
                    putInt(INetworkAdapter.BUNDLE, position)
                }
                return when (position) {
                    0 -> InfoFragment(args)
                    1 -> ActionsFragment(args)
                    2 -> LogFragment(args)
                    else -> SnapshotFragment(args)
                }
            }

            override fun getPageTitle(position: Int): CharSequence {
                return when (position) {
                    0 -> "Adapter #1"
                    1 -> "Adapter #2"
                    2 -> "Adapter #3"
                    else -> "Adapter #4"
                }
            }
        }
        pager_tabs.setupWithViewPager(pager)
    }
}

