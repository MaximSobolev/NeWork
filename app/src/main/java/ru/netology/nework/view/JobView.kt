package ru.netology.nework.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.nework.R
import ru.netology.nework.dto.Job
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.utils.AppUtils
import kotlin.random.Random

class JobView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes
) {
    var jobList: List<Job> = emptyList()
        set(value) {
            field = value
            requestLayout()
        }

    private var smallRadius = AndroidUtils.dp(context, 6).toFloat()
    private var bigRadius = AndroidUtils.dp(context, 8).toFloat()
    private var usedRadius = 0F
    private var distance = AndroidUtils.dp(context, 62).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 2).toFloat()
    private var textStartX = AndroidUtils.dp(context, 32).toFloat()
    private var textStartY = 0F
    private var roundX = AndroidUtils.dp(context, 8).toFloat()
    private var roundY = 0F
    private var stopLineY = 0F
    private var viewColor = randomColor()
    private var jobTextSize = AndroidUtils.dp(context, 16).toFloat()
    private var anotherTextSize = AndroidUtils.dp(context, 14).toFloat()
    private var jobTextColor = randomColor()
    private var linkTextColor = randomColor()
    private var fromToTextColor = randomColor()
    private var viewTextWidth = 0F
    private var linkWidth = 0F

    init {
        context.withStyledAttributes(attributeSet, R.styleable.JobView) {
            viewColor = getColor(R.styleable.JobView_color, viewColor)
            jobTextSize = getDimension(R.styleable.JobView_jobTextSize, jobTextSize)
            anotherTextSize = getDimension(R.styleable.JobView_anotherTextSize, anotherTextSize)
            jobTextColor = getColor(R.styleable.JobView_jobTextColor, jobTextColor)
            linkTextColor = getColor(R.styleable.JobView_linkTextColor, linkTextColor)
            fromToTextColor = getColor(R.styleable.JobView_fromToColor, fromToTextColor)
        }

    }

    private var strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = viewColor
    }
    private var jobTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.LEFT
        textSize = jobTextSize
        color = jobTextColor
    }
    private var linkTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.LEFT
        textSize = anotherTextSize
        color = linkTextColor
    }
    private var fromToTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.LEFT
        textSize = anotherTextSize
        color = fromToTextColor
    }
    private var roundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = viewColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        viewTextWidth = w - textStartX
    }

    override fun onDraw(canvas: Canvas) {
        if (jobList.isEmpty()) {
            return
        }

        jobList.map { job ->
            textStartY = if (stopLineY == 0F) {
                textStartY + jobTextPaint.textSize
            } else {
                if (job.link == null) {
                    stopLineY
                } else {
                    stopLineY - linkTextPaint.textSize/2
                }
            }
            usedRadius = if (jobList.lastOrNull()?.id == job.id) {
                bigRadius
            } else {
                smallRadius
            }
            if (job.link != null) {
                canvas.drawText(
                    "${job.position} - ${job.name}",
                    textStartX,
                    textStartY,
                    jobTextPaint
                )
                textStartY += linkTextPaint.textSize
                canvas.drawText(reductionLink(job.link), textStartX, textStartY, linkTextPaint)
                roundY = textStartY - (linkTextPaint.textSize / 2)
                canvas.drawCircle(roundX, roundY, usedRadius, roundPaint)
                textStartY += fromToTextPaint.textSize
                if (job.finish != null) {
                    canvas.drawText(
                        "${AppUtils.parseDateTimeForJobs(job.start)} - ${AppUtils.parseDateTimeForJobs(job.finish)}",
                        textStartX,
                        textStartY,
                        fromToTextPaint
                    )
                } else {
                    canvas.drawText(
                        "${AppUtils.parseDateTimeForJobs(job.start)} - Now",
                        textStartX,
                        textStartY,
                        fromToTextPaint
                    )
                }
            } else {
                canvas.drawText(
                    "${job.position} - ${job.name}",
                    textStartX,
                    textStartY,
                    jobTextPaint
                )
                roundY = textStartY
                canvas.drawCircle(roundX, roundY, usedRadius, roundPaint)
                textStartY += fromToTextPaint.textSize
                if (job.finish != null) {
                    canvas.drawText(
                        "${AppUtils.parseDateTimeForJobs(job.start)} - ${AppUtils.parseDateTimeForJobs(job.finish)}",
                        textStartX,
                        textStartY,
                        fromToTextPaint
                    )
                } else {
                    canvas.drawText(
                        "${AppUtils.parseDateTimeForJobs(job.start)} - Now",
                        textStartX,
                        textStartY,
                        fromToTextPaint
                    )
                }
            }

            if (jobList.lastOrNull()?.id != job.id) {
                stopLineY = roundY + distance
                canvas.drawLine(roundX, roundY, roundX, stopLineY, strokePaint)
            } else {
                textStartY = 0F
                stopLineY = 0F
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = calculateWidth()
        val desiredHeight = calculateHeight()

        val resolvedWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val resolveHeight = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(resolvedWidth, resolveHeight)
    }

    private fun calculateWidth() : Int = AndroidUtils.screenWidth(context)

    private fun calculateHeight() : Int {
        return if (jobList.count() > 1) {
            if (jobList.lastOrNull()?.link != null) {
                ((jobList.count() - 1) * distance + 6 * bigRadius).toInt()
            } else {
                ((jobList.count() - 1) * distance + 4 * bigRadius).toInt()
            }
        } else {
            (4 * bigRadius).toInt()
        }
    }

    private fun randomColor(): Int =
        Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

    private fun reductionLink(link : String) : String {
        linkWidth = linkTextPaint.measureText(link)
        return if (linkWidth > viewTextWidth) {
            val widths = FloatArray(link.length)
            linkTextPaint.getTextWidths(link, widths)
            val count = linkTextPaint.breakText(link, true, viewTextWidth, widths)
            val response = link.take(count - 3)
            "$response..."
        } else {
            link
        }
    }

}