### IDE ###
Our reference IDE is an English Eclipse 3.7 Indigo.

Additional recommended Eclipse Plug-ins are:
  * Subclipse 1.6.x
  * m2e (Maven Integration for Eclipse)
    * Tycho Project Configurators

### Source Code Format ###
We work with the source code format defined in the idocit\_formatter.xml (available in the Downloads-section).

How to import it:

  1. Open the Preferences-Dialog (Window > Preferences).
  1. Choose the page Java > Code Style > Formatter.
  1. Import the downloaded file via the "Import ..."-button.

### Copyrights ###
Due to the licences of our projects we add a copyright-header to each of our source-files. We use the Eclipse Copyright Tool to add these predefined texts.

#### Installation of Eclipse Copyright Tool ####
Update Site: http://download.eclipse.org/eclipse/updates/3.6

Node: Releng Tools > Eclipse Releng Tools

#### Configuration ####

  1. Open the Eclipse Preference Dialog via "Window" > "Preferences".
  1. Choose the preference page "Copyright Tool"
  1. Add one of the following copyright-templates to the corresponding text box.
  1. Activate the checkbox "Replace all existing copyright comments [...]"

#### Copyright Templates ####

For projects:
  * de.akra.idocit
  * de.akra.idocit.ui
  * de.akra.idocit.java
  * de.akra.idocit.java.ui
  * de.akra.idocit.wsdl
  * de.akra.idocit.common

```
Copyright ${date} AKRA GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

#### How to use the Copyright Tool? ####
  1. Right click the resource you want to add the copyright headers to.
  1. Select item "Fix copyrights" from the context-menu.