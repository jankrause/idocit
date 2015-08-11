# Feature list of iDocIt! #

## General ##
  * Extended support by documenting source code with the new approach of thematic grids and thematic roles (read more in "Hands on tutorial")
  * Graphical user interface to insert documentations
  * Seamless integration into Eclipse as plugins
  * Supports Eclipse 3.6
  * Installation via the Eclipse update site http://idocit.googlecode.com/svn/updatesite/
  * HTML export of the documentations in a simple format
  * Define Reference Grid for each operation (categorize your operations, e.g. "Searching Operation" or "Mathematical Operation")

## Java support ##
  * iDocIt! documentations are written into Javadoc comments into the Java source file
  * Existing Javadoc comments can be converted to the iDocIt! format
  * You can document:
    * interfaces
    * classes
    * enumerations
    * methods
    * parameters (and if applicable directly the accessible attributes of the parameters)
    * return types (and if applicable directly the accessible attributes of the return type)
    * exceptions (and if applicable directly the accessible attributes of exception objects)

## WSDL 1.1 support ##
  * iDocIt! documentations are written into the documentations elements in a WSDL file
  * You can document following elements:
    * portType
    * operation
    * input message (and if applicable directly the containing complexType's elements)
    * output message (and if applicable directly the containing complexType's elements)
    * fault message (and if applicable directly the containing complexType's elements)