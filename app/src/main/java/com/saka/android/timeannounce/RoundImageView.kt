package com.saka.android.timeannounce

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RoundImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var radius = DEFAULT_CORNER
    private var border = DEFAULT_BORDER

    private var rect: RectF = RectF()
    private var path: Path = Path()

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
        radius = typeArray.getDimension(R.styleable.RoundImageView_imageCornerRadius, DEFAULT_CORNER)
        typeArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        path.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas?.clipPath(path)
        super.onDraw(canvas)
    }

    fun setRoundCorner(round: Float) {
        radius = round
        invalidate()
    }

    companion object {
        private const val DEFAULT_CORNER = 12F
        private const val DEFAULT_BORDER = 0F
    }
}
