package org.flowpaint.model


import brush.Brush
import filters.StrokeListener
import renderer.{PictureProvider, RenderSurface}
import renderer.StrokeRenderer
import scala.collection.jcl.ArrayList
import util.{DataSample, RectangleInt}

/**
 *     A brush stroke on some layer.
 *
 * @author Hans Haggstrom
 */
case class Stroke(brush: Brush) extends PictureProvider {
  private val points: ArrayList[DataSample] = new ArrayList[DataSample]()


  /**
   *   Adds a stroke point.  Doesn't update the picture.
   */
  def addPoint(data: DataSample) {
    points.add(data)
  }

  /**
   * Removes all stroke points from this stroke.
   */
  def clear() {
    points.clear
  }

  /**
   *   Adds a stroke point and updates the render surface with the latest stroke segment
   */
  def addPoint(data: DataSample, surface: RenderSurface) {

    addPoint(data)

    renderStroke(surface, false)

  }


  def updateSurface(surface: RenderSurface) = {

    renderStroke(surface, true)

  }


  private def renderStroke(surface: RenderSurface, allSegments: Boolean) {
    val startPoint: DataSample = new DataSample()
    val endPoint: DataSample = new DataSample()

    if (points.length > 0)
      {
        endPoint.setValuesFrom(points(0))
      }

    for (i <- 0 until (points.length - 1)) {

      // Remember the variable values along the line even if they are only present
      // in the points when they have changed rom the previous value.
      startPoint.setValuesFrom(points(i))
      endPoint.setValuesFrom(points(i + 1))

      if (allSegments || i == points.length - 2)
        renderStrokeSegment(startPoint, endPoint, surface)
    }

  }

  private def renderStrokeSegment(startPoint: DataSample, endPoint: DataSample, surface: RenderSurface) {

    val renderer = new StrokeRenderer() 
    renderer.drawStrokeSegment( startPoint, endPoint, brush, surface)

  }
}