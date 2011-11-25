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

import rabbit.data.internal.xml.JaxbUtil;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.ObjectFactory;

import org.junit.Test;

import java.io.File;

import javax.xml.bind.JAXBElement;

/**
 * @see JaxbUtil
 */
public class JaxbUtilTest {

  private ObjectFactory objectFactory = new ObjectFactory();

  @Test(expected = IllegalArgumentException.class)
  public void testMarshal_withElementNull() throws Exception {
    File f = File.createTempFile("tmpJaxb", "abc");
    JaxbUtil.marshal(null, f);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMarshal_withFileNull() throws Exception {
    JAXBElement<EventListType> doc = objectFactory.createEvents(objectFactory
        .createEventListType());
    JaxbUtil.marshal(doc, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMarshal_withNull() throws Exception {
    JaxbUtil.marshal(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnmarshal_withFileNull() throws Exception {
    JaxbUtil.unmarshal(null);
  }
}
