package de.darwinspl.feature.graphical.editor.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

import de.darwinspl.feature.graphical.editor.actions.DwLinearTemporalElementChangeValidityAction;
import de.darwinspl.feature.graphical.editor.actions.attribute.DwAttributeCreateBooleanAction;
import de.darwinspl.feature.graphical.editor.actions.attribute.DwAttributeCreateEnumAction;
import de.darwinspl.feature.graphical.editor.actions.attribute.DwAttributeCreateNumberAction;
import de.darwinspl.feature.graphical.editor.actions.attribute.DwAttributeCreateStringAction;
import de.darwinspl.feature.graphical.editor.actions.attribute.DwAttributeRenameAction;
import de.darwinspl.feature.graphical.editor.actions.attribute.DwNumberAttributeSetNumberRangeAction;
import de.darwinspl.feature.graphical.editor.actions.enumeration.DwFeatureAttributeEnumCreateEnumAction;
import de.darwinspl.feature.graphical.editor.actions.enumeration.DwFeatureAttributeEnumCreateLiteralAction;
import de.darwinspl.feature.graphical.editor.actions.feature.DwFeatureChangeTypeAction;
import de.darwinspl.feature.graphical.editor.actions.feature.DwFeatureCreateChildAction;
import de.darwinspl.feature.graphical.editor.actions.feature.DwFeatureCreateSiblingAction;
import de.darwinspl.feature.graphical.editor.actions.feature.DwFeatureDeletePermanentlyAction;
import de.darwinspl.feature.graphical.editor.actions.feature.DwFeatureEditNamesAction;
import de.darwinspl.feature.graphical.editor.actions.feature.DwSetFeatureLinkAction;
import de.darwinspl.feature.graphical.editor.actions.group.DwGroupChangeGroupTypeToAlternativeTypeAction;
import de.darwinspl.feature.graphical.editor.actions.group.DwGroupChangeGroupTypeToAndTypeAction;
import de.darwinspl.feature.graphical.editor.actions.group.DwGroupChangeGroupTypeToOrTypeAction;
import de.darwinspl.feature.graphical.editor.actions.version.DwVersionCreateSuccessorAction;
import de.darwinspl.feature.graphical.editor.actions.version.DwVersionCreateVersionAction;

public class DwGraphicalFeatureModelEditorContextMenuProvider extends ContextMenuProvider	{
	private ActionRegistry actionRegistry;

	public DwGraphicalFeatureModelEditorContextMenuProvider(EditPartViewer viewer, final ActionRegistry actionRegistry) {
		super(viewer);

		setActionRegistry(actionRegistry);
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);
		
        IAction action;
        
        // Basic operations: undo, redo, delete
        action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
        action = getActionRegistry().getAction(ActionFactory.REDO.getId());
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
        action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
        
        action = getActionRegistry().getAction(DwFeatureDeletePermanentlyAction.FEATURE_DELETE_PERMANENTLY);
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
        
 
        // Feature actions: create child, create sibling
        action = getActionRegistry().getAction(DwFeatureCreateChildAction.FEATURE_CREATE_CHILD);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        action = getActionRegistry().getAction(DwFeatureCreateSiblingAction.FEATURE_CREATE_SIBLING);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);       
        action = getActionRegistry().getAction(DwFeatureEditNamesAction.FEATURE_EDIT_NAMES);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);      
        action = getActionRegistry().getAction(DwFeatureChangeTypeAction.FEATURE_CHANGE_TYPE);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);      
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());
        
        

        action = getActionRegistry().getAction(DwGroupChangeGroupTypeToAlternativeTypeAction.CHANGE_GROUP_TYPE_TO_ALTERNATIVE);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        action = getActionRegistry().getAction(DwGroupChangeGroupTypeToAndTypeAction.CHANGE_GROUP_TYPE_TO_AND);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        action = getActionRegistry().getAction(DwGroupChangeGroupTypeToOrTypeAction.CHANGE_GROUP_TYPE_TO_OR);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());
        
        action = getActionRegistry().getAction(DwLinearTemporalElementChangeValidityAction.FEATURE_CHANGE_VALIDITY);
        
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());
        action = getActionRegistry().getAction(DwVersionCreateVersionAction.FEATURE_ADD_VERSION);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        action = getActionRegistry().getAction(DwVersionCreateSuccessorAction.FEATURE_CREATE_SUCCESSOR_VERSION);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);         
        
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());
        action = getActionRegistry().getAction(DwAttributeCreateNumberAction.FEATURE_ADD_NUMBER_ATTRIBUTE);     
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        action = getActionRegistry().getAction(DwAttributeCreateBooleanAction.FEATURE_ADD_BOOLEAN_ATTRIBUTE);     
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        action = getActionRegistry().getAction(DwAttributeCreateStringAction.FEATURE_ADD_STRING_ATTRIBUTE);     
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);      
        action = getActionRegistry().getAction(DwAttributeCreateEnumAction.FEATURE_ADD_ENUM_ATTRIBUTE);     
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);    


        action = getActionRegistry().getAction(DwAttributeRenameAction.ATTRIBUTE_EDIT_NAMES);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        
        action = getActionRegistry().getAction(DwNumberAttributeSetNumberRangeAction.ATTRIBUTE_EDIT_MIN_AND_MAX);     
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);    
        
        action = getActionRegistry().getAction(DwFeatureAttributeEnumCreateLiteralAction.ATTRIBUTE_CREATE_LITERAL);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        
        action = getActionRegistry().getAction(DwFeatureAttributeEnumCreateEnumAction.ATTRIBUTE_CREATE_ENUM);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());
        action = getActionRegistry().getAction(DwLinearTemporalElementChangeValidityAction.FEATURE_CHANGE_VALIDITY);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());
        action = getActionRegistry().getAction(DwSetFeatureLinkAction.ID);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
	}
	
	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}
	public void setActionRegistry(ActionRegistry actionRegistry) {
		this.actionRegistry = actionRegistry;
	}

}
