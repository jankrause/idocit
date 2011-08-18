/*******************************************************************************
 * Copyright 2011 AKRA GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package javax.wsdl.extensions.soap;

import java.util.*;
import javax.wsdl.extensions.*;

/**
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public interface SOAPBody extends ExtensibilityElement, java.io.Serializable
{
  /**
   * Set the parts for this SOAP body.
   *
   * @param parts the desired parts
   */
  public void setParts(List parts);

  /**
   * Get the parts for this SOAP body.
   */
  public List getParts();

  /**
   * Set the use for this SOAP body.
   *
   * @param use the desired use
   */
  public void setUse(String use);

  /**
   * Get the use for this SOAP body.
   */
  public String getUse();

  /**
   * Set the encodingStyles for this SOAP body.
   *
   * @param encodingStyles the desired encodingStyles
   */
  public void setEncodingStyles(List encodingStyles);

  /**
   * Get the encodingStyles for this SOAP body.
   */
  public List getEncodingStyles();

  /**
   * Set the namespace URI for this SOAP body.
   *
   * @param namespaceURI the desired namespace URI
   */
  public void setNamespaceURI(String namespaceURI);

  /**
   * Get the namespace URI for this SOAP body.
   */
  public String getNamespaceURI();
}