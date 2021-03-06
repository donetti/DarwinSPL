/**
 * 
 */
package de.darwinspl.feature.evolution.complex.operations;

import java.util.Date;

import de.darwinspl.feature.evolution.atomic.operations.DeleteFeatureChild;
import de.darwinspl.feature.evolution.atomic.operations.DeleteGroup;
import de.darwinspl.feature.evolution.atomic.operations.DeleteGroupComposition;
import de.darwinspl.feature.evolution.atomic.operations.DeleteGroupType;
import de.darwinspl.feature.evolution.invoker.EvolutionOperation;
import eu.hyvar.feature.HyFeatureChild;
import eu.hyvar.feature.HyGroup;
import eu.hyvar.feature.HyGroupComposition;
import eu.hyvar.feature.HyGroupType;

/**
 * Delete a new group with type, feature child and group composition
 */
public class DeleteGroupWithTypeChildAndComposition extends ComplexOperation {

	private HyGroup group;
	
	private HyFeatureChild featureChild;
	private HyGroupType groupType;
	private HyGroupComposition groupComposition;
	
	public DeleteGroupWithTypeChildAndComposition(HyGroup group, Date timestamp) {
		
		this.group = group;
		this.timestamp = timestamp;
		
	}
	/* (non-Javadoc)
	 * @see de.darwinspl.feature.evolution.basic.operations.ComplexOperation#execute()
	 */
	@Override
	public void execute() {
		
		//not optimal especially for groups with huge quantity of entries
		//get the valid object from grouptype, groupComposition and featureChild of the group
		for (HyGroupType groupType : group.getTypes()) {
			if (groupType.getValidUntil() == null) {
				this.groupType = groupType;
				break;
			}
		}
		for (HyGroupComposition composition : group.getParentOf()) {
			if (composition.getValidUntil() == null) {
				this.groupComposition = composition;
				break;
			}
		}
		for (HyFeatureChild child : group.getChildOf()) {
			if (child.getValidUntil() == null) {
				this.featureChild = child;
				break;
			}
		}
		
		DeleteGroupType deleteGroupType = new DeleteGroupType(groupType, timestamp);
		DeleteGroupComposition deleteGroupComposition = new DeleteGroupComposition(groupComposition, timestamp);
		DeleteFeatureChild deleteFeatureChild = new DeleteFeatureChild(featureChild, timestamp);
		DeleteGroup deleteGroup = new DeleteGroup(group, timestamp);
		
		addToComposition(deleteGroupType);
		addToComposition(deleteGroupComposition);
		addToComposition(deleteFeatureChild);
		addToComposition(deleteGroup);
		
		for (EvolutionOperation evolutionOperation : evoOps) {
			evolutionOperation.execute();
		}
		
		this.featureChild = deleteFeatureChild.getFeatureChild();
		this.groupType = deleteGroupType.getGroupType();
		this.groupComposition = deleteGroupComposition.getGroupComposition();

	}

	/* (non-Javadoc)
	 * @see de.darwinspl.feature.evolution.basic.operations.ComplexOperation#undo()
	 */
	@Override
	public void undo() {
		//check if the execute method was executed, otherwise leave this method
		if (group.getValidUntil().compareTo(timestamp) != 0) {
			return;
		}
		
		for (EvolutionOperation evolutionOperation : evoOps) {
			evolutionOperation.undo();
		}
		groupType = null;
		groupComposition = null;
		featureChild = null;

		//remove each evo op to avoid that on a redo the evoOps list will contain the same evo op twice
		for (EvolutionOperation evolutionOperation : evoOps) {
			removeFromComposition(evolutionOperation);
			if (evoOps.size() == 0) {
				break;
			}
		}
		
	}
	//Getters
	public HyGroup getGroup() {
		return group;
	}
	public HyFeatureChild getFeatureChild() {
		return featureChild;
	}
	public HyGroupType getGroupType() {
		return groupType;
	}
	public HyGroupComposition getGroupComposition() {
		return groupComposition;
	}

}
