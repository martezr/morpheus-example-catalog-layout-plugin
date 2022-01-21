# Getting Started with Custom Catalog Layouts

The Morpheus plugin architecture is a library which allows users to extend functionality in several categories, including new Cloud providers, Task types, UI views, custom reports, and more. In this guide, we will take a look at developing a custom self-service catalog layout to customize the look and feel of a catalog item. Complete developer documentation including the full API documentation and links to Github repositories containing complete code examples are available in the Developer Portal.

Custom plugin development requires programming experience but this guide is designed to break down the required steps into a digestible process that users can quickly run with. Morpheus plugins are written in Java or Groovy, our example here will be written in Groovy. Support for additional languages is planned but not yet available at the time of this writing. If you’re not an experienced Java or Groovy developer, it may help to clone an [example code repository](https://github.com/gomorpheus/morpheus-plugin-core/tree/master/samples/morpheus-standard-catalog-layout-plugin) which we link to in our developer portal. An additional example, which this guide is based on, is [here](https://github.com/martezr/morpheus-example-catalog-layout-plugin). You can read the example code and tweak it to suit your needs using the guidance in this document.

Before you begin, ensure you have the following installed in your development environment:

* Gradle 6.5 or later
* Java 8 or 11

In this example, you’ll create a custom self-service catalog item layout that adds a link that displays a modal with example automation code. The example highlights using external JavaScript and CSS to customize the catalog item.

# Developing the Plugin
To begin, create a new directory to house the project. You’ll ultimately end up with a file structure typical of Java or Groovy projects, looking something like this:

Configure the .gitignore file to ignore the build/ directory which will appear after performing the first build. Project packages live within src/main/groovy and contain source files ending in .groovy. View resources are stored in the src/main/resources subfolder and vary depending on the view renderer of choice. Static assets, like icons or custom javascript, live within the src/assets folder. Consult the table below for key files, their purpose, and their locations. Example code and further discussion of relevant files is included in the following sections.


## Creating the build.gradle File
Gradle is the build tool used to compile Morpheus plugins so build.gradle is required. An example build file is given below but some useful values to call out are as follows:

* Group: The package group in Java, typically your reverse DNS name
* Version: The version number for your plugin. This will be displayed in the Plugins section of Morpheus UI for reference when later versions of your plugin are developed
* Plugin-class: This will vary based on the plugin type being developed but for a custom layout, use com.morpheusdata.reports.ReportsPlugin

## Creating the Plugin Class
Next, create a plugin class which handles registration of the new report, sets a name and description, and targets the appropriate report provider class which we’ll go over in the next section.

## Creating the Report Provider Class
The catalog layout provider class contains the code which will fetch and compile the targeted data so it can be rendered in the report view. An example report provider is reproduced below with comments to increase readability of the code.

## Create the Custom Layout View
By default, custom plugin views are handled by a Handlebars template provider to populate HTML sections with your own content. Though it can be overridden, we’ll use the default template provider for this example. There is more information on view rendering in the Morpheus Developer Portal.


# Build the JAR

With the code written, use gradle to build the JAR which we can upload to Morpheus so the report can be viewed. To do so, change directory into the location of the directory created earlier to hold your custom catalog layout code.

Build your new plugin.

```
gradle shadowJar
```

Once the build process has completed, locate the JAR in the build/libs directory

# Upload the Custom Catalog Layout Plugin to the Morpheus UI
Custom plugins are added to Morpheus through the Plugins tab in the Integrations section (Administration > Integrations > Plugins). Navigate to this section and click CHOOSE FILE. Browse for your JAR file and upload it to Morpheus. The new plugin will be added next to any other custom plugins that may have been developed for your appliance.