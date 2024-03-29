/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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
package de.akra.idocit.java.ui;

import java.util.logging.Logger;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin implements IStartup
{
	/**
	 * Logger.
	 */
	static Logger log = Logger.getLogger(Activator.class.getName());

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "de.akra.idocit.java.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private RecommendedGridsViewHandler recommendedGridsViewHandler;

	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		this.recommendedGridsViewHandler = new RecommendedGridsViewHandler();
		this.recommendedGridsViewHandler.start();
	}

	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
		this.recommendedGridsViewHandler.stop();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return plugin;
	}

	@Override
	public void earlyStartup()
	{
		log.info("Java Support UI plugin has been loaded.");
	}
}
