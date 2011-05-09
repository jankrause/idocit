package de.akra.idocit.wsdl.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Output;
import javax.wsdl.PortType;
import javax.wsdl.WSDLElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import de.akra.idocit.core.structure.Delimiters;
import de.akra.idocit.core.structure.Documentation;
import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.Operation;
import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.ParameterPathElement;
import de.akra.idocit.core.structure.Parameters;
import de.akra.idocit.core.structure.SignatureElement;
import de.akra.idocit.core.utils.ObjectStructureUtils;
import de.akra.idocit.wsdl.structure.WSDLInterface;
import de.akra.idocit.wsdl.structure.WSDLInterfaceArtifact;
import de.akra.idocit.wsdl.structure.WSDLMessage;
import de.akra.idocit.wsdl.structure.WSDLOperation;
import de.akra.idocit.wsdl.structure.WSDLParameter;
import de.jankrause.diss.wsdl.common.services.WSDLParsingService;

/**
 * The WSDL interface parser. It extracts the {@link PortType}s with its
 * {@link javax.wsdl.Operation}s and {@link Message}s and creates a
 * {@link WSDLInterfaceArtifact} which holds the structure.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class WSDLInterfaceParser
{
	private static final String CATEGORY_PART = "Part";

	private static final String CATEGORY_PORT_TYPE = "PortType";

	private static final String CATEGORY_ARTIFACT = "Artifact";

	private static final String CATEGORY_OPERATION = "Operation";

	private static final String CATEGORY_OUTPUT_MESSAGE = "OutputMessage";

	private static final String CATEGORY_INPUT_MESSAGE = "InputMessage";

	private static final String CATEGORY_FAULT_MESSAGE = "FaultMessage";
	
	/**
	 * For debugging purpose.
	 */
	private static long timeCounter = 0;

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(WSDLInterfaceParser.class.getName());

	/**
	 * The WSDL structure.
	 */
	private Definition wsdlDefinition;

	/**
	 * The name of this artifact
	 */
	private String artifactName;

	/**
	 * The delimiters to use.
	 */
	private Delimiters delimiters;

	/**
	 * Constructor.
	 * 
	 * @param wsdlDefinition
	 *            The WSDL structure.
	 * @param artifactName
	 *            The name of the artifact (e.g. the file name).
	 * @param delimiters
	 *            The delimiters to use.
	 */
	public WSDLInterfaceParser(Definition wsdlDefinition, String artifactName,
			Delimiters delimiters)
	{
		this.wsdlDefinition = wsdlDefinition;
		this.artifactName = artifactName;
		this.delimiters = delimiters;
	}

	/**
	 * Parses the WSDL file and extracts all needed information.
	 * 
	 * @return The WSDL file structure.
	 */
	public WSDLInterfaceArtifact parse()
	{
		WSDLInterfaceArtifact ifaceArtifact = new WSDLInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, CATEGORY_ARTIFACT,
				wsdlDefinition, artifactName);
		ifaceArtifact.setInterfaces(readPortTypes(ifaceArtifact));

		logger.log(Level.INFO, "Time for WSDLParsingService.extractRoles = " + timeCounter + "ms");
		
		return ifaceArtifact;
	}

	/**
	 * Extracts the {@link PortType}s from the WSDL {@link Definition}
	 * <code>wsdlDefinition</code>.
	 * 
	 * @param parent
	 *            The parent for the new {@link Interface}s.
	 * 
	 * @return The {@link List} of {@link Interface}s with the parsed {@link PortType}s.
	 */
	@SuppressWarnings("unchecked")
	private List<Interface> readPortTypes(SignatureElement parent)
	{

		// Get only the PortTypes defined in this WSDL definition. With
		// getAllPortTypes() we would get more PortTypes.
		Map<QName, PortType> portTypes = (Map<QName, PortType>) wsdlDefinition
				.getPortTypes();
		Iterator<PortType> i = portTypes.values().iterator();

		List<Interface> iList = new ArrayList<Interface>(portTypes.size());

		while (i.hasNext())
		{
			PortType p = i.next();

			WSDLInterface wsdlInterface = new WSDLInterface(parent, CATEGORY_PORT_TYPE);
			wsdlInterface.setIdentifier(p.getQName() != null ? p.getQName()
					.getLocalPart() : SignatureElement.ANONYMOUS_IDENTIFIER);
			wsdlInterface.setPortType(p);
			wsdlInterface.setDocumentations(DocumentationParser.parseDocElements(p
					.getDocumentationElements()));
			
			wsdlInterface.setOperations(readOperations(wsdlInterface, p.getOperations()));

			iList.add(wsdlInterface);
		}
		return iList;
	}

	/**
	 * Parses the {@link javax.wsdl.Operation}s of a {@link PortType}.
	 * 
	 * @param parent
	 *            The parent for the new {@link WSDLOperation}s.
	 * @param operations
	 *            The {@link List} of {@link Operation}s which should be parsed.
	 * @return A {@link List} with the {@link WSDLOperation}s parsed from
	 *         <code>operations</code>.
	 */
	@SuppressWarnings("unchecked")
	private List<WSDLOperation> readOperations(SignatureElement parent,
			List<javax.wsdl.Operation> operations)
	{
		List<WSDLOperation> opList = new ArrayList<WSDLOperation>(operations.size());

		for (javax.wsdl.Operation o : operations)
		{
			WSDLOperation newOp = new WSDLOperation(parent, CATEGORY_OPERATION);
			newOp.setIdentifier(o.getName());
			newOp.setOperation(o);
			newOp.setDocumentations(DocumentationParser.parseDocElements(o
					.getDocumentationElements()));

			WSDLMessage wsdlMessage;

			// create object structure for the input message
			wsdlMessage = buildWSDLMessageStructure(newOp, o.getInput().getMessage(),
					CATEGORY_INPUT_MESSAGE);
			if (wsdlMessage != null)
			{
				wsdlMessage.setMessageRef(o.getInput());
				newOp.setInputParameters(wsdlMessage);
			}
			else
			{
				logger.log(Level.SEVERE, "Input message could not be built.");
			}

			// create object structure for the output message
			wsdlMessage = buildWSDLMessageStructure(newOp, o.getOutput().getMessage(),
					CATEGORY_OUTPUT_MESSAGE);
			if (wsdlMessage != null)
			{
				wsdlMessage.setMessageRef(o.getOutput());
				newOp.setOutputParameters(wsdlMessage);
			}
			else
			{
				logger.log(Level.SEVERE, "Output message could not be built.");
			}

			// create object structure for the fault messages
			List<Parameters> faultMessages = new ArrayList<Parameters>(o.getFaults()
					.size());
			for (Fault fault : ((Map<String, Fault>) o.getFaults()).values())
			{
				wsdlMessage = buildWSDLMessageStructure(newOp, fault.getMessage(),
						CATEGORY_FAULT_MESSAGE);

				if (wsdlMessage != null)
				{
					wsdlMessage.setMessageRef(fault);
					faultMessages.add(wsdlMessage);
				}
				else
				{
					logger.log(Level.SEVERE, "Fault message could not be built.");
				}
			}
			newOp.setExceptions(faultMessages);

			// match documentations (docpart elements) to embedded Messages
			attachDocumentation(newOp);

			opList.add(newOp);

		}

		return opList;
	}

	/**
	 * Build the object structure for a WSDL message element with all referenced type
	 * structures. It builds the structure for all part elements in the message down to
	 * the last simple type or to the last accessible information. Complex types are
	 * divided again into several {@link Parameter}s.
	 * 
	 * @param parent
	 *            The parent for the new {@link WSDLMessage}.
	 * @param message
	 *            The {@link Message} of an {@link javax.wsdl.Operation} which should be
	 *            divided into its {@link Parameter}.
	 * @param category
	 *            The category for the new {@link WSDLMessage}.
	 * @return The {@link WSDLMessage} that represents the whole structure of an WSDL
	 *         message element. <code>null</code> if there exists no {@link Message}.
	 * @see WSDLParsingService#extractRoles(Message, javax.wsdl.Types)
	 */
	private WSDLMessage buildWSDLMessageStructure(SignatureElement parent,
			Message message, String category)
	{
		// path =
		// <MESSAGE_NAME>"."<PART_NAME>"("<ELEMENT_TYPE>")"["."<ELEMENT_NAME>"("<ELEMENT_TYPE>")"]*

		WSDLMessage wsdlMessage = null;
		
		//TODO remove time counter
		long start = System.currentTimeMillis();
		List<String> rolePaths = WSDLParsingService.extractRoles(message,
				wsdlDefinition.getTypes());
		long end = System.currentTimeMillis();
		timeCounter += end-start;

		// TODO delete this if WSDLParsingService is finished
		rolePaths = reformatRolePaths(rolePaths);

		if (!rolePaths.isEmpty())
		{
			// the WSDLMessage represents an embedded message of an Operation
			wsdlMessage = new WSDLMessage(parent, category);
			
			String firstPath = rolePaths.get(0);
			ParameterPathElement msgPathElem = ObjectStructureUtils
			.parsePathElement(delimiters, extractMessageName(firstPath));
			
			wsdlMessage.setIdentifier(msgPathElem.getIdentifier());
			wsdlMessage.setQualifiedIdentifier(msgPathElem.getQualifiedIdentifier());

			for (String path : rolePaths)
			{
				// split path into elements
				String[] pathArray = path.split(delimiters.getQuotedPathDelimiter());

				if (pathArray.length > 1)
				{
					// search for existing Parameter
					// TODO test qualified name things
					ParameterPathElement paramPathElem = ObjectStructureUtils
							.parsePathElement(delimiters, pathArray[1]);
					Parameter partElem = findExistingParameter(
							wsdlMessage.getParameters(),
							paramPathElem.getQualifiedIdentifier(),
							paramPathElem.getQualifiedTypeName());

					if (partElem == null)
					{
						// if not found, create new Input ...
						partElem = new WSDLParameter(parent, CATEGORY_PART);
						partElem.setIdentifier(paramPathElem.getIdentifier());
						partElem.setQualifiedIdentifier(paramPathElem
								.getQualifiedIdentifier());
						partElem.setDataTypeName(paramPathElem.getTypeName());
						partElem.setQualifiedDataTypeName(paramPathElem
								.getQualifiedTypeName());

						// ... and add the "part" to the "message"
						wsdlMessage.addParameter(partElem);
					}

					// build hierarchy of the part parameters
					buildParameterHierarchy(pathArray, 2, partElem);

					ObjectStructureUtils.setParametersPaths(delimiters,
							wsdlMessage.getIdentifier(), partElem);
				}
			}
		}
		return wsdlMessage;
	}

	/**
	 * TODO this is only a temporary wrapper, because the WSDLParsingService is not
	 * finished yet.
	 */
	private List<String> reformatRolePaths(List<String> rolePaths)
	{
		List<String> newPaths = Collections.emptyList();
		if (!rolePaths.isEmpty())
		{
			newPaths = new ArrayList<String>();
			for (String path : rolePaths)
			{
				String newPath = path
						.replaceAll(Pattern.quote("."), delimiters.pathDelimiter)
						.replaceAll(Pattern.quote("("), delimiters.typeDelimiter)
						.replaceAll(Pattern.quote(":"), delimiters.namespaceDelimiter)
						.replaceAll(Pattern.quote(")"), "");
				newPaths.add(newPath);
			}
		}
		return newPaths;
	}

	/**
	 * A recursive method that builds the hierarchy of {@link Parameter}s out of the array
	 * <code>elemPath</code> containing the path of one parameter/argument of a WSDL
	 * message.
	 * 
	 * @param elemPath
	 *            array containing the path of one parameter/argument of a WSDL message.
	 * @param offset
	 *            The current position for the array index.
	 * @param parent
	 *            The parent {@link Parameter} to which new sub elements are added.
	 * @throws IllegalArgumentException
	 */
	private void buildParameterHierarchy(String[] elemPath, int offset, Parameter parent)
			throws IllegalArgumentException
	{
		// exit condition for recursion
		if (offset != elemPath.length)
		{
			// search for existing Parameter
			// TODO test qualified things
			ParameterPathElement paramPathElem = ObjectStructureUtils.parsePathElement(
					delimiters, elemPath[offset]);
			Parameter param = findExistingParameter(parent.getComplexType(),
					paramPathElem.getQualifiedIdentifier(),
					paramPathElem.getQualifiedTypeName());

			// if not found, ...
			if (param == null)
			{
				// ... create new Parameter
				param = new WSDLParameter(parent, "");
				param.setIdentifier(paramPathElem.getIdentifier());
				param.setQualifiedIdentifier(paramPathElem.getQualifiedIdentifier());
				param.setDataTypeName(paramPathElem.getTypeName());
				param.setQualifiedDataTypeName(paramPathElem.getQualifiedTypeName());

				// add new Parameter
				parent.addParameter(param);
			}

			// go deeper
			buildParameterHierarchy(elemPath, offset + 1, param);
		}
	}

	/**
	 * Finds in the {@link List} of {@link Parameter} an existing {@link Parameter} with
	 * the same name <code>qualifiedElemName</code> and type
	 * <code>qualifiedElemType</code>. If no {@link Parameter} matches the
	 * <code>qualifiedElemName</code> and <code>qualifiedElemType</code> <code>null</code>
	 * is returned.
	 * 
	 * @param paramList
	 *            The {@link List} of {@link Parameter} which should be the source for the
	 *            searching.
	 * @param qualifiedElemName
	 *            The qualified searched element name.
	 * @param qualifiedElemType
	 *            The qualified searched element type.
	 * 
	 * @return The found {@link Parameter}, otherwise <code>null</code>.
	 */
	private Parameter findExistingParameter(List<Parameter> paramList,
			String qualifiedElemName, String qualifiedElemType)
	{
		Parameter result = null;
		Iterator<Parameter> i = paramList.iterator();
		while (i.hasNext() && result == null)
		{
			Parameter p = i.next();
			if (p.getQualifiedIdentifier().equals(qualifiedElemName)
					&& p.getQualifiedDataTypeName().equals(qualifiedElemType))
			{
				result = p;
			}
		}
		return result;
	}

	/**
	 * Attaches the {@link Documentation}s of the {@link Input}, {@link Output} and
	 * {@link Fault} of a {@link javax.wsdl.Operation} hold in <code>wsdlOperation</code>
	 * to the {@link WSDLOperation#getInputParameters()},
	 * {@link WSDLOperation#getOutputParameters()} and
	 * {@link WSDLOperation#getExceptions()}.
	 * 
	 * @param wsdlOperation
	 *            The {@link WSDLOperation} to which the {@link Documentation}s should be
	 *            added.
	 */
	@SuppressWarnings("unchecked")
	private void attachDocumentation(WSDLOperation wsdlOperation)
	{
		// match documentations out of wsdlDocElems (docpart elements) to the Parameters
		// in WSDLMessage
		attachDocumentation((WSDLMessage) wsdlOperation.getInputParameters());
		attachDocumentation((WSDLMessage) wsdlOperation.getOutputParameters());

		for (WSDLMessage msg : (List<WSDLMessage>) wsdlOperation.getExceptions())
		{
			attachDocumentation(msg);
		}
	}

	/**
	 * Converts the documentation {@link Element} of the {@link WSDLElement} hold in
	 * {@link WSDLMessage} to {@link Documentation}s and attaches them to the
	 * {@link Parameter}s in <code>wsdlMessage</code>.
	 * 
	 * @param wsdlMessage
	 *            The {@link WSDLMessage} to which the {@link Documentation}s should be
	 *            added.
	 */
	private void attachDocumentation(WSDLMessage wsdlMessage)
	{
		List<Element> wsdlDocElems = wsdlMessage.getMessageRef()
				.getDocumentationElements();

		List<Documentation> documentations = DocumentationParser
				.parseDocElements(wsdlDocElems);

		attachDocpartsToParameters(documentations, wsdlMessage);
	}

	/**
	 * It searches for all {@link Documentation}s in <code>msgPartDocumentation</code> a
	 * corresponding {@link Parameter} in the structure of <code>paramList</code> or the
	 * {@link Parameters} itself. If a {@link Documentation} is found for a
	 * {@link Parameter} or the {@link Parameters} it is attached to it.
	 * 
	 * @param paramList
	 *            The message element as {@link Parameters} with its underlying structure
	 *            of {@link Parameter}s.
	 * @param msgPartDocumentations
	 *            {@link List} of {@link Documentation}s which should be assigned to the
	 *            {@link Parameter}s in <code>paramList</code>.
	 */
	private void attachDocpartsToParameters(List<Documentation> msgPartDocumentations,
			Parameters paramList)
	{
		// iterate over all items in msgPartDocumentations
		for (Documentation doc : msgPartDocumentations)
		{
			// find right Parameter and add documentation
			if (!paramList.addMatchingDocumentation(delimiters, doc))
			{
				// if not found, log it
				logger.log(
						Level.INFO,
						"Found docpart is not assignable: "
								+ doc.getSignatureElementIdentifier());
			}
		}
	}

	/**
	 * Extract the &lt;MESSAGE_NAME&gt; out of the pattern described for
	 * {@link WSDLParsingService#extractRoles(Message, javax.wsdl.Types)}.
	 * 
	 * @param path
	 *            The message path.
	 * @return The &lt;MESSAGE_NAME&gt;.
	 * @see WSDLParsingService#extractRoles(Message, javax.wsdl.Types)
	 */
	private String extractMessageName(String path)
	{
		int endPosOfName = path.indexOf(delimiters.pathDelimiter);
		if (endPosOfName == -1)
		{
			endPosOfName = path.length();
		}
		return path.substring(0, endPosOfName);
	}
}
