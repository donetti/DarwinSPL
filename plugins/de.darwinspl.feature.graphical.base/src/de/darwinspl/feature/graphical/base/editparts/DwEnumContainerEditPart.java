package de.darwinspl.feature.graphical.base.editparts;

import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;

import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.base.figures.DwEnumContainerFigure;
import de.darwinspl.feature.graphical.base.model.DwEnumContainerWrapped;
import de.darwinspl.feature.graphical.base.model.DwFeatureModelWrapped;
import de.darwinspl.feature.graphical.base.model.DwTemporalPosition;
import de.darwinspl.feature.graphical.base.model.DwEnumContainerWrapped.EditorAlignment;
import eu.hyvar.dataValues.HyEnum;
import eu.hyvar.evolution.util.HyEvolutionUtil;
import eu.hyvar.feature.HyFeature;

public class DwEnumContainerEditPart extends DwAbstractEditPart{
	private DwEnumContainerAdapter adapter;
		
	private class DwEnumContainerAdapter implements Adapter {

		@Override 
		public void notifyChanged(Notification notification) {
			refreshChildren();
			refreshVisuals();
		}

		@Override 
		public Notifier getTarget() {
			return featureModel.getModel();
		}

		@Override public void setTarget(Notifier newTarget) {
			// Do nothing.
		}

		@Override public boolean isAdapterForType(Object type) {
			return type.equals(HyFeature.class);
		}
	} 
	
	
	public DwEnumContainerEditPart(DwGraphicalFeatureModelViewer viewer, DwFeatureModelWrapped featureModel) {
		super(viewer, featureModel);
		
		adapter = new DwEnumContainerAdapter();
	}

	@Override
	protected IFigure createFigure() {		
		return new DwEnumContainerFigure(editor);
	}

	@Override
	protected void createEditPolicies() {}
	
	
	

	@Override 
	public void activate() {
		if(!isActive()) {
			featureModel.getModel().eAdapters().add(adapter);
		}
		
		super.activate();
	}

	@Override 
	public void deactivate() {
		if(isActive()) {
			featureModel.getModel().eAdapters().remove(adapter);
		}
		super.deactivate();
	}

	
	@Override 
	protected List<HyEnum> getModelChildren() {
		return featureModel.getModel().getEnums();
	}

	@Override
	protected void addChild(EditPart child, int index){
		if(child instanceof DwEnumEditPart){
			((DwEnumEditPart)child).addPropertyChangeListener(this);
		}
		
		super.addChild(child, index);
	}
	
	@Override 
	protected void removeChild(EditPart child) {
		if(child instanceof DwEnumEditPart){
			((DwEnumEditPart)child).removePropertyChangeListener(this);
		}
		
		super.removeChild(child);
	};

	
	@Override 
	public void refresh(){
		this.refreshChildren();
		refreshVisuals();
	}

	protected Rectangle getFigureConstraint(){
		GraphicalEditPart parent = (GraphicalEditPart)getParent();
		Rectangle parentVisibleBounds = parent.getFigure().getClientArea().getCopy();
		
		Rectangle bounds;
		if(parentVisibleBounds.width < 0 || parentVisibleBounds.height < 0) {
			bounds = new Rectangle(0, 0, 300, -1);
		}else{
			Date date = featureModel.getSelectedDate();
			
			DwEnumContainerWrapped model = (DwEnumContainerWrapped)getModel();
			DwTemporalPosition position = model.getPosition(date);
			
			
			Point desiredDefaultPosition = new Point(editor.getEditorGraphicalDimension().width-20-320, 20);
			if(position == null || !position.getPosition().equals(desiredDefaultPosition) && model.getAlignment() == EditorAlignment.RightTop){
				model.addPosition(desiredDefaultPosition, date, false);
			}
		
			bounds = new Rectangle(model.getPosition(date).getPosition(), new Dimension(300, -1));
		}	
		
		return bounds;
	}	
	
	@Override
	public void refreshVisuals(){	
		GraphicalEditPart parent = (GraphicalEditPart)getParent();
		if(parent != null){
			int validEnums = HyEvolutionUtil.getValidTemporalElements(featureModel.getModel().getEnums(), featureModel.getSelectedDate()).size();
			
			figure.setVisible(validEnums != 0);
			
			parent.setLayoutConstraint(this, figure, getFigureConstraint());
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals();
	}
}
