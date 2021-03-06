package de.darwinspl.feature.graphical.editor.util;

import org.eclipse.emf.ecore.util.EcoreUtil;

import de.darwinspl.feature.graphical.base.model.DwParentChildConnection;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyFeatureAttribute;
import eu.hyvar.feature.HyFeatureChild;
import eu.hyvar.feature.HyGroup;
import eu.hyvar.feature.HyGroupComposition;
import eu.hyvar.feature.HyGroupType;
import eu.hyvar.feature.HyVersion;

public class DwEcoreUtil {
	public static HyFeature copy(HyFeature o){
		HyFeature result = (HyFeature)EcoreUtil.copy(o);
		result.setId(o.getId());
		
		for(int i=0; i<o.getNames().size(); i++){
			result.getNames().get(i).setId(o.getNames().get(i).getId());
		}
		
		for(int i=0; i<o.getTypes().size(); i++){
			result.getTypes().get(i).setId(o.getTypes().get(i).getId());
		}	
		
		for(HyGroupComposition composition : o.getGroupMembership()) {
			result.getGroupMembership().add(DwEcoreUtil.copy(composition));
		}
		
		/*
		for(HyFeatureChild child : o.getParentOf()) {
			result.getParentOf().add(DwEcoreUtil.copy(child));
		}
		*/
		
		return result;	
	}
	
	public static HyFeatureChild copy(HyFeatureChild o){
		HyFeatureChild result = (HyFeatureChild)EcoreUtil.copy(o);
		result.setId(o.getId());
		
		result.setParent(DwEcoreUtil.copy((o.getParent())));
		
		return result;	
	}	
	
	public static HyGroupComposition copy(HyGroupComposition o){
		HyGroupComposition result = (HyGroupComposition)EcoreUtil.copy(o);
		
		result.setCompositionOf(DwEcoreUtil.copy(o.getCompositionOf()));
		
		result.setId(o.getId());
		
		return result;	
	}
	
	public static HyGroup copy(HyGroup o){
		HyGroup result = (HyGroup)EcoreUtil.copy(o);
		result.setId(o.getId());
		result.getParentOf().clear();
		
		for(int i=0; i<o.getTypes().size(); i++){
			result.getTypes().get(i).setId(o.getTypes().get(i).getId());
		}	
		
		for(HyFeatureChild child : o.getChildOf()) {
			result.getChildOf().add(DwEcoreUtil.copy(child));
		}
		
		//for(HyGroupComposition composition : o.getParentOf()) {
		//	result.getParentOf().add(DwEcoreUtil.copy(composition));
		//}
		
		return result;	
	}
	
	public static HyFeatureAttribute copy(HyFeatureAttribute o){
		HyFeatureAttribute result = (HyFeatureAttribute)EcoreUtil.copy(o);
		result.setId(o.getId());
		
		for(int i=0; i<o.getNames().size(); i++){
			result.getNames().get(i).setId(o.getNames().get(i).getId());
		}
		
		result.setFeature(DwEcoreUtil.copy(o.getFeature()));
		
		return result;
	}
	
	public static HyVersion copy(HyVersion o){
		HyVersion result = (HyVersion)EcoreUtil.copy(o);
		result.setId(o.getId());
		
		return result;
	}
	
	public static DwParentChildConnection copy(DwParentChildConnection o){
		DwParentChildConnection result = new DwParentChildConnection();
		result.setValidSince(o.getValidSince());
		result.setValidUntil(o.getValidUntil());
		result.setModel(o.getModel());
		result.setSource(o.getSource());
		result.setTarget(o.getTarget());
		result.setId(o.getId());

		return result;
	}
	
	public static HyGroupType copy(HyGroupType o) {
		HyGroupType result = (HyGroupType)EcoreUtil.copy(o);
		result.setId(o.getId());
		
		result.setSupersededElement(o.getSupersededElement());
		result.setSupersedingElement(o.getSupersedingElement());
		
		return result;
	}
}
