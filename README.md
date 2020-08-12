PS Project
========

This a content package project generated using the multimodule-content-package-archetype.

Project Overview
--------

This is a test to fulfil the requirements of a test
1.	Create a service in AEM which makes a GET request to fetch response from https://api.chucknorris.io/jokes/random after interval of every 2mins. Store the last 10 (configurable) responses from above API in AEM.
    Make sure an object does not appear twice in the last 10 responses stored.
2.	Create a component to display the response on a page

Once installed you should be able to navigate to the page localhost:450X/content/chuck-norris-page/display-page.html to view the rendered page.
The Chuck Norris jokes are stored under the folder "/etc/chuck-norris/chuck-norris-data".
The content pages and the joke storage locations are created on the install of the bundle.

The service user "chuckserviceuser" is created making use of, ACS Commons Ensure user, which requires acs commons (>3.17.0) to be present on the running target instance for installation. 

Please refer to the **NOTES** and **BUILDING** sections on how to deploy.

Notes
--------

**PLEASE MAKE SURE ACS-AEM-COMMONS-CONTENT** is installed first before building this package

This project depends on acs-aem-commons-content, the assumption is that this has aready 
been installed on the AEM instance.

I personally made use of version 3.17.0 but this may not be the same for all users, so I 
did not include a sub package installation of the specific version that I needed.


Building
--------

**PLEASE MAKE SURE ACS-AEM-COMMONS-CONTENT** is installed first before building this package

This project uses Maven for building. Common commands:

From the root directory, run ``mvn -PautoInstallPackage clean install`` to build the bundle and content package and install to a CQ instance.

From the bundle directory, run ``mvn -PautoInstallBundle clean install`` to build *just* the bundle and install to a CQ instance.

Using with VLT
--------------

To use vlt with this project, first build and install the package to your local CQ instance as described above. Then cd to `content/src/main/content/jcr_root` and run

    vlt --credentials admin:admin checkout -f ../META-INF/vault/filter.xml --force http://localhost:4502/crx

Once the working copy is created, you can use the normal ``vlt up`` and ``vlt ci`` commands.

Specifying CRX Host/Port
------------------------

The CRX host and port can be specified on the command line with:
mvn -Dcrx.host=otherhost -Dcrx.port=5502 <goals>


