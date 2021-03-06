package de.darwinspl.feature.graphical.base.deltaecore.wrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.deltaecore.feature.graphical.base.editor.DEGraphicalEditor;
import org.deltaecore.feature.graphical.base.util.DEGeometryUtil;
import org.deltaecore.feature.graphical.base.util.DEGraphicalEditorTheme;
import org.eclipse.draw2d.geometry.Rectangle;

import de.darwinspl.feature.graphical.base.deltaecore.wrapper.layouter.version.DwVersionLayouterManager;
import de.darwinspl.feature.graphical.base.deltaecore.wrapper.layouter.version.DwVersionTreeLayouter;
import eu.hyvar.evolution.HyName;
import eu.hyvar.evolution.util.HyEvolutionUtil;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyFeatureAttribute;
import eu.hyvar.feature.HyFeatureChild;
import eu.hyvar.feature.HyGroup;
import eu.hyvar.feature.HyGroupComposition;
import eu.hyvar.feature.HyGroupType;
import eu.hyvar.feature.HyGroupTypeEnum;
import eu.hyvar.feature.HyVersion;
import eu.hyvar.feature.util.HyFeatureModelWellFormednessException;
import eu.hyvar.feature.util.HyFeatureUtil;

public class DwGeometryUtil {
	public static int calculateFeatureWidth(HyFeature feature, Date date) {
		if(date == null) date = new Date();

		DEGraphicalEditorTheme theme = DEGraphicalEditor.getTheme();
		int rawFeatureWidth = theme.getFeatureMinimumWidth();

		//Label width
		HyName name = HyEvolutionUtil.getValidTemporalElement(feature.getNames(), date);
		String featureName = (name != null) ? name.getName() : "No Valid Name";
		int labelWidth = DEGeometryUtil.getTextWidth(featureName, theme.getFeatureFont());
		rawFeatureWidth = Math.max(rawFeatureWidth, labelWidth);

		//Version tree width
		if (!feature.getVersions().isEmpty()) {
			DwVersionTreeLayouter versionTreeLayouter = DwVersionLayouterManager.getLayouter(feature, date);

			if(versionTreeLayouter != null){
				Rectangle versionTreeBounds = versionTreeLayouter.getTreeBounds();
				int versionTreeWidth = versionTreeBounds.width; // + HyVersionTreeLayouter.getOffsetX();
				rawFeatureWidth = Math.max(rawFeatureWidth, versionTreeWidth);
			}
		}


		for(HyFeatureAttribute attribute : HyEvolutionUtil.getValidTemporalElements(feature.getAttributes(), date)){
			HyName attributeName = HyEvolutionUtil.getValidTemporalElement(attribute.getNames(), date);
			if(attributeName != null){
				int nameWidth = DEGeometryUtil.getTextWidth(attributeName.getName(), theme.getFeatureFont());
				int typeWidth = DEGeometryUtil.getTextWidth("Boolean", theme.getFeatureFont());

				rawFeatureWidth = Math.max(rawFeatureWidth, nameWidth + typeWidth + 80);
			}
		}

		if(!feature.getParentOf().isEmpty()){
			HyFeatureChild child = HyEvolutionUtil.getValidTemporalElement(feature.getParentOf(), date);
			if(child != null){
				HyGroup group = child.getChildGroup();

				if(group != null){
					HyGroupComposition composition = HyEvolutionUtil.getValidTemporalElement(group.getParentOf(), date);

					if(composition != null){
						List<HyFeature> children = HyEvolutionUtil.getValidTemporalElements(composition.getFeatures(), date);

						if(!children.isEmpty())
							rawFeatureWidth += 18;
					}
				}
			}
		}

		// +16 to display an icon on the left for warnings, errors etc.
		return rawFeatureWidth + 2 * theme.getPrimaryMargin() + 16;
	}

