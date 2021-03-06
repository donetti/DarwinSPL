package de.darwinspl.feature.graphical.configurator.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.base.editparts.DwFeatureEditPart;
import de.darwinspl.feature.graphical.base.model.DwFeatureModelWrapped;
import de.darwinspl.feature.graphical.base.model.DwFeatureWrapped;
import de.darwinspl.feature.graphical.configurator.editor.DwFeatureModelConfiguratorEditor;
import de.darwinspl.feature.graphical.configurator.figures.DwConfiguratorFeatureFigure;
import de.darwinspl.feature.graphical.configurator.predicates.DwConfiguratorElementHasFeaturePredicate;
import de.darwinspl.feature.graphical.configurator.util.DwConfiguratorEditorUtil;
import eu.hyvar.feature.configuration.HyConfiguration;

public class DwConfiguratorEditorFeatureEditPart extends DwFeatureEditPart{

	public DwConfiguratorEditorFeatureEditPart(DwGraphicalFeatureModelViewer editor, DwFeatureModelWrapped featureModel) {
		super(editor, featureModel);
	}
	
	@Override
	protected IFigure createFigure() {	
		return new DwConfiguratorFeatureFigure(editor, (DwFeatureWrapped)getModel());
	}


	@Override
	public void performRequest(Request request) {
		//React to double click
		if (request.getType() == RequestConstants.REQ_OPEN) {
			DwFeatureWrapped feature = (DwFeatureWrapped)getModel();

			DwFeatureModelConfiguratorEditor editor = (DwFeatureModelConfiguratorEditor)getEditor();
			HyConfiguration configuration = editor.getSelectedConfiguration();
			
			DwConfiguratorElementHasFeaturePredicate<Object> predicate = new DwConfiguratorElementHasFeaturePredicate<>();
			predicate.feature = feature.getWrappedModelElement();
			
			if(!configuration.getElements().removeIf(predicate)){
				DwConfiguratorEditorUtil.addFeatureToConfiguration(configuration, feature.getWrappedModelElement(), editor.getCurrentSelectedDate());
			}else{
				DwConfiguratorEditorUtil.removeChildrenFeaturesFromConfiguration(configuration, feature.getWrappedModelElement(), editor.getCurrentSelectedDate());
			}
		}
		
		editor.refreshView();
	}	
}
