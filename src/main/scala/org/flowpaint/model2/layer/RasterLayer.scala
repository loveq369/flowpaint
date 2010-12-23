package org.flowpaint.model2

import _root_.org.flowpaint.util.Rectangle
import data.DataMap
import layer.Layer
import raster.Raster

/**
 * A layer with raster data, rendering the data on top of the provided raster data.
 */
class RasterLayer(val picture: Picture) extends Layer {
  var raster: Raster = new Raster()

  override def channel(name: Symbol) = raster.channels.get(name)

  def channels = raster.channels

  def renderLayer(area: Rectangle, targetRaster: Raster, targetData: DataMap) {
    targetRaster.overlay(raster, area)
  }

  override def runOperation(operation: Operation) {
    val affectedChannels = operation.affectedChannels(picture, this)
    val tiles = operation.affectedTiles(picture, this)

    tiles foreach {tileId =>
      val tiles =  affectedChannels map ( c => (c, channel(c).get)) toMap
      operation.renderToTile(picture, this, tileId, tiles)
    }
  }

}

