package de.darwinspl.feature.graphical.editor.policies.enums;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.base.editparts.DwEnumEditPart;
import de.darwinspl.feature.graphical.base.figures.DwEnumFigure;
import de.darwinspl.feature.graphical.editor.commands.enumeration.DwEnumRenameCommand;
import eu.hyvar.dataValues.HyEnum;

public class DwEnumDirectEditPolicy extends DirectEditPolicy {

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		DwEnumEditPart editPart = (DwEnumEditPart)getHost();
		DwEnumRenameCommand command = new DwEnumRenameCommand((HyEnum)editPart.getModel(), (DwGraphicalFeatureModelViewer)editPart.getParent());
		command.setNewName((String)request.getCellEditor().getValue());
		
		return command;
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		String value = (String) request.getCellEditor().getValue();
		DwEnumFigure figure = (DwEnumFigure)getHostFigure();
		figure.getLabel().setText(value);
	}
}
