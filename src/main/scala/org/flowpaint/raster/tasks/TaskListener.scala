package org.flowpaint.raster.tasks

/**
 * 
 */
trait TaskListener {

  def onFinished(operation: Operation)
  def onStarted(operation: Operation)
  def onProgress(operation: Operation, progress: Float)

}