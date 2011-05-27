/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.ui.composites;

import org.pocui.core.actions.IActionConfiguration;

import de.akra.idocit.ui.actions.ThematicGridFactory;

/**
 * Action configuration for {@link ManageThematicGridsComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class ManageThematicGridsCompositeAC implements IActionConfiguration
{

	@Override
	public boolean isComplete()
	{
		return true;
	}

	/**
	 * 
	 * @return the {@link ThematicGridFactory}.
	 */
	public ThematicGridFactory getThematicGridFactory()
	{
		return new ThematicGridFactory();
	}
}