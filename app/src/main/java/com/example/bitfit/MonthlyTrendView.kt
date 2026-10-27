package com.example.bitfit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.max

/** A lightweight line chart showing daily logged intake across the selected month. */
class MonthlyTrendView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.green)
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.green)
        style = Paint.Style.FILL
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.chart_grid)
        strokeWidth = 1.5f
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.muted)
        textSize = 12f * resources.displayMetrics.scaledDensity
    }
    private var points: List<Pair<Int, Int>> = emptyList()
    private var month: YearMonth = YearMonth.now()

    fun setEntries(entries: List<WaterEntry>, selectedMonth: YearMonth) {
        month = selectedMonth
        points = entries.mapNotNull { entry ->
            runCatching { LocalDate.parse(entry.date).dayOfMonth to entry.ounces }.getOrNull()
        }.sortedBy { it.first }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val left = 42f * resources.displayMetrics.density
        val right = width - 14f * resources.displayMetrics.density
        val top = 18f * resources.displayMetrics.density
        val bottom = height - 28f * resources.displayMetrics.density
        if (right <= left || bottom <= top) return

        if (points.isEmpty()) {
            labelPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("No entries for this month yet", width / 2f, height / 2f, labelPaint)
            return
        }

        val minValue = points.minOf { it.second }
        val maxValue = points.maxOf { it.second }
        val span = max(1, maxValue - minValue)
        for (step in 0..2) {
            val y = top + (bottom - top) * step / 2f
            canvas.drawLine(left, y, right, y, gridPaint)
            val label = (maxValue - span * step / 2f).toInt().toString()
            labelPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(label, left - 8f * resources.displayMetrics.density, y + labelPaint.textSize / 3f, labelPaint)
        }

        val path = Path()
        points.forEachIndexed { index, point ->
            val x = left + (point.first - 1).toFloat() / max(1, month.lengthOfMonth() - 1) * (right - left)
            val y = bottom - (point.second - minValue).toFloat() / span * (bottom - top)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, linePaint)
        points.forEach { point ->
            val x = left + (point.first - 1).toFloat() / max(1, month.lengthOfMonth() - 1) * (right - left)
            val y = bottom - (point.second - minValue).toFloat() / span * (bottom - top)
            canvas.drawCircle(x, y, 4.5f * resources.displayMetrics.density, dotPaint)
        }
        labelPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("1", left, height - 5f * resources.displayMetrics.density, labelPaint)
        labelPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(month.lengthOfMonth().toString(), right, height - 5f * resources.displayMetrics.density, labelPaint)
    }
}
