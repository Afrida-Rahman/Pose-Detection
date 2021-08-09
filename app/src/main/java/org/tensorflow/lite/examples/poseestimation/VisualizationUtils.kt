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
            textSize = 30F
            color = Color.WHITE
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

//      Elbow, Shoulder, Hip (Left)
        val pA = person.keyPoints[7].coordinate
        val pB = person.keyPoints[5].coordinate
        val pC = person.keyPoints[11].coordinate
//      Elbow, Shoulder, Hip (Right)
        val pD = person.keyPoints[8].coordinate
        val pE = person.keyPoints[6].coordinate
        val pF = person.keyPoints[12].coordinate

//      left
        val aToB = sqrt((pB.x - pA.x).toDouble().pow(2.0) + (pB.y - pA.y).toDouble().pow(2.0))
        val bToC = sqrt((pB.x - pC.x).toDouble().pow(2.0) + (pB.y - pC.y).toDouble().pow(2.0))
        val cToA = sqrt((pC.x - pA.x).toDouble().pow(2.0) + (pC.y - pA.y).toDouble().pow(2.0))
//      right
        val dToE = sqrt((pE.x - pD.x).toDouble().pow(2.0) + (pE.y - pD.y).toDouble().pow(2.0))
        val eToF = sqrt((pE.x - pF.x).toDouble().pow(2.0) + (pE.y - pF.y).toDouble().pow(2.0))
        val fToD = sqrt((pF.x - pD.x).toDouble().pow(2.0) + (pF.y - pD.y).toDouble().pow(2.0))
//      left
        val desiredAngle = round(acos((bToC*bToC+aToB*aToB-cToA*cToA)/(2*bToC*aToB)) *(180/Math.PI))
        val startAngle = 180/Math.PI* atan2((pC.y-pB.y).toDouble(),(pC.x - pB.x).toDouble())
//      right
        val desiredAngle1 = round(acos((eToF*eToF+dToE*dToE-fToD*fToD)/(2*eToF*dToE)) *(180/Math.PI))
        val startAngle1 = 180/Math.PI* atan2((pD.y-pE.y).toDouble(),(pD.x - pE.x).toDouble())

        val radius = 70F
        val oval = RectF()
        oval.set(pB.x-radius, pB.y-radius,pB.x+radius, pB.y+radius)

        val oval1 = RectF()
        oval1.set(pE.x-radius, pE.y-radius,pE.x+radius, pE.y+radius)

        originalSizeCanvas.drawArc(oval, startAngle.toFloat(),-(desiredAngle.toFloat()),true,paintRectF)
        originalSizeCanvas.drawArc(oval1, startAngle1.toFloat(),-(desiredAngle1.toFloat()),true,paintRectF)

        originalSizeCanvas.drawText("$desiredAngle", pC.x-150,pC.y-20,paintText)
        originalSizeCanvas.drawText("$desiredAngle1", pF.x+130,pF.y-20,paintText)

        return output
    }
}

