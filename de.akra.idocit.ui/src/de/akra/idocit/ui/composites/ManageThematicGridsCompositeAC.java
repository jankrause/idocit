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
