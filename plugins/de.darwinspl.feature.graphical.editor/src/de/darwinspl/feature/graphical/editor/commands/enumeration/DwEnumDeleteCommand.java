package de.darwinspl.feature.graphical.editor.commands.enumeration;

import java.util.Date;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;

import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import eu.hyvar.dataValues.HyEnum;

public class DwEnumDeleteCommand extends Command{
	private class EnumTemporalDeleteNotification extends NotificationImpl{

		public EnumTemporalDeleteNotification(int eventType, Object oldValue, Object newValue) {
			super(eventType, oldValue, newValue);
		}

	
	}
	private DwGraphicalFeatureModelViewer editor;
	private HyEnum enumeration;
	
	public DwGraphicalFeatureModelViewer getEditor() {
		return editor;
	}

	public void setEditor(DwGraphicalFeatureModelViewer editor) {
		this.editor = editor;
	}

	public HyEnum getEnumeration() {
		return enumeration;
	}

	public void setEnumeration(HyEnum enumeration) {
		this.enumeration = enumeration;
	}
		
	public DwEnumDeleteCommand(DwGraphicalFeatureModelViewer editor) {
		this.editor = editor;
	}
	
	@Override
	public boolean canExecute() {
		return editor.isLastDateSelected();
	}

	
	@Override
	public void execute(){
		Date date = editor.getCurrentSelectedDate();
		enumeration.setValidUntil(date);
		
		for(Adapter adapter : enumeration.eAdapters())
			adapter.notifyChanged(new EnumTemporalDeleteNotification(0, date, date));
		
		editor.getModelWrapped().rearrangeFeatures();
		editor.refreshView();
	}
}
