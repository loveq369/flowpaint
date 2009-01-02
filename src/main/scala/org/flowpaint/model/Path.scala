package org.flowpaint.model

import _root_.org.flowpaint.renderer.{StrokeRenderer, RenderSurface}
import _root_.org.flowpaint.util.DataSample
import brush.Brush
import filters.{StrokeListener, PathProcessor}
import ink.Ink
import property.{DataImpl, Data}
/**
 *  A path is a sequence of samples, forming a path on a 2D surface.  The Path can be connected to other paths.
 *  A path can be iterated in both directions, and it can be tested for overlap with a rectangular area.
 *
 * @author Hans Haggstrom
 */
class Path(brush: Brush) extends Renderable {

    val commonProperties = new DataImpl( brush.settings )

    private val pixelProcessors : List[Ink] = brush.createPixelProcessors()
    private val pathProcessors : List[PathProcessor]= brush.createPathProcessors()

    private var path: List[Data] = Nil

    def addPoint(data : Data) {

      addPoint(data, null)
    }


    def addPoint(data : Data, surface: RenderSurface) {

        // OPTIMIZE, can probably be done shorter with some list methods
        var points = List( data )
        pathProcessors.elements foreach ((p : PathProcessor) => points = p.handlePath( points ))


        val pointsToRender = if (path.isEmpty) points else path.last :: points

       path = path ::: points

        if (surface != null && !points.isEmpty) renderPoints(surface, pointsToRender)
    }


    def render(surface: RenderSurface) {
      renderPoints(surface, path)
    }

    def renderPoints(surface: RenderSurface, points: List[Data]) {

        if (points.isEmpty || points.tail.isEmpty ) return

        val segmentStartData: Data = new DataImpl()
        val segmentEndData: Data = new DataImpl()
        segmentEndData.setValuesFrom( points.head )

        var remainingPath = points.tail
        var previous = points.head

        while ( !remainingPath.isEmpty ) {
            val next = remainingPath.head

            // Remember the variable values along the line even if they are only present
            // in the points when they have changed from the previous value.
            segmentStartData.setValuesFrom(previous)
            segmentEndData.setValuesFrom(next)

            renderStrokeSegment(segmentStartData, segmentEndData, surface)

            previous = next
            remainingPath = remainingPath.tail
        }
    }

    private def renderStrokeSegment(startPoint: Data, endPoint: Data, surface: RenderSurface) {

        def processPixel( pixelData : Data ) {
          pixelProcessors foreach (_.processPixel(pixelData ))
        }

        val renderer = new StrokeRenderer()
        renderer.drawStrokeSegment(commonProperties, startPoint, endPoint, processPixel, surface)

    }

}

