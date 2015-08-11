# Hands-on Tutorial 1 #


# Prerequisites: #
  * You can install iDocIt! via our Update Site https://idocit.akra.de/updatesite (nightly build: https://idocit.akra.de/updatesite-nb) (see the [installation guide](InstallationAndConfiguration.md) for further details).
  * We assume that you have successfully installed iDocIt! and use it in its default configuration.

So, what are we going to learn in this session?

# Goals: #
  * You know about the semantic gap in current approaches to API-documentation. You are familiar with iDocIt!'s approach to bridge this gap.
  * You know how to work with iDocIt! and how to customize it.

# Create and set up an Eclipse Project: #
Ok, let's get started. At first we need a Web Service Project in our Eclipse IDE.

  1. Start Eclipse and open your workspace.
  1. Right click into the view "Project Explorer" and choose "New > Project" in the context menu.
  1. In the upcoming wizard choose "General > Project" and click "Next".
  1. Enter "customer\_care\_service" as project name and click "Finish".
  1. Finally we have to copy the downloaded WSDL-file into the newly created project.

That's it. Now we can start to document it.

# Gaps in current aproaches to API-documentation #
> The following text has been taken from the [paper](http://nats-www.informatik.uni-hamburg.de/pub/User/JanChristianKrause/SETP-10-Pub.pdf) [Jan Christian](http://nats-www.informatik.uni-hamburg.de/User/JanChristianKrause) presented on the [SETP-10-conference](http://www.promoteresearch.org/2010/setp/index.html) in 2010.

In natural language, activities are mainly described by means of verbs, while the actors or attributes of the activity depend syntactically and semantically on the verb. For this reason it is common to choose the identifier of a Web Service operation in a way that it contains a verb which describes the activity to be carried out. A simplified example is given the following table:

| Name of Operation: | _findCustomerById_ |
|:-------------------|:-------------------|
| Contents of Output-Message: | customer::Customer |
| Contents of Input-Message: | id::integer        |
| Faults:            | None               |

From a linguistic point of view, this labeling style is an imperative sentence (with a missing article, a missing conjunction and two missing personal pronouns: Find the customer by his or her id.). In linguistics, the concept of phrases is used to structure natural language sentences. Phrases split sentences into typed groups of words, which can be moved or replaced without rendering the sentence ungrammatical.

For example, the verb find binds two other nominal phrases (arguments of the verb): the subject and the object. The formalism used to specify the arguments of a verb is called a thematic grid and each required argument is called a thematic role. An argument could be mandatory or optional.  A simplified thematic grid for the example verb find would be

Find `[OBJECT]` `[COMPARISON]` `[SOURCE]`

Here the thematic roles object, comparison and source are used. The role agent is only implicitly given (“you”). Its entity initiates and carries out the activity described by the verb. The object is the entity which is affected by the action. The comparison provides a criterion which classifies the object and the source specifies the place where the object comes from. In this example, the agent and object are mandatory arguments, while the comparison and source are optional. Now the operation’s signature could be matched to the thematic grid, for example as

Find `[OBJECT` <sub>Customer</sub>`]` `[COMPARISON` <sub>id</sub>`]` `[SOURCE` <sub>?</sub>`]`.

In this example the source is not specified as a parameter. Nevertheless the algorithm has to look somewhere for the customer with the given id. The necessity of including this information into the documentation is contributed by the thematic grid of the verb. By assigning its thematic roles to the elements mentioned in the operation’s signature, a consumed storage-resource (e.g. a database) has been identified, which is not mentioned in the operation’s signature. In line with [Clements et al.](http://www.amazon.com/Documenting-Software-Architectures-p-Clements-F-Bachmann-L-Bass-D-Garlan-J-Ivers-R-Little/dp/B003WEPPOC/ref=sr_1_6?ie=UTF8&s=books&qid=1303241275&sr=8-6) this resource should definitely be documented because the result of the operation depends crucially on the availability and the state of this resource. Current state-of-the-art approaches to the documentation of Web Services derive the set of elements to be documented only from the operation’s signature and therefore would not cover the source.

By using the thematic grid of the verb used in the operation’s identifier and assigning the signature-elements to the thematic roles of this grid, required hidden parameters and resources can be identified without deep knowledge about the implementation of the operation. Furthermore the use of thematic roles can decrease the effort for documentation, because the developer only assigns the uninstanciated role of the grid to the corresponding parameter or resource. It is assumed that in many cases the thematic role explains the parameter sufficiently. Possibly this advantage will lead to a high degree of acceptance on the side of developers. If necessary, the documentation could also be complemented with pre- or post-conditions or textual annotations.

# Basic Configuration and Customization of iDocIt! #

## Addressees ##

You can specify the groups of groups of addressees of you API documentation via the corresponding Preference Page.

  * On the left hand side you can see a list of configured addressee-groups. By clicking the buttons below this list, you can add new addressee-groups or delete the selected ones.

  * On the right hand side you edit the name of the selected group and enter a description text. The group-name is be used as label for the tab in the docpart-editing component. The description text is the tooltip of the corresponding addressee-tab.

  * If the checkbox "Default" for an addressee-group is activated, a tab for this group is created with every newly added docpart.

**Please note:** iDocIt! uses the stakeholders of interface documentation identified and published by Clements et al. as default-addressees. For further information on them see the great book [Documenting Software Architectures](http://www.amazon.com/Documenting-Software-Architectures-p-Clements-F-Bachmann-L-Bass-D-Garlan-J-Ivers-R-Little/dp/B003WEPPOC/ref=sr_1_6?ie=UTF8&s=books&qid=1303241275&sr=8-6), pages 237 - 239.

## Thematic Roles ##

You can define the underlying thematic grids of iDocIt! via the corresponding Preference Page.

  * On the left you can see a list of all locally defined thematic grids. Locally means that these grids are available in you current workspace. You can define new grids or remove existing ones by clicking the corresponding buttons below this list.
  * On the right you can edit the name and the description of the choosen grid. The names are used to identify the grid when recommending roles to you in the iDocIt!-editor.
  * You have to enter the verbs the current grid belongs to. In case of more than one word, you have to seperate them with commas.
  * You also have to define which thematic roles belong to the current grid. This can be done by activating the checkbox before the role's name in the listbox bottom right. Via a right click on the role you can define wheher it is mandatory or optional for the current grid. Mandatory roles must to be documented. For optional roles it is recommended to document them.

# Document the API: #
Well, let's open our undocumented WSDL-file with iDocIt!.

  1. Right click on the WSDL-file and choose "iDocIt!".<br /><br />**Please note:** iDocIt! does not support this context-menu extension for other API-languages like Java yet. This will be implemented with this [issue](http://code.google.com/p/idocit/issues/detail?id=7) – we promise ;). For other languages as WSDL choose "Open with … > Other" and then "iDocIt!".<br />
  1. iDocIt! opens its editor. Its GUI is structured as follows:<br /><br />**Select signature element:**<br />use this tree to select the element of the API you want to document.<br /><br />**Overview of recommended roles for …:**<br />iDocIt! provides semantic recommendations for your documentationn in this tree in form of one or more thematic grids. iDocIt! will show all  thematic grids which hold for the predicate in the operation's identifier. The roles of a grid are highlighted in the following different ways: <br /><br /><table border='1'><tr><td><b>Optical Property</b></td><td><b>Meaning</b></td></tr><tr><td>Bold Font</td><td>Mandatory Role: you have to document it.</td></tr><tr><td>Normal Font</td><td>Optional role: for most cases it is recommended to document the role, but you don't have to ;).</td></tr><tr><td>Green Font Color</td><td>This role occurs at least one time in your documentation. This seems to be fine :).</td></tr><tr><td>Red Font Color</td><td>This role is mandatory and has not been documented.</td></tr><tr><td>Orange Font Color</td><td>This role is optional and has not been documented.</td></tr></table><br />**Document Signature element …:**<br />here you can enter your documtation text. Initially this area contains no input fields. You can add them by clicking the button "Add thematic role documentation".
  1. Expand the tree of signature elements and select the operation "findCustomerById". IdocIt! displays the recommended thematic roles to document immediately.
  1. Let's start with the documentation of the performed COMPARISON. Select the node "id (Type: int)" (leaf-node of the input-message-node) and click on the button "Add Thematic Role documentation" on the right side of the editor.
  1. Select "COMPARISON" as thematic role and "EXPLICIT" as scope. The explicit scope means that the documented thematic role is visible as element of the declaration of the operation. You can describe the COMPARISON in the textbox, e.g. "Must be greater than zero.". As you can see, iDocIt! marks the role COMPARISON with bold green font in the tree of recommended roles.
  1. Ok, so now you know how to document a thematic role with an explicit scope. But how are implicit scopes handeled? The thematic grid recommends to document a SOURCE. The SOURCE describes the storage where the retrieved customer is saved, e.g. a database. You can see that such a database is not one of the parameters of "findCustomerById". Let's see how it could be documented. Select the operation in the tree again and add a thematic role documentation.<br /><br />**Please note:** if more than one operation share the same themtic role (e.g. the SOURCE), its documentation also be placed on level of the interface (Port Type). In this case all operations of this interface inherit the documented role.
  1. Select SOURCE as thematic role and IMPLICIT as scope. Let's assume that we want to document in which file the database-connection could be configured for the administrator (intgegrator). To add this new addressee click on the tab "+" and choose "Integrator" from the upcoming context menu.
  1. Since we are not going to document something for the developer, we should remove the corresponding tab. Select the tab "Developer", click on the tab "-" and confirm the message box with "Yes".
  1. Now enter the documentation-text for the administrator into the textbox: _The datasource is named DATA\_SOURCE\_CCS and could be configured in the file WEB.INF/web.xml_.
  1. Save you documentations by pressing CRTL-S. As you can see, many roles remain to be undocumented. Feel free to document them to get used to iDocIt!'s way of API-Documentation ;).

## How to proceed? ##
We believe, that the set of thematic roles used to be in the API-documentation is very domain specific and should be customized. There will be roles such as e.g. an "Orderer" in a company offering a web shop like Amazon. The propability for this role in a ship auditing company will be close to zero. But there you will find a role "Ship". We made the experience that even within a company, the roles could differ between different software-applications.