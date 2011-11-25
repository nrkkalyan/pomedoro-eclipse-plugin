package app;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Main program to convert the old formats to new formats.
 */
public class Program {
  
  /*
   * In Rabbit 1.0, data about sessions are actually the sum of perspective
   * durations grouped by dates. In Rabbit 1.1, session tracking has became
   * independent, so it no longer relies on perspective data. Therefore data
   * about perspectives before 1.1 will be extract out to separate session 
   * files (so that the user can still see them in Rabbit View), new data will 
   * be tracked by a session tracker and stored in new files.
   * 
   * For example, if we have a 1.0 data file "perspectiveEvents-2010-03.xml":
   * 
   * <events>
   *   <perspectiveEvents date="2010-03-01">
   *     <perspectiveEvent perspectiveId="org.eclipse.debug.ui.DebugPerspective" duration="1" />
   *     <perspectiveEvent perspectiveId="org.eclipse.pde.ui.PDEPerspective" duration="1" />
   *   </perspectiveEvents>
   * </events>
   * 
   * The above data will be copied and modified to another file 
   * "sessionEvents-2010-03.xml":
   * 
   * <events>
   *   <sessionEvents date="2010-03-01">
   *     <sessionEvents duration="2" />
   *   </sessionEvents>
   * </events>
   * 
   */
  
  /*
   * In Rabbit 1.0, file events are storing the IDs of the files, in 1.1 it will 
   * be changed to store the paths of the files. This way renaming/moving files
   * within the workspace will no longer be monitored. The Rabbit 1.1 way thinks
   * renaming/moving files are parts of the history of the projects, so they 
   * should be retained instead of mapping the old files to the new location
   * after renaming/moving. This also improves the performance of Rabbit as no
   * mapping needs to be held in memory.
   * 
   * Before:
   * 
   * <events>
   *   <fileEvents date="2010-03-01">
   *     <fileEvent fileId="1298237445" duration="123" />
   *   </fileEvents>
   * </events>
   * 
   * After:
   * 
   * <events>
   *   <fileEvents date="2010-03-01">
   *     <fileEvent filePath="/rabbit/plugin.xml" duration="123" />
   *   </fileEvents>
   * </events>
   * 
   */
  
  /** Date attribute */
  private static final String ATTR_DATE = "date";
  /** Duration attribute */
  private static final String ATTR_DURATION = "duration";
  /** File ID attribute */
  private static final String ATTR_FILE_ID = "fileId";
  /** File path attribute */
  private static final String ATTR_FILE_PATH = "filePath";
  /** Event list tag */
  private static final String TAG_EVENT_LIST = "events";
  /** File event tag */
  private static final String TAG_FILE_EVENT = "fileEvent";
  /** File event list tag */
  private static final String TAG_FILE_EVENT_LIST = "fileEvents";
  /** Perspective event tag */
  private static final String TAG_PERSPECTIVE_EVENT = "perspectiveEvent";
  /** Perspective event list tag */
  private static final String TAG_PERSPECTIVE_EVENT_LIST = "perspectiveEvents";
  /** Session event tag */
  private static final String TAG_SESSION_EVENT = "sessionEvent";
  /** Session event list tag */
  private static final String TAG_SESSION_EVENT_LIST = "sessionEvents";
  
  /**
   * An unmodifiable map of file IDs to file paths.
   */
  private static Map<String, String> fileIdToFilePath;

  private static DocumentBuilder builder;
  private static Transformer transformer;
  
  static {
    try {
      builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      System.err.println(e.getMessage());
    }
    
    try {
      transformer = TransformerFactory.newInstance().newTransformer();
    } catch (TransformerConfigurationException e) {
      System.err.println(e.getMessage());
    } catch (TransformerFactoryConfigurationError e) {
      System.err.println(e.getMessage());
    }
  }
  
