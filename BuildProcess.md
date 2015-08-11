# Introduction #

The build-process of iDocIt! should be automated to achieve the following goals:

  1. Save time and ensure consistency when releasing a new version of iDocIt!
  1. Use the latest development state in projects to collect first experiences with our new features.
  1. Identify bugs and side effects through automated tests very early after their implementation.

# Build Process and Architecture #

We use Maven 3 to implement iDocIt!'s build process. The following figure illustrates the default build lifecycle of Maven 3 and the current implementation state of each phase. The default build lifecycle of Maven is described [here](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html) in detail.

![http://idocit.googlecode.com/svn/images/idocit_build_impl_state.png](http://idocit.googlecode.com/svn/images/idocit_build_impl_state.png)

The following figure shows the systems and their maintainers participating in the build system. The source code of iDocIt! is hosted by Google at Google Code. At AKRA we have a Hudson instance which checks out the source code and starts the Maven build. After building Maven uploads the Updates to one of iDocIt!'s update sites.

![http://idocit.googlecode.com/svn/images/build_participants.png](http://idocit.googlecode.com/svn/images/build_participants.png)

# Implementation #

  * Before starting the Maven Build Hudson sends the following command to idocit.akra.de. This is neccessary to remove the JARs of old builds, since we don't want to keep old nightly-builds.
```
ssh USERNAME@idocit.akra.de "rm -rf /home/idocit/http/updatesite-nb/*"
```

  * The complete build and upload to an update site is started by executing the command "mvn package -P _PROFILE_" on the parent-project (/trunk/parent-pom).
  * _PROFILE_ could be "nightly-build" or "release-build". The profiles define the update site to which the binaries are uploaded.
  * The user executing Maven must have a settings.xml defining the username and password for server with id "idocit-update-site" (see the following example).

```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
	<servers>
		<server>
			<id>idocit-update-site</id>
			<username>USERNAME</username>
			<password>PASSWORD</password>
			<configuration>
        			<scpExecutable>scp</scpExecutable>
        			<sshExecutable>ssh</sshExecutable>
        			<scpArgs>-v</scpArgs>
        			<sshArgs>-v</sshArgs>
      		</configuration>
		</server>
	</servers>
</settings>
```

  * Each generated JAR-file (plugins and features) has a unique build id (timestamp). This is achieved by adding the postfix ".qualifier" to each plugin / feature version. The PDE build replaces this string with a unique timestamp automatically. Visit [this page](http://help.eclipse.org/ganymede/index.jsp?topic=/org.eclipse.pde.doc.user/tasks/pde_version_qualifiers.htm) for further information.
  * The nightly-build is scheduled to `00 00 * * *`.

# Further Issues #

  * Run the Unit-Tests with each build (issue [#78](http://code.google.com/p/idocit/issues/detail?id=78)).
  * Generate and provide the Javadoc of iDocIt! during the build (issue [#76](http://code.google.com/p/idocit/issues/detail?id=76)).
  * Integrate FindBugs into the build (issue [#77](http://code.google.com/p/idocit/issues/detail?id=77)).