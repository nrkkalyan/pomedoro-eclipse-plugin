package app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @see Program
 */
public class ProgramTest {
  
  private static DocumentBuilder builder;
  private static Transformer transformer;
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    transformer = TransformerFactory.newInstance().newTransformer();
  }
  
  @Test
  public void testHandlePerspectiveEventFile() throws Exception {
    /*
     * Builds an XML document, called "perspectiveEvents-2010-01.xml" that 
     * represents the perspective events:
     * 
     * <events>
     *   <perspectiveEvents date="2010-01-01">
     *     <perspectiveEvent perspectiveId="abcdefg" duration="10" />
     *     <perspectiveEvent perspectiveId="1234567" duration="20" />
     *   </perspectiveEvents>
     * </events>
     * 
     * After conversion, a new document called "sessionEvents-2010-01.xml" 
     * should be created under the same directory that look like this:
     * 
     * <events>
     *   <sessionEvents date="2010-1-1">
     *     <sessionEvent duration="10" />
     *     <sessionEvent duration="20" />
     *   </sessionEvents>
     * </events>
     */
    long duration1 = 10;
    long duration2 = 20;
    
    // Builds the document: 
    Document document = builder.newDocument();
    Element root = document.createElement("events");
    document.appendChild(root);
    
    Element perspectiveEvents = document.createElement("perspectiveEvents");
    perspectiveEvents.setAttribute("date", "2010-01-01");
    root.appendChild(perspectiveEvents);
    
    Element event = document.createElement("perspectiveEvent");
    event.setAttribute("perspectiveId", "abcdefg");
    event.setAttribute("duration", duration1 + "");
    perspectiveEvents.appendChild(event);
    
    event = document.createElement("perspectiveEvent");
    event.setAttribute("perspectiveId", "1234567");
    event.setAttribute("duration", duration2 + "");
    perspectiveEvents.appendChild(event);
    
    // Writes the file to disk:
    File file = new File(System.getProperty("user.home") + File.separator + "perspectiveEvents-2010-01.xml");
    FileOutputStream out = new FileOutputStream(file);
    transformer.transform(new DOMSource(document), new StreamResult(out));
    out.close();
    
    // Call the method:
    Program.handlePerspectiveEventFile(file);

    // A new file should be created:
    File newFile = new File(file.getParentFile().getAbsoluteFile() + File.separator + "sessionEvents-2010-01.xml");
    assertTrue(newFile.exists());
    
    // Check the new file:
    document = builder.parse(newFile);
    root = (Element) document.getFirstChild();
    assertEquals("events", root.getNodeName());
    
    NodeList sessionEventList = root.getElementsByTagName("sessionEvents");
    assertEquals(1, sessionEventList.getLength());
    Element sessionEvents = (Element) sessionEventList.item(0);
    assertEquals("2010-01-01", sessionEvents.getAttribute("date"));
    
    NodeList eventList = sessionEvents.getElementsByTagName("sessionEvent");
    assertEquals(2, eventList.getLength());
    event = (Element) eventList.item(0);
    assertEquals(duration1 + "", event.getAttribute("duration"));
    assertNull(event.getAttributeNode("perspectiveId"));
    event = (Element) eventList.item(1);
    assertEquals(duration2 + "", event.getAttribute("duration"));
    assertNull(event.getAttributeNode("perspectiveId"));
    
    file.delete();
    newFile.delete();
  }
  
  @Test
  public void testHandleFileEventFile() throws Exception {
    /*
     * Builds an XML document that represents the file events:
     * 
     * <events>
     *   <fileEvents date="2010-01-01">
     *     <fileEvent fileId="abcdefg" duration="10" />
     *     <fileEvent fileId="1234567" duration="20" />
     *   </fileEvents>
     * </events>
     * 
     * After conversion, it should look like this:
     * 
     * <events>
     *   <fileEvents date="2010-01-01">
     *     <fileEvent filePath="/a/b/c/e.txt" duration="10" />
     *     <fileEvent filePath="/1/2/3.xml" duration="20" />
     *   </fileEvents>
     * </events>
     */
    
    long duration1 = 10;
    long duration2 = 20;
    String fileId1 = "abcdefg";
    String fileId2 = "1234567";
    String filePath1 = "/a/b/c/e.txt";
    String filePath2 = "/1/2/3.xml";
    Map<String, String> fileIdToPath = new HashMap<String, String>();
    fileIdToPath.put(fileId1, filePath1);
    fileIdToPath.put(fileId2, filePath2);
    
    Document document = builder.newDocument();
    Element root = document.createElement("events");
    document.appendChild(root);
    
    Element fileEvents = document.createElement("fileEvents");
    fileEvents.setAttribute("date", "2010-01-01");
    root.appendChild(fileEvents);
    
    Element event = document.createElement("fileEvent");
    event.setAttribute("fileId", fileId1);
    event.setAttribute("duration", String.valueOf(duration1));
    fileEvents.appendChild(event);
    
    event = document.createElement("fileEvent");
    event.setAttribute("fileId", fileId2);
    event.setAttribute("duration", String.valueOf(duration2));
    fileEvents.appendChild(event);
    
    File file = File.createTempFile("abc", String.valueOf(System.nanoTime()));
    FileOutputStream out = new FileOutputStream(file);
    transformer.transform(new DOMSource(document), new StreamResult(out));
    out.close();
    
    // Call the method:
    Program.handleFileEventFile(file, fileIdToPath);
    
    // Check the results:
    document = builder.parse(file);
    root = (Element) document.getFirstChild();
    assertEquals("events", root.getNodeName());
    
    NodeList fileEventList = root.getElementsByTagName("fileEvents");
    assertEquals(1, fileEventList.getLength());
    fileEvents = (Element) fileEventList.item(0);
    assertEquals("2010-01-01", fileEvents.getAttribute("date"));
    
    NodeList eventList = fileEvents.getElementsByTagName("fileEvent");
    assertEquals(2, eventList.getLength());
    event = (Element) eventList.item(0);
    assertEquals(filePath1, event.getAttribute("filePath"));
    assertEquals(String.valueOf(duration1), event.getAttribute("duration"));
    event = (Element) eventList.item(1);
    assertEquals(filePath2, event.getAttribute("filePath"));
    assertEquals(String.valueOf(duration2), event.getAttribute("duration"));
  }
  
  @Test
  public void testHandleFileEventFile_alreadyConvertedFile() throws Exception {
    /*
     * Builds an file event XML document that has the new format, calling 
     * converting on it should not change anything.
     * 
     * <events>
     *   <fileEvents date="2010-01-01">
     *     <fileEvent filePath="/a/b.txt" duration="10" />
     *   </fileEvents>
     * </events>
     */
    
    final String fileId = "1";
    final String filePath = "/a/b.txt";
    final String date = "2010-01-01";
    final String duration = "10";
    Map<String, String> fileIdToPath = new HashMap<String, String>();
    fileIdToPath.put(fileId, filePath);
    
    Document doc = builder.newDocument();
    Element root = doc.createElement("events");
    doc.appendChild(root);
    
    Element fileEvents = doc.createElement("fileEvents");
    fileEvents.setAttribute("date", date);
    root.appendChild(fileEvents);
    
    Element event = doc.createElement("fileEvent");
    event.setAttribute("filePath", filePath);
    event.setAttribute("duration", duration);
    fileEvents.appendChild(event);
    
    File file = File.createTempFile("abc", "123");
    transformer.transform(new DOMSource(doc), new StreamResult(file));
    
    Program.handleFileEventFile(file, fileIdToPath);
    
    doc = builder.parse(file);
    root = (Element) doc.getFirstChild();
    assertEquals("events", root.getNodeName());
    
    NodeList list = root.getChildNodes();
    assertEquals(1, list.getLength());
    fileEvents = (Element) list.item(0);
    assertEquals("fileEvents", fileEvents.getNodeName());
    assertEquals(date, fileEvents.getAttribute("date"));
    
    assertEquals(1, fileEvents.getElementsByTagName("fileEvent").getLength());
    event = (Element) fileEvents.getElementsByTagName("fileEvent").item(0);
    assertEquals("fileEvent", event.getNodeName());
    assertEquals(duration, event.getAttribute("duration"));
    assertEquals(filePath, event.getAttribute("filePath"));
  }

  @Test
  public void testLoadResourceMappings() throws Exception {
    /*
     * Builds an XML document that represents the resources mappings, all items
     * should be loaded into the Map<String, String> where keys are from
     * "resourceId" and values are from @path:
     * 
     * <resources>
     *   <resource path="/a/b/c/e.txt">
     *     <resourceId>abcdefg</resourceId>
     *     <resourceId>wxyz</resourceId>
     *   </resource>
     *   <resource path="/1/2/3.xml">
     *     <resourceId>1234567</resourceId>
     *   </resource>
     * </resources>
     */
    String fileId1 = "abcdefg";
    String fileId11 = "wxyz";
    String fileId2 = "1234567";
    String filePath1 = "/a/b/c/e.txt";
    String filePath2 = "/1/2/3.xml";
    
    Document document = builder.newDocument();
    Element root = document.createElement("resources");
    document.appendChild(root);
    
    Element resource = document.createElement("resource");
    resource.setAttribute("path", filePath1);
    root.appendChild(resource);
    Element resourceId = document.createElement("resourceId");
    resourceId.setTextContent(fileId1);
    resource.appendChild(resourceId);
    resourceId = document.createElement("resourceId");
    resourceId.setTextContent(fileId11);
    resource.appendChild(resourceId);
    
    resource = document.createElement("resource");
    resource.setAttribute("path", filePath2);
    root.appendChild(resource);
    resourceId = document.createElement("resourceId");
    resourceId.setTextContent(fileId2);
    resource.appendChild(resourceId);
    
    File file = File.createTempFile("abc", "123");
    FileOutputStream out = new FileOutputStream(file);
    transformer.transform(new DOMSource(document), new StreamResult(out));
    out.close();
    
    Map<String, String> fileIdToPath = new HashMap<String, String>();
    Program.loadResourceMappings(file, fileIdToPath);
    
    assertEquals(3, fileIdToPath.size());
    assertTrue(fileIdToPath.containsKey(fileId1));
    assertTrue(fileIdToPath.containsKey(fileId11));
    assertTrue(fileIdToPath.containsKey(fileId2));
    assertEquals(filePath1, fileIdToPath.get(fileId1));
    assertEquals(filePath1, fileIdToPath.get(fileId11));
    assertEquals(filePath2, fileIdToPath.get(fileId2));
  }
}
