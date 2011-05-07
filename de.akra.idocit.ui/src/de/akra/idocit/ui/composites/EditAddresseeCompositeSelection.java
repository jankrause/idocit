package de.akra.idocit.ui.composites;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.structure.Addressee;
import de.akra.idocit.structure.DescribedItem;

/**
 * Selection / state for {@link EditAddresseeComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class EditAddresseeCompositeSelection implements ISelection
{
	/**
	 * The original item.
	 */
	private Addressee addressee = null;

	/**
	 * The modified item.
	 */
	private Addressee modifiedAddressee = null;

	/**
	 * 
	 * @return the modified {@link DescribedItem}.
	 */
	public Addressee getModifiedAddressee()
	{
		return modifiedAddressee;
	}

	/**
	 * 
	 * @param modifiedAddressee
	 *            the modified {@link Addressee}.
	 */
	public void setModifiedAddressee(Addressee modifiedAddressee)
	{
		this.modifiedAddressee = modifiedAddressee;
	}

	/**
	 * 
	 * @return the original {@link Addressee}.
	 */
	public Addressee getAddressee()
	{
		return addressee;
	}

	/**
	 * 
	 * @param addressee
	 *            the original {@link Addressee}.
	 */
	public void setAddressee(Addressee addressee)
	{
		this.addressee = addressee;
	}

	
}