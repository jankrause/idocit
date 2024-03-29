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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;

import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.core.extensions.Parser;
import de.akra.idocit.core.extensions.ValidationReport;
import de.akra.idocit.core.extensions.ValidationReport.ValidationCode;
import de.akra.idocit.java.constants.PreferenceStoreConstants;
import de.akra.idocit.java.services.AbsJavadocParser;
import de.akra.idocit.java.services.AddresseeUtils;
import de.akra.idocit.java.services.JavaInterfaceGenerator;
import de.akra.idocit.java.services.JavadocGenerator;
import de.akra.idocit.java.services.JavadocParser;
import de.akra.idocit.java.services.SimpleJavadocGenerator;
import de.akra.idocit.java.services.SimpleJavadocParser;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;

/**
 * Parser implementation for Java.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class JavaParser implements Parser
{
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(JavaParser.class.getName());

	/**
	 * The type of supported files.
	 */
	private static final String SUPPORTED_TYPE = "java";

	/**
	 * The delimiters for Java.
	 */
	public static final Delimiters delimiters;

	/**
	 * Initialize <code>delimiters</code>.
	 */
	static
	{
		delimiters = new Delimiters();
		delimiters.setPathDelimiter("/");
		delimiters.setNamespaceDelimiter(".");
		delimiters.setTypeDelimiter(":");
	}

	/**
	 * Reads the java- and javadoc code from the given file and creates the
	 * returned {@link JavaInterfaceArtifact} from it.
	 * 
	 * @source_format Java and Javadoc according to their current specifications
	 *                <a href="http://docs.oracle.com/javase/specs/">Java</a>
	 *                <a href="http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html">Javadoc</a>
	 * @instrument To parse the Java and Javadoc code, the parser provided by
	 *             the <a href="http://www.eclipse.org/jdt/">Eclipse Java Development Tools</a> is used.
	 * @instrument (ERROR DOC) iDocIt! supports two different representations of thematic
	 *             grids in Javadoc: 
	 *             
	 *             The simplified version is very compact, but
	 *             supports only the addressee "Developer".
	 *             
	 *             The complex version
	 *             supports all addressees, but uses a lot of HTML-code.
	 * 
	 * @param iFile
	 *            [SOURCE] (ERROR DOC)
	 * 
	 * @return [OBJECT]
	 * 
	 * @throws Exception
	 * @see de.akra.idocit.core.extensions.Parser#parse(IFile)
	 * @thematicgrid Parsing Operations
	 */
	@Override
	public InterfaceArtifact parse(IFile iFile) throws Exception
	{
		logger.log(Level.INFO, "parse file: "
				+ iFile.getFullPath().toFile().getAbsolutePath());

		ICompilationUnit iCompilationUnit = JavaCore.createCompilationUnitFrom(iFile);

		if (iCompilationUnit == null)
		{
			logger.log(Level.SEVERE, "Can not create ICompilationUnit from "
					+ iFile.getLocation().toFile().getAbsolutePath());
			return InterfaceArtifact.NOT_SUPPORTED_ARTIFACT;
		}

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(iCompilationUnit.getWorkingCopy(null));
		parser.setResolveBindings(true); // bindings are needed for object reflection
		parser.setBindingsRecovery(true);
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(Job
				.getJobManager().createProgressGroup());
		compilationUnit.recordModifications();

		de.akra.idocit.java.services.JavaInterfaceParser jInterfaceParser = new de.akra.idocit.java.services.JavaInterfaceParser(
				compilationUnit, compilationUnit.getJavaElement().getElementName(),
				delimiters);

		AbsJavadocParser javadocParser = isSimpleModeConfigured() ? SimpleJavadocParser.INSTANCE
				: JavadocParser.INSTANCE;
		InterfaceArtifact artifact = jInterfaceParser.parse(javadocParser);

		return artifact;
	}

	/**
	 * {@inheritDoc}
	 * @see de.akra.idocit.core.extensions.Parser#write(InterfaceArtifact, IFile)
	 */
	@Override
	public void write(InterfaceArtifact interfaceStructure, IFile iFile) throws Exception
	{
		logger.log(Level.INFO, "write file: "
				+ iFile.getFullPath().toFile().getAbsolutePath());

		JavaInterfaceArtifact artifact = (JavaInterfaceArtifact) interfaceStructure;

		if (isSimpleModeConfigured())
		{
			JavaInterfaceGenerator.updateJavadocInAST(artifact,
					SimpleJavadocGenerator.INSTANCE);
		}
		else
		{
			JavaInterfaceGenerator
					.updateJavadocInAST(artifact, JavadocGenerator.INSTANCE);
		}
		writeToFile(artifact);
	}

	/**
	 * Write the changes in the {@link JavaInterfaceArtifact} to the underlying file.
	 * 
	 * @param artifact
	 *            The {@link JavaInterfaceArtifact} which changes should be written to the
	 *            file.
	 * @throws MalformedTreeException
	 * @throws BadLocationException
	 * @throws CoreException
	 * @see {@link CompilationUnit#rewrite(IDocument, java.util.Map)}
	 */
	private void writeToFile(JavaInterfaceArtifact artifact)
			throws MalformedTreeException, BadLocationException, CoreException
	{
		CompilationUnit unit = artifact.getCompilationUnit();

		ICompilationUnit iCompUnit = (ICompilationUnit) unit.getJavaElement();

		IDocument document = new Document(artifact.getOriginalDocument());
		TextEdit textEdit = unit.rewrite(document, null);
		textEdit.apply(document);

		iCompUnit.getBuffer().setContents(document.get());
		iCompUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);
		iCompUnit.commitWorkingCopy(true, null);
		iCompUnit.makeConsistent(null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.core.extensions.Parser#isSupported(java.lang.String)
	 */
	@Override
	public boolean isSupported(String type)
	{
		return type.equalsIgnoreCase(SUPPORTED_TYPE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.core.extensions.Parser#getSupportedType()
	 */
	@Override
	public String getSupportedType()
	{
		return SUPPORTED_TYPE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.core.extensions.Parser#getDelimiters()
	 */
	@Override
	public Delimiters getDelimiters()
	{
		return delimiters;
	}

	private boolean isSimpleModeConfigured()
	{
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		String mode = store.getString(PreferenceStoreConstants.JAVADOC_GENERATION_MODE);

		return PreferenceStoreConstants.JAVADOC_GENERATION_MODE_SIMPLE.equals(mode);
	}

	@Override
	public ValidationReport validateArtifact(InterfaceArtifact artifact)
	{
		if (artifact instanceof JavaInterfaceArtifact)
		{
			ValidationReport report = new ValidationReport();

			if (isSimpleModeConfigured())
			{
				JavaInterfaceArtifact javaArtifact = (JavaInterfaceArtifact) artifact;

				if (AddresseeUtils.containsOnlyOneAddressee(javaArtifact, "Developer"))
				{
					report.setReturnCode(ValidationCode.OK);
					report.setMessage("");
				}
				else
				{
					report.setReturnCode(ValidationCode.ERROR);
					report.setMessage("The generation of simplified Javadoc works only for addressee \"Developer\"");
				}
			}
			else
			{
				report.setReturnCode(ValidationCode.OK);
				report.setMessage("");
			}

			return report;
		}
		else
		{
			throw new RuntimeException("This Java Parser only accepts "
					+ JavaInterfaceArtifact.class.getName() + "s for validation.");
		}
	}

}
