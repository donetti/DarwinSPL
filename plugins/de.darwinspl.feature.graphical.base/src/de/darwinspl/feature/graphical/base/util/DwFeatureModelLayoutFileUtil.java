package de.darwinspl.feature.graphical.base.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.geometry.Point;

import de.darwinspl.feature.graphical.base.model.DwFeatureModelWrapped;
import de.darwinspl.feature.graphical.base.model.DwFeatureWrapped;
import de.darwinspl.feature.graphical.base.model.DwTemporalPosition;

public class DwFeatureModelLayoutFileUtil {
	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"); 
	
	/**
	 * Adds a dummy location (0,0) to each feature of the selected feature model.
	 * Features are initialized without a position. If no layout file exist, this function
	 * gets called to ensure that each feature contains one position that can be changed
	 * by the auto layout algorithm.
	 * 
	 * @param featureModel
	 */
	private static void addDummyPosition(DwFeatureModelWrapped featureModel){
		for(DwFeatureWrapped featureWrapped : featureModel.getFeatures(null)){
			featureWrapped.addPosition(new Point(0, 0), null, false);
		}
	}		
	
	/**
	 * Returns the file object of the feature model
	 * @param featureModel
	 * @return
	 */
	private static File getLayoutFile(DwFeatureModelWrapped featureModel){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		
		IFile file = workspaceRoot.getFile(new Path(featureModel.getModel().eResource().getURI().toPlatformString(true)));
		
		IPath path = ((IPath)file.getFullPath().clone()).removeFileExtension().addFileExtension("hylayout");
		IResource resourceInRuntimeWorkspace = workspaceRoot.findMember(path.toString());

		if(resourceInRuntimeWorkspace != null){
			return new File(resourceInRuntimeWorkspace.getLocationURI());	
		}else{
			return null;
		}
	}
	
	/**
	 * Collects all the dates of a layout file
	 * @param featureModel
	 * @return
	 */
	public static List<Date> loadDatesFromLayoutFile(DwFeatureModelWrapped featureModel){
		List<Date> dates = new ArrayList<Date>();
		
		File file = getLayoutFile(featureModel);
		
		if(file != null){
			if(file.exists()){
				List<String> lines;
				try {
					lines = Files.readAllLines(Paths.get(file.getPath()), Charset.defaultCharset());
					
					for(String line : lines){
						List<Object> container = parseLine(line);		
						
						Date since = (Date)container.get(1);
						Date until = (Date)container.get(2);
						
						if(!dates.contains(since) && since != null)
							dates.add(since);
						if(!dates.contains(until) && until != null)
							dates.add(until);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return dates;
	}
	
	/**
	 * parses a line of a layout file to a container of five objects. The structure of the container is (String, Date, Date, Int, Int)
	 * @param line
	 * @return
	 */
	private static List<Object> parseLine(String line){
		String[] parts = line.split(",");
		if(parts.length != 5){
			System.err.println("Layout file is corrupt at some point");
		}

		List<Object> container = new ArrayList<Object>();
		
		String id = parts[0];
		container.add(id);
		
		try{
		Date since = !parts[1].contains("null") ? dateFormat.parse(parts[1]) : null;
		Date until = !parts[2].contains("null") ? dateFormat.parse(parts[2]) : null;

		container.add(since);
		container.add(until);
		}catch(ParseException ex){
			System.err.println("Layout file has corrupt date at some point");
		}
		
		int x = Integer.parseInt(parts[3]);
		int y = Integer.parseInt(parts[4]);	
		container.add(x);
		container.add(y);
		
		return container;
	}
	
	/**
	 * Tries to load and parse a layout file and add the extracted positions to each feature of 
	 * the selected feature model. In case no layout file could be found or the layout file is corrupted,
	 * dummy positions are added and the feature model is marked as auto layouted. 
	 * @see addDummyPosition
	 *  
	 * @param featureModel
	 */
	public static void loadLayoutFile(DwFeatureModelWrapped featureModel){
		File file = getLayoutFile(featureModel);
		
		if(file != null){
			if(file.exists()){
				try {
					List<String> lines = Files.readAllLines(Paths.get(file.getPath()), Charset.defaultCharset());
					for(String line : lines){
						List<Object> container = parseLine(line);

						String id = (String)container.get(0);
						
						Date since = (Date)container.get(1);
						int x = (Integer)container.get(3);
						int y = (Integer)container.get(4);

						featureModel.setAutoLayoutActive(false);
						for(DwFeatureWrapped featureWrapped : featureModel.getFeatures(null)){
							if(id.equals(featureWrapped.getWrappedModelElement().getId())){
								featureWrapped.addPosition(new Point(x, y), since, false);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					
					addDummyPosition(featureModel);
					featureModel.setAutoLayoutActive(true);
				}
			}	
		}else{
			addDummyPosition(featureModel);
			featureModel.setAutoLayoutActive(true);
		}
	}
	
	/**
	 * Saves all positions for each feature of the selected feature model into a layout file.
	 * The file has the same name as the feature model (with "*.hylaout" file extension) and the
	 * same directory as the feature model. The generated text file is layouted as follows:
	 * Each line marks a position (line separation with \n). Each of these lines contains of 
	 * feature id, since date, until date, position. All values are separated by a comma.   
	 * 
	 * @param featureModel
	 */
	public static void saveLayout(DwFeatureModelWrapped featureModel){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		
		IFile file = workspaceRoot.getFile(new Path(featureModel.getModel().eResource().getURI().toPlatformString(true)));
		
		IPath path = ((IPath)file.getFullPath().clone()).removeFileExtension().addFileExtension("hylayout");

		String fileContent = "";
		for(DwFeatureWrapped featureWrapped : featureModel.getFeatures(null)){
			DwTemporalPosition position = featureWrapped.getFirstPosition();
			
			do{
				fileContent += featureWrapped.getWrappedModelElement().getId()+","+
						   (position.getValidSince() != null ? dateFormat.format(position.getValidSince()) : "null")+","+
						   (position.getValidUntil() != null ? dateFormat.format(position.getValidUntil()) : "null")+","+
						   position.getPosition().x()+","+
						   position.getPosition().y()+"\n";				
				
				position = position.getSuccessor();
			}while(position != null);
		}

		file = workspaceRoot.getFile(path);

		InputStream source = new ByteArrayInputStream(fileContent.getBytes());
		try {
			if(!file.exists()){
				file.create(source, IResource.NONE, null);
			}else{
				file.setContents(source, IResource.FORCE, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}	
}
