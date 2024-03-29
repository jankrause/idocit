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
package de.akra.idocit.core.services.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.CompactWriter;

import de.akra.idocit.common.constants.Misc;
import de.akra.idocit.common.factories.XStreamFactory;
import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.DescribedItemNameComparator;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.core.IDocItActivator;
import de.akra.idocit.core.constants.PreferenceStoreConstants;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.extensions.Parser;
import de.akra.idocit.core.extensions.ValidationReport;
import de.akra.idocit.core.listeners.IConfigurationChangeListener;
import de.akra.idocit.core.services.PersistenceService;
import de.akra.idocit.core.utils.ResourceUtils;

/**
 * Provides services to load and to write an {@link InterfaceArtifact}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class EclipsePersistenceService implements PersistenceService
{
	private static final String ERR_MSG_LISTENER_NULL = "Listener must not be null";

	/*
	 * Constants
	 */
	private static final String XML_ALIAS_ADDRESSEE = "addressee";

	/*
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(EclipsePersistenceService.class
			.getName());

	private boolean isInitialized = false;

	private Set<IConfigurationChangeListener> addresseChangeListeners = new ConcurrentSkipListSet<IConfigurationChangeListener>(
			new ConfigurationChangeListenerComparator());
	private Set<IConfigurationChangeListener> thematicGridChangeListeners = new ConcurrentSkipListSet<IConfigurationChangeListener>(
			new ConfigurationChangeListenerComparator());
	private Set<IConfigurationChangeListener> thematicRoleChangeListeners = new ConcurrentSkipListSet<IConfigurationChangeListener>(
			new ConfigurationChangeListenerComparator());

	@Override
	public void init()
	{
		isInitialized = true;
		logger.info("The PersistenceService is now initialized.");
	}

	@Override
	public InterfaceArtifact loadInterface(IFile iFile) throws Exception
	{
		logger.entering(EclipsePersistenceService.class.getName(), "loadInterface");

		// there must be a file extension to determine the type
		if (iFile == null || iFile.getFileExtension().isEmpty())
		{
			logger.log(Level.SEVERE, "iFile is not initialized or has no extension."
					+ (iFile != null ? " iFile=" + iFile.getFullPath().toOSString() : ""));
			return InterfaceArtifact.NOT_SUPPORTED_ARTIFACT;
		}

		// get Parser depending on the file extension
		Parser parser = ServiceManager.getInstance().getParsingService()
				.getParser(iFile.getFileExtension());

		if (parser == null)
		{
			logger.log(Level.INFO, "Not supported type: " + iFile.getFileExtension());
			return InterfaceArtifact.NOT_SUPPORTED_ARTIFACT;
		}

		InterfaceArtifact result = parser.parse(iFile);

		logger.exiting(EclipsePersistenceService.class.getName(), "loadInterface", result);
		return result;
	}

	@Override
	public void writeInterface(InterfaceArtifact interfaceArtifact, IFile iFile)
			throws Exception
	{
		if (interfaceArtifact != null && iFile != null)
		{
			// get Parser depending on the file extension
			Parser parser = ServiceManager.getInstance().getParsingService()
					.getParser(iFile.getFileExtension());
			if (parser != null)
			{
				parser.write(interfaceArtifact, iFile);
			}
			else
			{
				logger.log(Level.SEVERE, "Try to write into a not supported file.");
			}
		}
		else
		{
			logger.log(Level.SEVERE,
					"The input parameters must be initalized. interfaceStructure="
							+ interfaceArtifact + "; iFile=" + iFile);
			throw new IllegalArgumentException("The input parameters must be initalized.");
		}
	}

	/**
	 * 
	 * @return true, if the {@link IPreferenceStore} of Eclipse is loaded and it contains
	 *         a value for {@link PreferenceStoreConstants#ADDRESSEES}.
	 */
	@Override
	public boolean areAddresseesInitialized()
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		return prefStore.contains(PreferenceStoreConstants.ADDRESSEES)
				&& !"".equals(prefStore.getString(PreferenceStoreConstants.ADDRESSEES));
	}

	/**
	 * 
	 * @return true, if the {@link IPreferenceStore} of Eclipse is loaded and it contains
	 *         a value for {@link PreferenceStoreConstants#THEMATIC_ROLES}.
	 */
	@Override
	public boolean areThematicRolesInitialized()
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		return prefStore.contains(PreferenceStoreConstants.THEMATIC_ROLES)
				&& !"".equals(prefStore
						.getString(PreferenceStoreConstants.THEMATIC_ROLES));
	}

	private XStream configureXStreamForAddressee()
	{
		XStream stream = new XStream();
		stream.alias(XML_ALIAS_ADDRESSEE, Addressee.class);
		return stream;
	}

	/**
	 * Converts a {@link PropertyResourceBundle} to a Map.
	 * 
	 * @param resBundle
	 *            The {@link PropertyResourceBundle} to convert.
	 * @return Map with item name mapping to it's description.
	 */
	private Map<String, String> convertResourceBundleToMap(
			PropertyResourceBundle resBundle)
	{
		Map<String, String> roles = new HashMap<String, String>();
		Enumeration<String> keys = resBundle.getKeys();

		while (keys.hasMoreElements())
		{
			String role = keys.nextElement();
			String description = resBundle.getString(role);

			roles.put(role, description);
		}

		return roles;
	}

	/**
	 * Reads the in the plugin stored thematic roles.
	 * 
	 * @return Map of thematic role names mapping to their description.
	 */
	private Map<String, String> getInitialThematicRoles()
	{
		return convertResourceBundleToMap(IDocItActivator.getDefault()
				.getThematicRoleResourceBundle());
	}

	@Override
	public void persistAddressees(List<Addressee> addressees)
	{
		logger.fine("persist Addresses");
		// TODO delete old entries from preference store
		final IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		final XStream stream = configureXStreamForAddressee();

		for (final Addressee a : addressees)
		{
			a.setDescription(StringUtils.removeLineBreaks(a.getDescription()));
		}

		final String addresseeXML = stream.toXML(addressees);
		prefStore.putValue(PreferenceStoreConstants.ADDRESSEES, addresseeXML);

		logger.fine("notify addressee change listeners");
		notifyListeners(this.addresseChangeListeners);
	}

	@Override
	public void persistThematicRoles(List<ThematicRole> roles)
	{
		logger.fine("persist ThematicRoles");
		// TODO delete old entries from preference store
		final IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		final XStream stream = XStreamFactory.configureXStreamForThematicRoles();
		Collections.sort(roles, DescribedItemNameComparator.getInstance());

		for (final ThematicRole role : roles)
		{
			if (role.getDescription() != null)
			{
				role.setDescription(StringUtils.removeLineBreaks(role.getDescription()));
			}
		}

		final String rolesXML = stream.toXML(roles);
		prefStore.putValue(PreferenceStoreConstants.THEMATIC_ROLES, rolesXML);

		logger.fine("notify thematic role change listeners");
		notifyListeners(this.thematicRoleChangeListeners);
	}

	@Override
	public List<ThematicRole> readInitialThematicRoles()
	{
		List<ThematicRole> roles = new ArrayList<ThematicRole>();
		Map<String, String> initialRoles = getInitialThematicRoles();

		for (Entry<String, String> entry : initialRoles.entrySet())
		{
			ThematicRole role = new ThematicRole(entry.getKey());
			role.setDescription(entry.getValue());

			roles.add(role);
		}

		Collections.sort(roles, DescribedItemNameComparator.getInstance());

		return roles;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Addressee> readInitialAddressees()
	{
		XStream stream = XStreamFactory.configureXStreamForAddressee();
		List<Addressee> addressees = new ArrayList<Addressee>();

		try
		{
			addressees = (List<Addressee>) stream.fromXML(ResourceUtils
					.getResourceInputStream(ResourceUtils.ADDRESSEE_RESOURCE_FILE));
		}
		catch (XStreamException e)
		{
			logger.log(Level.WARNING, "No addressees were loaded.", e);
		}

		return addressees;
	}

	/**
	 * Removes the formatting characters from the given addressees.
	 * 
	 * @param grids
	 *            The addressees to cleanup
	 * @return The cleaned addressees
	 */
	private static List<Addressee> removeFormattingCharsAddressee(
			List<Addressee> addressees)
	{
		for (Addressee addressee : addressees)
		{
			addressee.setDescription(StringUtils.cleanFormatting(addressee
					.getDescription()));
			addressee.setName(StringUtils.cleanFormatting(addressee.getName()));
		}

		return addressees;
	}

	/**
	 * Load the {@link Addressee}s that are configured in the Eclipse preference pages for
	 * iDocIt!.
	 * 
	 * @return List of {@link Addressee}s.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Addressee> loadConfiguredAddressees()
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = configureXStreamForAddressee();
		List<Addressee> addressees = new ArrayList<Addressee>();

		String prefVal = prefStore.getString(PreferenceStoreConstants.ADDRESSEES);
		if (!prefVal.isEmpty())
		{
			try
			{
				addressees = (List<Addressee>) stream.fromXML(prefVal);
			}
			catch (XStreamException e)
			{
				logger.log(Level.WARNING, "No addressees were loaded.", e);
			}
		}

		return removeFormattingCharsAddressee(addressees);
	}

	/**
	 * Load the {@link ThematicRole}s that are configured in the Eclipse preference pages
	 * for iDocIt!.
	 * 
	 * @return List of {@link ThematicRole}s.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ThematicRole> loadThematicRoles()
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = XStreamFactory.configureXStreamForThematicRoles();
		List<ThematicRole> roles = new ArrayList<ThematicRole>();

		String prefVal = prefStore.getString(PreferenceStoreConstants.THEMATIC_ROLES);
		if (!prefVal.isEmpty())
		{
			try
			{
				roles = (List<ThematicRole>) stream.fromXML(prefVal);
			}
			catch (XStreamException e)
			{
				logger.log(Level.WARNING, "No thematic role were loaded.", e);
			}
		}

		Collections.sort(roles, DescribedItemNameComparator.getInstance());

		return removeFormattingCharsRoles(roles);
	}

	/**
	 * Removes the formatting characters from the given thematic roles.
	 * 
	 * @param grids
	 *            The roles to cleanup
	 * @return The cleaned roles
	 */
	private static List<ThematicRole> removeFormattingCharsRoles(List<ThematicRole> roles)
	{
		for (ThematicRole role : roles)
		{
			role.setDescription(StringUtils.cleanFormatting(role.getDescription()));
			role.setName(StringUtils.cleanFormatting(role.getName()));
		}

		return roles;
	}

	/**
	 * Removes the formatting characters from the given thematic grids.
	 * 
	 * @param grids
	 *            The grids to cleanup
	 * @return The cleaned grids
	 */
	private static List<ThematicGrid> removeFormattingCharsThematicGrids(
			List<ThematicGrid> grids)
	{
		for (ThematicGrid grid : grids)
		{
			grid.setDescription(StringUtils.cleanFormatting(grid.getDescription()));
			grid.setName(StringUtils.cleanFormatting(grid.getName()));

			Map<ThematicRole, Boolean> cleanedRoles = new HashMap<ThematicRole, Boolean>();

			for (Entry<ThematicRole, Boolean> roleEntry : grid.getRoles().entrySet())
			{
				if (roleEntry.getKey() != null)
				{
					ThematicRole cleanedRole = new ThematicRole();
					cleanedRole.setName(StringUtils.cleanFormatting(roleEntry.getKey()
							.getName()));
					cleanedRole.setDescription(StringUtils.cleanFormatting(roleEntry
							.getKey().getDescription()));

					cleanedRoles.put(cleanedRole, roleEntry.getValue());
				}
				else
				{
					logger.warning("No name for thematic role "
							+ String.valueOf(roleEntry));
				}
			}

			grid.setRoles(cleanedRoles);
		}

		return grids;
	}

	/**
	 * Load the {@link ThematicGrid}s that are configured in the Eclipse preference pages
	 * for iDocIt!.
	 * 
	 * @return List of {@link ThematicGrid}s.
	 * 
	 * @throws UnitializedIDocItException
	 *             If the default grids should be loaded, but their input-stream has not
	 *             been initialized yet via {@link PersistenceService#init(InputStream)}.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ThematicGrid> loadThematicGrids() throws UnitializedIDocItException
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		String verbClassRoleAssocsXML = prefStore
				.getString(PreferenceStoreConstants.VERBCLASS_ROLE_MAPPING);

		if ((verbClassRoleAssocsXML != null)
				&& (!"".equals(verbClassRoleAssocsXML.trim())))
		{
			try
			{
				List<ThematicGrid> grids = (List<ThematicGrid>) XStreamFactory
						.configureXStreamForThematicGrid()
						.fromXML(verbClassRoleAssocsXML);
				return removeFormattingCharsThematicGrids(grids);
			}
			catch (XStreamException e)
			{
				logger.log(Level.SEVERE, e.getMessage());
				throw new RuntimeException(
						"The preference store of this workspace contains no configured thematic grids, but it is expected to do so! It is recommended to setup a new, consistent workspace.");
			}
		}
		else
		{
			if (isInitialized)
			{
				InputStream defaultThematicGrids = ResourceUtils
						.getResourceInputStream(ResourceUtils.THEMATIC_GRIDS_RESOURCE_FILE);
				List<ThematicGrid> defaultGrids = (List<ThematicGrid>) XStreamFactory
						.configureXStreamForThematicGrid().fromXML(defaultThematicGrids);

				// Keep the default grids in preference store for the next time.
				persistThematicGrids(removeFormattingCharsThematicGrids(defaultGrids));

				return defaultGrids;
			}
			else
			{
				throw new UnitializedIDocItException(
						"The PersistenceService has not been initialized yet.");
			}
		}
	}

	@Override
	public void persistThematicGrids(List<ThematicGrid> verbClassRoleAssociations)
	{
		logger.fine("persist ThematicGrids");
		final IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		final XStream xs = XStreamFactory.configureXStreamForThematicGrid();
		final StringWriter sw = new StringWriter();
		xs.marshal(verbClassRoleAssociations, new CompactWriter(sw));

		final String verbClassRoleAssocsXML = sw.toString();

		prefStore.putValue(PreferenceStoreConstants.VERBCLASS_ROLE_MAPPING,
				verbClassRoleAssocsXML);

		logger.fine("notify thematic grid change listeners");
		notifyListeners(this.thematicGridChangeListeners);
	}

	@Override
	public void exportThematicGridsAsXml(final File destination, List<ThematicGrid> grids)
			throws IOException
	{
		XStream lvXStream = XStreamFactory.configureXStreamForThematicGrid();
		Writer lvWriter = null;

		try
		{
			lvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					destination), Charset.forName(Misc.DEFAULT_CHARSET)));
			lvXStream.toXML(grids, lvWriter);
		}
		finally
		{
			if (lvWriter != null)
			{
				lvWriter.close();
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ThematicGrid> importThematicGrids(final File source) throws IOException
	{
		Reader reader = null;
		List<ThematicGrid> grids = null;

		try
		{
			XStream xmlStream = XStreamFactory.configureXStreamForThematicGrid();
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(source), Charset.forName(Misc.DEFAULT_CHARSET)));
			grids = (List<ThematicGrid>) xmlStream.fromXML(reader);
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}

		return grids;
	}

	/**
	 * Converts the given verbs into an unordered list (HTML). Before generating the
	 * HTML-list, the verbs are sorted alphabetically.
	 * 
	 * @param verbs
	 *            The verbs to export
	 * @return The HTML-representation of the given verbs
	 */
	private String convertVerbListIntoHtml(Set<String> verbs)
	{
		List<String> sortedVerbs = new ArrayList<String>();
		sortedVerbs.addAll(verbs);
		Collections.sort(sortedVerbs);

		StringBuffer buffer = new StringBuffer("\n\t\t\t\t\t<ul>");

		for (String verb : sortedVerbs)
		{
			buffer.append("\n\t\t\t\t\t\t<li>" + verb + "</li>");
		}

		buffer.append("\n\t\t\t\t\t</ul>");

		return buffer.toString();
	}

	/**
	 * Converts the given map of {@link ThematicRole}s to booleans into an unordered list
	 * (HTML). The booleans indicate whether the role is mandatory or optional. Mandatory
	 * are printed in bold-font.
	 * 
	 * @param roles
	 *            The {@link ThematicRole}s to export
	 * @return The HTML-representation of the given roles
	 */
	private String convertRoleMapIntoHtml(Map<ThematicRole, Boolean> roles)
	{
		List<ThematicRole> sortedVerbs = new ArrayList<ThematicRole>();
		sortedVerbs.addAll(roles.keySet());
		Collections.sort(sortedVerbs);

		StringBuffer buffer = new StringBuffer("\n\t\t\t\t\t<ul>");

		for (ThematicRole role : sortedVerbs)
		{
			if (roles.get(role).booleanValue())
			{
				buffer.append("\n\t\t\t\t\t\t<li><b>" + role.getName() + "</b></li>");
			}
			else
			{
				buffer.append("\n\t\t\t\t\t\t<li>" + role.getName() + "</li>");
			}
		}

		buffer.append("\n\t\t\t\t\t</ul>");

		return buffer.toString();
	}

	@Override
	public void exportThematicGridsAsHtml(File destination, List<ThematicGrid> grids)
			throws IOException
	{
		BufferedWriter writer = null;

		try
		{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					destination), Charset.forName(Misc.DEFAULT_CHARSET)));
			writer.write("<html>\n\t<head/>\n\t<body>");

			for (ThematicGrid grid : grids)
			{
				writer.write("\n\t\t<table>");
				writer.write("\n\t\t\t<tr>\n\t\t\t\t<td colspan=\"2\" valign=\"top\"><h3>"
						+ grid.getName() + "</h3></td>\n\t\t\t</tr>");
				writer.write("\n\t\t\t<tr>\n\t\t\t\t<td colspan=\"2\" valign=\"top\">"
						+ grid.getDescription() + "</td>\n\t\t\t</tr>");
				writer.write("\n\t\t\t<tr>\n\t\t\t\t<td valign=\"top\"><u>Included verbs</u>"
						+ convertVerbListIntoHtml(grid.getVerbs()) + "\n\t\t\t\t</td>");
				writer.write("\n\t\t\t\t<td valign=\"top\"><u>Associated Thematic Roles</u>"
						+ convertRoleMapIntoHtml(grid.getRoles())
						+ "\n\t\t\t\t</td>\n\t\t\t</tr>");

				writer.write("\n\t\t</table>");
			}

			writer.write("\n\t<body>\n</html>");
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
			}
		}
	}

	@Override
	public ValidationReport validateInterfaceArtifact(InterfaceArtifact artifact,
			IFile iFile) throws Exception
	{
		if (artifact != null && iFile != null)
		{
			// get Parser depending on the file extension
			Parser parser = ServiceManager.getInstance().getParsingService()
					.getParser(iFile.getFileExtension());
			if (parser != null)
			{
				return parser.validateArtifact(artifact);
			}
			else
			{
				String message = "No parser for file extension "
						+ String.valueOf(iFile.getFileExtension()) + " found.";
				logger.log(Level.SEVERE, message);
				throw new IllegalStateException(message);
			}
		}
		else
		{
			logger.log(
					Level.SEVERE,
					"The input parameters must be initalized. interfaceStructure="
							+ String.valueOf(artifact) + "; iFile="
							+ String.valueOf(iFile));
			throw new IllegalArgumentException("The input parameters must be initalized.");
		}
	}

	@Override
	public void addAddresseChangeListener(final IConfigurationChangeListener listener)
	{
		if (listener == null)
		{
			throw new NullPointerException(ERR_MSG_LISTENER_NULL);
		}
		this.addresseChangeListeners.add(listener);
	}

	@Override
	public void removeAddresseChangeListener(final IConfigurationChangeListener listener)
	{
		if (listener != null)
		{
			this.addresseChangeListeners.remove(listener);
		}
	}

	@Override
	public void addThematicRoleChangeListener(final IConfigurationChangeListener listener)
	{
		if (listener == null)
		{
			throw new NullPointerException(ERR_MSG_LISTENER_NULL);
		}
		this.thematicRoleChangeListeners.add(listener);
	}

	@Override
	public void removeThematicRoleChangeListener(
			final IConfigurationChangeListener listener)
	{
		if (listener != null)
		{
			this.thematicRoleChangeListeners.remove(listener);
		}
	}

	@Override
	public void addThematicGridChangeListener(final IConfigurationChangeListener listener)
	{
		if (listener == null)
		{
			throw new NullPointerException(ERR_MSG_LISTENER_NULL);
		}
		this.thematicGridChangeListeners.add(listener);
	}

	@Override
	public void removeThematicGridChangeListener(
			final IConfigurationChangeListener listener)
	{
		if (listener != null)
		{
			this.thematicGridChangeListeners.remove(listener);
		}
	}

	@Override
	public void removeAllAddresseChangeListener()
	{
		this.addresseChangeListeners.clear();
	}

	@Override
	public void removeAllThematicRoleChangeListener()
	{
		this.thematicRoleChangeListeners.clear();
	}

	@Override
	public void removeAllThematicGridChangeListener()
	{
		this.thematicGridChangeListeners.clear();
	}

	/**
	 * Fires for all <code>listeners</code> the configuration change event.
	 * 
	 * @param listeners
	 *            [DESTINATION]
	 * @thematicgrid Sending Operations
	 */
	private void notifyListeners(final Collection<IConfigurationChangeListener> listeners)
	{
		for (final IConfigurationChangeListener l : listeners)
		{
			l.configurationChange();
		}
	}

	/**
	 * Comparator for {@link IConfigurationChangeListener}s. It implements a rudimentary
	 * compare method, so the listener can be used within {@link Set}.
	 * 
	 * @author Dirk Meier-Eickhoff
	 * 
	 */
	private static class ConfigurationChangeListenerComparator implements
			Comparator<IConfigurationChangeListener>, Serializable
	{

		private static final long serialVersionUID = 1L;

		/**
		 * Compare only the object references for equality.
		 */
		@Override
		public int compare(IConfigurationChangeListener o1,
				IConfigurationChangeListener o2)
		{
			if (o1 == o2)
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
	}
}