	public static int calculateFeatureHeight(HyFeature feature, Date date) {
		if(date == null) date = new Date();

		DEGraphicalEditorTheme theme = DEGraphicalEditor.getTheme();
		int versionAreaHeight = calculateVersionAreaHeight(feature, date);

		int featureHeight = theme.getFeatureNameAreaHeight() + versionAreaHeight;

		int variationHeight = 0;
		try {
			if(feature != null && !HyFeatureUtil.isRootFeature(feature, date)){
				HyGroupComposition composition = HyEvolutionUtil.getValidTemporalElement(feature.getGroupMembership(), date);
				if(composition != null){
					HyGroupType type = HyEvolutionUtil.getValidTemporalElement(composition.getCompositionOf().getTypes(), date);

					if(type != null)
						variationHeight = (type.getType() == HyGroupTypeEnum.AND ? theme.getFeatureVariationTypeExtent() : 0);
				}else{
					variationHeight = 4;
				}
			}
		} catch (HyFeatureModelWellFormednessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		featureHeight += variationHeight; // + theme.getLineWidth() * 2;


		int visibleAttributes = HyEvolutionUtil.getValidTemporalElements(feature.getAttributes(), date).size();
		featureHeight += (theme.getFeatureNameAreaHeight()+theme.getLineWidth() * 3) * visibleAttributes; 


		if(visibleAttributes > 0 && versionAreaHeight > 0)
			featureHeight -= 8;
		else if(visibleAttributes > 0){
			featureHeight += 1;
		}

		return featureHeight;
	}

	private static int calculateVersionAreaHeight(HyFeature feature, Date date) {
		//Empty version areas are not drawn. Hence, they are calculated as height 0.
		if (HyEvolutionUtil.getValidTemporalElements(feature.getVersions(), date).isEmpty()) {
			return 0;
		}

		DEGraphicalEditorTheme theme = DEGraphicalEditor.getTheme();

		//Start with default height
		int rawVersionAreaHeight = theme.getFeatureVersionAreaDefaultHeight();


		//See if individual height is greater
		DwVersionTreeLayouter versionTreeLayouter = DwVersionLayouterManager.getLayouter(feature, date);
		Rectangle versionTreeBounds = versionTreeLayouter.getTreeBounds();
		int versionTreeHeight = versionTreeBounds.height;

		rawVersionAreaHeight = Math.max(rawVersionAreaHeight, versionTreeHeight);


		//See if the height of another version area on that tree level is greater 
		int highestVersionAreaHightOnSameLevel = findHighestVersionAreaHightOnSameLevel(feature, date);
		rawVersionAreaHeight = Math.max(rawVersionAreaHeight, highestVersionAreaHightOnSameLevel);

		return rawVersionAreaHeight + 2 * theme.getPrimaryMargin();
	}

	private static int findHighestVersionAreaHightOnSameLevel(HyFeature feature, Date date){
		int highestVersionAreaHightOnSameLevel = 0;

		try {
			if(HyFeatureUtil.isRootFeature(feature, date)){
				DwVersionTreeLayouter versionTreeLayouter = DwVersionLayouterManager.getLayouter(feature, date);
				highestVersionAreaHightOnSameLevel = versionTreeLayouter.getTreeBounds().height;

			}else{
				List<HyFeature> featuresOnSameTreeLevel = null;
				if(HyFeatureUtil.isRootFeature(feature, date)){
					featuresOnSameTreeLevel = HyFeatureUtil.findFeaturesOnSameTreeLevel(feature, date);
				}else{
					featuresOnSameTreeLevel = new ArrayList<HyFeature>();
					featuresOnSameTreeLevel.add(feature);
				}


				for (HyFeature featureOnSameTreeLevel : featuresOnSameTreeLevel) {
					List<HyVersion> versions = featureOnSameTreeLevel.getVersions();

					if (!versions.isEmpty()) {
						DwVersionTreeLayouter versionTreeLayouter = DwVersionLayouterManager.getLayouter(featureOnSameTreeLevel, date);

						if(versionTreeLayouter != null){
							Rectangle versionTreeBounds = versionTreeLayouter.getTreeBounds();
							int versionTreeHeight = versionTreeBounds.height;

							highestVersionAreaHightOnSameLevel = Math.max(highestVersionAreaHightOnSameLevel, versionTreeHeight);
						}
					}
				}			
			}
		} catch (HyFeatureModelWellFormednessException e) {
			e.printStackTrace();
		}

		return highestVersionAreaHightOnSameLevel;
	}

}
