package com.kedzie.vbox.metrics

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView

import androidx.appcompat.widget.AppCompatTextView

import com.kedzie.vbox.R
import com.kedzie.vbox.VBoxApplication

class MetricView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : LinearLayout(context, attrs) {

    var header: String

    private lateinit var renderer: MetricRenderer
    private lateinit var metricNames: LinearLayout

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MetricView, 0, R.style.MetricView)
        try {
            header = a.getString(R.styleable.MetricView_header)
            val bgColor = a.getColor(R.styleable.MetricView_backgroundColor, 0)
            val gridColor = a.getColor(R.styleable.MetricView_gridColor, 0)
            val textColor = a.getColor(R.styleable.MetricView_textColor, 0)
            val borderColor = a.getColor(R.styleable.MetricView_borderColor, 0)
            createView(bgColor, gridColor, textColor, borderColor)
        } finally {
            a.recycle()
        }
    }

    fun createView(bgColor: Int, gridColor: Int, textColor: Int, borderColor: Int) {
        orientation = VERTICAL
        val titleTextView = AppCompatTextView(context)
        titleTextView.text = header
        addView(titleTextView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        renderer = MetricRenderer(context, bgColor, gridColor, textColor, borderColor)
        val p = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        p.weight = 1f
        addView(renderer, p)
        metricNames = LinearLayout(context)
        metricNames.orientation = HORIZONTAL
        addView(metricNames,LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }

    fun init(max: Int, metrics: Array<String>) {
        renderer.init(max, metrics)
        val p = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        for (m in metrics) {
            val textView = AppCompatTextView(context)
            textView.text = m
            textView.setTextColor(VBoxApplication.getInstance().getColor(context, m.replace('/', '_')))
            textView.setPadding(0, 2, 8, 0)
            metricNames.addView(textView, p)
        }
    }

    fun setMetricPrefs(count: Int, period: Int) {
        renderer.setMetricPrefs(count, period)
    }

    fun setQueries(q: Map<String, MetricQuery>) {
        renderer.setQuery(q)
    }
}
