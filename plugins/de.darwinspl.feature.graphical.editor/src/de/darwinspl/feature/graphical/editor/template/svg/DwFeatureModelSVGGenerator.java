package de.darwinspl.feature.graphical.editor.template.svg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.deltaecore.feature.graphical.base.editor.DEGraphicalEditor;
import org.deltaecore.feature.graphical.base.util.DEGraphicalEditorTheme;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import de.darwinspl.feature.graphical.base.deltaecore.wrapper.layouter.version.DwVersionLayouterManager;
import de.darwinspl.feature.graphical.base.deltaecore.wrapper.layouter.version.DwVersionTreeLayouter;
import de.darwinspl.feature.graphical.base.editor.DwGraphicalFeatureModelViewer;
import de.darwinspl.feature.graphical.base.model.DwFeatureModelWrapped;
import de.darwinspl.feature.graphical.base.model.DwFeatureWrapped;
import de.darwinspl.feature.graphical.base.model.DwGroupWrapped;
import de.darwinspl.feature.graphical.base.model.DwParentChildConnection;
import de.darwinspl.feature.graphical.editor.template.DwEclipseWorkspaceUtil;
import eu.hyvar.dataValues.HyEnum;
import eu.hyvar.dataValues.HyEnumLiteral;
import eu.hyvar.evolution.util.HyEvolutionUtil;
import eu.hyvar.evolution.HyName;
import eu.hyvar.feature.HyBooleanAttribute;
import eu.hyvar.feature.HyEnumAttribute;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyFeatureAttribute;
import eu.hyvar.feature.HyFeatureModel;
import eu.hyvar.feature.HyFeatureType;
import eu.hyvar.feature.HyFeatureTypeEnum;
import eu.hyvar.feature.HyGroupTypeEnum;
import eu.hyvar.feature.HyNumberAttribute;
import eu.hyvar.feature.HyRootFeature;
import eu.hyvar.feature.HyVersion;
import freemarker.core.ParseException;
import freemarker.core.TemplateNumberFormatFactory;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

/**
 * A generator which uses a feature model and a date to generate a SVG representation of the feature model at the
 * given date. The generated file will be placed in a separate directory which is placed in the same directory
 * as the feature model. 
 * 
 * @author Gil Engel
 *
 */
public class DwFeatureModelSVGGenerator {
	private Date date;

	/**
	 * Converts a version as specified as in the model and converts it into a data object for freemarker.
	 * All superseding elements are converted as well.
	 * @param version to convert and print in the svg file
	 * @param feature corresponding feature to specified version
	 * @return A freemarker data object @see{HyFeatureModelSVGVersionDataObject}
	 */
	private DwFeatureModelSVGVersionDataObject convertVersion(HyVersion version, HyFeature feature){

		List<DwFeatureModelSVGVersionDataObject> children = new ArrayList<DwFeatureModelSVGVersionDataObject>();
		DwVersionTreeLayouter versionTreeLayouter = DwVersionLayouterManager.getLayouter(feature, date);

		for(HyVersion child : HyEvolutionUtil.getValidTemporalElements(version.getSupersedingVersions(), date)){
			children.add(convertVersion(child, feature));
		}

		int x = 0;
		int y = 0;
		if(versionTreeLayouter != null){
			Rectangle bounds = versionTreeLayouter.getBounds(version);
			x = bounds.x;
			y = bounds.y;
		}


		return new DwFeatureModelSVGVersionDataObject(version.getNumber(), x, y, children);
	}
	
	/**
	 * Searches all versions of a feature for the root version and converts this using @see{convertVersion}.
	 * @param featureWrapped
	 * @return Root version in a data object @see{HyFeatureModelSVGVersionDataObject}
	 */
	private DwFeatureModelSVGVersionDataObject convertVersionsDataObjectList(DwFeatureWrapped featureWrapped){
		HyFeature feature = featureWrapped.getWrappedModelElement();
		HyVersion rootVersion = null;
		for(HyVersion version :	HyEvolutionUtil.getValidTemporalElements(feature.getVersions(), date)){
			if(version.getSupersededVersion() == null){
				rootVersion = version;
				break;
			}
		}	

		if(rootVersion != null){
			return convertVersion(rootVersion, feature);
		}else{
			return null;
		}
	}

