package eu.hyvar.feature.graphical.editor.actions.attribute;

import java.util.Date;

import org.eclipse.gef.Request;

import eu.hyvar.evolution.HyEvolutionFactory;
import eu.hyvar.evolution.HyName;
import eu.hyvar.feature.HyFeatureAttribute;
import eu.hyvar.feature.HyFeatureFactory;
import eu.hyvar.feature.HyNumberAttribute;
import eu.hyvar.feature.graphical.editor.editor.HyGraphicalFeatureModelEditor;

public class HyAttributeCreateNumberAction extends HyAttributeCreateAction{
	public static final String FEATURE_ADD_NUMBER_ATTRIBUTE = "AddFeatureNumberAttribute";
	public static final String REQ_FEATURE_ADD_NUMBER_ATTRIBUTE = "AddFeatureNumberAttribute";

	Request request;
	
	public HyAttributeCreateNumberAction(HyGraphicalFeatureModelEditor editor) {
		super(editor);

		setId(FEATURE_ADD_NUMBER_ATTRIBUTE);
		setText("Create Number Attribute");

		request = new Request(REQ_FEATURE_ADD_NUMBER_ATTRIBUTE);
	}

	@Override
	protected HyFeatureAttribute createNewAttribute(){
		Date date = editor.getCurrentSelectedDate();
		if(date.equals(new Date(Long.MIN_VALUE)))
			date = null;
		
		HyNumberAttribute attribute = HyFeatureFactory.eINSTANCE.createHyNumberAttribute();
		attribute.setValidSince(editor.getCurrentSelectedDate());
		HyName name = HyEvolutionFactory.eINSTANCE.createHyName();
		name.setName("New Number Attribute");
		name.setValidSince(date);
		attribute.getNames().add(name);
				
		return attribute;
	}
}
