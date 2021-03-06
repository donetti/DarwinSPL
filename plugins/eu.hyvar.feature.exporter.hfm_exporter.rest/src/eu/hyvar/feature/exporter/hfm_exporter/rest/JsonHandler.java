package eu.hyvar.feature.exporter.hfm_exporter.rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deltaecore.feature.DEFeature;
import org.deltaecore.feature.DEFeatureModel;
import org.deltaecore.feature.DEVersion;
import org.deltaecore.feature.configuration.DEConfiguration;
import org.deltaecore.suite.mapping.DEMappingModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.christophseidl.util.ecore.EcoreIOUtil;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyFeatureModel;
import eu.hyvar.feature.HyVersion;
import eu.hyvar.feature.configuration.HyConfiguration;
import eu.hyvar.feature.exporter.hfm_exporter.HFMConfigurationExporter;
import eu.hyvar.feature.exporter.hfm_exporter.HFMExporter;
import eu.hyvar.feature.exporter.hfm_exporter.HFMMappingExporter;
import eu.hyvar.feature.exporter.hfm_exporter.rest.input.HyVarInput;
import eu.hyvar.feature.exporter.hfm_exporter.rest.output.Configuration;
import eu.hyvar.feature.exporter.hfm_exporter.rest.output.DeltaEcoreOutput;
import eu.hyvar.feature.exporter.hfm_exporter.rest.output.FeatureModel;
import eu.hyvar.feature.exporter.hfm_exporter.rest.output.Mapping;
import eu.hyvar.feature.mapping.HyMappingModel;
import eu.hyvar.feature.util.HyFeatureModelWellFormednessException;

public class JsonHandler extends AbstractHandler {
	
	private static final String PROJECT_NAME = "hyvar_to_deltaecore";
	
	// folders have to be the same. Otherwise the path in the mapping will point to different folders than the variant generator expects them to be
//	private static final String MODELS_FOLDER = "models";
//	private static final String HYVAR_FOLDER = MODELS_FOLDER;
//	private static final String DELTAECORE_FOLDER = MODELS_FOLDER;
	
	private IFolder folder;
	
	private static final String MSG_TYPE_JSON_HYVAR_TO_DELTAECORE_INPUT = "hyvar2deltaecore";
	
	private static final String MSG_TYPE_JSON_OUTPUT = "variantgen_spl";
	
	private static final String FILENAME = "spl";
	
