package eu.hyvar.feature.graphical.configurator.factory;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;

import eu.hyvar.dataValues.HyEnum;
import eu.hyvar.dataValues.HyEnumLiteral;
import eu.hyvar.feature.HyFeatureAttribute;
import eu.hyvar.feature.HyVersion;
import eu.hyvar.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import eu.hyvar.feature.graphical.base.editparts.DwEnumContainerEditPart;
import eu.hyvar.feature.graphical.base.editparts.HyEnumEditPart;
import eu.hyvar.feature.graphical.base.editparts.HyEnumLiteralEditPart;
import eu.hyvar.feature.graphical.base.editparts.HyGroupEditPart;
import eu.hyvar.feature.graphical.base.editparts.HyParentChildConnectionEditPart;
import eu.hyvar.feature.graphical.base.factory.HyFeatureModelEditPartFactory;
import eu.hyvar.feature.graphical.base.model.DwEnumContainerWrapped;
import eu.hyvar.feature.graphical.base.model.HyFeatureModelWrapped;
import eu.hyvar.feature.graphical.base.model.HyFeatureWrapped;
import eu.hyvar.feature.graphical.base.model.HyGroupWrapped;
import eu.hyvar.feature.graphical.base.model.HyParentChildConnection;
import eu.hyvar.feature.graphical.base.model.HyRootFeatureWrapped;
import eu.hyvar.feature.graphical.configurator.editparts.HyConfiguratorEditorAttributeEditPart;
import eu.hyvar.feature.graphical.configurator.editparts.HyConfiguratorEditorFeatureEditPart;
import eu.hyvar.feature.graphical.configurator.editparts.HyConfiguratorEditorFeatureModelEditPart;
import eu.hyvar.feature.graphical.configurator.editparts.HyConfiguratorEditorVersionEditPart;

public class HyConfiguratorEditorEditPartFactory extends HyFeatureModelEditPartFactory{

	public HyConfiguratorEditorEditPartFactory(GraphicalViewer viewer, DwGraphicalFeatureModelViewer editor) {
		super(viewer, editor);
	}
	
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		
		if(model instanceof HyFeatureModelWrapped){
			part = new HyConfiguratorEditorFeatureModelEditPart(editor);
			viewer.getControl().addControlListener((HyConfiguratorEditorFeatureModelEditPart)part);
			viewer.addPropertyChangeListener((HyConfiguratorEditorFeatureModelEditPart)part);
			
			featureModel = (HyFeatureModelWrapped)model;
		}else if(model instanceof HyRootFeatureWrapped){
			part = new HyConfiguratorEditorFeatureEditPart(editor, featureModel);
		}else if(model instanceof HyFeatureWrapped){
			part = new HyConfiguratorEditorFeatureEditPart(editor, featureModel);
		}else if(model instanceof HyGroupWrapped){
			part = new HyGroupEditPart(editor, featureModel);
		}else if(model instanceof HyParentChildConnection){
			part = new HyParentChildConnectionEditPart(editor, featureModel);
		}else if(model instanceof HyVersion){
			part = new HyConfiguratorEditorVersionEditPart(editor, featureModel);
		}else if(model instanceof HyFeatureAttribute){
			part = new HyConfiguratorEditorAttributeEditPart(editor, featureModel);
		}else if(model instanceof HyEnum){
			part = new HyEnumEditPart(editor, featureModel);
		}else if(model instanceof DwEnumContainerWrapped){
			part = new DwEnumContainerEditPart(editor, featureModel);
		}else if(model instanceof HyEnum){
			part = new HyEnumEditPart(editor, featureModel);			
		}else if(model instanceof HyEnumLiteral){
			part = new HyEnumLiteralEditPart(editor, featureModel);
		}

		if(context != null && model != null && !(model instanceof HyParentChildConnection)){
			part.setParent(context);
		}
		if(part != null){
			part.setModel(model);
		}		
		
		return part;
	}


}