	/**
	 * Converts all groups of a feature model in a list of data objects
	 * @param featureModelWrapped
	 * @return
	 */
	private List<DwFeatureModelSVGGroupDataObject> convertGroups(DwFeatureModelWrapped featureModelWrapped){		
		List<DwFeatureModelSVGGroupDataObject> groups = new ArrayList<DwFeatureModelSVGGroupDataObject>();
		for(DwGroupWrapped group : featureModelWrapped.getGroups(date)){
				if(HyEvolutionUtil.getValidTemporalElements(HyEvolutionUtil.getValidTemporalElement(group.getWrappedModelElement().getParentOf(), date).getFeatures(), date).size() > 1){
					DEGraphicalEditorTheme theme = DEGraphicalEditor.getTheme();
					
					DwFeatureWrapped parentFeature = featureModelWrapped.getParentFeatureForGroup(group, featureModelWrapped.getSelectedDate());
					Point position = parentFeature.getPosition(date).getPosition().getCopy();
					position.x += parentFeature.getSize(date).width() / 2;
					//position.y += parentFeature.getSize(date).height(); //(int) (Integer)HyGeometryUtil.calculateFeatureHeight(parentFeature.getWrappedModelElement(), date); // - theme.getFeatureVariationTypeExtent() * 1.5+theme.getLineWidth());

					int size = HyEvolutionUtil.getValidTemporalElements(parentFeature.getWrappedModelElement().getAttributes(), date).size();
					position.y += theme.getFeatureNameAreaHeight() * (size + 1) + parentFeature.calculateVersionAreaBounds(date).height;
		
							
					DwFeatureWrapped leftest = null;
					DwFeatureWrapped rightest = null;
					for(HyFeature feature : group.getFeatures(date)){
						DwFeatureWrapped featureWrapped = featureModelWrapped.findWrappedFeature(feature);
						if(HyEvolutionUtil.isValid(featureWrapped.getWrappedModelElement(), date)){
							if(leftest == null){
								leftest = featureWrapped;
							}
							if(rightest == null){
								rightest = featureWrapped;
							}

							if(leftest.getPosition(date).getPosition().x > featureWrapped.getPosition(date).getPosition().x){
								leftest = featureWrapped;
							}

							if(rightest.getPosition(date).getPosition().x < featureWrapped.getPosition(date).getPosition().x){
								rightest = featureWrapped;
							}
						}
					}

					if(leftest != null && rightest != null){
						Point leftestPosition = leftest.getPosition(date).getPosition().getCopy();
						leftestPosition.x += leftest.getSize(date).getCopy().width / 2;

						Point rightestPosition = rightest.getPosition(date).getPosition().getCopy();
						rightestPosition.x += rightest.getSize(date).getCopy().width / 2;

						int modifier = HyEvolutionUtil.getValidTemporalElement(group.getWrappedModelElement().getTypes(), date).getType().getValue();

						DwFeatureModelSVGGroupArcDataObject arc = new DwFeatureModelSVGGroupArcDataObject(leftestPosition.x, leftestPosition.y, position.x, position.y, rightestPosition.x, rightestPosition.y);
						groups.add(new DwFeatureModelSVGGroupDataObject(modifier, arc));

					}
				}
			
		}

		return groups;
	}

	/**
	 * Converts a feature as specified by the model to a data object.
	 * @param featureWrapped
	 * @return
	 */
	private DwFeatureModelSVGFeatureDataObject convertFeature(DwFeatureWrapped featureWrapped){
		Point position = featureWrapped.getPosition(date).getPosition();

		HyName name = HyEvolutionUtil.getValidTemporalElement(featureWrapped.getWrappedModelElement().getNames(), date);



		List<DwFeatureModelSVGAttributeDataObject> attributes = new ArrayList<DwFeatureModelSVGAttributeDataObject>();
		for(HyFeatureAttribute attribute : HyEvolutionUtil.getValidTemporalElements(featureWrapped.getWrappedModelElement().getAttributes(), date)){
			HyName attributeName = HyEvolutionUtil.getValidTemporalElement(attribute.getNames(), date);

			int type = -1;
			if(attribute instanceof HyNumberAttribute){
				type = 0;
			}
			if(attribute instanceof HyBooleanAttribute){
				type = 1;
			}
			if(attribute instanceof HyEnumAttribute){
				type = 2;
			}

			attributes.add(new DwFeatureModelSVGAttributeDataObject(attributeName.getName(), type));
		}

		List<DwFeatureModelSVGFeatureDataObject> children = new ArrayList<DwFeatureModelSVGFeatureDataObject>();
		for(DwParentChildConnection connection : featureWrapped.getChildrenConnections(date)){
			children.add(convertFeature(connection.getTarget()));
		}



		HyGroupTypeEnum type = featureWrapped.getModfierAtDate(date);
		int modifier = -1;
		if(type != null && type.getValue() != HyGroupTypeEnum.ALTERNATIVE_VALUE && type.getValue() != HyGroupTypeEnum.OR_VALUE){
			HyFeatureType featureType = HyEvolutionUtil.getValidTemporalElement(featureWrapped.getWrappedModelElement().getTypes(), date);
			modifier = (featureType.getType() == HyFeatureTypeEnum.OPTIONAL) ? 0 : 1;
		}

		Dimension dimension = featureWrapped.getSize(date);

		DwVersionTreeLayouter versionTreeLayouter = DwVersionLayouterManager.getLayouter(featureWrapped.getWrappedModelElement(), date);

		int versionAreaWidth = 0;
		int versionAreaHeight = 0;
		if(versionTreeLayouter != null){
			Rectangle versionArea = featureWrapped.calculateVersionAreaBounds(date);
			versionAreaWidth = versionArea.width;
			versionAreaHeight = versionArea.height;
		}
		
		return new DwFeatureModelSVGFeatureDataObject(
				name.getName(), 
				position.x(), 
				position.y(), 
				dimension.width(), 
				dimension.height(), 
				versionAreaWidth,
				versionAreaHeight,
				modifier, 
				attributes, 
				children,
				convertVersionsDataObjectList(featureWrapped));
	}
	
	
	private List<DwFeatureModelSVGEnumDataObject> convertEnumerations(DwFeatureModelWrapped modelWrapped){
		List<DwFeatureModelSVGEnumDataObject> enums = new ArrayList<DwFeatureModelSVGEnumDataObject>();
		Date date = modelWrapped.getSelectedDate();
		
		for(HyEnum enumeration : HyEvolutionUtil.getValidTemporalElements(modelWrapped.getModel().getEnums(), date)){
			enums.add(convertEnumeration(enumeration, date));
		}
		
		return enums;
	}
	/**
	 * Converts a enumeration as specified by the model to a data object.
	 * @param featureWrapped
	 * @return
	 */
	private DwFeatureModelSVGEnumDataObject convertEnumeration(HyEnum enumeration, Date date){
		List<String> literals = new ArrayList<String>();
		for(HyEnumLiteral literal : HyEvolutionUtil.getValidTemporalElements(enumeration.getLiterals(), date)){
			literals.add(literal.getName());
		}
		
		return new DwFeatureModelSVGEnumDataObject(enumeration.getName(), literals);
	}

