/**
 * 
 */
package de.darwinspl.feature.evolution.basic.operations;

import java.util.Date;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import de.darwinspl.feature.evolution.complex.operations.AddGroupWithTypeChildAndComposition;
import de.darwinspl.feature.evolution.complex.operations.ComplexOperation;
import de.darwinspl.feature.evolution.complex.operations.RemoveFromGroupComposition;
import de.darwinspl.feature.evolution.invoker.EvolutionOperation;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyFeatureModel;
import eu.hyvar.feature.HyGroup;
import eu.hyvar.feature.HyGroupComposition;
import eu.hyvar.feature.HyGroupTypeEnum;

/**
 * Move a feature from a group, which will still exist after the evolution, under a feature where no group exists or fit.
 */
public class MoveFeatureAddGroup extends ComplexOperation {

	private HyFeature feature, parent;
	private HyGroupComposition oldGroupCompositionBefore, oldGroupCompositionAfter;	
	private HyGroup group;
	
	private HyFeatureModel tfm;
	
	/**
	 * 
	 * @param feature which should be move 
	 * @param parent of the new group
	 * @param timestamp
	 * @param tfm the tfm is needed because a new group will be added.
	 */
	public MoveFeatureAddGroup(HyFeature feature, HyFeature parent, Date timestamp, HyFeatureModel tfm) {
		
		this.feature = feature;
		this.parent = parent;
		this.timestamp = timestamp;
		this.tfm = tfm;
		
	}
	/* (non-Javadoc)
	 * @see de.darwinspl.feature.evolution.basic.operations.ComplexOperation#execute()
	 */
	@Override
	public void execute() {
		
		for (HyGroupComposition composition : feature.getGroupMembership()) {
			if (composition.getValidUntil() == null) {
				this.oldGroupCompositionBefore = composition;
				break;
			}
		}
		
		//remove the feature from the old group composition
		RemoveFromGroupComposition updateOldGroupComposition = new RemoveFromGroupComposition(oldGroupCompositionBefore, feature, timestamp);
		
		EList<HyFeature> features = new BasicEList<HyFeature>();
		features.add(feature);
		//add a new group for the feature und the parent. The group type must be AND because at this point the group will only have one feature in his composition
		AddGroupWithTypeChildAndComposition addGroup = new AddGroupWithTypeChildAndComposition(HyGroupTypeEnum.AND, parent, features, timestamp, tfm);
		
		addToComposition(updateOldGroupComposition);
		addToComposition(addGroup);
		
		for (EvolutionOperation operation : evoOps) {
			operation.execute();
		}
		
		this.oldGroupCompositionAfter = updateOldGroupComposition.getNewGroupComposition();
		this.group = addGroup.getGroup();

	}

	/* (non-Javadoc)
	 * @see de.darwinspl.feature.evolution.basic.operations.ComplexOperation#undo()
	 */
	@Override
	public void undo() {
		//check if the execute method was executed, otherwise leave this method
		if (oldGroupCompositionBefore == null) {
			return;
		}
		
		for (EvolutionOperation evolutionOperation : evoOps) {
			evolutionOperation.undo();
		}
		
		oldGroupCompositionAfter = null;
		group = null;
		oldGroupCompositionBefore = null;

		//remove each evo op to avoid that on a redo the evoOps list will contain the same evo op twice
		for (EvolutionOperation evolutionOperation : evoOps) {
			removeFromComposition(evolutionOperation);
			if (evoOps.size() == 0) {
				break;
			}
		}

	}
	
	//Getters
	public HyFeature getFeature() {
		return feature;
	}
	public HyFeature getParent() {
		return parent;
	}
	public HyGroupComposition getOldGroupCompositionAfter() {
		return oldGroupCompositionAfter;
	}
	public HyGroup getGroup() {
		return group;
	}

}
