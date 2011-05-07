package de.akra.idocit.java.structure;

import de.akra.idocit.structure.Parameters;
import de.akra.idocit.structure.SignatureElement;

/**
 * Representation of a collection of Java parameters (input, output or exceptions).
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 * 
 */
public class JavaParameters extends Parameters
{
	/**
	 * Constructor
	 * 
	 * @param parent
	 *            The parent for this element.
	 * @param category
	 *            The category of this element.
	 */
	public JavaParameters(SignatureElement parent, String category)
	{
		super(parent, category);
		this.setDocumentationAllowed(false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#createSignatureElement(de.akra.idocit
	 *      .structure.SignatureElement)
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new JavaParameters(parent, super.getCategory());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#doCopyTo(de.akra.idocit.structure.
	 *      SignatureElement)
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}
}