  public static void run() {
    String path = System.getProperty("user.home") + File.separator + "Rabbit";
    File home = new File(path);
    File[] subdirs = home.listFiles();
    if (subdirs == null) {
      return;
    }
    
    Map<String, String> fileIdToPath = new HashMap<String, String>();
    Set<File> perspectiveEventFiles = new HashSet<File>();
    Set<File> fileEventFiles = new HashSet<File>();
    for (File dir : subdirs) {
      
      // Loads the files to be processed:
      for (File f : dir.listFiles()) {
        if (f.isDirectory()) {
          continue;
        }
        if (f.getName().startsWith("perspectiveEvents")) {
          perspectiveEventFiles.add(f);
        } else if (f.getName().startsWith("fileEvents")) {
          fileEventFiles.add(f);
        }
      }
      
      // Loads the resource file paths and IDs:
      File resourceFile = new File(dir.getAbsoluteFile() + File.separator 
          + "ResourceDB" + File.separator + "Resources.xml");
      if (resourceFile.exists()) {
        try {
          loadResourceMappings(resourceFile, fileIdToPath);
        } catch (SAXException e) {
          System.err.println(e.getMessage());
        } catch (IOException e) {
          System.err.println(e.getMessage());
        }
      } else {
        System.err.println("Not exist: " + resourceFile.getAbsolutePath());
      }
    }
    
    fileIdToFilePath = Collections.unmodifiableMap(fileIdToPath);
    

    // Start converting:
    for (File file : perspectiveEventFiles) {
      try {
        handlePerspectiveEventFile(file);
      } catch (SAXException e) {
        System.err.println(e.getMessage());
      } catch (IOException e) {
        System.err.println(e.getMessage());
      } catch (TransformerException e) {
        System.err.println(e.getMessage());
      }
    }
    for (File file : fileEventFiles) {
      try {
        handleFileEventFile(file, fileIdToFilePath);
      } catch (SAXException e) {
        System.err.println(e.getMessage());
      } catch (IOException e) {
        System.err.println(e.getMessage());
      } catch (TransformerException e) {
        System.err.println(e.getMessage());
      }
    }
    
    System.out.println("Done.");
  }
  
  /**
   * Converts the file event data to the new format.
   * 
   * @param file The file containing the XML data.
   * @param fileIdToPath The map containing the mapping of file IDs to file paths.
   * @throws NullPointerException If file is null.
   * @throws SAXException If any parse errors occur.
   * @throws IOException If any IO errors occur.
   * @throws TransformerException If an unrecoverable error occurs during the
   *           course of the transformation while saving the data.
   */
  static void handleFileEventFile(File file, Map<String, String> fileIdToPath) 
      throws SAXException, IOException, TransformerException {
    
    Document document = builder.parse(file);
    Element root = (Element) document.getFirstChild();
    NodeList eventLists = root.getElementsByTagName(TAG_FILE_EVENT_LIST);
    for (int i = 0; i < eventLists.getLength(); i++) {
      
      Element eventList = (Element) eventLists.item(i);
      NodeList oldEvents = eventList.getElementsByTagName(TAG_FILE_EVENT);
      for (int j = 0; j < oldEvents.getLength(); j++) {
        
        Element event = (Element) oldEvents.item(j);
        String fileId = event.getAttribute(ATTR_FILE_ID);
        if (fileId.equals("")) {
          continue; // No fileId, can't be processed, may be converted already?
        }
        
        String filePath = fileIdToPath.get(fileId);
        if (filePath != null) {
          event.setAttribute(ATTR_FILE_PATH, filePath);
          event.removeAttribute(ATTR_FILE_ID);
        } else {
          eventList.removeChild(event);
        }
      }
    }
    
    FileOutputStream out = new FileOutputStream(file);
    try {
      transformer.transform(new DOMSource(document), new StreamResult(out));
    } finally {
      out.close();
    }
  }
  
