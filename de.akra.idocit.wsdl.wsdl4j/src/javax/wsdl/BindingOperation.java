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

package javax.wsdl;

import java.util.*;

/**
 * This interface represents a WSDL operation binding.
 * That is, it holds the information that would be
 * specified in the operation element contained within
 * a binding element.
 *
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public interface BindingOperation extends WSDLElement
{
  /**
   * Set the name of this operation binding.
   *
   * @param name the desired name
   */
  public void setName(String name);

  /**
   * Get the name of this operation binding.
   *
   * @return the operation binding name
   */
  public String getName();

  /**
   * Set the operation that this operation binding binds.
   *
   * @param operation the operation this operation binding binds
   */
  public void setOperation(Operation operation);

  /**
   * Get the operation that this operation binding binds.
   *
   * @return the operation that this operation binding binds
   */
  public Operation getOperation();

  /**
   * Set the input binding for this operation binding.
   *
   * @param bindingInput the new input binding
   */
  public void setBindingInput(BindingInput bindingInput);

  /**
   * Get the input binding for this operation binding.
   *
   * @return the input binding
   */
  public BindingInput getBindingInput();

  /**
   * Set the output binding for this operation binding.
   *
   * @param bindingOutput the new output binding
   */
  public void setBindingOutput(BindingOutput bindingOutput);

  /**
   * Get the output binding for this operation binding.
   *
   * @return the output binding for the operation binding
   */
  public BindingOutput getBindingOutput();

  /**
   * Add a fault binding.
   *
   * @param bindingFault the new fault binding
   */
  public void addBindingFault(BindingFault bindingFault);
  
  /**
   * Remove a fault binding.
   *
   * @param name the name of the fault binding to be removed
   * @return the BindingFault which was removed
   */
  public BindingFault removeBindingFault(String name);

  /**
   * Get the specified fault binding.
   *
   * @param name the name of the desired fault binding.
   * @return the corresponding fault binding, or null if there wasn't
   * any matching fault binding
   */
  public BindingFault getBindingFault(String name);

  /**
   * Get all the fault bindings associated with this operation binding.
   *
   * @return names of fault bindings
   */
  public Map getBindingFaults();

}