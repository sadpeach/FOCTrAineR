package com.example.foctrainer.exercise

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.pose.PoseLandmark

class RectOverlay constructor(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val STROKE_WIDTH = 3f // has to be float
    private val drawColor = Color.RED
    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    fun clear() {
        extraCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    internal fun drawLine(
        startLandmark: PoseLandmark?,
        endLandmark: PoseLandmark?
    ) {
        val start = startLandmark!!.position
        val end = endLandmark!!.position


        val xmul = 3.3f;
        val ymul = 3.3f;

        extraCanvas.drawLine(
            (start.x * xmul) - 250, start.y* ymul, (end.x* xmul) -250, end.y* ymul, paint
        )
        invalidate();
    }

    internal fun drawNeck(
        _occhioSx: PoseLandmark?,
        _occhioDx: PoseLandmark?,
        _spallaSx: PoseLandmark?,
        _spallaDx: PoseLandmark?
    ) {

        val xmul = 3.3f;
        val ymul = 3.3f;


        val occhioSx = _occhioSx!!.position
        val occhioDx = _occhioDx!!.position
        val spallaSx = _spallaSx!!.position
        val spallaDx = _spallaDx!!.position


        val fineColloX =  occhioDx.x +  ((occhioSx.x - occhioDx.x) / 2);
        val fineColloY = occhioDx.y + ((occhioSx.y - occhioDx.y) / 2);

        val inizioColloX = spallaDx.x + ((spallaSx.x - spallaDx.x ) / 2);
        val inizioColloY = spallaDx.y + ((spallaSx.y - spallaDx.y) / 2);

        extraCanvas.drawLine(
            (fineColloX * xmul) - 250, fineColloY* ymul, (inizioColloX* xmul) -250, inizioColloY* ymul, paint
        )

        extraCanvas.drawLine(
            (occhioSx.x * xmul) - 250, occhioSx.y* ymul, (occhioDx.x* xmul) -250, occhioDx.y* ymul, paint
        )
        invalidate();
    }


}