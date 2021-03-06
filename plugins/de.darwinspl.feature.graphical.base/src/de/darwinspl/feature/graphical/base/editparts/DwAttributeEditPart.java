package de.darwinspl.feature.graphical.base.editparts;

import java.util.Date;

import org.deltaecore.feature.graphical.base.editor.DEGraphicalEditor;
import org.deltaecore.feature.graphical.base.util.DEGraphicalEditorTheme;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.base.figures.DwAttributeFigure;
import de.darwinspl.feature.graphical.base.model.DwFeatureModelWrapped;
import de.darwinspl.feature.graphical.base.model.DwFeatureWrapped;
import de.darwinspl.feature.graphical.base.model.DwTemporalPosition;
import eu.hyvar.evolution.util.HyEvolutionUtil;
import eu.hyvar.evolution.HyName;
import eu.hyvar.feature.HyFeatureAttribute;

public class DwAttributeEditPart extends DwAbstractEditPart{
	private DwAttributeAdapter adapter = new DwAttributeAdapter();
	
	public class DwAttributeAdapter implements Adapter {

		@Override 
		public void notifyChanged(Notification notification) {				
			//refreshChildren();
			//refreshVisuals();
		}

		@Override 
		public Notifier getTarget() {
			return (HyFeatureAttribute)getModel();
		}

		@Override public void setTarget(Notifier newTarget) {
			// Do nothing.
		}

		@Override public boolean isAdapterForType(Object type) {
			return type.equals(HyFeatureAttribute.class);
		}
	} 
	
	public DwAttributeEditPart(DwGraphicalFeatureModelViewer editor, DwFeatureModelWrapped featureModel) {
		super(editor, featureModel);
	}

	@Override
	protected IFigure createFigure() {
		return new DwAttributeFigure(editor, (HyFeatureAttribute)getModel());
	}
	

	@Override 
	public void activate() {
		if(!isActive()) {
			HyFeatureAttribute model = ((HyFeatureAttribute)getModel());
			model.eAdapters().add(adapter);
		}

		super.activate();
	}

	@Override 
	public void deactivate() {
		if(isActive()) {
			HyFeatureAttribute model = ((HyFeatureAttribute)getModel());
			
			model.eAdapters().remove(adapter);
		}
		super.deactivate();
	}

	@Override
	protected void createEditPolicies() {
	}
	
	
	protected Rectangle getFigureConstraint(){
		DEGraphicalEditorTheme theme = DEGraphicalEditor.getTheme();
		int lineWidth = theme.getLineWidth();
		
		DwFeatureEditPart parent = (DwFeatureEditPart)getParent();
		
		
		if(parent == null) 
			return new Rectangle(0, 0, 0, 0);
		
		DwFeatureWrapped feature = (DwFeatureWrapped)parent.getModel();
		Date date = featureModel.getSelectedDate();
		
		HyFeatureAttribute attribute = (HyFeatureAttribute)getModel();
		int index = HyEvolutionUtil.getValidTemporalElements(attribute.getFeature().getAttributes(), date).indexOf(attribute);
		
		DwTemporalPosition parentPosition = feature.getPosition(date);
		int featurePositionY = feature.calculateVersionAreaBounds(date).getBottomLeft().y - parentPosition.getPosition().y + (int)Math.floor(theme.getLineWidth() * 1.5);
		int attributeHeight = theme.getFeatureNameAreaHeight();
		
		
		Rectangle layout = new Rectangle(new Point(0, featurePositionY + index * attributeHeight), new Dimension(feature.getSize(date).width, attributeHeight));
		layout = layout.shrink(new Insets(0, (int)Math.floor(lineWidth * 2.5), 0, (int)Math.floor(lineWidth * 2.5)));
		
		return layout;
	}
	
	@Override
	public void refreshVisuals(){
		super.refreshVisuals();
		
		DwAttributeFigure figure = (DwAttributeFigure)getFigure();
		HyFeatureAttribute attribute = (HyFeatureAttribute)getModel();
		
		Date date = featureModel.getSelectedDate();
		
		HyName name = HyEvolutionUtil.getValidTemporalElement(attribute.getNames(), date);
		if(name != null){
			figure.setName(name.getName());
		}else{
			// TODO show error that no name for the attribute exist at this timestamp
		}
		
		((DwAttributeFigure)getFigure()).update();
	
	}
}