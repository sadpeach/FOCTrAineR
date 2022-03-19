package com.example.foctrainer.exercise

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;

class InferenceInfoGraphic : GraphicOverlay.Graphic {

    private val TEXT_COLOR: Int = Color.WHITE
    private val TEXT_SIZE = 60.0f

    private var textPaint: Paint? = null
    private var overlay: GraphicOverlay? = null
    private var frameLatency: Long = 0
    private var detectorLatency: Long = 0

    // Only valid when a stream of input images is being processed. Null for single image mode.
    @Nullable
    private var framesPerSecond: Int? = null
    private var showLatencyInfo = true

    constructor(
        overlay: GraphicOverlay?,
        frameLatency: Long,
        detectorLatency: Long,
        @Nullable framesPerSecond: Int?
    ): super(overlay){
        this.overlay = overlay
        this.frameLatency = frameLatency
        this.detectorLatency = detectorLatency
        this.framesPerSecond = framesPerSecond
        textPaint = Paint()
        textPaint!!.color = TEXT_COLOR
        textPaint!!.textSize = TEXT_SIZE
        textPaint!!.setShadowLayer(5.0f, 0f, 0f, Color.BLACK)
        postInvalidate()
    }

    /** Creates an [InferenceInfoGraphic] to only display image size.  */
    constructor(overlay: GraphicOverlay?) : this(overlay,0,0,null) {
        showLatencyInfo = false
    }

    @Synchronized
    override fun draw(canvas: Canvas?) {
        val x = TEXT_SIZE * 0.5f
        val y = TEXT_SIZE * 1.5f
        textPaint?.let {
            canvas?.drawText(
                "InputImage size: " + overlay!!.getImageHeight() + "x" + overlay!!.getImageWidth(),
                x,
                y,
                it
            )
        }
        if (!showLatencyInfo) {
            return
        }
        // Draw FPS (if valid) and inference latency
        if (framesPerSecond != null) {
            if (canvas != null) {
                textPaint?.let {
                    canvas.drawText(
                        "FPS: $framesPerSecond, Frame latency: $frameLatency ms",
                        x,
                        y + TEXT_SIZE,
                        it
                    )
                }
            }
        } else {
            textPaint?.let {
                canvas?.drawText("Frame latency: $frameLatency ms", x, y + TEXT_SIZE,
                    it
                )
            }
        }
        textPaint?.let {
            canvas?.drawText(
                "Detector latency: $detectorLatency ms", x, y + TEXT_SIZE * 2, it
            )
        }
    }
}