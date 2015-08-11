# Installation #

## Javadoc Taglets ##

Download the idocittaglets.jar from our download-site and save it on your build-server. You have to extend command generating your Javadoc as follows:

```
javadoc [...] -taglet de.akra.idocit.java.javadocext.ThematicGridTaglet -tagletpath <YOUR_TAGLET_DIR>\idocittaglets.jar
```

## Eclipse Installation ##

**Prerequisites:**
  * Eclipse 3.7 (Indigo) [Download](http://www.eclipse.org/downloads/)

**Installation:**
  1. Select the entry „Help“ > „Install new software ...“ in your main menubar of Eclipse.
  1. Click the „Add“-button to create a new Update Site for iDocIt!.
  1. Enter „“ as name and „iDocIt Update Site“ and „https://idocit.akra.de/updatesite-nb/“ as location and click „OK“.
  1. Select the iDocIt!-plugins you want to install. You need everything except the Java- and WSDL-support. From these plugins you have to select the language you need.
  1. Click „Next“ twice.
  1. Accept the licence agreements and click „Finish“.
  1. Accept the unsigned JARs by clicking „OK“ in the upcoming message box.
  1. Restart your Eclipse.

Congratulations, your iDocIt! plugin is now up and running :).

# Configuration #
**Please note:** have a look at our [HandsOnTutorial1](HandsOnTutorial1.md) to see how iDocIt could be customized and configured.

# How to open iDocIt? #
  1. Select the interface-file you want to document in the view „Package Explorer“.
  1. In case of a WSDL-file right-click the file and choose the entry „iDocIt!“ from the context menu. ![http://idocit.googlecode.com/svn/images/launch-idocit.png](http://idocit.googlecode.com/svn/images/launch-idocit.png)
  1. In any other case choose the entry „Open with ...“, „Other“ and then select „iDocIt“ from the list of editors. Confirm your selection by clicking the „OK“-button.
![http://idocit.googlecode.com/svn/images/open-with-contextmenu.png](http://idocit.googlecode.com/svn/images/open-with-contextmenu.png)
![http://idocit.googlecode.com/svn/images/open-with-dialog.png](http://idocit.googlecode.com/svn/images/open-with-dialog.png)