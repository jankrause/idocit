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

import static org.junit.Assert.assertTrue;
import static de.akra.idocit.java.JavadocTestUtils.NEW_LINE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.common.utils.ThematicRoleUtils;
import de.akra.idocit.core.extensions.ValidationReport;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.core.utils.TestUtils;
import de.akra.idocit.java.AllIDocItJavaTests;
import de.akra.idocit.java.JavadocTestUtils;
import de.akra.idocit.java.constants.PreferenceStoreConstants;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.ParserOutput;
import de.akra.idocit.java.utils.JavaInterfaceArtifactComparatorUtils;
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

	private static final String PROJECT_NAME = "iDocItTestProject";

	@Before
	public void setupWorkspace() throws CoreException, IOException
	{
		/*
		 * The implementation of the initialization of the Test-Java Project has been
		 * guided by http://sdqweb.ipd.kit.edu/wiki/JDT_Tutorial:
		 * _Creating_Eclipse_Java_Projects_Programmatically.
		 * 
		 * Thanks to the authors.
		 */

		// Create Java Project
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(PROJECT_NAME);
		project.create(progressMonitor);
		project.open(progressMonitor);

		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
		IJavaProject javaProject = JavaCore.create(project);
		javaProject.open(progressMonitor);

		// Add Java Runtime to the project
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
		for (LibraryLocation element : locations)
		{
			entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null,
					null));
		}

		// Add libs to project class path
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]),
				null);

		// Create source folder
		IFolder srcFolder = project.getFolder("src");
		srcFolder.create(true, true, progressMonitor);

		IPackageFragmentRoot fragmentRoot = javaProject.getPackageFragmentRoot(srcFolder);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(fragmentRoot.getPath());
		javaProject.setRawClasspath(newEntries, null);

		// Create the package
		IFolder packageFolder = srcFolder.getFolder("source");
		packageFolder.create(true, true, progressMonitor);

		// Create Java files
		File customerFile = new File(AllIDocItJavaTests.SOURCE_DIR + "Customer.java");
		IFile customerWorkspaceFile = packageFolder.getFile("Customer.java");
		customerWorkspaceFile.create(new FileInputStream(customerFile), true,
				progressMonitor);

		File nameParametersFile = new File(AllIDocItJavaTests.SOURCE_DIR
				+ "NameParameters.java");
		IFile nameParametersWorkspaceFile = packageFolder.getFile("NameParameters.java");
		nameParametersWorkspaceFile.create(new FileInputStream(nameParametersFile), true,
				progressMonitor);

		File customerNameParamFile = new File(AllIDocItJavaTests.SOURCE_DIR
				+ "CustomerNameParameters.java");
		IFile customerNameParamWorkspaceFile = packageFolder
				.getFile("CustomerNameParameters.java");
		customerNameParamWorkspaceFile.create(new FileInputStream(customerNameParamFile),
				true, progressMonitor);

		File javaFile = new File(AllIDocItJavaTests.SOURCE_DIR + "CustomerService.java");
		IFile javaWorkspaceFile = packageFolder.getFile("CustomerService.java");
		javaWorkspaceFile.create(new FileInputStream(javaFile), true, progressMonitor);

		File specialExceptionFile = new File(AllIDocItJavaTests.SOURCE_DIR
				+ "SpecialException.java");
		IFile specialExceptionWorkspaceFile = packageFolder
				.getFile("SpecialException.java");
		specialExceptionWorkspaceFile.create(new FileInputStream(specialExceptionFile),
				true, progressMonitor);

		// Copy source code of JavaParser.java to test workspace.
		File srcJavaParserFile = new File(AllIDocItJavaTests.SOURCE_DIR
				+ "JavaParser.java");
		IFile srcJavaParserWorkspaceFile = packageFolder.getFile("JavaParser.java");
		srcJavaParserWorkspaceFile.create(new FileInputStream(srcJavaParserFile), true,
				progressMonitor);

		File inconsistentFile = new File(AllIDocItJavaTests.SOURCE_DIR
				+ "InconsistentService.java");
		IFile inconsistentWorkspaceFile = packageFolder
				.getFile("InconsistentService.java");
		inconsistentWorkspaceFile.create(new FileInputStream(inconsistentFile), true,
				progressMonitor);

		File veryInconsistentFile = new File(AllIDocItJavaTests.SOURCE_DIR
				+ "VeryInconsistentService.java");
		IFile veryInconsistentWorkspaceFile = packageFolder
				.getFile("VeryInconsistentService.java");
		veryInconsistentWorkspaceFile.create(new FileInputStream(veryInconsistentFile),
				true, progressMonitor);

		project.refreshLocal(IProject.DEPTH_INFINITE, progressMonitor);

		// Activate Simple Parser
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		store.setValue(PreferenceStoreConstants.JAVADOC_GENERATION_MODE,
				PreferenceStoreConstants.JAVADOC_GENERATION_MODE_SIMPLE);
	}

	@After
	public void clearWorkspace() throws CoreException
	{
		// Delete Java Project
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(PROJECT_NAME);
		project.delete(true, progressMonitor);

		// Deactivate Simple Parser
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		store.setValue(PreferenceStoreConstants.JAVADOC_GENERATION_MODE, StringUtils.EMPTY);
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
				ParserOutput output = JavadocTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IProject project = root.getProject(PROJECT_NAME);
				IFile testSourceFolder = project
						.getFile("src/source/CustomerService.java");

				JavaInterfaceArtifact refInterfaceArtifact = TestDataFactory
						.createCustomerService("Developer", false, cu);

				JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
						.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);

				JavaInterface refInterface = (JavaInterface) refInterfaceArtifact
						.getInterfaces().get(0);
				JavaInterface actInterface = (JavaInterface) actInterfaceArtifact
						.getInterfaces().get(0);

				JavaMethod refMethod = (JavaMethod) refInterface.getOperations().get(0);
				JavaMethod actMethod = (JavaMethod) actInterface.getOperations().get(0);

				List<? extends Parameter> refInputParameters = refMethod
						.getInputParameters().getParameters();
				List<? extends Parameter> actInputParameters = actMethod
						.getInputParameters().getParameters();

				List<? extends Parameter> refOutputParameters = refMethod
						.getOutputParameters().getParameters();
				List<? extends Parameter> actOutputParameters = actMethod
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
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IProject project = root.getProject(PROJECT_NAME);
				IFile testSourceFolder = project.getFile("src/source/JavaParser.java");

				JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
						.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);
				Operation javaMethod = actInterfaceArtifact.getInterfaces().get(0)
						.getOperations().get(0);
				Documentation documentation = javaMethod.getDocumentations().get(0);

				List<ThematicRole> roles = ServiceManager.getInstance()
						.getPersistenceService().loadThematicRoles();
				ThematicRole roleNone = ThematicRoleUtils.findRoleByName("NONE", roles);
				documentation.setThematicRole(roleNone);

				for (Operation operation : actInterfaceArtifact.getInterfaces().get(0)
						.getOperations())
				{
					operation.setDocumentationChanged(true);
				}

				ValidationReport report = ServiceManager
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
				String refJavadoc = String.format("/**%1$s"
						+ " * Parser implementation for Java.%1$s" + " * %1$s"
						+ " * @author Dirk Meier-Eickhoff%1$s" + " * @since 0.0.1%1$s"
						+ " * @version 0.0.1%1$s" + " * %1$s" + " */%1$s", NEW_LINE);
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IProject project = root.getProject(PROJECT_NAME);
				IFile testSourceFolder = project.getFile("src/source/JavaParser.java");

				JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
						.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);

				Documentation documentation = actInterfaceArtifact.getInterfaces().get(0)
						.getDocumentations().get(0);

				List<ThematicRole> roles = ServiceManager.getInstance()
						.getPersistenceService().loadThematicRoles();
				ThematicRole roleNone = ThematicRoleUtils.findRoleByName("NONE", roles);

				Assert.assertEquals(roleNone, documentation.getThematicRole());

				actInterfaceArtifact.getInterfaces().get(0).setDocumentationChanged(true);
				ServiceManager
						.getInstance()
						.getPersistenceService()
						.validateInterfaceArtifact(actInterfaceArtifact, testSourceFolder);

				ServiceManager.getInstance().getPersistenceService()
						.writeInterface(actInterfaceArtifact, testSourceFolder);

				File javaFile = testSourceFolder.getRawLocation().makeAbsolute().toFile();
				String actJavadoc = readLinesFromFile(javaFile, 51, 59);

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
				final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				final IProject project = root.getProject(PROJECT_NAME);
				final IFile testSourceFolder = project
						.getFile("src/source/JavaParser.java");

				final JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
						.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);
				JavaMethod parseMethod = (JavaMethod) actInterfaceArtifact
						.getInterfaces().get(0).getOperations().get(0);

				final List<Documentation> refDocumentations = TestDataFactory
						.createDocsForParseMethod("Developer",
								parseMethod.getQualifiedIdentifier());

				parseMethod.getDocumentations().clear();

				for (Documentation documentation : refDocumentations)
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
								+ "\t * @param iFile [SOURCE]%1$s"
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

			// #########################################################################
			// # Test case #5: the void- and parameterless method "foo" could be parsed
			// # without a NullPointerException
			// #########################################################################
			{
				final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				final IProject project = root.getProject(PROJECT_NAME);
				final IFile testSourceFolder = project
						.getFile("src/source/VeryInconsistentService.java");

				ServiceManager.getInstance().getPersistenceService()
						.loadInterface(testSourceFolder);
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
				final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				final IProject project = root.getProject(PROJECT_NAME);
				final IFile testSourceFolder = project
						.getFile("src/source/InconsistentService.java");
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
		}
	}

	private String readLinesFromFile(File javaFile, int startLine, int endLine)
			throws FileNotFoundException, IOException
	{
		final BufferedReader reader = new BufferedReader(new FileReader(javaFile));
		final StringBuffer actJavadoc = new StringBuffer();
		try
		{
			// The java-file contain the javadoc in lines 51 - 58.
			for (int i = 0; i < endLine; i++)
			{
				if (i >= startLine)
				{
					actJavadoc.append(reader.readLine());
					actJavadoc.append(JavadocTestUtils.NEW_LINE);
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

}