package com.kedzie.vbox.metrics

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.PagerAdapter
import com.kedzie.vbox.R
import com.kedzie.vbox.SettingsMetricFragment
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IPerformanceCollector
import com.kedzie.vbox.machine.MachineListViewModel
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.android.synthetic.main.metric_cpu.*
import kotlinx.android.synthetic.main.metric_ram.*
import kotlinx.android.synthetic.main.metrics.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

/**
 * Activity to view metric graphs for Virtual Machine or Host
 * @author Marek Kedzierski
 * @apiviz.stereotype activity
 */
class MetricFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val model: MachineListViewModel by sharedViewModel { activity!!.intent.let {
        parametersOf(it.getParcelableExtra(VBoxSvc.BUNDLE), it.getParcelableExtra(IMachine.BUNDLE)) } }

    private val args: MetricFragmentArgs by navArgs()

    private val sharedPreferences: SharedPreferences by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences , key: String?) {
        val count = sharedPreferences.getString(SettingsMetricFragment.PREF_METRIC_COUNT, "30").toInt()
        val period = sharedPreferences.getString(SettingsMetricFragment.PREF_METRIC_PERIOD, "2").toInt()
        cpu_metrics.setMetricPrefs(count, period)
        ram_metrics.setMetricPrefs(count, period)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.metrics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val count = sharedPreferences.getString(SettingsMetricFragment.PREF_METRIC_COUNT, "30").toInt()
        val period = sharedPreferences.getString(SettingsMetricFragment.PREF_METRIC_PERIOD, "2").toInt()

        if(flipper != null) {
            //for smaller devices show a single metric graph on the page
            flipper.adapter = MetricViewPagerAdapter(listOf(
                    layoutInflater.inflate(R.layout.metric_cpu, flipper, false) as MetricView,
                    layoutInflater.inflate(R.layout.metric_ram, flipper, false) as MetricView))
        }

        cpu_metrics.init(100, arrayOf(IPerformanceCollector.CPU_LOAD_KERNEL, IPerformanceCollector.CPU_LOAD_USER))
        cpu_metrics.setMetricPrefs(count, period)
        ram_metrics.init(args.ramAvailable * 1000, arrayOf(IPerformanceCollector.RAM_USAGE_USED))
        ram_metrics.setMetricPrefs(count, period)
    }

    override fun onStart() {
        super.onStart()

        model.viewModelScope.launch {
            val period = sharedPreferences.getString(SettingsMetricFragment.PREF_METRIC_PERIOD, "2").toInt()

            while(isActive) {
                val data = model.queryMetrics(args.target)
                cpu_metrics.setQueries(data)
                ram_metrics.setQueries(data)
                delay(period*1000L)
            }
        }
    }

    internal inner class MetricViewPagerAdapter(private val views: List<MetricView>) : PagerAdapter() {

        override fun getCount(): Int {
            return views.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return views[position].header
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = views[position]
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
