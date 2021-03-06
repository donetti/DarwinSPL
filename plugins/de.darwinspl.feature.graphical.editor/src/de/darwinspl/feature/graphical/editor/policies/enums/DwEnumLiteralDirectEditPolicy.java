package de.darwinspl.feature.graphical.editor.policies.enums;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.base.editparts.DwEnumLiteralEditPart;
import de.darwinspl.feature.graphical.base.figures.DwEnumLiteralFigure;
import de.darwinspl.feature.graphical.editor.commands.enumeration.DwEnumLiteralRenameCommand;
import eu.hyvar.dataValues.HyEnumLiteral;

public class DwEnumLiteralDirectEditPolicy extends DirectEditPolicy {

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		DwEnumLiteralEditPart editPart = (DwEnumLiteralEditPart)getHost();
		DwEnumLiteralRenameCommand command = new DwEnumLiteralRenameCommand((HyEnumLiteral)editPart.getModel(), (DwGraphicalFeatureModelViewer)editPart.getParent());
		command.setNewName((String)request.getCellEditor().getValue());
		
		return command;
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		String value = (String) request.getCellEditor().getValue();
		DwEnumLiteralFigure figure = (DwEnumLiteralFigure)getHostFigure();
		figure.getLabel().setText(value);
	}
}