	/**
	 * Sets the template intern variables in order to generate the SVG file
	 * @return
	 */
	private Map<String, Object> prepareDataForExportFile(){
		Map<String, Object> input = new HashMap<String, Object>();



		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		DwGraphicalFeatureModelViewer editor = (DwGraphicalFeatureModelViewer)page.getActiveEditor();

		DwFeatureModelWrapped modelWrapped = editor.getModelWrapped();
		HyFeatureModel model = modelWrapped.getModel();

		HyRootFeature rootFeature = HyEvolutionUtil.getValidTemporalElement(model.getRootFeature(), date);
		DwFeatureWrapped featureWrapped = modelWrapped.findWrappedFeature(rootFeature.getFeature());

	
		DwFeatureModelSVGFeatureDataObject root = convertFeature(featureWrapped);
		input.put("rootFeature", root);

		List<DwFeatureModelSVGGroupDataObject> groups = convertGroups(modelWrapped);
		input.put("groups", groups);	
		
		List<DwFeatureModelSVGEnumDataObject> enums = convertEnumerations(modelWrapped);
		input.put("enums", enums);	
		// add theme
		DEGraphicalEditorTheme theme = DEGraphicalEditor.getTheme();
		input.put("theme", theme);	
		
		Dimension editorDimension = editor.getEditorGraphicalDimension();
		input.put("editorAreaWidth", editorDimension.width);
		input.put("editorAreaHeight", editorDimension.height);

		return input;
	}

	/**
	 * Configures freemarker for usage.
	 * @return
	 * @throws URISyntaxException
	 * @throws TemplateNotFoundException
	 * @throws MalformedTemplateNameException
	 * @throws ParseException
	 * @throws IOException
	 * @throws TemplateException
	 */
	private Configuration initializeConfiguration() throws URISyntaxException, TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException{
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setClassForTemplateLoading(DwFeatureModelSVGGenerator.class, "templates");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		Bundle bundle = Platform.getBundle("de.darwinspl.feature.graphical.editor");
		URL fileURL = bundle.getEntry("templates/");

		File file = new File(FileLocator.resolve(fileURL).toURI());
		cfg.setDirectoryForTemplateLoading(file);
		
		Map<String, TemplateNumberFormatFactory> customNumberFormats = new HashMap<String, TemplateNumberFormatFactory>();

		customNumberFormats.put("hex", DwHexTemplateNumberFormatFactory.INSTANCE);

		cfg.setCustomNumberFormats(customNumberFormats);

		return cfg;
	}


	/**
	 * Starts the generation process and generates on success a SVG file containing the feature model at a given
	 * date
	 * @param date
	 */
	public void createFile(Date date){
		this.date = date;

		Template template = null;
		try{
			Configuration cfg = initializeConfiguration();

			template = cfg.getTemplate("svg_exporter.ftl");
		}catch(Exception ex){
			ex.printStackTrace();
			return;
		}


		String modelName = DwEclipseWorkspaceUtil.getFilenameFromCurrentOpenEditorFile();

		// create the folder where the overview file will be generated and
		// all style related files will copied
		IPath overviewPath = DwEclipseWorkspaceUtil.createFolderInPathFromCurrentOpenEditorFile("overview");

		try{
			File oFile = DwEclipseWorkspaceUtil.createFileInPath(modelName+".svg", overviewPath);
			Writer fileWriter = new FileWriter(oFile);

			Map<String, Object> input = prepareDataForExportFile();
			template.process(input, fileWriter);

			fileWriter.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}catch(TemplateException ex){
			ex.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
