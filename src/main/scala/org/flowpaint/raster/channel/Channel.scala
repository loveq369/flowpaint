package org.flowpaint.raster.channel

import org.flowpaint.util.Rectangle
import org.flowpaint.raster.tile.{DataTile, TileId, Tile}
import org.flowpaint.raster.change.TileChange
import org.flowpaint.raster.blend.Blender

/**
 * A channel of data, e.g. red, green, blue, alpha, height data, x normals, etc..
 * Channels are floating point (32 bit/channel value).
 * Channels are divided into tiles, where only the tiles with actual data have an array with a value for each pixel.
 * A default tile can be specified which will be used for the places where the channel doesn't have any data tiles.
 */
trait Channel {

  /**
   * Name of the channel.
   * E.g. 'red, 'green, 'blue, 'alpha
   */
  def identifier: Symbol

  /**
   * Set the tile to use as default for this channel (e.g. a solid value)
   */
  def setDefaultTile(defaultTile: Tile)

  /**
   * The tile to use as default for this channel (e.g. a solid value)
   */
  def defaultTile: Tile

  /**
   * Returns the tile at the specified tile id.
   */
  def getTile(tileId: TileId): Tile

  /**
   * Returns the tile at the specified coordinates.
   */
  def getTileAt(canvasX: Int, canvasY: Int): Tile

  /**
   * Returns the tile at the specified coordinates, ready for modification.
   */
  def getTileForModification(tileId: TileId): DataTile

  /**
   * The tiles intersecting with the specified area
   */
  def getTilesIn(area: Rectangle): Set[Tile]

  /**
   * The ids of the tiles intersecting with the specified area
   */
  def getTileIdsIn(area: Rectangle): Set[TileId]

  /**
   * Returns the data tiles defined in this channel.
   * The data tiles should not be directly modified.
   */
  def tiles: Map[TileId, DataTile]

  /**
   * Value at the specified canvas location.
   */
  def getValueAt(x: Int, y: Int): Float

  final def getByteValueAt(x: Int, y: Int): Int = (getValueAt(x, y) * 255).toInt
  
  /**
   * Interpolated value at the specified fractional canvas location.
   */
  def getAntialiasedValueAt(x: Float, y: Float): Float

  /**
   * Sets the value at the specified canvas location.
   */
  def setValueAt(x: Int, y: Int, value: Float)

  /**
   * Takes a snapshot of the changes since the last snapshot, used for undo data.
   */
  def takeSnapshot(layer: Symbol): TileChange
  def undoChange(change: TileChange)
  def redoChange(change: TileChange)

  /**
   * The tiles that have been changed since the last call to cleanDirtyTiles.
   * Used for determine what tiles to render.
   */
  def dirtyTileIds: Set[TileId]

  /**
   * True if the whole screen should be redrawn (background color changed etc)
   */
  def allDirty: Boolean

  /**
   *  Marks the dirty tile ids as clean.
   */
  def cleanDirtyTiles()

  /**
   * Blends in the specified channel on top of this channel, using the specified blend function
   * and the specified alpha / blend parameter channel.
   */
  def blend(over: Channel, area: Rectangle, alpha: Option[Channel], blender: Blender)

}
