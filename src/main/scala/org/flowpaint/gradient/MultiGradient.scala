package org.flowpaint.gradient

import org.flowpaint.property.{Data, DataImpl}
import scala.xml.{Elem, Node}
import java.awt.Color
import java.util.ArrayList
import org.flowpaint.util.DataSample
import scala.collection.JavaConversions._

case class GradientPoint(var position: Float, data: Data) extends Comparable[GradientPoint] {
    def compareTo(p1: GradientPoint): Int = {
        if (position < p1.position) -1
        else if (position > p1.position) 1
        else 0
    }

    def toXML() : Elem = { <gradientPoint position={position.toString}>{data.toXML()}</gradientPoint> }
}

object MultiGradient {
    def fromXML(node: Node): Gradient = {

        val identifier = (node \ "@id").text

        val gradient = new MultiGradient(identifier)

        val points = node \ "gradientPoint"
        points foreach ((pointNode: Node) => {
            val pos = (pointNode \ "@position").text.toFloat
            val data = Data.fromXML(pointNode)
            gradient.addPoint(new GradientPoint(pos, data))
        })

        gradient
    }
}

/**
 *    A Gradient with multiple datapoints
 *
 * @author Hans Haggstrom
 */
class MultiGradient(val identifier: String) extends Gradient {

    if (identifier == null || identifier.trim() == "") throw new IllegalArgumentException( "The identifier should not be null or whitespace")

    def this(identifier: String, points: GradientPoint*) {
        this (identifier)

        points.foreach(addPoint)
    }

    private val myPoints = new ArrayList[GradientPoint]()
    private val tempSearchPoint = new GradientPoint(0, new DataImpl())

    def getPoints() : List[GradientPoint] = {
      // TODO: Switch completely to scala lists..
      var points : List[GradientPoint] = Nil
      var i = myPoints.size() - 1
      while (i >= 0 ) {
        points = myPoints.get( i ) :: points
        i -= 1
      }

      points
    }

    def getValue( t : Float, outputData : Data )  {

      // TODO, better NaN check?
      val zeroToOne  = if (t < 0f || t != t ) 0f
      else if (t > 1f) 1f
      else t

/*
        println("Gradient point "+zeroToOne+" requested for " + identifier)
*/

        def searchForValueIndex(value: Float): Int =
            {
                tempSearchPoint.position = value
                return java.util.Collections.binarySearch(myPoints, tempSearchPoint);
            }

        val pointsSize = myPoints.size()

        if (pointsSize == 0)
            {
                // No values
            }
        else if (pointsSize == 1)
            {
                // Only one point, return its value
                outputData.setValuesFrom(myPoints.get(0).data)
            }
        else
            {
                val pointIndex = searchForValueIndex(zeroToOne)
                if (pointIndex >= 0)
                    {
                        // Found exact value match, return that point
                        outputData.setValuesFrom(myPoints.get(pointIndex).data)
                    }
                else
                    {
                        // Search returns -insertionPoint - 1 if the exact value was not found,
                        // calculate the insertion point
                        val insertionPointIndex = -(pointIndex + 1)

                        if (insertionPointIndex == pointsSize)
                            {
                                // Last point is closest
                                outputData.setValuesFrom(myPoints.get(pointsSize - 1).data)
                            }
                        else if (insertionPointIndex == 0)
                            {
                                // First color is closest
                                outputData.setValuesFrom(myPoints.get(0).data)
                            }
                        else
                            {
                                val startPoint = myPoints.get(insertionPointIndex - 1)
                                val endPoint = myPoints.get(insertionPointIndex)
                                val startValue = startPoint.position
                                val endValue = endPoint.position

                                val relativePosition = (zeroToOne - startValue) / (endValue - startValue)

                                outputData.setValuesFrom(startPoint.data)
                                outputData.interpolate(relativePosition, endPoint.data)
                            }
                    }
            }
    }

    /**
     *    Removes all points from this gradient.
     */
    def clearPoints()
        {
            myPoints.clear();
        }


    /**
     *    Adds the specified ColorGradientPoint.
     *
     * @param addedColorGradientPoint should not be null or already added.
     */
    def addPoint(point: GradientPoint)
        {
            myPoints.add(point)

            java.util.Collections.sort(myPoints)
        }


    /**
     *    Removes the specified ColorGradientPoint.
     *
     * @param removedColorGradientPoint should not be null.
     */
    def removePoint(point: GradientPoint)
        {
            myPoints.remove(point);
        }


    def toXML() : Elem = <gradient id={identifier}>{myPoints map ( _.toXML() ) }</gradient>
}



