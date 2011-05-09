package de.akra.idocit.ui.composites.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.Documentation;
import de.akra.idocit.core.structure.ThematicRole;
import de.akra.idocit.ui.composites.DocumentItemListComposite;
import de.akra.idocit.ui.composites.DocumentItemListCompositeSelection;

/**
 * Factory to create a {@link DocumentItemListComposite} for testing.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class DocumentItemListCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemListCompositeSelection>
{

	@Override
	public AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemListCompositeSelection> createComposite(
			Composite pvParent)
	{
		DocumentItemListComposite docItemComp = new DocumentItemListComposite(pvParent,
				SWT.NONE);
		DocumentItemListCompositeSelection selection = new DocumentItemListCompositeSelection();

		// Here we have to use the same roles as returned by DocumentationTestFactory.createDocumentation().
		List<Addressee> addresseeList = new ArrayList<Addressee>();
		Addressee addresseeDeveloper = new Addressee("Developer");
		Addressee addresseeArchitect = new Addressee("Tester");
		Addressee addresseeProjectManager = new Addressee("Manager");
		
		addresseeList.add(addresseeDeveloper);
		addresseeList.add(addresseeArchitect);
		addresseeList.add(addresseeProjectManager);
		
		List<ThematicRole> thematicRoleList = new ArrayList<ThematicRole>();
		ThematicRole roleAGENT = new ThematicRole("AGENT");
		ThematicRole roleACTION = new ThematicRole("ACTION");
		
		thematicRoleList.add(roleAGENT);
		thematicRoleList.add(roleACTION);
		selection
				.setAddresseeList(addresseeList);
		selection.setThematicRoleList(thematicRoleList);
		
		selection.setDisplayedAddresseesOfDocumentations(new ArrayList<List<Addressee>>());

		List<Documentation> docs = new Vector<Documentation>();
		docs.add(DocumentationTestFactory.createDocumentation());
		docs.add(DocumentationTestFactory.createDocumentation());
		docs.add(DocumentationTestFactory.createDocumentation());
		selection.setDocumentations(docs);

		List<Integer> activeAddressees = new Vector<Integer>();
		activeAddressees.add(1);
		activeAddressees.add(2);
		activeAddressees.add(7);
		selection.setActiveAddressees(activeAddressees);

		docItemComp.setSelection(selection);
		return docItemComp;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}

}
