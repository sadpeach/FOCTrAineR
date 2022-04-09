package com.example.foctrainer.exercise

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View
import androidx.core.util.Preconditions


class GraphicOverlay : View {
    private val lock = Any()
    private val graphics: MutableList<Graphic> = ArrayList()

    private val transformationMatrix: Matrix = Matrix()

    private var imageWidth = 0
    private var imageHeight = 0
    private var scaleFactor = 1.0f


    private var postScaleWidthOffset = 0f
    private var postScaleHeightOffset = 0f
    private var isImageFlipped = false
    private var needUpdateTransformation = true

    abstract class Graphic(private val overlay: GraphicOverlay?) {

        abstract fun draw(canvas: Canvas?)
        fun scale(imagePixel: Float): Float {
            return imagePixel * overlay!!.scaleFactor
        }


        fun translateX(x: Float): Float {
            if (overlay!!.isImageFlipped) {
                return overlay.getWidth() - (scale(x) - overlay.postScaleWidthOffset);
            } else {
                return scale(x) - overlay.postScaleWidthOffset;
            }
        }

        fun translateY(y: Float): Float {
            return scale(y) - overlay!!.postScaleHeightOffset
        }

        fun getTransformationMatrix(): Matrix {
            return overlay!!.transformationMatrix
        }

        fun postInvalidate() {
            overlay!!.postInvalidate()
        }
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context,attrs) {
        addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            needUpdateTransformation = true
        }
    }

    fun clear() {
        synchronized(lock) { graphics.clear() }
        postInvalidate()
    }

    fun add(graphic: Graphic) {
        synchronized(lock) { graphics.add(graphic) }
    }

    fun remove(graphic: Graphic) {
        synchronized(lock) { graphics.remove(graphic) }
        postInvalidate()
    }

    @SuppressLint("RestrictedApi")
    fun setImageSourceInfo(imageWidth: Int, imageHeight: Int, isFlipped: Boolean) {
        Preconditions.checkState(imageWidth > 0, "image width must be positive")
        Preconditions.checkState(imageHeight > 0, "image height must be positive")
        synchronized(lock) {
            this.imageWidth = imageWidth
            this.imageHeight = imageHeight
            isImageFlipped = isFlipped
            needUpdateTransformation = true
        }
        postInvalidate()
    }

    fun getImageWidth(): Int {
        return imageWidth
    }

    fun getImageHeight(): Int {
        return imageHeight
    }

    private fun updateTransformationIfNeeded() {
        if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) {
            return
        }
        val viewAspectRatio = getWidth().toFloat() / getHeight()
        val imageAspectRatio = imageWidth.toFloat() / imageHeight
        postScaleWidthOffset = 0f
        postScaleHeightOffset = 0f
        if (viewAspectRatio > imageAspectRatio) {
            // The image needs to be vertically cropped to be displayed in this view.
            scaleFactor = getWidth().toFloat() / imageWidth
            postScaleHeightOffset = (getWidth().toFloat() / imageAspectRatio - getHeight()) / 2
        } else {
            // The image needs to be horizontally cropped to be displayed in this view.
            scaleFactor = getHeight().toFloat() / imageHeight
            postScaleWidthOffset = (getHeight().toFloat() * imageAspectRatio - getWidth()) / 2
        }
        transformationMatrix.reset()
        transformationMatrix.setScale(scaleFactor, scaleFactor)
        transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset)
        if (isImageFlipped) {
            transformationMatrix.postScale(-1f, 1f, getWidth() / 2f, getHeight() / 2f)
        }
        needUpdateTransformation = false
    }

    /** Draws the overlay with its associated graphic objects.  */
    protected override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(lock) {
            updateTransformationIfNeeded()
            for (graphic in graphics) {
                graphic.draw(canvas)
            }
        }
    }
}