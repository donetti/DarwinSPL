package de.darwinspl.feature.graphical.editor.editparts;

import java.util.Date;

import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.jface.viewers.TextCellEditor;

import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.base.editparts.DwFeatureEditPart;
import de.darwinspl.feature.graphical.base.figures.DwFeatureFigure;
import de.darwinspl.feature.graphical.base.model.DwFeatureModelWrapped;
import de.darwinspl.feature.graphical.base.model.DwFeatureWrapped;
import de.darwinspl.feature.graphical.editor.commands.feature.DwFeatureChangeTypeCommand;
import de.darwinspl.feature.graphical.editor.locators.DwFeatureCellEditorLocator;
import de.darwinspl.feature.graphical.editor.managers.DwFeatureDirectEditManager;
import de.darwinspl.feature.graphical.editor.policies.feature.DwFeatureComponentEditPolicy;
import de.darwinspl.feature.graphical.editor.policies.feature.DwFeatureDirectEditPolicy;
import de.darwinspl.feature.graphical.editor.policies.feature.DwFeatureGraphicalNodeEditPolicy;
import de.darwinspl.feature.graphical.editor.policies.feature.DwFeatureXYLayoutPolicy;
import eu.hyvar.evolution.util.HyEvolutionUtil;
import eu.hyvar.feature.HyFeatureType;
import eu.hyvar.feature.HyFeatureTypeEnum;

public class DwFeatureEditorEditPart extends DwFeatureEditPart{
	public DwFeatureEditorEditPart(DwGraphicalFeatureModelViewer editor, DwFeatureModelWrapped featureModel) {
		super(editor, featureModel);		
	}

	@Override
	protected void createEditPolicies(){
		super.createEditPolicies();

		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DwFeatureComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new DwFeatureGraphicalNodeEditPolicy(editor, featureModel));
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DwFeatureDirectEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DwFeatureXYLayoutPolicy());
	}
	
	/**
	 * This method is used to enable direct name editing in the feature
	 */
	@Override public void performRequest(Request req) {
		if(!editor.isLastDateSelected())
			return;
		
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			
		}
		if(req.getType() == RequestConstants.REQ_OPEN){
			DwFeatureWrapped feature = (DwFeatureWrapped)getModel();
			Date date = editor.getCurrentSelectedDate();
			
			if(feature.calculateVariationTypeCircleBounds(date).contains(((SelectionRequest)req).getLocation())){
				
				HyFeatureType type = HyEvolutionUtil.getValidTemporalElement(feature.getWrappedModelElement().getTypes(), date);
				
				HyFeatureTypeEnum newType = type.getType() == HyFeatureTypeEnum.MANDATORY ? HyFeatureTypeEnum.OPTIONAL : HyFeatureTypeEnum.MANDATORY;
				
				DwFeatureChangeTypeCommand command = new DwFeatureChangeTypeCommand(feature.getWrappedModelElement(), newType, editor);
				editor.executeCommand(command);
				this.refreshVisuals();
			}else{
				performDirectEditing();
			}
		}
	}

	private void performDirectEditing() {		
		Label label = ((DwFeatureFigure)getFigure()).getLabel();
		DwFeatureDirectEditManager manager = new DwFeatureDirectEditManager(this, TextCellEditor.class, new DwFeatureCellEditorLocator(label), label);
		manager.show();
	}   
}
