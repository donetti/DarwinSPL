package eu.hyvar.feature.graphical.base.editparts;


import java.util.Date;

import org.deltaecore.feature.graphical.base.editor.DEGraphicalEditor;
import org.deltaecore.feature.graphical.base.util.DEGraphicalEditorTheme;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import eu.hyvar.feature.HyVersion;
import eu.hyvar.feature.graphical.base.deltaecore.wrapper.layouter.version.HyVersionLayouterManager;
import eu.hyvar.feature.graphical.base.deltaecore.wrapper.layouter.version.HyVersionTreeLayouter;
import eu.hyvar.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import eu.hyvar.feature.graphical.base.figures.HyVersionFigure;
import eu.hyvar.feature.graphical.base.model.HyFeatureModelWrapped;

public class HyVersionEditPart extends HyAbstractEditPart{

	public HyVersionEditPart(DwGraphicalFeatureModelViewer editor, HyFeatureModelWrapped featureModel) {
		super(editor, featureModel);
	}

	@Override
	protected IFigure createFigure() {
		return new HyVersionFigure((HyVersion)getModel());
	}

	@Override
	protected void createEditPolicies() {
	}

	protected Rectangle getFigureConstraint(){
		DEGraphicalEditorTheme theme = DEGraphicalEditor.getTheme();
		Date date = ((DwGraphicalFeatureModelViewer)editor).getCurrentSelectedDate();

		HyVersion model = (HyVersion)getModel();

		int offset = 0;
		if(this.getSelected() == SELECTED_PRIMARY){
			offset = theme.getLineWidth();
		}


		HyVersionTreeLayouter versionTreeLayouter = HyVersionLayouterManager.getLayouter(model.getFeature(), date);
		if(versionTreeLayouter != null){
			Rectangle bounds = versionTreeLayouter.getBounds(model);
			bounds.setX(bounds.x - offset);
			bounds.setY(bounds.y - offset);
			bounds.setWidth(bounds.width+offset*2);
			bounds.setHeight(bounds.height+offset*2);

			return bounds;	
		}

		return new Rectangle(0, 0, 0, 0);
	}
	
	@Override
	public void refreshVisuals(){
		super.refreshVisuals();

		((HyVersionFigure)getFigure()).update();
	}
}