  /**
   * Converts the old perspective event data to separate session data.
   * Independent session tracking is introduced in Rabbit 1.1, will not rely on
   * perspective event data any more.
   * 
   * @param file The file containing the XML data, which has the format
   *          "perspectiveEvents-yyyy-MM.xml" as it name, where "yyyy"
   *          represents the year, and "MM" represents the month.
   * @throws NullPointerException If file is null.
   * @throws SAXException If any parse errors occur.
   * @throws IOException If any IO errors occur.
   * @throws TransformerException If an unrecoverable error occurs during the
   *           course of the transformation while saving the data.
   */
  static void handlePerspectiveEventFile(File file) 
      throws SAXException, IOException, TransformerException {
    
    Document oldDocument = builder.parse(file);
    Document newDocument = builder.newDocument();
    Element newRoot = newDocument.createElement(TAG_EVENT_LIST);
    newDocument.appendChild(newRoot);
    
    Element oldRoot = (Element) oldDocument.getFirstChild();
    NodeList oldEventLists = oldRoot.getElementsByTagName(TAG_PERSPECTIVE_EVENT_LIST);
    for (int i = 0; i < oldEventLists.getLength(); i++) {
      
      Element oldEventList = (Element) oldEventLists.item(i);
      Attr oldDate = oldEventList.getAttributeNode(ATTR_DATE);
      if (oldDate == null) {
        System.err.println("Attr == null");
        continue;
      }
      Element newEventList = newDocument.createElement(TAG_SESSION_EVENT_LIST);
      Attr newDate = newDocument.createAttribute(oldDate.getName());
      newDate.setValue(oldDate.getValue());
      newEventList.setAttributeNode(newDate);
      newRoot.appendChild(newEventList);
      
      NodeList oldEvents = oldEventList.getElementsByTagName(TAG_PERSPECTIVE_EVENT);
      for (int j = 0; j < oldEvents.getLength(); j++) {
        
        Element oldEvent = (Element) oldEvents.item(j);
        Attr oldDuration = oldEvent.getAttributeNode(ATTR_DURATION);
        if (oldDuration == null) {
          System.err.println("Duration == null");
          continue;
        }
        
        Element newEvent = newDocument.createElement(TAG_SESSION_EVENT);
        Attr newDuration = newDocument.createAttribute(oldDuration.getName());
        newDuration.setValue(oldDuration.getValue());
        newEvent.setAttributeNode(newDuration);
        newEventList.appendChild(newEvent);
      }
    }
    
    String fileName = file.getName();
    fileName = "sessionEvents" + fileName.substring(fileName.indexOf('-'));
    file = new File(file.getParentFile().getAbsoluteFile() + File.separator + fileName);
    FileOutputStream out = new FileOutputStream(file);
    try {
      transformer.transform(new DOMSource(newDocument), new StreamResult(out));
    } finally {
      out.close();
    }
  }
  
  /**
   * Loads the resource mapping from the file into the map.
   * 
   * @param file The file containing the mapping of file IDs and file paths.
   * @param idToPath The map that maps from file ID to file paths, results will
   *          be put into this map.
   * @throws NullPointerException If file is null.
   * @throws SAXException If any parse errors occur.
   * @throws IOException If any IO errors occur.
   */
  static void loadResourceMappings(File file, Map<String, String> idToPath) 
      throws SAXException, IOException {
    
    Document document = builder.parse(file);
    Element root = (Element) document.getFirstChild();
    NodeList resources = root.getElementsByTagName("resource");
    for (int i = 0; i < resources.getLength(); i++) {
      
      Element resource = (Element) resources.item(i);
      NodeList resourceIds = resource.getElementsByTagName("resourceId");
      for (int j = 0; j < resourceIds.getLength(); j++) {
        
        Element resourceId = (Element) resourceIds.item(j);
        String fileId = resourceId.getTextContent();
        String filePath = resource.getAttribute("path");
        idToPath.put(fileId, filePath);
      }
    }
  }
}
