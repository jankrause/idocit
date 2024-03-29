/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
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
package source;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.akra.idocit.core.extensions.Parser;

public final class ParsingService2
{

	private static Logger logger = Logger.getLogger(ParsingService2.class.getName());

	private static final String PARSER_EXTENSION_POINT_ID = "de.akra.idocit.extensions.Parser";

	private static Map<String, Parser> extensions;

	private ParsingService2()
	{}

	public static Parser getParser(String type)
	{
		if (extensions == null)
		{
			loadParserExtensions();
		}
		return extensions.get(type);
	}

	public static boolean isSupported(String type)
	{
		if (extensions == null)
		{
			loadParserExtensions();
		}
		return extensions.get(type) != null;
	}

	private static void loadParserExtensions()
	{
		// TODO register somewhere to get notifications for installed and uninstalled
		// extensions

		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(PARSER_EXTENSION_POINT_ID);

		extensions = Collections.synchronizedMap(new HashMap<String, Parser>());

		try
		{
			for (IConfigurationElement e : config)
			{
				logger.log(Level.INFO, "Evaluating extensions");
				final Object o = e.createExecutableExtension("class");
				if (o instanceof Parser)
				{
					Parser parser = (Parser) o;
					extensions.put(parser.getSupportedType(), parser);

					logger.log(Level.INFO,
							"Loaded parser \"" + parser.getClass().toString()
									+ "\" supports \"" + parser.getSupportedType()
									+ "\".");
				}
			}
		}
		catch (CoreException ex)
		{
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
}
