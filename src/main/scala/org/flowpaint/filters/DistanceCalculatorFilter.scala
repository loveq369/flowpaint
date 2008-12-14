package org.flowpaint.filters

import property.Data
import util.{DataSample, PropertyRegister, MathUtils}

/**
 *  Calculates the distance along the line, as well as the length of the previous stroke.
 *
 * @author Hans Haggstrom
 */
// TODO: Add velocity calculation too
class DistanceCalculatorFilter extends PathProcessor {

    var previousX = 0f
    var previousY = 0f
    var previousDistance = 0f

    override protected def onInit() = {

        previousX = 0f
        previousY = 0f
        previousDistance = 0f
    }

    def processPathPoint(pointData: Data, callback: (Data) => Unit) = {


        val x = pointData.getFloatProperty(PropertyRegister.X, 0)
        val y = pointData.getFloatProperty(PropertyRegister.Y, 0)

        if (firstPoint) previousDistance = 0

        val previousSegmentLength = if (firstPoint) 0f else MathUtils.distance(previousX, previousY, x, y)

        pointData.setFloatProperty(PropertyRegister.PREVIOUS_SEGMENT_LENGTH, previousSegmentLength)
        pointData.setFloatProperty(PropertyRegister.DISTANCE, previousDistance + previousSegmentLength)

        previousX = x
        previousY = y
        previousDistance += previousSegmentLength

        callback(pointData)
    }

}