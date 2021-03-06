package org.flowpaint.ui
import org.flowpaint.brush.Brush
import org.flowpaint.ui.editors.Editor
import java.awt.{Dimension, Color}
import javax.swing.{JPanel, JLabel}
import net.miginfocom.swing.MigLayout
import org.flowpaint.property.DataEditor

/**
 * 
 * 
 * @author Hans Haggstrom
 */

class BrushUi {

  def ui = editorPanel.ui
  val editorPanel = new ParameterPanel()

  private var brush : Brush = null

  private val PREVIEW_BACKGROUND_COLOR: Color = new Color(0.666f, 0.666f, 0.666f)

  def setBrush( brushToShow : Brush ) {

    if (brush != brushToShow) {
      brush = brushToShow

      editorPanel.clear()

      if (brush != null){


        // Add brush title
        val brushTitle: JLabel = new JLabel(brush.name)
        editorPanel.addUi( brushTitle)

        // Add brush preview
        val preview: BrushPreview = new BrushPreview(brush)
        preview.setPreferredSize( new Dimension( 64,64 ) )
        preview.setBackgroundColor( PREVIEW_BACKGROUND_COLOR )
        editorPanel.addUi( preview)

        // Create the editors for editing this brush
        brush.createEditors foreach { (e : Editor) => editorPanel.addUi( e.ui )  }


/*

        brush.editors.elements.foreach( (d: DataEditor) => {
*/
/* Takes up space and kind of redundant, so commented out.
          editorPanel.addUi( new JLabel( d.title ))
*/
/*
          editorPanel.addUi( d.createEditor( brush.settings, brush )  ) } )
*/

      }

      editorPanel.ui.repaint()
    }
  }
}