package org.flowpaint.ink

import org.flowpaint.property.Data
import org.flowpaint.gradient.Gradient
import org.flowpaint.util.{ColorUtils, DataSample, PropertyRegister}

/**
 * Multiplies existing color with the color derived from the hue, sat, lightness -properties,
 * or if no existing color is available, uses the color directly.
 * 
 * @author Hans Haggstrom
 */
class ColorInk() extends Ink {

  def processPixel(pixelData: Data) {


    val hue = pixelData.getFloatProperty( PropertyRegister.HUE, 0 )
    val saturation = pixelData.getFloatProperty( PropertyRegister.SATURATION, 0 )
    val lightness = pixelData.getFloatProperty( PropertyRegister.LIGHTNESS, 0 )

    val (red, green, blue )= ColorUtils.HSLtoRGB( hue, saturation, lightness )

    val r = pixelData.getFloatProperty(PropertyRegister.RED,1) * red
    val g = pixelData.getFloatProperty(PropertyRegister.GREEN,1) * green
    val b = pixelData.getFloatProperty(PropertyRegister.BLUE,1) * blue

    pixelData.setFloatProperty( PropertyRegister.RED, r )
    pixelData.setFloatProperty( PropertyRegister.GREEN, g)
    pixelData.setFloatProperty( PropertyRegister.BLUE, b )
  }


}