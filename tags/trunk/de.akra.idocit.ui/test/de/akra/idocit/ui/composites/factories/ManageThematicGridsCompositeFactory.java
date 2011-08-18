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
package de.akra.idocit.ui.composites.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.ui.composites.ManageThematicGridsComposite;
import de.akra.idocit.ui.composites.ManageThematicGridsCompositeAC;
import de.akra.idocit.ui.composites.ManageThematicGridsCompositeSelection;

/**
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class ManageThematicGridsCompositeFactory
		implements
		ICompositeFactory<ManageThematicGridsCompositeAC, EmptyResourceConfiguration, ManageThematicGridsCompositeSelection>
{

	@Override
	public ManageThematicGridsComposite createComposite(Composite arg0)
	{
		ManageThematicGridsComposite composite = new ManageThematicGridsComposite(arg0,
				new ManageThematicGridsCompositeAC());

		ManageThematicGridsCompositeSelection selection = new ManageThematicGridsCompositeSelection();

		List<ThematicRole> roles = new ArrayList<ThematicRole>();
		roles.add(new ThematicRole("AGENT"));
		roles.add(new ThematicRole("ACTION"));

		selection.setRoles(roles);

		ThematicGrid grid = new ThematicGrid();
		grid.setDescription("My Description");
		grid.setName("My Name");
		selection.setActiveThematicGrid(grid);

		List<ThematicGrid> grids = new ArrayList<ThematicGrid>();
		grids.add(grid);
		selection.setThematicGrids(grids);

		Map<ThematicRole, Boolean> myRoles = new HashMap<ThematicRole, Boolean>();
		for (ThematicRole role : roles)
		{
			myRoles.put(role, Boolean.TRUE);
		}
		grid.setRoles(myRoles);

		Set<String> verbs = new HashSet<String>();
		verbs.add("get");
		verbs.add("put");
		grid.setVerbs(verbs);

		composite.setSelection(selection);

		return composite;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}
}