	private static synchronized IFolder createFolder(IProject openedProject) throws CoreException {
		IFolder folder = null;
		while (folder == null || folder.exists()) {
			folder = openedProject.getFolder(UUID.randomUUID().toString());
		}
		
		folder.create(true, true, null);

		return folder;
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspaceRoot.getProject(PROJECT_NAME);

		try {
			if (!project.exists()) {
				project.create(progressMonitor);
			}
			if(!project.isOpen()) {
				project.open(progressMonitor);				
			}
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		try {
			folder = createFolder(project);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		// TODO shared storage instead of strings?
		
		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		sb.append(reader.readLine());
		String jsonInput = sb.toString();

		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(jsonInput).getAsJsonObject();
		String msgType = obj.get("msg_type").getAsString();

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();

		if (msgType.equals(MSG_TYPE_JSON_HYVAR_TO_DELTAECORE_INPUT)) {
			HyVarInput input = gson.fromJson(jsonInput, HyVarInput.class);

			List<EObject> hyVarModels = getHyVarModels(input);

			List<EObject> deltaEcoreModels = translateHyVarModelsToDeltaEcore(hyVarModels);
			
			boolean featureModelTranslated = false;
			boolean configurationTranslated = false;
			boolean mappingTranslated = false;
			
			for(EObject deModel: deltaEcoreModels) {
				if(deModel != null) {
					if(deModel instanceof DEFeatureModel) {
						featureModelTranslated = true;
					} else if(deModel instanceof DEConfiguration) {
						configurationTranslated = true;
					} else if(deModel instanceof DEMappingModel) {
						mappingTranslated = true;
					}
				}
			}
			
			if(featureModelTranslated && configurationTranslated && mappingTranslated) {
				String outputJson = createOutputJson(deltaEcoreModels);
				System.out.println(outputJson);
				
				response.setContentType("application/json; charset=utf-8");
				PrintWriter out = response.getWriter();
				out.write(outputJson);
				response.setStatus(HttpServletResponse.SC_OK);				
			}
			else {
				PrintWriter out = response.getWriter();
				out.write("Some models could not be translated");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);			
			}
			
			baseRequest.setHandled(true);
		}
		
		try {
			folder.delete(true, progressMonitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Create JSON representation for delta ecore models
	 * @param deltaEcoreModels
	 * @return
	 */
	private String createOutputJson(List<EObject> deltaEcoreModels) {
		DEFeatureModel deFeatureModel = null;
		DEConfiguration deConfiguration = null;
		DEMappingModel deMappingModel = null;
		
		for(EObject eObject : deltaEcoreModels) {
			if(eObject instanceof DEFeatureModel) {
				deFeatureModel = (DEFeatureModel) eObject;
			} else if(eObject instanceof DEConfiguration) {
				deConfiguration = (DEConfiguration) eObject;
			} else if(eObject instanceof DEMappingModel) {
				deMappingModel = (DEMappingModel) eObject;
			}
		}
		
//		IProgressMonitor progressMonitor = new NullProgressMonitor();
//		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//		IProject project = workspaceRoot.getProject(PROJECT_NAME);
		
//		try {
//			if (!project.exists()) {
//				project.create(progressMonitor);
//			}
//			project.open(progressMonitor);
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
//		
//		IFolder folder = project.getFolder(FOLDER);
//		if (!folder.exists()) {
//			try {
//				folder.create(true, true, progressMonitor);
//			} catch (CoreException e) {
//				e.printStackTrace();
//			}
//		}
		
		IFile fmFile = folder.getFile(FILENAME + ".defeature");		
		EcoreIOUtil.saveModelAs(deFeatureModel, fmFile);
		
		IFile configFile = folder.getFile(FILENAME + ".deconfiguration");
		EcoreIOUtil.saveModelAs(deConfiguration, configFile);
		
		IFile mappingFile = folder.getFile(FILENAME + ".demapping");
		EcoreIOUtil.saveModelAs(deMappingModel, mappingFile);
		
		
		
		try {
			DeltaEcoreOutput output = new DeltaEcoreOutput();
			
			// Read fm file and create JSON object
			Scanner scanner;
			scanner = new Scanner(fmFile.getContents(), "UTF-8");
			scanner.useDelimiter("\\A");
			String fmString = scanner.next();
//			System.out.println(fmString);
			scanner.close();
			
			FeatureModel featureModelJson = new FeatureModel();
			featureModelJson.setContent(fmString);
			featureModelJson.setName(FILENAME);
			output.setFeatureModel(featureModelJson);
			
			
			// Read config file and create JSON object
			scanner = new Scanner(configFile.getContents(), "UTF-8");
			scanner.useDelimiter("\\A");
			String configString = scanner.next();
//			System.out.println(configString);
			scanner.close();
			
			Configuration configurationJson = new Configuration();
			configurationJson.setContent(configString);
			configurationJson.setName(FILENAME);
			output.setConfiguration(configurationJson);
			
			// Read mapping file and create JSON object
			scanner = new Scanner(mappingFile.getContents(), "UTF-8");
			scanner.useDelimiter("\\A");
			String mappingString = scanner.next();
//			System.out.println(mappingString);
			scanner.close();
			
			Mapping mappingJson = new Mapping();
			mappingJson.setContent(mappingString);
			mappingJson.setName(FILENAME);
			output.setMapping(mappingJson);
			
			
			// set Message Type to MSG_TYPE_JSON_OUTPUT
			output.setMsgType(MSG_TYPE_JSON_OUTPUT);
			
			
			
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			
			
			return gson.toJson(output);
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return "";
	}
	
	/**
	 * Tranlsates HyVar FM, Config, Mapping to DeltaEcore FM, Config, Mapping
	 * @param hyVarModels
	 * @return
	 */
	private List<EObject> translateHyVarModelsToDeltaEcore(List<EObject> hyVarModels) {
		List<EObject> deltaEcoreModels = new ArrayList<EObject>();
		
		HyFeatureModel hyFM = null;
		HyConfiguration hyConfig = null;
		HyMappingModel hyMappingModel = null;
		
		for(EObject eObject: hyVarModels) {
			if(eObject instanceof HyFeatureModel) {
				hyFM = (HyFeatureModel) eObject;
			} else if(eObject instanceof HyConfiguration) {
				hyConfig = (HyConfiguration) eObject;
			} else if(eObject instanceof HyMappingModel) {
				hyMappingModel = (HyMappingModel) eObject;
			}
		}
		
		try {
			HFMExporter hfmExporter = new HFMExporter();
			// TODO date - evolution?
			DEFeatureModel deFeatureModel = hfmExporter.exportFeatureModel(hyFM, null);
			Map<HyFeature, DEFeature> featureMapping = hfmExporter.getFeatureMapping();
			Map<HyVersion, DEVersion> versionMapping = hfmExporter.getVersionMapping();
			
			HFMConfigurationExporter hfmConfigExporter = new HFMConfigurationExporter(featureMapping, versionMapping, hyFM, deFeatureModel);
			DEConfiguration deConfiguration = hfmConfigExporter.exportConfiguration(hyConfig, null);
			
			HFMMappingExporter hfmMappingExporter = new HFMMappingExporter(featureMapping, versionMapping);
			DEMappingModel deMappingModel = hfmMappingExporter.exportMappingModel(hyMappingModel, null);
			
			deltaEcoreModels.add(deFeatureModel);
			deltaEcoreModels.add(deConfiguration);
			deltaEcoreModels.add(deMappingModel);
			
		} catch (HyFeatureModelWellFormednessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return deltaEcoreModels;
	}
	
	/**
	 * 
	 * @return List of HyVar model files
	 */
	private List<EObject> getHyVarModels(HyVarInput input) {
		IProgressMonitor progressMonitor = new NullProgressMonitor();
//		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//		IProject project = workspaceRoot.getProject(PROJECT_NAME);
//		
//		try {
//			if (!project.exists()) {
//				project.create(progressMonitor);
//			}
//			if(!project.isOpen()) {
//				project.open(progressMonitor);				
//			}
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
//		
//		IFolder folder = project.getFolder(FOLDER);
//		if (!folder.exists()) {
//			try {
//				folder.create(true, true, progressMonitor);
//			} catch (CoreException e) {
//				e.printStackTrace();
//			}
//		}
		
		IFile fmFile = folder.getFile(input.getFeatureModel().getFilename() + ".hyfeature");
		InputStream inputStream = new ByteArrayInputStream(input.getFeatureModel().getSpecification().getBytes());
		try {
			if(!fmFile.exists()) {
				fmFile.create(inputStream, true, progressMonitor);				
			} else {
				fmFile.setContents(inputStream, IFile.FORCE, progressMonitor);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		IFile configurationFile = folder.getFile(input.getConfiguration().getFilename() + ".hyconfiguration");
		inputStream = new ByteArrayInputStream(input.getConfiguration().getSpecification().getBytes());
		try {
			if(!configurationFile.exists()) {
				configurationFile.create(inputStream, true, progressMonitor);				
			} else {
				 configurationFile.setContents(inputStream, IFile.FORCE,
				 progressMonitor);				
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		IFile mappingFile = folder.getFile(input.getMapping().getFilename() + ".hymappingmodel");
		inputStream = new ByteArrayInputStream(input.getMapping().getSpecification().getBytes());
		try {
			if(!mappingFile.exists()) {
				mappingFile.create(inputStream, true, progressMonitor);				
			} else {
				mappingFile.setContents(inputStream, IFile.FORCE,
				 progressMonitor);				
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		try {
			folder.refreshLocal(IFolder.DEPTH_INFINITE, progressMonitor);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		
		List<IFile> modelFiles = new ArrayList<IFile>();
		modelFiles.add(fmFile);
		modelFiles.add(configurationFile);
		modelFiles.add(mappingFile);
		
		return EcoreIOUtil.loadModels(modelFiles);
	}
	

}
