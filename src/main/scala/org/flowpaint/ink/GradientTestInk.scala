package org.flowpaint.brush

import org.flowpaint.property.Data
import org.flowpaint.ink.Ink
import org.flowpaint.util.{PerlinNoise, MathUtils, DataSample, PropertyRegister}

/**
 *   Just a simple test brush.
 *
 * @author Hans Haggstrom
 */
class GradientTestInk extends Ink {

  var blueParam = 0f
  var bumpiness = 1f

  def this( blue : Float, bumpiness : Float ) {
    this()
    blueParam = blue
    this.bumpiness = bumpiness
  }


  def processPixel(pixelData: Data)   {

    val positionAlongStroke = pixelData.getFloatProperty( PropertyRegister.POSTION_ALONG_STROKE,0  )
    val positionAcrossStroke = pixelData.getFloatProperty( PropertyRegister.POSITION_ACROSS_STROKE,0  )


    val time: Float = pixelData.getFloatProperty("time", 0)

    //val dots = noise.noise2( time*0.1f, positionAcrossStroke*5 )
    val noise = 0.5f+0.5f*PerlinNoise.noise3( time*60f, positionAcrossStroke*2.3f, 1.1f )
    val dots2 = MathUtils.interpolate(bumpiness, 1f, noise)
    val stripes = 0.25f + 0.25f * Math.sin(time*8).toFloat

    val red = stripes + 0.5f
    val blue = blueParam
    val green = stripes * 0.5f + 0.25f
    val alpha = (1f - Math.abs(positionAcrossStroke)) * dots2 * pixelData.getFloatProperty(PropertyRegister.ALPHA, 1)

    pixelData.setFloatProperty( PropertyRegister.RED, red )
    pixelData.setFloatProperty( PropertyRegister.GREEN, green)
    pixelData.setFloatProperty( PropertyRegister.BLUE, blue)
    pixelData.setFloatProperty( PropertyRegister.ALPHA, alpha)

  }
}
