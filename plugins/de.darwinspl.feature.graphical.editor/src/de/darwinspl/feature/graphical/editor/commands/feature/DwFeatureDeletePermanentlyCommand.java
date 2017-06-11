package de.darwinspl.feature.graphical.editor.commands.feature;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;

import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.base.editparts.DwFeatureEditPart;
import de.darwinspl.feature.graphical.base.editparts.DwRootFeatureEditPart;
import de.darwinspl.feature.graphical.base.model.DwFeatureWrapped;
import de.darwinspl.feature.graphical.base.model.DwGroupWrapped;
import de.darwinspl.feature.graphical.base.model.DwParentChildConnection;
import de.darwinspl.feature.graphical.editor.commands.DwFeatureModelEditorCommand;
import de.darwinspl.feature.graphical.editor.util.DwEcoreUtil;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyFeatureChild;
import eu.hyvar.feature.HyFeatureModel;
import eu.hyvar.feature.HyGroup;
import eu.hyvar.feature.HyGroupComposition;

public class DwFeatureDeletePermanentlyCommand extends DwFeatureModelEditorCommand{
	EditPart host;

	public DwFeatureDeletePermanentlyCommand(DwGraphicalFeatureModelViewer viewer, EditPart host) {
		super(viewer);

		this.host = host;
	}

	private DwFeatureWrapped feature;
	private HyFeature oldFeature;

	private List<HyGroupComposition> groupMemberships = new ArrayList<HyGroupComposition>();
	private List<HyFeatureChild> featureChildren = new ArrayList<HyFeatureChild>();

	private HyGroup group;

	public void setFeature(DwFeatureWrapped feature) {
		this.feature = feature;
	}

	@Override
	public boolean canExecute() {
		boolean correctElement = (host instanceof DwFeatureEditPart) && !(host instanceof DwRootFeatureEditPart);

		if(correctElement){
			DwFeatureWrapped feature = (DwFeatureWrapped)((DwFeatureEditPart)host).getModel();
			if(feature.getChildrenConnections(viewer.getCurrentSelectedDate()).size() != 0){
				return false;
			}else
				return true;
		}

		return false;
	}

	private void updateFeatureModel(){
		// delete the selection from the element
		host.setSelected(0);
		
		viewer.getModelWrapped().rearrangeFeatures();
		viewer.refreshView();	
	}
	
	private void redoConnections(){
		for(DwParentChildConnection connection : feature.getParentConnections()){
			connection.getSource().removeParentToChildConnection(connection);
		}
	}
	
	private void undoConnections(){
		for(DwParentChildConnection connection : feature.getParentConnections()){
			connection.getSource().addParentToChildConnection(connection);
		}
	}
		
	public HyGroup getRealModelGroup(HyGroup backupGroup){
		HyFeatureModel featureModel = viewer.getInternalFeatureModel();
		
		for(HyGroup group : featureModel.getGroups()){
			if(group.getId().equals(backupGroup.getId())){
				return group;
			}
		}
		
		return null;
	}
	
	public HyFeature getRealModelFeature(HyFeature backupFeature){
		HyFeatureModel featureModel = viewer.getInternalFeatureModel();
		
		for(HyFeature feature : featureModel.getFeatures()){
			if(feature.getId().equals(backupFeature.getId())){
				return feature;
			}
		}
		
		return null;
	}	
	
	public HyGroupComposition getRealModelGroupComposition(HyGroup group, HyGroupComposition backupGroupComposition){
		for(HyGroupComposition composition : group.getParentOf()){
			if(composition.getId().equals(backupGroupComposition.getId())){
				return composition;
			}
		}
		
		return null;
	}	
	
	public void redo(){
		if(!groupMemberships.isEmpty())
			groupMemberships.clear();
		
		group = null;
		
		redoConnections();
		
		int index = viewer.getModelWrapped().getFeatures(null).indexOf(this.feature);
		
		HyFeature feature = this.feature.getWrappedModelElement();
		
		// save a copy to the feature with dummy references to all related elements
		oldFeature = DwEcoreUtil.copy(feature);
		
		HyFeatureModel featureModel = viewer.getInternalFeatureModel();
		featureModel.getFeatures().remove(feature);

		EList<HyGroupComposition> compositions = feature.getGroupMembership();
		for(HyGroupComposition composition : compositions){

			HyGroupComposition backupComposition = DwEcoreUtil.copy(composition);
			oldFeature.getGroupMembership().add(backupComposition);
			EList<HyFeature> features = composition.getFeatures();

			// if the group has only one feature delete the group
			if(features.size() == 1){
				HyGroup realGroup = composition.getCompositionOf();
				group = DwEcoreUtil.copy(realGroup);
				
				// save all feature child references of this group
				EList<HyFeatureChild> featureChildren = realGroup.getChildOf();
				for(HyFeatureChild featureChild : featureChildren){
					
					HyFeatureChild featureChildBackup = DwEcoreUtil.copy(featureChild);
					this.featureChildren.add(featureChildBackup);

					HyFeature parent = DwEcoreUtil.copy(featureChild.getParent());

					featureChildBackup.setParent(parent);
					featureChildBackup.setChildGroup(this.group);
					this.group.getChildOf().add(featureChildBackup);
					
					// remove the feature child from parent model feature
					featureChild.getParent().getParentOf().remove(featureChild);
				}

				viewer.getModelWrapped().removeGroup(viewer.getModelWrapped().findWrappedGroup(realGroup));
			}else{
				this.group = DwEcoreUtil.copy(composition.getCompositionOf());
			}
			
			// delete all parent elements of the group
			if(this.group != null)
				this.group.getParentOf().clear();
			
			backupComposition.setCompositionOf(this.group);			
			groupMemberships.add(backupComposition);
		}
		
		feature.getGroupMembership().clear();

		for(HyGroupComposition composition : groupMemberships){
			composition.getFeatures().add(oldFeature);
		}
		
		// remove feature and update feature model view
		viewer.getModelWrapped().removeFeature(index);
		
		updateFeatureModel();
	}


	public void undo(){
		HyFeatureModel featureModel = viewer.getInternalFeatureModel();
		
		undoConnections();

		// check if feature already exist in feature model
		boolean exists = this.getRealModelFeature(feature.getWrappedModelElement()) != null;
		
		feature.setWrappedModelElement(oldFeature);
		if(!exists)
			viewer.getModelWrapped().addFeature(feature);
		
		int size = oldFeature.getGroupMembership().size();
		for(int i=0; i<size; i++){
			HyGroupComposition backupComposition = oldFeature.getGroupMembership().get(i);
		
			HyGroup backupGroup = backupComposition.getCompositionOf();
			HyGroup group = getRealModelGroup(backupGroup);
			
			if(group == null){
				featureModel.getGroups().add(backupGroup);
				
				for(int j=0; j<backupGroup.getChildOf().size(); j++){
					HyFeatureChild featureChild = backupGroup.getChildOf().get(i);
					HyFeature feature = getRealModelFeature(featureChild.getParent());
					
					if(feature != null){
						feature.getParentOf().add(featureChild);
					}
					
					viewer.getModelWrapped().addGroup(new DwGroupWrapped(backupGroup));
				}
			}else{
				HyGroupComposition composition = getRealModelGroupComposition(group, backupComposition);
				
				if(composition != null){
					oldFeature.getGroupMembership().set(i, composition);
					
					oldFeature.getGroupMembership().add(composition);
					composition.getFeatures().add(oldFeature);
				}else
					System.err.println("GroupComposition is not part of the feature model...");
			}
		}
	
		updateFeatureModel();
	}

	@Override
	public void execute(){
		redo();
	}
}