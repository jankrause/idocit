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
package de.akra.idocit.ui.actions;

import java.util.HashMap;
import java.util.HashSet;

import de.akra.idocit.core.structure.DescribedItem;
import de.akra.idocit.core.structure.ThematicGrid;
import de.akra.idocit.core.structure.ThematicRole;

/**
 * Factory for {@link ThematicGrid}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ThematicGridFactory extends AbsItemFactory
{

	@Override
	public DescribedItem createNewItem()
	{
		ThematicGrid grid = new ThematicGrid();

		grid.setName("<NOT DEFINED YET>");
		grid.setDescription("");
		grid.setVerbs(new HashSet<String>());
		grid.setRoles(new HashMap<ThematicRole, Boolean>());
		return grid;
	}
}