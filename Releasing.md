Update POM with the release version

`svn -m "Updating for release" commit pom.xml`

Tag release

`svn -m "Tagging <version>" copy https://nagios-was.googlecode.com/svn/trunk/ https://nagios-was.googlecode.com/svn/tags/<version>`

Build

`mvn assembly:assembly`

Upload to Google code

Update POM for new development

`svn -m "Updating for new development" commit pom.xml`