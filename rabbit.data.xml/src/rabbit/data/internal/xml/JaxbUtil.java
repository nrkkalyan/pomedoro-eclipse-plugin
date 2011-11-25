/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.data.internal.xml;

import rabbit.data.internal.xml.schema.events.ObjectFactory;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

/**
 * Utility class contains JAXB related operations.
 */
public class JaxbUtil {

  private static JAXBContext context;
  private static Marshaller mar;
  private static Unmarshaller unmar;

  static {
    try {
      initialize();
    } catch (JAXBException e) {
      try {
        initialize();
      } catch (JAXBException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Marshals the given element to file.
   * 
   * @param e The element.
   * @param f The file.
   * @throws JAXBException If any unexpected problem occurs during the
   *           marshalling.
   * @throws MarshalException If the ValidationEventHandler returns false from
   *           its handleEvent method or the Marshaller is unable to marshal obj
   *           (or any object reachable from obj).
   * @throws IllegalArgumentException If any of the method parameters are null
   */
  public static void marshal(JAXBElement<?> e, File f) throws JAXBException {
    mar.marshal(e, f);
  }

  /**
   * Unmarshals a file.
   * 
   * @param f The file.
   * @return The object unmarshaled.
   * @throws JAXBException If any unexpected errors occur while unmarshalling
   * @throws UnmarshalException If the ValidationEventHandler returns false from
   *           its handleEvent method or the Unmarshaller is unable to perform
   *           the XML to Java binding.
   * @throws IllegalArgumentException If the file parameter is null
   */
  public static Object unmarshal(File f) throws JAXBException {
    return unmar.unmarshal(f);
  }

  private static void initialize() throws JAXBException {
    context = JAXBContext.newInstance(ObjectFactory.class);
    mar = context.createMarshaller();
    unmar = context.createUnmarshaller();
  }

  private JaxbUtil() {
  }
}
