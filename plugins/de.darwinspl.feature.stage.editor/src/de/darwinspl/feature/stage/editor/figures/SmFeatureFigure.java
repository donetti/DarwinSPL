package de.darwinspl.feature.stage.editor.figures;

import java.util.Date;

import org.deltaecore.feature.graphical.base.editor.DEGraphicalEditor;
import org.deltaecore.feature.graphical.base.util.DEGraphicalEditorTheme;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.draw2d.geometry.Point;
import de.darwinspl.feature.stage.Stage;
import de.darwinspl.feature.stage.StageComposition;
import de.darwinspl.feature.stage.base.util.SmDrawingUtil;
import de.darwinspl.feature.stage.editor.editor.SmStageModelEditor;
//import de.darwinspl.feature.stage.editor.editor.SmStageModelEditor;
import eu.hyvar.evolution.util.HyEvolutionUtil;
import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.base.figures.DwFeatureFigure;
import de.darwinspl.feature.graphical.base.model.DwFeatureWrapped;
import de.darwinspl.feature.graphical.base.util.DwFeatureUtil;

public class SmFeatureFigure extends DwFeatureFigure  {
	protected SmStageModelEditor stageEditor;
	public SmFeatureFigure(DwGraphicalFeatureModelViewer editor, DwFeatureWrapped feature, SmStageModelEditor stageEditor) {
		super(editor, feature);		
		this.stageEditor = stageEditor;

	}	


	/**
	 * Override paint Function to add own Borders depending on current Stage
	 */
	@Override 
	protected void paintFigure(Graphics graphics) {
		Date date = editor.getCurrentSelectedDate();
		Stage currentStage = stageEditor.getCurrentSelectedStage();


		if (feature.hasVersionsAtDate(date)) {
			paintVersionAreaBackground(graphics);
			
			paintConnection(graphics, HyEvolutionUtil.getValidTemporalElements(feature.getWrappedModelElement().getVersions(), date).get(0));
		}		
		
		if (feature.hasAttributesAtDate(date)) {
			paintAttributeAreaBackground(graphics);
		}
			
		paintNameAreaBackground(graphics);
		
		if( currentStage != null && currentStage.getComposition() != null){
			StageComposition currentComposition = HyEvolutionUtil.getValidTemporalElement(currentStage.getComposition(), editor.getCurrentSelectedDate());
			if(currentComposition!= null){
				if(currentComposition.getFeatures().contains(feature.getWrappedModelElement())){
							paintStageRepresentation(graphics);
				}
			}
		}

	}
	
	
	
	/**
	 * Paint a representation for Stages
	 */
	//TODO Alex: This function has to be implemented to show colored borders
	protected void paintStageRepresentation(Graphics graphics){
		
		Rectangle featureMarkRectangle = getStageMarkRectangle(feature);
		SmDrawingUtil.drawGreenRectangle(graphics, featureMarkRectangle, this, false);
	}
	
	/**
	 * Marking Function from the configurator to draw Stage Representation
	 */
	protected Rectangle getStageMarkRectangle(DwFeatureWrapped feature) {
		DEGraphicalEditorTheme theme = DEGraphicalEditor.getTheme();
		Date date = editor.getCurrentSelectedDate();
		Rectangle stageMarkRectangle = new Rectangle(new Point(5,5), new Point(5,5));

		if(DwFeatureUtil.isWithModifier(feature.getWrappedModelElement(), date)) {
			int increment = theme.getFeatureVariationTypeExtent();
			stageMarkRectangle.y += increment;
		}
		return stageMarkRectangle;
	}

}