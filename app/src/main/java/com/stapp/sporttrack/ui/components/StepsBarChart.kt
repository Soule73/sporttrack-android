package com.stapp.sporttrack.ui.components

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class StepsBarChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BarChart(context, attrs, defStyleAttr) {

    fun setData(dailySteps: Map<String, Long>, textColor: Int, barColor: Int) {
        val entries = dailySteps.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val dataSet = BarDataSet(entries, "Pas Quotidiens")

        // Personnaliser la couleur des barres
        dataSet.color = barColor

        val data = BarData(dataSet)
        this.data = data

        // Personnaliser l'axe X
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.textColor = textColor
        xAxis.textSize = 12f

        // Personnaliser l'axe Y
        axisLeft.setDrawGridLines(false)
        axisLeft.textColor = textColor
        axisLeft.textSize = 12f
        axisRight.isEnabled = false

        // Personnaliser la description et la légende
        description.isEnabled = false
        legend.isEnabled = false

        invalidate()
    }
}
