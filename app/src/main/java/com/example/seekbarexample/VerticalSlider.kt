package com.example.seekbarexample

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View



class VerticalSlider : View {
    private var mThumbRadius = 0
    private var mTrackBgThickness = 0
    private var mTrackFgThickness = 0
    private var mThumbFgPaint: Paint? = null
    private var mTrackBgPaint: Paint? = null
    private var mTrackFgPaint: Paint? = null
    private var mTrackRect: RectF? = null
    private var mListener: OnProgressChangeListener? = null
    private var mProgress = 0.0f

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs, defStyleAttr)
    }

    fun setThumbColor(color: Int) {
        mThumbFgPaint?.setColor(color)
        invalidate()
    }

    fun setTrackFgColor(color: Int) {
        mTrackFgPaint?.setColor(color)
        invalidate()
    }

    fun setTrackBgColor(color: Int) {
        mTrackBgPaint?.setColor(color)
        invalidate()
    }

    fun setThumbRadiusPx(radiusPx: Int) {
        mThumbRadius = radiusPx
        invalidate()
    }

    fun setTrackFgThicknessPx(heightPx: Int) {
        mTrackFgThickness = heightPx
        invalidate()
    }

    fun setTrackBgThicknessPx(heightPx: Int) {
        mTrackBgThickness = heightPx
        invalidate()
    }

    fun setProgress(progress: Float) {
        setProgress(progress, false)
    }

    fun setProgress(progress: Float, notifyListener: Boolean) {
        onProgressChanged(progress, notifyListener)
    }

    fun setOnSliderProgressChangeListener(listener: OnProgressChangeListener?) {
        mListener = listener
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        // to support non-touchable environment
        setFocusable(true)
        val colorDefaultBg = resolveAttrColor("colorControlNormal", COLOR_BG)
        val colorDefaultFg = resolveAttrColor("colorControlActivated", COLOR_FG)
        mThumbFgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mThumbFgPaint?.setStyle(Paint.Style.FILL)
        mThumbFgPaint?.setColor(colorDefaultFg)
        mTrackBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTrackBgPaint?.setStyle(Paint.Style.FILL)
        mTrackBgPaint?.setColor(colorDefaultBg)
        mTrackFgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTrackFgPaint?.setStyle(Paint.Style.FILL)
        mTrackFgPaint?.setColor(colorDefaultFg)
        mTrackRect = RectF()
        val dm: DisplayMetrics = getResources().getDisplayMetrics()
        mThumbRadius =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, THUMB_RADIUS_FG.toFloat(), dm)
                .toInt()
        mTrackBgThickness =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TRACK_HEIGHT_BG.toFloat(), dm)
                .toInt()
        mTrackFgThickness =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TRACK_HEIGHT_FG.toFloat(), dm)
                .toInt()
        if (attrs != null) {
            val arr: TypedArray = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.VerticalSlider,
                defStyleAttr,
                0
            )
            val thumbColor =
                arr.getColor(R.styleable.VerticalSlider_vs_thumb_color, mThumbFgPaint!!.getColor())
            mThumbFgPaint?.setColor(thumbColor)
            val trackColor =
                arr.getColor(R.styleable.VerticalSlider_vs_track_fg_color, mTrackFgPaint!!.getColor())
            mTrackFgPaint?.setColor(trackColor)
            val trackBgColor =
                arr.getColor(R.styleable.VerticalSlider_vs_track_bg_color, mTrackBgPaint!!.getColor())
            mTrackBgPaint?.setColor(trackBgColor)
            mThumbRadius = arr.getDimensionPixelSize(
                R.styleable.VerticalSlider_vs_thumb_radius,
                mThumbRadius
            )
            mTrackFgThickness = arr.getDimensionPixelSize(
                R.styleable.VerticalSlider_vs_track_fg_thickness,
                mTrackFgThickness
            )
            mTrackBgThickness = arr.getDimensionPixelSize(
                R.styleable.VerticalSlider_vs_track_bg_thickness,
                mTrackBgThickness
            )
            arr.recycle()
        }
    }

    private fun resolveAttrColor(attrName: String, defaultColor: Int): Int {
        val packageName: String = getContext().getPackageName()
        val attrRes: Int = getResources().getIdentifier(attrName, "attr", packageName)
        if (attrRes <= 0) {
            return defaultColor
        }
        val value = TypedValue()
        val theme: Resources.Theme = getContext().getTheme()
        theme.resolveAttribute(attrRes, value, true)
        return getResources().getColor(value.resourceId)
    }

     override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec)
        val contentWidth: Int = getPaddingLeft() + mThumbRadius * 2 + getPaddingRight()
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode != MeasureSpec.EXACTLY) {
            width = Math.max(contentWidth, getSuggestedMinimumWidth())
        }
        setMeasuredDimension(width, height)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled()) {
            return false
        }
        val y = event.y
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val height: Int =
                    getHeight() - getPaddingTop() - getPaddingBottom() - 2 * mThumbRadius
                onProgressChanged(1 - y / height, true)
            }
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mProgress < 1f) {
                onProgressChanged(mProgress + 0.02f, true)
                return true
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mProgress > 0f) {
                onProgressChanged(mProgress - 0.02f, true)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun onProgressChanged(progress: Float, notifyChange: Boolean) {
        mProgress = progress
        if (mProgress < 0) {
            mProgress = 0f
        } else if (mProgress > 1f) {
            mProgress = 1f
        }
        invalidate()
        if (notifyChange && mListener != null) {
            mListener!!.onProgress(mProgress)
        }
    }

     override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTrack(canvas, mThumbRadius, mTrackBgThickness, 0, mTrackBgPaint, 1f)
        val trackPadding =
            if (mTrackBgThickness > mTrackFgThickness) mTrackBgThickness - mTrackFgThickness shr 1 else 0
        drawTrack(canvas, mThumbRadius, mTrackFgThickness, trackPadding, mTrackFgPaint, mProgress)

        // draw bg thumb
        val width: Int = getWidth() - getPaddingLeft() - getPaddingRight()
        val height: Int =
            getHeight() - getPaddingTop() - getPaddingBottom() - 2 * mThumbRadius - 2 * trackPadding
        val leftOffset = width - mThumbRadius * 2 shr 1
         mThumbFgPaint?.let {
             canvas.drawCircle(
                 (getPaddingLeft() + leftOffset + mThumbRadius).toFloat(),
                 getPaddingTop() + mThumbRadius + (1 - mProgress) * height + trackPadding,
                 mThumbRadius.toFloat(),
                 it
             )
         }
    }

    private fun drawTrack(
        canvas: Canvas,
        thumbRadius: Int,
        trackThickness: Int,
        trackPadding: Int,
        trackPaint: Paint?,
        progress: Float
    ) {
        val width: Int = getWidth() - getPaddingLeft() - getPaddingRight()
        val height: Int = getHeight() - getPaddingTop() - getPaddingBottom() - 2 * thumbRadius
        val trackLeft: Int = getPaddingLeft() + (width - trackThickness shr 1)
        val trackTop =
            (getPaddingTop() + thumbRadius + (1 - progress) * height)
        val trackRight = trackLeft + trackThickness
        val trackBottom: Int = getHeight() - getPaddingBottom() - thumbRadius - trackPadding
        val trackRadius = trackThickness * 0.5f
        mTrackRect!![trackLeft.toFloat(), trackTop.toFloat(), trackRight.toFloat()] =
            trackBottom.toFloat()
        trackPaint?.let { canvas.drawRoundRect(mTrackRect!!, trackRadius, trackRadius, it) }
    }

    interface OnProgressChangeListener {
        fun onProgress(progress: Float)
    }

    companion object {
        private const val THUMB_RADIUS_FG = 6
        private const val TRACK_HEIGHT_BG = 4
        private const val TRACK_HEIGHT_FG = 2
        private val COLOR_BG: Int = Color.parseColor("#dddfeb")
        private val COLOR_FG: Int = Color.parseColor("#7da1ae")
    }
}