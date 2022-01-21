# Getting Started with Custom Catalog Layouts

The Morpheus plugin architecture is a library which allows users to extend functionality in several categories, including new Cloud providers, Task types, UI views, custom reports, and more. In this guide, we will take a look at developing a custom self-service catalog layout to customize the look and feel of a catalog item. Complete developer documentation including the full API documentation and links to Github repositories containing complete code examples are available in the Developer Portal.

Custom plugin development requires programming experience but this guide is designed to break down the required steps into a digestible process that users can quickly run with. Morpheus plugins are written in Java or Groovy, our example here will be written in Groovy. Support for additional languages is planned but not yet available at the time of this writing. If you’re not an experienced Java or Groovy developer, it may help to clone an [example code repository](https://github.com/gomorpheus/morpheus-plugin-core/tree/master/samples/morpheus-standard-catalog-layout-plugin) which we link to in our developer portal. An additional example, which this guide is based on, is [here](https://github.com/martezr/morpheus-example-catalog-layout-plugin). You can read the example code and tweak it to suit your needs using the guidance in this document.

Before you begin, ensure you have the following installed in your development environment:

* Gradle 6.5 or later
* Java 8 or 11

In this example, you’ll create a custom self-service catalog item layout that adds a link that displays a modal with example automation code. The example highlights using external JavaScript and CSS to customize the catalog item.

# Developing the Plugin
To begin, create a new directory to house the project. You’ll ultimately end up with a file structure typical of Java or Groovy projects, looking something like this:

```
./
.gitignore
build.gradle
src/main/groovy/
src/main/resources/renderer/hbs/
src/test/groovy/
src/assets/images/
src/assets/javascript/
src/assets/stylesheets/
```

Configure the .gitignore file to ignore the build/ directory which will appear after performing the first build. Project packages live within src/main/groovy and contain source files ending in .groovy. View resources are stored in the src/main/resources subfolder and vary depending on the view renderer of choice. Static assets, like icons or custom javascript, live within the src/assets folder. Consult the table below for key files, their purpose, and their locations. Example code and further discussion of relevant files is included in the following sections.


## Creating the build.gradle File
Gradle is the build tool used to compile Morpheus plugins so build.gradle is required. An example build file is given below but some useful values to call out are as follows:

* Group: The package group in Java, typically your reverse DNS name
* Version: The version number for your plugin. This will be displayed in the Plugins section of Morpheus UI for reference when later versions of your plugin are developed
* Plugin-class: This will vary based on the plugin type being developed but for a custom layout, use `com.morpheusdata.cataloglayout.CatalogLayoutPlugin`

```
plugins {
    id "com.bertramlabs.asset-pipeline" version "3.3.2"
    id "com.github.johnrengelman.plugin-shadow" version "2.0.3"
}

apply plugin: 'java'
apply plugin: 'groovy'
apply plugin: 'maven-publish'

group = 'com.morpheusdata'
version = '0.0.1'

sourceCompatibility = '1.8'
targetCompatibility = '1.8'

ext.isReleaseVersion = !version.endsWith("SNAPSHOT")

repositories {
    mavenCentral()
}

dependencies {
    compileOnly 'com.morpheusdata:morpheus-plugin-api:0.12.0'
    compileOnly 'org.codehaus.groovy:groovy-all:2.5.6'
    compileOnly 'io.reactivex.rxjava2:rxjava:2.2.0'
    compileOnly "org.slf4j:slf4j-api:1.7.26"
    compileOnly "org.slf4j:slf4j-parent:1.7.26"
}

jar {
    manifest {
        attributes(
	    'Plugin-Class': 'com.morpheusdata.cataloglayout.CatalogLayoutPlugin',
            'Plugin-Version': archiveVersion.get() // Get version defined in gradle
        )
    }
}

tasks.assemble.dependsOn tasks.shadowJar
```

## Creating the Plugin Class
Next, create a plugin class which handles registration of the new catalog layout, sets a name and description, and targets the appropriate catalog layout provider class which we’ll go over in the next section.

```
package com.morpheusdata.cataloglayout

import com.morpheusdata.core.Plugin
import com.morpheusdata.model.Permission
import com.morpheusdata.views.HandlebarsRenderer
import com.morpheusdata.views.ViewModel
import com.morpheusdata.web.Dispatcher
import com.morpheusdata.model.OptionType

class CatalogLayoutPlugin extends Plugin {

	@Override
	String getCode() {
		return 'example-catalog-layout-plugin'
	}

	@Override
	void initialize() {
		BasicCatalogLayoutProvider basicCatalogLayoutProvider = new BasicCatalogLayoutProvider(this, morpheus)
		this.pluginProviders.put(basicCatalogLayoutProvider.code, basicCatalogLayoutProvider)
		this.setName("Example Catalog Layout Plugin")
		this.setDescription("Example plugin for customizing self-service catalog items")
		this.setAuthor("Martez Reed")
		this.setSourceCodeLocationUrl("https://github.com/martezr/morpheus-example-catalog-layout-plugin")
		this.setIssueTrackerUrl("https://github.com/martezr/morpheus-example-catalog-layout-plugin/issues")
		this.setPermissions([Permission.build('Example Catalog Layout','example-catalog-view', [Permission.AccessType.none, Permission.AccessType.full])])
        }
	@Override
	void onDestroy() {}
}
```

## Creating the Report Provider Class
The catalog layout provider class contains the code which will fetch and compile the targeted data so it can be rendered in the report view. An example report provider is reproduced below with comments to increase readability of the code.

```
package com.morpheusdata.cataloglayout

import com.morpheusdata.core.AbstractCatalogItemLayoutProvider
import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.model.Account
import com.morpheusdata.model.CatalogItemType
import com.morpheusdata.model.TaskConfig
import com.morpheusdata.model.ContentSecurityPolicy
import com.morpheusdata.model.User
import com.morpheusdata.views.HTMLResponse
import com.morpheusdata.views.ViewModel

class BasicCatalogLayoutProvider extends AbstractCatalogItemLayoutProvider {
	Plugin plugin
	MorpheusContext morpheus

	String code = 'basic-catalog-layout'
	String name = 'Basic Catalog Layout'

	BasicCatalogLayoutProvider(Plugin plugin, MorpheusContext context) {
		this.plugin = plugin
		this.morpheus = context
	}

	@Override
	HTMLResponse renderTemplate(CatalogItemType catalogItemType, User user) {
		ViewModel<CatalogItemType> model = new ViewModel<>()

        // Create a payload to pass to the HTML template		
		def HashMap<String, String> catalogItemPayload = new HashMap<String, String>();

        // Add the catalog item to the payload object
		catalogItemPayload.put("catalogItem", catalogItemType)

        // Fetch the web nonce to align with the content security policy (CSP)
		// to enable the execution of the JavaScript script and add
		// it to the payload object used by the HTML template
		def webnonce = morpheus.getWebRequest().getNonceToken()
		catalogItemPayload.put("webnonce",webnonce)
		
		model.object = catalogItemPayload
		getRenderer().renderTemplate("hbs/basicCatalogItem", model)
	}

	@Override
	ContentSecurityPolicy getContentSecurityPolicy() {
		def csp = new ContentSecurityPolicy()
		csp
	}
}
```

## Create the Custom Layout View
By default, custom plugin views are handled by a Handlebars template provider to populate HTML sections with your own content. Though it can be overridden, we’ll use the default template provider for this example. There is more information on view rendering in the Morpheus Developer Portal.


# Build the JAR

With the code written, use gradle to build the JAR which we can upload to Morpheus so the report can be viewed. To do so, change directory into the location of the directory created earlier to hold your custom catalog layout code.

```
cd path/to/your/directory
```

Build your new plugin.

```
gradle shadowJar
```

Once the build process has completed, locate the JAR in the `build/libs` directory

# Upload the Custom Catalog Layout Plugin to the Morpheus UI
Custom plugins are added to Morpheus through the Plugins tab in the Integrations section (Administration > Integrations > Plugins). Navigate to this section and click CHOOSE FILE. Browse for your JAR file and upload it to Morpheus. The new plugin will be added next to any other custom plugins that may have been developed for your appliance.