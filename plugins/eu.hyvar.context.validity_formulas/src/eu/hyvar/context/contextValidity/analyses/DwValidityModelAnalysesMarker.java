package eu.hyvar.context.contextValidity.analyses;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import eu.hyvar.feature.expression.HyExpression;

public class DwValidityModelAnalysesMarker {

	private List<HyExpression> affectedExpressions;
	private List<EObject> affectedObjects;
	private String message;
	private MarkerTypeEnum markerType;
	
	public enum MarkerTypeEnum {
		WARNING, ERROR
	}
	
	public DwValidityModelAnalysesMarker(List<HyExpression> affectedExpressions, List<EObject> affectedObjects, String message, MarkerTypeEnum markerType) {
		this.affectedObjects = affectedObjects;
		this.affectedExpressions = affectedExpressions;
		this.message = message;
		this.markerType = markerType;
	}
	
	public List<HyExpression> getAffectedExpressions() {
		return affectedExpressions;
	}

	public String getMessage() {
		return message;
	}

	public MarkerTypeEnum getMarkerType() {
		return markerType;
	}

	public List<EObject> getAffectedObjects() {
		return affectedObjects;
	}
	
}
