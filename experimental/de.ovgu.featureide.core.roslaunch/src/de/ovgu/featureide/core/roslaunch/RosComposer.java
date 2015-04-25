package de.ovgu.featureide.core.roslaunch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.ConfigurationReader;

/**
 * compose all Feature to one launch file.
 * @author janSchum
 *
 */
public class RosComposer{

	private final LinkedHashSet<String> extensions;

	public RosComposer() {
		extensions = new LinkedHashSet<String>(Arrays.asList("launch"));
	}
	
	/**
	 * all selected features to one launch file.
	 * @param configFile current config
	 */
	public void compose(IFile configFile, IFeatureProject _project) {
		IFolder bFolder;
		Configuration c;
		ConfigurationReader reader;
		List<Feature> selectedFeatures;
		XPathFactory xPathfactory;
		XPath xpath;
		XPathExpression expr;

		List<Feature> orderFeatures;
		IFile file;

		try {
			c = new Configuration(_project.getFeatureModel());
			reader = new ConfigurationReader(c);
			reader.readFromFile(configFile);
			selectedFeatures = c.getSelectedFeatures();
			if (selectedFeatures != null) {

				HashMap<String, Document> launchFiles = new HashMap<String, Document>();
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				FeatureModel featureModel = _project.getFeatureModel();
				List<String> featureOrderList = featureModel
						.getFeatureOrderList();
				orderFeatures = new ArrayList<Feature>(selectedFeatures.size());
				for (String name : featureOrderList) {

					for (Feature feature : selectedFeatures) {
						if (feature.getName().equals(name)) {
							orderFeatures.add(feature);
							selectedFeatures.remove(feature);
							break;
						}
					}
				}
				for (Feature feature : orderFeatures) {
					/*
					 * read the files.
					 */
					if (feature.isAbstract()) {
						continue;
					}
					IFolder featureFolder = _project.getSourceFolder()
							.getFolder(feature.getName());
					for (IResource res : featureFolder.members()) {

						if (res instanceof IFile
								&& extensions()
										.contains(res.getFileExtension())) {

							file = (IFile) res;

							Document launchFile = launchFiles.get(file
									.getName());
							Node launch;

							if (launchFile == null) {
								launchFile = dBuilder.newDocument();
								launch = launchFile.createElement("launch");
								launchFile.appendChild(launch);
								launchFiles.put(file.getName(), launchFile);
							} else {
								launch = launchFile.getFirstChild();
							}
							Document doc = dBuilder.parse(file.getLocation()
									.toFile());
							NodeList nList;
							xPathfactory = XPathFactory.newInstance();
							xpath = xPathfactory.newXPath();
							expr = xpath.compile("/launch/* | /launch/comment() | /*[not(self::launch)] | /comment()");
							nList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
							
							launch.appendChild(launchFile.createTextNode("\n\n"));
							Node commentFeature;
							commentFeature = launchFile
									.createComment("\nFeature: "
											+ feature.getName()+"\n\t");
							launch.appendChild(commentFeature);
							
							for (int temp = 0; temp < nList.getLength(); temp++) {

								Node nNode = nList.item(temp);
								int type = nNode.getNodeType() ;
								if(type != Document.ELEMENT_NODE && type != Document.COMMENT_NODE){
									continue;
								}
								if (argIsDouble(nNode, launch)) {
									continue;
								}
								Node clone = nNode.cloneNode(true);
								launchFile.adoptNode(clone);
								launch.appendChild(clone);
							}
						}
					}
				}

				/*
				 * build the files from the document
				 */
				bFolder = _project.getBuildFolder();
				if (!bFolder.exists()) {
					bFolder.create(false, true, null);
				}

				DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
				DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
				LSSerializer writer = impl.createLSSerializer();
				writer.getDomConfig().setParameter("format-pretty-print",
						Boolean.TRUE);

				for (Entry<String, Document> e : launchFiles.entrySet()) {
					
					this.remapGenrater(e.getValue());

		            file = bFolder.getFile(e.getKey());
		            writer.writeToURI(e.getValue(), file.getLocationURI().toString());
				}

			}
		} catch (Exception e) {
			CorePlugin.getDefault().logError(e);
		}
	}

	private boolean argIsDouble(Node nNode, Node launch) {

		if (!nNode.getNodeName().equals("arg")) {
			return false;
		}
		if (nNode instanceof Element) {
			String argName;
			NodeList launchChilds;

			argName = ((Element) nNode).getAttribute("name");
			if (argName == null || argName.isEmpty()) {
				return false;
			}
			launchChilds = launch.getChildNodes();
			for (int temp = 0; temp < launchChilds.getLength(); temp++) {
				Node lChild;
				lChild = launchChilds.item(temp);
				if (lChild.getNodeName().equals("arg")
						&& lChild instanceof Element) {
					String name;
					name = ((Element) lChild).getAttribute("name");
					if (argName.equals(name)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void remapGenrater(Document _doc) {
		NodeList remaps;
		Element remap;
		String in;
		Map<String, List<Element>> remapMap;
		XPathFactory xPathfactory;
		XPath xpath;
		XPathExpression expr;
		int len;
		List<Element> listIn;
		
		try {
			xPathfactory = XPathFactory.newInstance();
			xpath = xPathfactory.newXPath();
			expr = xpath.compile("//node/remap[@to]");
			remaps = (NodeList) expr.evaluate(_doc, XPathConstants.NODESET);
			
			in = null;
			remapMap = new HashMap<String, List<Element>>();
			len = remaps.getLength();

			for (int x = 0; x < len; x++) {
				if (!(remaps.item(x) instanceof Element)) {
					continue;
				}
				remap = (Element) remaps.item(x);
				if (in == null) {
					in = remap.getAttribute("to");
					listIn = remapMap.get(in);
					if (listIn != null && !listIn.isEmpty()) {
						String genIn = listIn.get(listIn.size()-1).getAttribute("to");
						remap.setAttribute("to", genIn);
					}
				} else {
//					remapMap.put(in, remap.getAttribute("to"));
					listIn = remapMap.get(in);
					if(listIn == null){
						remapMap.put(in, new ArrayList<Element>(Arrays.asList(remap)));
					}
					else{
						listIn.add(remap);
					}
					in = null;
				}
			}
			for (List<Element> e : remapMap.values())
				Collections.reverse(e);
			replaceRemapArgs(remapMap, _doc, xpath, "value");
			replaceRemapArgs(remapMap, _doc, xpath, "default");

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void replaceRemapArgs(Map<String, List<Element>> remapMap,
			Document _doc, XPath xpath, String attributeName) throws XPathExpressionException {
		XPathExpression expr;
		int len;
		NodeList args;
		Element arg;
		
		for (Entry<String, List<Element>> e : remapMap.entrySet()) {
			expr = xpath.compile("//arg[@name and @" + attributeName + "='"
					+ e.getKey() + "']");
			args = (NodeList) expr.evaluate(_doc, XPathConstants.NODESET);
			len = args.getLength();

			for (int x = 0; x < len; x++) {
				if (!(args.item(x) instanceof Element)) {
					continue;
					}
				arg = (Element) args.item(x);
				arg.setAttribute(attributeName, findGenOut(arg,e.getValue(),attributeName));
				}
		}			
	}
	
	private String findGenOut(Element arg, List<Element> value,
			String _attributeName) {
		for (Element e : value) {
			if (e.compareDocumentPosition(arg) == Node.DOCUMENT_POSITION_FOLLOWING) {
				return e.getAttribute("to");
			}
		}
		return arg.getAttribute(_attributeName);
	}

	public LinkedHashSet<String> extensions() {
		return extensions;
	}
}
