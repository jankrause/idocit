##################################################################################################
Readme zur Einrichtung von iDocIt!.

Inhalt:
1. Vorwort
2. Systemvoraussetzungen
3. Variante 1: Starten in einer Testumgebung
4. Variante 2: iDocIt! Plug-in als jar zu Eclipse hinzuf�gen
5. Konfiguration von iDocIt!
6. �ffnen des iDocIt! Editors

Stand: 13.01.2011
##################################################################################################
--------------------------------------------------------------------------------------------------
1. Vorwort

Zur Einrichtung von iDocIt! in Eclipse gibt es zwei Varianten, die in Kapitel 3 und 4 beschrieben sind.
Es braucht nur eine davon durchgef�hrt werden. F�r die Benutzung von iDocIt! ab Kapitel 5 weiterlesen.
Getestet wurde iDocIt! bisher nur unter Windows XP mit Java 1.6 und Eclipse Helios 3.6 (alles 32Bit).

--------------------------------------------------------------------------------------------------
2. Systemvoraussetzungen

Ben�tigte Software:
- Java 1.6
- Eclipse Helios 3.6 (z. B. \\10.0.0.202\public\programs\Software - Windows\Editor\eclipse\eclipse-rcp-helios-SR1-win32.zip)

Hinweis:
Die Java und die Eclipse Version m�ssen entweder beide 32Bit oder 64Bit sein, wenn sie unterschiedlich sind, started Eclipse nicht.

Ben�tigte Dateien:
- WordNet-3.0 W�rterbuch (\\10.0.0.202\public\programs\JAVA\iDocIt!\WordNet-3.0\dict\)
- Standord Part-Of-Speech Tagger (POS Tagger) Modell (\\10.0.0.202\programs\JAVA\iDocIt!\left3words-distsim-wsj-0-18.tagger)

--------------------------------------------------------------------------------------------------
3. Variante 1: Starten in einer Testumgebung

Ben�tigte Plug-ins f�r Eclipse:
	- Sublicpse (http://subclipse.tigris.org/servlets/ProjectProcess?pageID=p4wYuA)

Einrichten der iDocIt!-Projekte:
In Eclipse aus dem SVN (https://svn.akra.de/idocit/tags/tag_idocit_2010-12-07) folgende Projekte auschecken:
	- de.akra.idocit
	- de.akra.idocit.ui
	- de.akra.idocit.wsdl
	- de.akra.idocit.java

Starten von Eclipse mit iDocIt!:
In Eclipse �ber "Run" -> "Run configurations" eine neue "Eclipse Application" Konfiguration erstellen.
F�r die neue Konfiguration m�ssen auf dem Reiter "Plug-ins" zus�tzlich die Projekte de.akra.idocit, de.akra.idocit.ui 
de.akra.idocit.java und de.akra.idocit.wsdl als Plug-ins ausgew�hlt werden, damit diese mitstarten. 
Am einfachsten w�hlt man dazu in der Combobox "Launch with" die Option "all workspace and enabled target plug-ins".
Falls auf dem Reiter "Arguments" in dem Feld "VM arguments" eine Option "-vm" mit einem anderen JRE Verzeichnis angegeben ist, 
sollte diese Option entfernt werden. Dann wird die in Eclipse standardm��ig eingestellte JRE Version verwendet.
Wenn gew�nscht, kann auf dem Reiter "Main" unter "Workspace Data" die "Location" des Workspaces ge�ndert werden.
Nachdem die Einstellungen fertig sind, kann die neue Konfiguration mit dem Klick auf "Run" gestartet werden.

--------------------------------------------------------------------------------------------------
4. Variante 2: iDocIt! Plug-in als jar zu Eclipse hinzuf�gen

Die jar Dateien

	- de.akra.idocit_1.0.0.SNAPSHOT.jar
	- de.akra.idocit.ui_1.0.0.SNAPSHOT.jar
	- de.akra.idocit.wsdl_1.0.0.SNAPSHOT.jar
	- de.akra.idocit.java_1.0.0.SNAPSHOT.jar

nach <Eclipse Programmverzeichnis>\plugins kopieren. Danach Eclipse (neu) starten.

--------------------------------------------------------------------------------------------------
5. Konfiguration von iDocIt!

In Eclipse m�ssen �ber "Window" -> "Preferences" -> "iDocIt!" zuerst die Pfade zum WordNet-3.0 W�rterbuch
und zum Standord Part-Of-Speech Tagger (POS Tagger) Modell (siehe Kapitel 2) angegeben werden.
�ber die weiteren Untermen�s k�nnen weitere Einstellungen get�tigt werden. 

--------------------------------------------------------------------------------------------------
6. �ffnen des iDocIt! Editors

Der iDocIt!-Editor wird in Eclipse �ber das Kontextmen� einer WSDL oder Java Datei und dort "Open with" -> "iDocIt!" ge�ffnet.