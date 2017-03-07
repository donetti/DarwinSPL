package eu.hyvar.feature.graphical.editor.commands.feature;


import java.util.Date;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import eu.hyvar.evolution.HyEvolutionFactory;
import eu.hyvar.evolution.HyName;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyFeatureFactory;
import eu.hyvar.feature.HyFeatureType;
import eu.hyvar.feature.graphical.base.editor.HyGraphicalFeatureModelViewer;
import eu.hyvar.feature.graphical.base.model.HyFeatureModelWrapped;
import eu.hyvar.feature.graphical.base.model.HyFeatureWrapped;
import eu.hyvar.feature.graphical.base.model.HyParentChildConnection;

public class HyFeatureCreateCommand extends Command {
	private HyFeatureWrapped parent;
	private HyFeatureWrapped newFeature;
	private HyGraphicalFeatureModelViewer editor;
	private HyParentChildConnection connection;
	private Date date;
	
	public HyFeatureCreateCommand(HyFeatureWrapped parent, HyGraphicalFeatureModelViewer editor){
		this.parent = parent;
		this.editor = editor;
	}

	/**
	 * Undo creating a new feature
	 */
	@Override
	public void undo() {
		
		HyFeatureModelWrapped featureModel = editor.getModelWrapped();
		featureModel.removeConnection(connection, date);
		
		featureModel.getModel().getFeatures().remove(newFeature.getWrappedModelElement());
		
		featureModel.rearrangeFeatures();
		editor.refreshView();
		
	}

	@Override
	public void redo() {
		HyFeatureModelWrapped featureModel = editor.getModelWrapped();
		date = editor.getCurrentSelectedDate();
		if(date.equals(new Date(Long.MIN_VALUE))){
			date = null;
		}		
		
		
		// Create a new feature model and editor representation
		HyFeature feature = HyFeatureFactory.eINSTANCE.createHyFeature();
		feature.setValidSince(date);
		newFeature = new HyFeatureWrapped(feature, featureModel);
		newFeature.addPosition(new Point(0, 0), date, false);
		
		HyName name = HyEvolutionFactory.eINSTANCE.createHyName();
		name.setName(featureModel.getValidNewFeatureName());
		name.setValidSince(date);
		feature.getNames().add(name);

		HyFeatureType type = HyFeatureFactory.eINSTANCE.createHyFeatureType();
		feature.getTypes().add(type);
		
		connection = new HyParentChildConnection();
		connection.setSource(parent);
		connection.setTarget(newFeature);
		connection.setModel(featureModel);
		
		featureModel.addConnection(connection, featureModel.getSelectedDate(), null);
		
		parent.addParentToChildConnection(connection);
		newFeature.addChildToParentConnection(connection);
		
		newFeature.setWrappedModelElement(feature);
		featureModel.addFeature(newFeature);
		
		featureModel.rearrangeFeatures();
		editor.refreshView();	
	}	
	
	@Override 
	public void execute(){
		redo();
	}
}
