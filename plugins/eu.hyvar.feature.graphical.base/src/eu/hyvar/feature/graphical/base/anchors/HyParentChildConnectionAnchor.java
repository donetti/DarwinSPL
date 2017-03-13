package eu.hyvar.feature.graphical.base.anchors;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import eu.hyvar.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;


public class HyParentChildConnectionAnchor extends HyFeatureModelPositionAnchor {

	public HyParentChildConnectionAnchor(IFigure owner, DwGraphicalFeatureModelViewer editor) {
		super(owner, editor);
	}

	@Override
	public Point getLocation(Point reference) {
		Point position = super.getLocation(reference);
		Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getOwner().getBounds());
		
		position.x = r.x + r.width() / 2;
		position.y = r.y + r.height() / 2;
		
		getOwner().translateToAbsolute(position);
		
		return position;
	}	
	
}