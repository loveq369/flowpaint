package org.flowpaint.property

import _root_.org.flowpaint.util.DataSample

/**
 *  Holds some simple key-value data.  Provides listener support.
 *
 * @author Hans Haggstrom
 */
trait Data {

  /**
   *  A listener that takes the changed Data object and the name of the changed parameter as parameters.
   *  If the parameter is null, the whole data may have changed.
   */
  type DataListener = (Data, String) => Unit
  
  def addListener(listener: DataListener)

  def removeListener(listener: DataListener)


  def getFloatProperty(name: String, default: Float): Float

  def setFloatProperty(name: String, value: Float)

  def getStringProperty(name: String, default: String): String

  def setStringProperty(name: String, value: String)

  /**
   *  Copies the float properties of this Data to the specified sample.
   */
  def getFloatProperties(target: DataSample)

  /**
   *  Copies the float properties of the specified sample to this Data.
   */
  def setFloatProperties(values: DataSample)


  /**
   * Removes all properties.
   */
  def clear()

  def removeFloatProperty( name : String )

  def removeStringProperty( name : String )
}