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
package de.akra.idocit.java.services;

import static de.akra.idocit.java.utils.JavaTestUtils.NEW_LINE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.akra.idocit.common.constants.Misc;
import de.akra.idocit.common.constants.ThematicGridConstants;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.common.utils.TestUtils;
import de.akra.idocit.common.utils.ThematicRoleUtils;
import de.akra.idocit.core.extensions.ValidationReport;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.java.AllIDocItJavaTests;
import de.akra.idocit.java.constants.Constants;
import de.akra.idocit.java.constants.PreferenceStoreConstants;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.ParserOutput;
import de.akra.idocit.java.utils.JavaInterfaceArtifactComparatorUtils;
import de.akra.idocit.java.utils.JavaTestUtils;
import de.akra.idocit.java.utils.TestDataFactory;

/**
 * Tests for {@link JavaParser}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavaParserTest
{
	private static Logger logger = Logger.getLogger(JavaParserTest.class.getName());

	private IProject project;

	@Before
	public void setupWorkspace() throws CoreException, IOException
	{
		final List<File> filesToAdd = new ArrayList<File>();

		filesToAdd.add(new File(AllIDocItJavaTests.SOURCE_DIR + "Customer.java"));
		filesToAdd.add(new File(AllIDocItJavaTests.SOURCE_DIR + "NameParameters.java"));
		filesToAdd.add(new File(AllIDocItJavaTests.SOURCE_DIR
				+ "CustomerNameParameters.java"));
		filesToAdd.add(new File(AllIDocItJavaTests.SOURCE_DIR + "CustomerService.java"));
		filesToAdd.add(new File(AllIDocItJavaTests.SOURCE_DIR + "SpecialException.java"));
		filesToAdd.add(new File(AllIDocItJavaTests.SOURCE_DIR + "JavaParser.java"));
		filesToAdd
				.add(new File(AllIDocItJavaTests.SOURCE_DIR + "JavadocRawComment.java"));
		filesToAdd.add(new File(AllIDocItJavaTests.SOURCE_DIR
				+ "InconsistentService.java"));
		filesToAdd.add(new File(AllIDocItJavaTests.SOURCE_DIR
				+ "InconsistentService2.java"));
		filesToAdd.add(new File(AllIDocItJavaTests.SOURCE_DIR
				+ "VeryInconsistentService.java"));
		filesToAdd.add(new File(AllIDocItJavaTests.SOURCE_DIR
				+ "TestReferenceGridName.java"));

		project = JavaTestUtils.initProjectInWorkspace(JavaTestUtils.PROJECT_NAME,
				filesToAdd);

		// Activate Simple Parser
		final IPreferenceStore store = PlatformUI.getPreferenceStore();
		store.setValue(PreferenceStoreConstants.JAVADOC_GENERATION_MODE,
				PreferenceStoreConstants.JAVADOC_GENERATION_MODE_SIMPLE);
	}

	@After
	public void clearWorkspace() throws CoreException
	{
		// Delete Java Project
		JavaTestUtils.deleteProjectFromWorkspace(JavaTestUtils.PROJECT_NAME);

		// Deactivate Simple Parser
		final IPreferenceStore store = PlatformUI.getPreferenceStore();
		store.setValue(PreferenceStoreConstants.JAVADOC_GENERATION_MODE,
				StringUtils.EMPTY);
	}

	/**
	 * Tests {@link JavaParser#parse(org.eclipse.core.resources.IFile)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJavaParserWithSimpleParser() throws Exception
	{
		/*
		 * Positive tests
		 */
		{
			// #########################################################################
			// # Test case #1: parser the customer service correctly. Then store it and
			// # parse it again. Both objects must be equal.
			// #########################################################################
			{
				final ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				final CompilationUnit cu = output.getCompilationUnit();
				final IFile testSourceFolder = this.project
						.getFile(JavaTestUtils.REL_SOURCE_PATH + "CustomerService.java");

				final JavaInterfaceArtifact refInterfaceArtifact = TestDataFactory
						.createCustomerService("Developer", false, cu);

				JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
						.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);

				final JavaInterface refInterface = (JavaInterface) refInterfaceArtifact
						.getInterfaces().get(0);
				final JavaInterface actInterface = (JavaInterface) actInterfaceArtifact
						.getInterfaces().get(0);

				final JavaMethod refMethod = (JavaMethod) refInterface.getOperations()
						.get(0);
				final JavaMethod actMethod = (JavaMethod) actInterface.getOperations()
						.get(0);

				final List<? extends Parameter> refInputParameters = refMethod
						.getInputParameters().getParameters();
				final List<? extends Parameter> actInputParameters = actMethod
						.getInputParameters().getParameters();

				final List<? extends Parameter> refOutputParameters = refMethod
						.getOutputParameters().getParameters();
				final List<? extends Parameter> actOutputParameters = actMethod
						.getOutputParameters().getParameters();

				Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsParameters(
						refInputParameters, actInputParameters));
				Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsParameters(
						refOutputParameters, actOutputParameters));
				Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsExceptions(
						refMethod.getExceptions(), actMethod.getExceptions()));
				Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsMethods(
						refMethod, actMethod));
				Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsInterfaces(
						refInterface, actInterface));
				Assert.assertTrue(JavaInterfaceArtifactComparatorUtils
						.equalsInterfaceArtifacts(refInterfaceArtifact,
								actInterfaceArtifact));

				ServiceManager.getInstance().getPersistenceService()
						.writeInterface(actInterfaceArtifact, testSourceFolder);

				actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
						.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);

				Assert.assertTrue(JavaInterfaceArtifactComparatorUtils
						.equalsInterfaceArtifacts(refInterfaceArtifact,
								actInterfaceArtifact));
			}

			clearWorkspace();
			setupWorkspace();

			// #########################################################################
			// # Test case #2: parse the source code of JavaParser.java without any error.
			// # In a former implementation this test causes a NullPointerException
			// # because of void-methods (input or output-parameters == null).
			// #########################################################################
			{
				final IFile testSourceFolder = this.project
						.getFile(JavaTestUtils.REL_SOURCE_PATH + "JavaParser.java");

				final JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
						.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);
				final Operation javaMethod = actInterfaceArtifact.getInterfaces().get(0)
						.getOperations().get(0);
				final Documentation documentation = javaMethod.getDocumentations().get(0);

				final List<ThematicRole> roles = ServiceManager.getInstance()
						.getPersistenceService().loadThematicRoles();
				final ThematicRole roleNone = ThematicRoleUtils.findRoleByName("NONE",
						roles);
				documentation.setThematicRole(roleNone);

				for (final Operation operation : actInterfaceArtifact.getInterfaces()
						.get(0).getOperations())
				{
					operation.setDocumentationChanged(true);
				}

				final ValidationReport report = ServiceManager
						.getInstance()
						.getPersistenceService()
						.validateInterfaceArtifact(actInterfaceArtifact, testSourceFolder);

				Assert.assertNotNull(actInterfaceArtifact);
				Assert.assertNotNull(report);

				ServiceManager.getInstance().getPersistenceService()
						.writeInterface(actInterfaceArtifact, testSourceFolder);
			}

			clearWorkspace();
			setupWorkspace();

			// #########################################################################
			// # Test case #3: the description of a class or interface should have
			// # thematic role "NONE" in the idocit-structure and no thematic role in the
			// # Javadoc-structure (see reference Javadoc).
			// #########################################################################
			{
				final String refJavadoc = String.format("/**%1$s"
						+ " * Parser implementation for Java.%1$s" + " * %1$s"
						+ " * @author Dirk Meier-Eickhoff%1$s" + " * @since 0.0.1%1$s"
						+ " * @version 0.0.1%1$s" + " * %1$s" + " */%1$s", NEW_LINE);
				final IFile testSourceFolder = this.project
						.getFile(JavaTestUtils.REL_SOURCE_PATH + "JavaParser.java");

				final JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
						.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);

				final Documentation documentation = actInterfaceArtifact.getInterfaces()
						.get(0).getDocumentations().get(0);

				final List<ThematicRole> roles = ServiceManager.getInstance()
						.getPersistenceService().loadThematicRoles();
				final ThematicRole roleNone = ThematicRoleUtils.findRoleByName("NONE",
						roles);

				Assert.assertEquals(roleNone, documentation.getThematicRole());

				actInterfaceArtifact.getInterfaces().get(0).setDocumentationChanged(true);
				ServiceManager
						.getInstance()
						.getPersistenceService()
						.validateInterfaceArtifact(actInterfaceArtifact, testSourceFolder);

				ServiceManager.getInstance().getPersistenceService()
						.writeInterface(actInterfaceArtifact, testSourceFolder);

				final File javaFile = testSourceFolder.getRawLocation().makeAbsolute()
						.toFile();
				final String actJavadoc = readLinesFromFile(javaFile, 51, 59);

				Assert.assertEquals(refJavadoc, actJavadoc);
			}

			clearWorkspace();
			setupWorkspace();

			// #########################################################################
			// # Test case #4: javadoc with line-wrapped texts is correctly parsed. Line
			// # breaks or empty lines in iDocIt!'s GUI are correctly mapped to empty rows
			// # in the generated javadoc (starting with a '*').
			// #########################################################################
			{
				final IFile testSourceFolder = this.project
						.getFile(JavaTestUtils.REL_SOURCE_PATH + "JavaParser.java");

				final JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
						.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);
				JavaMethod parseMethod = (JavaMethod) actInterfaceArtifact
						.getInterfaces().get(0).getOperations().get(0);

				final List<Documentation> refDocumentations = TestDataFactory
						.createDocsForParseMethod("Developer",
								parseMethod.getQualifiedIdentifier());

				parseMethod.getDocumentations().clear();

				for (final Documentation documentation : refDocumentations)
				{
					parseMethod.addDocpart(documentation);
				}

				parseMethod.setDocumentationChanged(true);

				ServiceManager
						.getInstance()
						.getPersistenceService()
						.validateInterfaceArtifact(actInterfaceArtifact, testSourceFolder);

				ServiceManager.getInstance().getPersistenceService()
						.writeInterface(actInterfaceArtifact, testSourceFolder);

				final File javaFile = testSourceFolder.getRawLocation().makeAbsolute()
						.toFile();
				final String actJavadoc = readLinesFromFile(javaFile, 87, 111);

				final String refJavadoc = String
						.format("\t/**%1$s"
								+ "\t * Reads the java- and javadoc code from the given <b>file and<br/>%1$s"
								+ "\t * creates</b> the returned {@link JavaInterfaceArtifact} from it.<br/>%1$s"
								+ "\t * Escape Test: &Ouml;%1$s"
								+ "\t * %1$s"
								+ "\t * @source_format Java and Javadoc according to their current specifications:<br/>%1$s"
								+ "\t * <br/>%1$s"
								+ "\t * <a href=\"http://docs.oracle.com/javase/specs/\">Java</a><br/>%1$s"
								+ "\t * Javadoc%1$s"
								+ "\t * %1$s"
								+ "\t * @instrument To parse the Java and Javadoc code, the parser provided by the Eclipse Java Development Tools is used.%1$s"
								+ "\t * @instrument iDocIt! supports two different representations of thematicgrids in Javadoc:<br/>%1$s"
								+ "\t * <br/>%1$s"
								+ "\t * The simplified version is very compact, but supports only the addressee &quot;Developer&quot;.<br/>%1$s"
								+ "\t * The complex version supports all addressees, but uses a lot of HTML-code.%1$s"
								+ "\t * %1$s"
								+ "\t * @param iFile [SOURCE] ("
								+ Constants.ERROR_CASE_DOCUMENTATION_TEXT
								+ ")%1$s"
								+ "\t * %1$s"
								+ "\t * @return [OBJECT]%1$s"
								+ "\t * %1$s"
								+ "\t * @throws Exception%1$s"
								+ "\t * @see de.akra.idocit.core.extensions.Parser#parse(IFile)%1$s"
								+ "\t * @thematicgrid Parsing Operations%1$s"
								+ "\t */%1$s", NEW_LINE);

				Assert.assertEquals(refJavadoc, actJavadoc);

				final JavaInterfaceArtifact loadedArtifact = (JavaInterfaceArtifact) ServiceManager
						.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);
				parseMethod = (JavaMethod) loadedArtifact.getInterfaces().get(0)
						.getOperations().get(0);

				final List<Documentation> actDocumentations = parseMethod
						.getDocumentations();

				Assert.assertEquals(refDocumentations.toString(),
						actDocumentations.toString());
			}

			clearWorkspace();
			setupWorkspace();

			// #########################################################################
			// # Test case #5: the void- and parameterless method "foo" could be parsed
			// # without a NullPointerException
			// #########################################################################
			{
				final IFile testSourceFolder = this.project
						.getFile(JavaTestUtils.REL_SOURCE_PATH
								+ "VeryInconsistentService.java");

				ServiceManager.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);
			}

			clearWorkspace();
			setupWorkspace();

			// #########################################################################
			// # Test case #6: Methods that are documented with raw Javadoc must be read
			// # without any exception. The '@throws IllegalArgumentException' tag must
			// # be added to the additionalTags of the method.
			// #########################################################################
			{
				final IFile testSourceFolder = this.project
						.getFile(JavaTestUtils.REL_SOURCE_PATH + "JavadocRawComment.java");

				final InterfaceArtifact artifact = ServiceManager.getInstance()
						.getPersistenceService().loadInterface(testSourceFolder);

				final JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
						.getOperations().get(0);
				assertNotNull(method);
				assertNotNull(method.getAdditionalTags());
				assertEquals(4, method.getAdditionalTags().size());
				assertEquals(
						"There must be 0 documentations, because nothing is documented.",
						0, method.getDocumentations().size());

				final List<TagElement> additionalTags = method.getAdditionalTags();
				assertEquals(4, additionalTags.size());
				final List<TagElement> foundThrowsTags = findTagByName(additionalTags,
						TagElement.TAG_THROWS);
				assertEquals(1, foundThrowsTags.size());

				final SimpleName name = (SimpleName) foundThrowsTags.get(0).fragments()
						.get(0);
				assertEquals("IllegalArgumentException", name.getIdentifier());
				assertEquals(1, findTagByName(additionalTags, TagElement.TAG_SEE).size());
				assertEquals(1, findTagByName(additionalTags, TagElement.TAG_AUTHOR)
						.size());

				final JavaMethod method2 = (JavaMethod) artifact.getInterfaces().get(0)
						.getOperations().get(1);
				assertNotNull(method2);
				assertNotNull(method2.getAdditionalTags());
				assertEquals(4, method2.getAdditionalTags().size());

				final List<Documentation> docs2 = TestUtils
						.collectAllDocumentations(method2);
				assertEquals("There must be 4 documentations.", 4, docs2.size());

				final List<TagElement> additionalTags2 = method2.getAdditionalTags();
				assertEquals(4, additionalTags2.size());
				final List<TagElement> foundThrowsTags2 = findTagByName(additionalTags2,
						TagElement.TAG_THROWS);
				assertEquals(1, foundThrowsTags2.size());

				final SimpleName name2 = (SimpleName) foundThrowsTags2.get(0).fragments()
						.get(0);
				assertEquals("IllegalArgumentException", name2.getIdentifier());
				assertEquals("maybe if illegal arg is inserted",
						((TextElement) foundThrowsTags2.get(0).fragments().get(1))
								.getText());
				assertEquals(1, findTagByName(additionalTags2, TagElement.TAG_SEE).size());
				assertEquals(1, findTagByName(additionalTags2, TagElement.TAG_AUTHOR)
						.size());
			}
		}

		/*
		 * Negative tests
		 */
		{
			// #########################################################################
			// # Test case #1: a documented, but not existent parameter causes a
			// # ParsingException.
			// #########################################################################
			{
				final IFile testSourceFolder = this.project
						.getFile(JavaTestUtils.REL_SOURCE_PATH
								+ "InconsistentService.java");
				boolean parsingExceptionOccured = false;

				try
				{
					ServiceManager.getInstance().getPersistenceService()
							.loadInterface(testSourceFolder);
				}
				catch (ParsingException pEx)
				{
					parsingExceptionOccured = true;
				}

				assertTrue(parsingExceptionOccured);
			}

			// #########################################################################
			// # Test case #2: If a method has no return type, but the Javadoc contains a
			// # '@return ...' a ParsingException must be thrown.
			// #########################################################################
			{
				final String expectedErrorMsg = "de.akra.idocit.java.exceptions.ParsingException: For method 'foo' there is documented a return type although it does not exist. Please delete the '@return ...' tag from Javadoc comment and open the file again.";
				final IFile testSourceFolder = this.project
						.getFile(JavaTestUtils.REL_SOURCE_PATH
								+ "InconsistentService2.java");

				boolean err = false;
				try
				{
					ServiceManager.getInstance().getPersistenceService()
							.loadInterface(testSourceFolder);
				}
				catch (final ParsingException e)
				{
					err = expectedErrorMsg.equals(e.toString());
				}
				Assert.assertTrue("ParsingException is expected.", err);
			}
		}
	}

	/**
	 * 
	 * @param tags
	 *            [SOURCE]
	 * @param tagName
	 *            [COMPARISON]
	 * @return a sublist of {@code tags} that contains only {@link TagElement}s whose tag
	 *         name equals to {@code tagName}.
	 */
	private List<TagElement> findTagByName(final List<TagElement> tags,
			final String tagName)
	{
		if (tags == null || tags.isEmpty() || StringUtils.isBlank(tagName))
		{
			return Collections.emptyList();
		}

		final List<TagElement> foundTags = new ArrayList<TagElement>(tags.size());
		for (final TagElement tag : tags)
		{
			if (tagName.equals(tag.getTagName()))
			{
				foundTags.add(tag);
			}
		}
		return foundTags;
	}

	private String readLinesFromFile(final File javaFile, final int startLine, final int endLine)
			throws FileNotFoundException, IOException
	{
		final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(javaFile), 
				Charset.forName(Misc.DEFAULT_CHARSET)));
		final StringBuffer actJavadoc = new StringBuffer();
		try
		{
			// The java-file contain the javadoc in lines 51 - 58.
			for (int i = 0; i < endLine; i++)
			{
				if (i >= startLine)
				{
					actJavadoc.append(reader.readLine());
					actJavadoc.append(JavaTestUtils.NEW_LINE);
				}
				else
				{
					reader.readLine();
				}
			}
		}
		finally
		{
			reader.close();
		}
		return actJavadoc.toString();
	}

	/**
	 * Try using the AST.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAST() throws Exception
	{
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		String fileContent = TestUtils.readFile(AllIDocItJavaTests.SOURCE_DIR
				+ "ParsingService.java");

		parser.setSource(fileContent.toCharArray());
		parser.setResolveBindings(true); // we need bindings later on
		CompilationUnit cu = (CompilationUnit) parser
				.createAST(null /* IProgressMonitor */);

		@SuppressWarnings("unchecked")
		List<TypeDeclaration> types = (List<TypeDeclaration>) cu.types();
		logger.log(Level.INFO, "types.size=" + types.size());

		TypeDeclaration td = types.get(0);
		MethodDeclaration[] methods = td.getMethods();
		logger.log(Level.INFO, "methods.length=" + methods.length);

		for (MethodDeclaration method : methods)
		{
			logger.log(Level.INFO, "\t"
					+ (method.getJavadoc() != null ? method.getJavadoc().toString()
							: "// no javadoc"));
			logger.log(Level.INFO, "\t" + method.getName().toString());
		}
	}

	/**
	 * Parses the test-file JavaParser.java and should set the error-documentation flag
	 * for the SOURCE-documentation to true.
	 * 
	 * @throws Exception
	 *             Should never happen
	 */
	@Test
	public void testParseErrorDocumentationFlag() throws Exception
	{
		final IFile testSourceFolder = this.project.getFile(JavaTestUtils.REL_SOURCE_PATH
				+ "JavaParser.java");

		final JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
				.getInstance().getPersistenceService().loadInterface(testSourceFolder);

		final Operation firstOperation = actInterfaceArtifact.getInterfaces().get(0)
				.getOperations().get(0);
		assertEquals("SOURCE", firstOperation.getInputParameters().getParameters().get(0)
				.getDocumentations().get(0).getThematicRole().getName());

		assertTrue(firstOperation.getInputParameters().getParameters().get(0)
				.getDocumentations().get(0).isErrorCase());

		assertEquals("INSTRUMENT", firstOperation.getDocumentations().get(2)
				.getThematicRole().getName());

		assertFalse(firstOperation.getDocumentations().get(2).isErrorCase());

		assertEquals("INSTRUMENT", firstOperation.getDocumentations().get(3)
				.getThematicRole().getName());

		assertTrue(firstOperation.getDocumentations().get(3).isErrorCase());
	}

	/**
	 * Parses and writes the test file TestReferenceGridName.java to test reading and
	 * writing of the reference thematic grid of an operation.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWritingAndReadingReferenceGridName() throws Exception
	{
		final IFile testSourceFile = this.project.getFile(JavaTestUtils.REL_SOURCE_PATH
				+ "TestReferenceGridName.java");

		JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
				.getInstance().getPersistenceService().loadInterface(testSourceFile);

		// ##############################################################################
		// # Test case 1: Read Javadoc with reference grid.
		// ##############################################################################
		JavaMethod method = (JavaMethod)actInterfaceArtifact.getInterfaces().get(0).getOperations().get(0);
		assertEquals("Searching Operations", method.getThematicGridName());

		// ##############################################################################
		// # Test case 2: Write Javadoc with other reference grid and read it again.
		// ##############################################################################
		method.setThematicGridName("Putting Operations");
		method.setDocumentationChanged(true);
		ServiceManager.getInstance().getPersistenceService()
				.writeInterface(actInterfaceArtifact, testSourceFile);
		
		testSourceFile.refreshLocal(IProject.DEPTH_ZERO, null);
		
		actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager.getInstance()
				.getPersistenceService().loadInterface(testSourceFile);

		method = (JavaMethod)actInterfaceArtifact.getInterfaces().get(0).getOperations().get(0);
		assertEquals("Putting Operations", method.getThematicGridName());

		// ##############################################################################
		// # Test case 3: Write Javadoc with reference grid "None" that shall not be
		// # persisted.
		// ##############################################################################

		method.setThematicGridName(ThematicGridConstants.THEMATIC_GRID_DEFAULT_NAME);
		method.setDocumentationChanged(true);
		ServiceManager.getInstance().getPersistenceService()
				.writeInterface(actInterfaceArtifact, testSourceFile);
		actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager.getInstance()
				.getPersistenceService().loadInterface(testSourceFile);

		method = (JavaMethod)actInterfaceArtifact.getInterfaces().get(0).getOperations().get(0);
		// by default the reference grid is "None" if no other was found
		assertEquals(ThematicGridConstants.THEMATIC_GRID_DEFAULT_NAME,
				method.getThematicGridName());

		// check if the tag @thematicgrid is not in the Javadoc 
		final String refJavadoc = String.format("\t/**%1$s" + "\t * Find Customer.%1$s"
				+ "\t * %1$s" + "\t * @param id [PRIMARY_KEY]%1$s" + "\t * %1$s"
				+ "\t * @return [OBJECT]%1$s"
				+ "\t */%1$s\tpublic Object findCustomer(final int id);%1$s", NEW_LINE);
		final String actJavadoc = readLinesFromFile(testSourceFile.getRawLocation().makeAbsolute()
						.toFile(), 19, 27);
		assertEquals(refJavadoc, actJavadoc);
	}
}