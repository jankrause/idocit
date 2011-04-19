package de.akra.idocit.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.akra.idocit.structure.ThematicGrid;
import de.akra.idocit.structure.ThematicRole;
import de.jankrause.diss.wsdl.common.services.WSDLTaggingService;
import de.jankrause.diss.wsdl.common.services.verbs.VerbClassificationService;
import de.jankrause.diss.wsdl.common.structure.wsdl.TaggedOperationIdentifier;
import de.jankrause.diss.wsdl.common.utils.StringUtils;

/**
 * Service to derive thematic grids for a verb out of an identifier.
 * 
 * @author Jan Christian Krause
 * 
 */
public class ThematicGridService
{
	private static final Logger logger = Logger.getLogger(ThematicGridService.class
			.getName());

	/**
	 * Finds the thematic grids for the given <code>verb</code>.
	 * 
	 * @param verb
	 *            The verb for which should be find the thematic grids.
	 * @return List of found {@link ThematicGrid}s.
	 */
	private static List<ThematicGrid> findMatchingGrids(String verb)
	{
		List<ThematicGrid> matchingGrids = new ArrayList<ThematicGrid>();
		List<ThematicGrid> definedGrids = PersistenceService.loadThematicGrids();

		for (ThematicGrid definedGrid : definedGrids)
		{
			Set<String> verbs = definedGrid.getVerbs();

			if (verbs.contains(verb))
			{
				matchingGrids.add(definedGrid);
			}
		}

		return matchingGrids;
	}

	/**
	 * Finds the thematic grids for the verb out of the <code>identifier</code>.
	 * 
	 * @param identifier
	 *            The identifier from which the verb should be extracted and the thematic
	 *            grids should be derived.
	 * @return Map of thematic grid names linking to Set of {@link ThematicRole}s.
	 */
	public static Map<String, Map<ThematicRole, Boolean>> deriveThematicGrid(String identifier)
	{
		Map<String, Map<ThematicRole, Boolean>> matchingRoles = new HashMap<String, Map<ThematicRole, Boolean>>();

		// Tag the tokens and find the verb.
		List<String> identifiers = new ArrayList<String>();
		identifiers.add(identifier);

		try
		{
			// Add blanks before each word. A word is identified by applying
			// the camel case-syntax.
			List<TaggedOperationIdentifier> operationIdentifiers = WSDLTaggingService
					.performTwoPhaseIdentifierTagging(StringUtils
							.addBlanksToCamelSyntax(identifiers));

			if (!operationIdentifiers.isEmpty())
			{
				// Identify the verb.
				String verb = VerbClassificationService
						.findFirstVerb(operationIdentifiers.get(0));

				if (!VerbClassificationService.EMPTY_VERB.equals(verb))
				{
					// Classify the verb.
					List<ThematicGrid> matchingVerbClasses = findMatchingGrids(verb);

					if (!matchingVerbClasses.isEmpty())
					{
						// Lookup the recommended arguments and modificators.
						for (ThematicGrid verbClass : matchingVerbClasses)
						{
							matchingRoles.put(verbClass.getName(), verbClass.getRoles());
						}
					}
				}
			}
		}
		catch (IOException ioEx)
		{
			logger.log(Level.WARNING, "The identifier could not be tagged.", ioEx);
		}

		return matchingRoles;
	}
}
