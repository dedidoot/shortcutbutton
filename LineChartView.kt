package com.linechart.linechartview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.linechart.R
import kotlin.collections.ArrayList

class LineChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val points: MutableList<PointModel>
    private val paint = Paint()
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val chartDrawable: ShapeDrawable
    private val yLabelDrawable: ShapeDrawable
    private val xLabelDrawable: ShapeDrawable
    private var style: LineChartStyle

    private var manualXGridUnit: Long? = null
    private var manualYGridUnit: Long? = null
    private var yLabelWidth: Long = 0
    private var xLabelHeight: Long = 0
    private var chartTopMargin: Long = 0
    private var chartRightMargin: Long = 0
    private var manualXLabels: List<Long>? = null
    private var manualYLabels: List<Long>? = null
    private var manualMinX: Long? = null

    private var manualMaxX: Long? = null

    private var manualMinY: Long? = null
    private var manualMaxY: Long? = null

    init {
        points = ArrayList()
        style = LineChartStyle()
        paint.isAntiAlias = true
        yLabelDrawable = ShapeDrawable()
        xLabelDrawable = ShapeDrawable()
        chartDrawable = ShapeDrawable()
    }

    fun updateDrawables() {
        drawXLabels(xLabelDrawable)
        drawYLabels(yLabelDrawable)
        drawLineChart(chartDrawable)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        yLabelDrawable.draw(canvas)
        xLabelDrawable.draw(canvas)
        chartDrawable.draw(canvas)
        this.canvas = canvas
    }

    private var canvas: Canvas? = null

    private fun drawYLabels(labelDrawable: ShapeDrawable) {
        Log.e("eksekusi", "sip")
        measureYLabel()
        labelDrawable.setBounds(0, 0, width, height)
        labelDrawable.shape = object : Shape() {
            override fun draw(canvas: Canvas, paint: Paint) {
                labelPaint.textAlign = Paint.Align.RIGHT
                labelPaint.textSize = style.labelTextSize
                labelPaint.color = style.labelTextColor
                val minY = minY
                val maxY = maxY
                val yrange = maxY - minY
                val height = height
                val left = getYLabelWidth()
                val yLabels = yLabels
                for (y in yLabels) {
                    Log.e("kesini", "sip $y")
                    val label = formatYLabel(y)
                    val yCoordinate = getYCoordinate(height, y, minY, yrange)
                    canvas.drawText(label, left, yCoordinate, labelPaint)
                }
            }
        }
    }

    private fun measureYLabel() {
        labelPaint.textAlign = Paint.Align.RIGHT
        labelPaint.textSize = style.labelTextSize
        val minY = minY
        val maxY = maxY
        val yGridUnit = yGridUnit
        Log.e("kesitu", "minY $minY")
        Log.e("kesitu", "maxY $maxY")
        Log.e("kesitu", "yGridUnit $yGridUnit")
        yLabelWidth = 0
        chartTopMargin = 0
        var y = minY
        val textBounds = Rect()
        while (y < maxY) {
            val label = formatYLabel(y)
            Log.e("kesitu", "sip $y")
            labelPaint.getTextBounds(label, 0, label.length, textBounds)
            if (textBounds.width() > yLabelWidth) {
                yLabelWidth = textBounds.width().toLong()
            }
            chartTopMargin = textBounds.height().toLong()
            y += yGridUnit
        }
    }

    private fun formatYLabel(y: Long): String {
        return "oke$y"
    }

    private fun drawXLabels(labelDrawable: ShapeDrawable) {
        measureXLabel()
        labelDrawable.setBounds(0, 0, width, height)
        labelDrawable.shape = object : Shape() {
            override fun draw(canvas: Canvas, paint: Paint) {
                labelPaint.textAlign = Paint.Align.CENTER
                labelPaint.textSize = style.labelTextSize
                labelPaint.color = style.labelTextColor
                labelPaint.typeface = ResourcesCompat.getFont(context, R.font.istok_web_bold)
                val minX = minX
                val maxX = maxX
                val xrange = maxX - minX
                val width = width
                val height = height
                val labelHeight = height - style.xLabelMargin
                val textBounds = Rect()
                val xLabels = xLabels
                for (x in xLabels) {
                    val label = formatXLabel(x)
                    labelPaint.getTextBounds(label, 0, label!!.length, textBounds)
                    val xCoordinate = getXCoordinate(width, x, minX, xrange)
                    canvas.drawText(label, xCoordinate, labelHeight, labelPaint)
                }
            }
        }
    }

    private fun measureXLabel() {
        labelPaint.textAlign = Paint.Align.CENTER
        labelPaint.textSize = style.labelTextSize
        val minX = minX
        val maxX = maxX
        val xGridUnit = xGridUnit
        xLabelHeight = 0
        chartRightMargin = 0
        var x = minX
        val textBounds = Rect()
        while (x <= maxX) {
            var label: String? = ""
            if (x < points.size) {
                label = formatXLabel(x)
            }
            labelPaint.getTextBounds(label, 0, label!!.length, textBounds)
            val height = (textBounds.height() + style.xLabelMargin * 2).toInt()
            if (height > xLabelHeight) {
                xLabelHeight = height.toLong()
            }
            chartRightMargin = textBounds.width() / 2.toLong()
            x += xGridUnit
        }
    }

    private fun formatXLabel(x: Long): String? {
        return if (points.size > 0) {
            points[x.toInt()].textX
        } else {
            "0"
        }
    }

    private fun drawLineChart(chartDrawable: ShapeDrawable) {
        chartDrawable.shape = object : Shape() {
            override fun draw(canvas: Canvas, paint: Paint) {
                val minX = minX
                val maxX = maxX
                val xrange = maxX - minX
                val minY = minY
                val maxY = maxY
                val yrange = maxY - minY

                drawXGrid(canvas, minX, xrange)
                drawYGrid(canvas, minY, yrange)
                drawLines(canvas, minX, xrange, minY, yrange)

                val width = width
                val height = height
                val left: Float = chartLeftMargin
                val top = getChartTopMargin()
                val right = width - getChartRightMargin()
                val bottom: Float = height - chartBottomMargin

                if (style.isDrawPoint) {
                    drawPoints(canvas, minX, xrange, minY, yrange)
                }
            }
        }
    }


    private val chartLeftMargin: Float
        get() = getYLabelWidth() + style.yLabelMargin

    private fun getChartTopMargin(): Float {
        return chartTopMargin.toFloat()
    }

    private fun getChartRightMargin(): Float {
        return chartRightMargin.toFloat()
    }

    private val chartBottomMargin: Float
        get() = getXLabelHeight() + style.xLabelMargin

    private val minX: Long
        get() = manualMinX ?: rawMinX

    private val rawMinX: Long
        get() = if (points.isEmpty()) {
            0
        } else points[0].x

    fun setManualMaxX(maxX: Long) {
        manualMaxX = maxX
    }

    private val maxX: Long
        get() {
            if (manualMaxX != null) {
                return manualMaxX as Long
            }
            val rawMaxX = rawMaxX
            val step = getUnit(absMaxX)
            return ((Math.floor(1.0 * rawMaxX / step) + 1) * step).toLong()
        }

    private val rawMaxX: Long
        get() = if (points.isEmpty()) {
            DEFAULT_MAX_X
        } else points[points.size - 1].x

    private val absMaxX: Long
        get() {
            if (points.isEmpty()) {
                return DEFAULT_MAX_X
            }
            var absMaxX = Long.MIN_VALUE
            for (point in points) {
                val x = Math.abs(point.x)
                if (x > absMaxX) {
                    absMaxX = x
                }
            }
            return absMaxX
        }

    private fun getXCoordinate(width: Float, point: PointModel, minX: Long, xrange: Long): Float {
        return getXCoordinate(width, point.x, minX, xrange)
    }

    private fun getXCoordinate(width: Float, x: Long, minX: Long, xrange: Long): Float {
        return getXCoordinate(width, x, minX, xrange, true)
    }

    private fun getXCoordinate(
        width: Float,
        x: Long,
        minX: Long,
        xrange: Long,
        inChartArea: Boolean
    ): Float {
        return if (inChartArea) {
            val left = chartLeftMargin
            val right = getChartRightMargin()
            val margin = left + right
            (width - margin) * (x - minX) * 1.0f / xrange + left
        } else {
            width * (x - minX) * 1.0f / xrange
        }
    }

    private val absMaxY: Long
        get() {
            if (points.isEmpty()) {
                return DEFAULT_MAX_Y
            }
            var absMaxY = Long.MIN_VALUE
            for (point in points) {
                val y = Math.abs(point.y)
                if (y > absMaxY) {
                    absMaxY = y
                }
            }
            return absMaxY
        }

    fun setManualMinY(minY: Long) {
        manualMinY = minY
    }

    val minY: Long
        get() {
            if (manualMinY != null) {
                return manualMinY as Long
            }
            val rawMinY = rawMinY
            val step = getUnit(absMaxY)
            return ((Math.ceil(1.0 * rawMinY / step) - 1) * step).toLong()
        }

    private val rawMinY: Long
        get() {
            if (points.isEmpty()) {
                return 0
            }
            var minY = Long.MAX_VALUE
            for (point in points) {
                val y = point.y
                if (y < minY) {
                    minY = y
                }
            }
            return minY
        }

    private val maxY: Long
        get() {
            if (manualMaxY != null) {
                return manualMaxY as Long
            }
            val rawMaxY = rawMaxY
            val step = getUnit(absMaxY)
            Log.e("stepppp", "xa $step")
            return ((Math.floor(1.0 * rawMaxY / step) + lineHorizontalTopOffset) * step).toLong()
        }

    private val rawMaxY: Long
        get() {
            if (points.isEmpty()) {
                return DEFAULT_MAX_Y
            }
            var maxY = Long.MIN_VALUE
            for (point in points) {
                val y = point.y
                if (y > maxY) {
                    maxY = y
                }
            }
            return maxY
        }

    private fun getUnit(maxValue: Long): Long {
        val digits = Math.log10(maxValue.toDouble()).toInt()
        val unit = Math.pow(10.0, digits.toDouble()).toLong()
        Log.e("unit", "oke $digits unit $unit")
        return unit
    }

    private fun getYCoordinate(height: Float, point: PointModel, minY: Long, yrange: Long): Float {
        return getYCoordinate(height, point.y, minY, yrange)
    }

    private fun getYCoordinate(height: Float, y: Long, minY: Long, yrange: Long): Float {
        return getYCoordinate(height, y, minY, yrange, true)
    }

    private fun getYCoordinate(height: Float, y: Long, minY: Long, yrange: Long, inChartArea: Boolean): Float {
        return if (inChartArea) {
            val top = getChartTopMargin()
            val bottom = chartBottomMargin
            val margin = top + bottom
            (height - margin) * (1.0f - (y - minY) * 1.0f / yrange) + top
        } else {
            height * (1.0f - (y - minY) * 1.0f / yrange)
        }
    }

    private fun drawXGrid(canvas: Canvas, minX: Long, xrange: Long) {
        val maxX = maxX
        val width = width.toFloat()
        val height = height.toFloat()
        val top = getChartTopMargin()
        val bottom = height - chartBottomMargin
        val paintDrawX = Paint()
        paintDrawX.pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0F)
        paintDrawX.color = Color.parseColor("#E8E8E8")
        paintDrawX.strokeWidth = 6f

        val xCoordinateMin = getXCoordinate(width, 0, minX, xrange)
        canvas.drawLine(xCoordinateMin, bottom, xCoordinateMin, top, paintDrawX)

        val xCoordinateMax = getXCoordinate(width, maxX, minX, xrange)
        canvas.drawLine(xCoordinateMax, bottom, xCoordinateMax, top, paintDrawX)
    }

    private fun drawYGrid(canvas: Canvas, minY: Long, yrange: Long) { // line vertical
        //val yGridUnit = yGridUnit
        val maxY = maxY
        val width = width.toFloat()
        val height = height.toFloat()
        val left = chartLeftMargin
        val right = width - getChartRightMargin()
        val paintDrawY = Paint()
        paintDrawY.pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0F)
        paintDrawY.color = Color.parseColor("#E8E8E8")
        paintDrawY.strokeWidth = 6f

        val yCoordinateMin = getYCoordinate(height, 0, minY, yrange)
        canvas.drawLine(
            left - lineVerticalLeftOffset,
            yCoordinateMin,
            right + lineVerticalRightOffset,
            yCoordinateMin,
            paintDrawY
        )

        val yCoordinateMiddle = getYCoordinate(height, maxY / 2, minY, yrange)
        canvas.drawLine(
            left - lineVerticalLeftOffset,
            yCoordinateMiddle,
            right + lineVerticalRightOffset,
            yCoordinateMiddle,
            paintDrawY
        )

        val yCoordinateMax = getYCoordinate(height, maxY, minY, yrange)
        canvas.drawLine(
            left - lineVerticalLeftOffset,
            yCoordinateMax,
            right + lineVerticalRightOffset,
            yCoordinateMax,
            paintDrawY
        )
    }

    private fun drawLines(canvas: Canvas, minX: Long, xrange: Long, minY: Long, yrange: Long) {
        var prevPoint: PointModel? = null
        var px = 0.0f
        var py = 0.0f
        val width = width.toFloat()
        val height = height.toFloat()
        paint.color = style.lineColor
        paint.strokeWidth = style.lineWidth
        paint
        for (point in points) {
            val x = getXCoordinate(width, point, minX, xrange)
            val y = getYCoordinate(height, point, minY, yrange)
            if (prevPoint != null) {

                val paintBg = Paint()
                paintBg.color = Color.parseColor(point.color)
                val yCoordinateMin = getYCoordinate(height, 0, minY, yrange)
                canvas.drawRect(px, py+50, x, yCoordinateMin, paintBg)

                Log.e("top", "top "+y)
                Log.e("top", "topx "+point.y)

                canvas.drawLine(px, py, x, y, paint)

            }
            prevPoint = point
            px = x
            py = y
        }
    }

    private fun drawPoints(canvas: Canvas, minX: Long, xrange: Long, minY: Long, yrange: Long) {
        val width = width.toFloat()
        val height = height.toFloat()
        for (point in points) {
            val x = getXCoordinate(width, point, minX, xrange)
            val y = getYCoordinate(height, point, minY, yrange)
            paint.color = style.lineColor
            canvas.drawCircle(x, y, style.pointSize, paint)
            if (style.isDrawPointCenter) {
                paint.color = style.backgroundColor
                canvas.drawCircle(x, y, style.pointCenterSize, paint)
            }
        }
    }

    private val xGridUnit: Long get() = manualXGridUnit ?: getUnit(absMaxX)

    private val yGridUnit: Long get() = manualYGridUnit ?: getUnit(absMaxY)

    private val xLabels: List<Long>
        get() {
            if (manualXLabels != null) {
                return manualXLabels as List<Long>
            }
            val minX = minX
            val maxX = maxX
            val xGridUnit = xGridUnit
            var x = calcMinGridValue(minX, xGridUnit)
            val xLabels: MutableList<Long> =
                ArrayList()
            while (x <= maxX) {
                xLabels.add(x)
                x += xGridUnit
            }
            return xLabels
        }

    private val yLabels: List<Long>
        get() {
            if (manualYLabels != null) {
                return manualYLabels as List<Long>
            }
            val minY = minY
            val maxY = maxY
            val yGridUnit = yGridUnit
            var y = calcMinGridValue(minY, yGridUnit)
            val yLabels: MutableList<Long> =
                ArrayList()
            while (y < maxY) {
                yLabels.add(y)
                y += yGridUnit
            }
            return yLabels
        }

    private fun calcMinGridValue(min: Long, gridUnit: Long): Long {
        return (Math.ceil(1.0 * min / gridUnit) * gridUnit).toLong()
    }

    fun getYLabelWidth(): Float {
        return if (style.yLabelWidth != LineChartStyle.AUTO_WIDTH) {
            style.yLabelWidth
        } else yLabelWidth.toFloat()
    }

    private fun getXLabelHeight(): Float {
        return if (style.xLabelHeight != LineChartStyle.AUTO_HEIGHT) {
            style.xLabelHeight
        } else xLabelHeight.toFloat()
    }

    fun setPoints(points: List<PointModel>) {
        this.points.clear()
        this.points.addAll(points)
    }

    fun setStyle(lineChartStyle: LineChartStyle) {
        this.style = lineChartStyle
        updateDrawables()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateDrawables()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.e(
            "onTouchEvent",
            "getX " + event.x + " getY " + event.y + " getAction " + event.action
        )
        return super.onTouchEvent(event)
    }

    companion object {
        private const val DEFAULT_MAX_X: Long = 1000
        private const val DEFAULT_MAX_Y: Long = 1000
        private const val lineVerticalRightOffset = 16
        private const val lineVerticalLeftOffset = 16
        private const val lineHorizontalTopOffset = 0.3
    }
}