package de.darwinspl.feature.graphical.editor.commands.feature;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.commands.Command;

import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.editor.util.DwElementEditorUtil;
import eu.hyvar.evolution.util.HyEvolutionUtil;
import eu.hyvar.evolution.HyLinearTemporalElement;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyFeatureFactory;
import eu.hyvar.feature.HyFeatureType;
import eu.hyvar.feature.HyFeatureTypeEnum;

public class DwFeatureChangeTypeCommand extends Command{
	private HyFeature feature;
	private HyFeatureTypeEnum newGroupTypeEnum;
	private DwGraphicalFeatureModelViewer editor;
	
	HyFeatureType newType;
	HyFeatureType oldType;
	HyFeatureType changedType;
	
	List<HyFeatureType> oldTypes;
	
	public DwFeatureChangeTypeCommand(HyFeature feature, HyFeatureTypeEnum newFeatureTypeEnum, DwGraphicalFeatureModelViewer editor){
		this.feature = feature;
		this.newGroupTypeEnum = newFeatureTypeEnum;
		this.editor = editor;
	}
	
	@Override
	public void execute(){
		redo();
	}
	
	@Override
	public void undo() {	
		feature.getTypes().clear();
		feature.getTypes().addAll(oldTypes);
	}
	
	private List<HyFeatureType> backupTypes(){
		List<HyFeatureType> types = new ArrayList<HyFeatureType>();
		
		for(HyFeatureType type : feature.getTypes()){
			types.add(EcoreUtil.copy(type));
		}
		
		return types;
	}

	@Override
	public void redo() {
		oldTypes = backupTypes();
		
		
		Date date = editor.getCurrentSelectedDate();
		if(date.equals(new Date(Long.MIN_VALUE)))
			date = null;
		
		HyFeatureType type = HyEvolutionUtil.getValidTemporalElement(feature.getTypes(), date);
		changedType = type;
		oldType = EcoreUtil.copy(type);
		newType = HyFeatureFactory.eINSTANCE.createHyFeatureType();
		newType.setType(newGroupTypeEnum);
		feature.getTypes().add(newType);
			
		if(date == null){
			feature.getTypes().remove(type);
		}else{
			newType.setValidSince(date);
			type.setValidUntil(date);
			
			HyLinearTemporalElement successor = type.getSupersedingElement();
			

			if(successor != null){
				newType.setSupersedingElement(successor);
				successor.setSupersededElement(newType);
			}
			
			type.setSupersedingElement(newType);
			newType.setSupersededElement(type);
		}
		
		
		DwElementEditorUtil.cleanFeatureTypes(feature);
	}
	
	@Override
	public boolean canExecute() {
		return editor.isLastDateSelected();
	}
}