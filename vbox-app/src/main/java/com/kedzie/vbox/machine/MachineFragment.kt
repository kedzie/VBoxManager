package com.kedzie.vbox.machine


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IMachineStateChangedEvent
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.android.synthetic.main.machine_fragment.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class MachineFragment : Fragment() {

    private val model: MachineListViewModel by sharedViewModel { activity!!.intent.let {
        parametersOf(it.getParcelableExtra(VBoxSvc.BUNDLE), it.getParcelableExtra(IMachine.BUNDLE)) } }

    private fun loadData() {
        model.machine.value?.let {
            model.viewModelScope.launch {
                machine_view.update(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.machine_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount() = 4

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> InfoFragment(arguments!!)
                    1 -> ActionsFragment(arguments!!)
                    2 -> LogFragment(arguments!!)
                    else -> SnapshotFragment(arguments!!)
                }
            }

            override fun getPageTitle(position: Int): CharSequence {
                return when (position) {
                    0 -> getString(R.string.machine_tab_info)
                    1 -> getString(R.string.machine_tab_actions)
                    2 -> getString(R.string.machine_tab_log)
                    else -> getString(R.string.machine_tab_snapshots)
                }
            }
        }
        pager_tabs.setupWithViewPager(pager)

        model.events.observe(this, Observer {
            when(it) {
                is IMachineStateChangedEvent -> {
                    Timber.d("got event loading data %s", it)
                    loadData()
                }
            }
        })

        model.vmgr.observe(this, Observer {
            it?.let { vmgr ->
                loadData()
            }
        })
    }
}
