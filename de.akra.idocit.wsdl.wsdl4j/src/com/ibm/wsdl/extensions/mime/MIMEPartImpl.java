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

package com.ibm.wsdl.extensions.mime;

import java.util.*;
import javax.wsdl.extensions.*;
import javax.wsdl.extensions.mime.*;
import javax.xml.namespace.*;

/**
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public class MIMEPartImpl implements MIMEPart
{
  protected QName elementType = MIMEConstants.Q_ELEM_MIME_PART;
  // Uses the wrapper type so we can tell if it was set or not.
  protected Boolean required = null;
  protected List extElements = new Vector();

  public static final long serialVersionUID = 1;

  /**
   * Set the type of this extensibility element.
   *
   * @param elementType the type
   */
  public void setElementType(QName elementType)
  {
    this.elementType = elementType;
  }

  /**
   * Get the type of this extensibility element.
   *
   * @return the extensibility element's type
   */
  public QName getElementType()
  {
    return elementType;
  }

  /**
   * Set whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public void setRequired(Boolean required)
  {
    this.required = required;
  }

  /**
   * Get whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public Boolean getRequired()
  {
    return required;
  }

  /**
   * Add an extensibility element. This is where the MIME
   * elements go.
   *
   * @param extElement the extensibility element to be added
   */
  public void addExtensibilityElement(ExtensibilityElement extElement)
  {
    extElements.add(extElement);
  }
  
  /**
   * Remove an extensibility element.
   *
   * @param extElement the extensibility element to be removed
   * @return the extensibility element which was removed
   */
  public ExtensibilityElement removeExtensibilityElement(ExtensibilityElement extElement)
  {
    if(extElements.remove(extElement))
      return extElement;
    else
      return null;
  }

  /**
   * Get all the extensibility elements defined here.
   */
  public List getExtensibilityElements()
  {
    return extElements;
  }

  public String toString()
  {
    StringBuffer strBuf = new StringBuffer();

    strBuf.append("MIMEPart (" + elementType + "):");
    strBuf.append("\nrequired=" + required);

    if (extElements != null)
    {
      Iterator extIterator = extElements.iterator();

      while (extIterator.hasNext())
      {
        strBuf.append("\n" + extIterator.next());
      }
    }

    return strBuf.toString();
  }
}