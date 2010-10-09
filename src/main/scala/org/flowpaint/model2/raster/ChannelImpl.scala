package org.flowpaint.model2.raster

import tile.ZeroTile
import org.flowpaint.model2.blend.Blender

/**
 * 
 */
// IDEA: As a memory optimization, we could check if a tile is 100% the same value after a brush stroke, and replace it with a solid tile in that case..
final class ChannelImpl(val identifier: Symbol) extends Channel {

  var defaultTile: Tile = ZeroTile
  var tiles: Map[TileId, DataTile] = Map()

  private var dirtyTiles: Set[TileId] = Set()
  private var allDirty: Boolean = true

  private var oldTiles: Map[TileId, DataTile] = Map()
  private var newTiles: Map[TileId, DataTile] = Map()
  private var oldDefaultTile: Tile = null

  def setDefaultTile(newDefaultTile: Tile) {
    if (newDefaultTile != defaultTile) {
      oldDefaultTile = defaultTile
      defaultTile = newDefaultTile
      allDirty = true
    }
  }

  def getTile(tileId: TileId): Tile = tiles.get(tileId).getOrElse(defaultTile)
  def getTileAt(x: Int, y: Int): Tile = tiles.get(TileId.forLocation(x, y)).getOrElse(defaultTile)

  def getValueAt(x: Int, y: Int) {
    val tileId = TileId.forLocation(x, y)
    val tile: Tile = getTile(tileId)
    tile(x - tileId.tileX * TileService.tileWidth, y - tileId.tileY * TileService.tileHeight)
  }

  def getAntialiasedValueAt(x: Float, y: Float) {
    val x1 = x.floor.toInt
    val x2 = x.ceil.toInt
    val y1 = y.floor.toInt
    val y2 = y.ceil.toInt

    if (x1 == x2 && y1 == y2) getValueAt(x1, y1)
    else {
      val xf = x - x1
      val yf = y - y1

      val v1 = getValueAt(x1, y1) * (1f - xf) + getValueAt(x2, y1) * xf
      val v2 = getValueAt(x1, y2) * (1f - xf) + getValueAt(x2, y2) * xf

      v1 * (1f - yf) + v2 * yf
    }
  }

  def setValueAt(x: Int, y: Int, value: Float) {
    val tileId: TileId = TileId.forLocation(x, y)
    getTileForModification(tileId).update(x - tileId.tileX * TileService.tileWidth, y - tileId.tileY * TileService.tileHeight, value)
  }


  def runOperation(operation: Operation) {
    val affectedTiles = operation.affectedTiles(identifier)
    affectedTiles foreach { (affectedTile: TileId) =>
      val dataTile = getTileForModification(affectedTile)
      operation.processTile(identifier, affectedTile, dataTile)
      addDirtyTile(affectedTile)
    }
  }
  
  def blend(over: Channel, alpha: Channel, blender: Blender) {
    // Blend the background
    defaultTile = blender.blendBackground(defaultTile, over.defaultTile, alpha.defaultTile)

    // Find the tiles with some content in this layer, the layer to blend, or the channel to blend by
    val tilesToBlend = tiles.keySet ++ over.tiles.keySet ++ alpha.tiles.keySet

    // Blend tiles with data
    tilesToBlend foreach { (tid: TileId) =>
      blender.blendData(getTileForModification(tid), over.getTile(tid), alpha.getTile(tid))
    }
  }

  private def getTileForModification(tileId: TileId): DataTile = {
    if (!newTiles.contains(tileId)) {
      // Store the current state of the tile, so we can undo to it, if there was any edits for the tile
      if (tiles.contains(tileId)) oldTiles += tileId -> tiles(tileId)

      // Create and return a new copy to work on 
      val newTile = TileService.allocateDataTile(getTile(tileId))
      newTiles += tileId -> newTile
      tiles -= tileId
      tiles += tileId -> newTile
      newTile
    }
    else tiles(tileId)
  }


  private def cleanRecordedChanges() {
    oldTiles = Map()
    newTiles = Map()
    oldDefaultTile = null
  }

  def takeSnapshot(layer: Symbol): TileChange = {
    val change = new TileChange(layer, identifier, oldTiles, newTiles, oldDefaultTile, defaultTile)

    cleanRecordedChanges()

    change
  }

  def undoChange(change: TileChange) {
    cleanRecordedChanges()

    change.newTiles.keySet foreach ( t => tiles -= t )
    tiles ++= change.oldTiles
    dirtyTiles ++= change.oldTiles.keySet
    dirtyTiles ++= change.newTiles.keySet

    if (change.defaultTileChanged) {
      defaultTile = change.oldDefaultTile
      allDirty = true
    }
  }

  def redoChange(change: TileChange) {
    cleanRecordedChanges()

    change.oldTiles.keySet foreach ( t => tiles -= t )
    tiles ++= change.newTiles
    dirtyTiles ++= change.oldTiles.keySet
    dirtyTiles ++= change.newTiles.keySet
    
    if (change.defaultTileChanged) {
      defaultTile = change.newDefaultTile
      allDirty = true
    }
  }

  def cleanDirtyTiles() {
    dirtyTiles = Set()
    allDirty = false
  }

  def dirtyTileIds = dirtyTiles

  private def addDirtyTile(tileId: TileId) = dirtyTiles += tileId

}