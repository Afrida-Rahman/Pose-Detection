package org.tensorflow.lite.examples.poseestimation

import android.graphics.*
import androidx.core.graphics.scaleMatrix
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.data.Person
import kotlin.math.*

object VisualizationUtils {
    private const val CIRCLE_RADIUS = 5f
    private const val LINE_WIDTH = 4f

    private val bodyJoints = listOf(
        Pair(BodyPart.NOSE, BodyPart.LEFT_EYE),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_EYE),
        Pair(BodyPart.LEFT_EYE, BodyPart.LEFT_EAR),
        Pair(BodyPart.RIGHT_EYE, BodyPart.RIGHT_EAR),
        Pair(BodyPart.NOSE, BodyPart.LEFT_SHOULDER),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW),
        Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
        Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
        Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
        Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
        Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
    )

    fun drawBodyKeypoints(input: Bitmap, person: Person): Bitmap {
        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.RED
            style = Paint.Style.STROKE
        }
        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val paintRectF = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.WHITE
            style = Paint.Style.STROKE
        }

        val paintText = Paint().apply {
            textSize = 24F
            color = Color.RED
            style = Paint.Style.FILL
        }
        val output = input.copy(Bitmap.Config.ARGB_8888,true)
        val originalSizeCanvas = Canvas(output)

        bodyJoints.forEach {
            val pointA = person.keyPoints[it.first.position].coordinate
            val pointB = person.keyPoints[it.second.position].coordinate
//            println(person.keyPoints)
            originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
        }

        person.keyPoints.forEach { point ->
            originalSizeCanvas.drawCircle(point.coordinate.x, point.coordinate.y, CIRCLE_RADIUS, paintCircle)
        }

        val pA = person.keyPoints[6].coordinate
        val pB = person.keyPoints[8].coordinate
        val pC = person.keyPoints[10].coordinate

        val aToB = sqrt((pB.x - pA.x).toDouble().pow(2.0) + (pB.y - pA.y).toDouble().pow(2.0))
        val bToC = sqrt((pB.x - pC.x).toDouble().pow(2.0) + (pB.y - pC.y).toDouble().pow(2.0))
        val cToA = sqrt((pC.x - pA.x).toDouble().pow(2.0) + (pC.y - pA.y).toDouble().pow(2.0))
        val angle = round(acos((bToC*bToC+aToB*aToB-cToA*cToA)/(2*bToC*aToB)) *(180/Math.PI))
        println("angle : ${angle}")
        val startAngle = 180/Math.PI* atan2((pA.y-pB.y).toDouble(),(pA.x - pB.x).toDouble())
//        val rectF = RectF(0F,0F, (pB.x*2).toFloat(), (pB.y*2).toFloat())
//        val rectF = RectF(((pB.x/2)-radius).toFloat(),((pB.y/2)-radius).toFloat(), ((pB.x/2)+radius).toFloat(),
//            ((pB.y/2)+radius).toFloat())
        val radius = 50F
        val oval = RectF()
        oval.set(pB.x-radius, pB.y-radius,pB.x+radius, pB.y+radius)

        originalSizeCanvas.drawArc(oval, startAngle.toFloat(),-(angle.toFloat()),true,paintRectF)
        originalSizeCanvas.drawText("angle: $angle", 0F,50F,paintText)
        return output
    }
}

