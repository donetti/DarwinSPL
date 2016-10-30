package eu.hyvar.feature.graphical.editor.editparts;


import java.util.Date;

import org.deltaecore.feature.graphical.base.editor.DEGraphicalEditor;
import org.deltaecore.feature.graphical.base.util.DEGraphicalEditorTheme;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import eu.hyvar.evolution.HyEvolutionUtil;
import eu.hyvar.feature.HyVersion;
import eu.hyvar.feature.graphical.base.deltaecore.wrapper.layouter.version.HyVersionLayouterManager;
import eu.hyvar.feature.graphical.base.deltaecore.wrapper.layouter.version.HyVersionTreeLayouter;
import eu.hyvar.feature.graphical.base.editor.GraphicalFeatureModelEditor;
import eu.hyvar.feature.graphical.base.editparts.HyVersionEditPart;
import eu.hyvar.feature.graphical.base.figures.HyVersionFigure;
import eu.hyvar.feature.graphical.base.model.HyFeatureModelWrapped;
import eu.hyvar.feature.graphical.editor.editor.GraphicalEvolutionFeatureModelEditor;
import eu.hyvar.feature.graphical.editor.policies.HyVersionComponentPolicy;
import eu.hyvar.feature.graphical.editor.policies.HyVersionSelectionHandlesPolicy;


public class HyVersionEditorEditPart extends HyVersionEditPart{

	public HyVersionEditorEditPart(GraphicalFeatureModelEditor editor, HyFeatureModelWrapped featureModel) {
		super(editor, featureModel);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new HyVersionSelectionHandlesPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new HyVersionComponentPolicy((GraphicalEvolutionFeatureModelEditor)editor, featureModel));
	}
	
	@Override
	public void setSelected(int value){
		super.setSelected(value);
		
		refreshVisuals();
	}
}