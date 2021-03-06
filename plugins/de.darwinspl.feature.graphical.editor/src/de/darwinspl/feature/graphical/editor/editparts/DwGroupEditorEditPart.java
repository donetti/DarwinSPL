package de.darwinspl.feature.graphical.editor.editparts;

import java.beans.PropertyChangeEvent;
import java.util.Date;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.gef.EditPolicy;

import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.base.editparts.DwGroupEditPart;
import de.darwinspl.feature.graphical.base.model.DwFeatureModelWrapped;
import de.darwinspl.feature.graphical.base.model.DwFeatureWrapped;
import de.darwinspl.feature.graphical.base.model.DwGroupWrapped;
import de.darwinspl.feature.graphical.base.model.DwRepaintNotification;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyGroup;
import eu.hyvar.feature.HyGroupType;

public class DwGroupEditorEditPart extends DwGroupEditPart {
	public static String GROUP_MODEL_CHANGED = "GROUP_MODEL_CHANGED";
	public class HyGroupAdapter implements Adapter {

		@Override 
		public void notifyChanged(Notification notification) {
			if(notification instanceof ENotificationImpl) {
				ENotificationImpl enotification = (ENotificationImpl)notification;
				if(enotification.getEventType() == Notification.ADD && enotification.getNewValue() instanceof HyGroupType) {
					refreshVisuals();
				}
				if(enotification.getEventType() == Notification.REMOVE && enotification.getOldValue() instanceof HyGroupType) {
					refreshVisuals();
				}
			}		
			
			if(notification.getEventType() != Notification.REMOVING_ADAPTER){
				DwGroupWrapped groupWrapped = (DwGroupWrapped)getModel();
				
				switch(notification.getEventType()){
				case Notification.REMOVE:
					// check if the group has no compositions left
					if(groupWrapped.getWrappedModelElement().getParentOf().isEmpty()){
						featureModel.removeGroup((DwGroupWrapped)getModel());
					}else{

					}

					// remove feature from the child list of the group
					if(notification.getNotifier() instanceof HyFeature){
						DwFeatureWrapped removedFeatureWrapped = featureModel.findWrappedFeature((HyFeature)notification.getNotifier());
						groupWrapped.getFeatures().remove(removedFeatureWrapped);
					}	
					
					refreshVisuals(); 
					
					break;
				case Notification.SET:
					// group was deleted from feature model
					if(notification.getPosition() == -1){
						
					}
					break;
				}
	

				
				
			}
		}

		@Override 
		public Notifier getTarget() {
			return (HyGroup)((DwGroupWrapped)getModel()).getWrappedModelElement();
		}

		@Override 
		public void setTarget(Notifier newTarget) {
		}

		@Override 
		public boolean isAdapterForType(Object type) {
			return type.equals(HyGroup.class);
		}
	} 
	
	HyGroupAdapter adapter = new HyGroupAdapter();
	
	public DwGroupEditorEditPart(DwGraphicalFeatureModelViewer editor, DwFeatureModelWrapped featureModel) {
		super(editor, featureModel);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Date date = editor.getCurrentSelectedDate();
		
		if(evt.getPropertyName() == DwGroupWrapped.PROPERTY_CHILD_FEATURES){
			DwGroupWrapped model = (DwGroupWrapped)getModel();
			EList<HyFeature> features = model.getFeatures(date);
			if(features.size() == 0){
				model.getWrappedModelElement().getParentOf().remove(model.getComposition(date));
			}
		}
		refreshVisuals();
	}

	
	@Override 
	public void activate() {
		if(!isActive()) {
			DwGroupWrapped model = ((DwGroupWrapped)getModel());
			
			DwFeatureWrapped parentFeature = featureModel.getParentFeatureForGroup((DwGroupWrapped)getModel(), featureModel.getSelectedDate());
			
			if(parentFeature != null)
				parentFeature.addPropertyChangeListener(this);
			
			for(DwFeatureWrapped child : model.getFeatures()){
				child.addPropertyChangeListener(this);
			}
			
			model.addPropertyChangeListener(this);
			model.getWrappedModelElement().eAdapters().add(adapter);
		}
		super.activate();
	}

	@Override 
	public void deactivate() {
		if(isActive()) {
			DwGroupWrapped model = ((DwGroupWrapped)getModel());
			//HyFeatureWrapped parentFeature = featureModel.getParentFeatureForGroup((HyGroupWrapped)getModel(), featureModel.getSelectedDate());
			
			//parentFeature.removePropertyChangeListener(this);
			for(DwFeatureWrapped child : model.getFeatures()){
				child.removePropertyChangeListener(this);
			}
			
			model.removePropertyChangeListener(this);
			model.getWrappedModelElement().eAdapters().remove(adapter);
		}
		super.deactivate();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.NODE_ROLE, null);

	}
	
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		
		DwGroupWrapped model = (DwGroupWrapped)getModel();
		for(HyFeature feature : model.getFeatures(editor.getCurrentSelectedDate())) {
			feature.eNotify(new DwRepaintNotification((InternalEObject)model.getWrappedModelElement(), -1, model.getWrappedModelElement().eContainingFeature(), false, true));
		}
	}
}
