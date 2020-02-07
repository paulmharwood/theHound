# the Hound - AWS CFN converter

The hound is a simple java application that aims to convert your AWS YAML Cloudformation templates into useful README files.  

## How does it work?
This app parses the YAML template and works its way through the sections.  It documents:

* template description section
  
  Template description is parsed as is under the template filename.

* metadata section

  This app iterates through the metadata section and pulls out the sub headings (ignoring the default AWS ones), and then processes any description attributes underneath each respsective sub heading.  This is useful if you want to add your own README sections, see the example below.

* parameter section

  Parameters are parsed into a table.

* resource section

  Resources are parsed into bulllet point lists, with logical name and resource type.

* output section

  Outputs are parsed into a table.

## How do I build it?

Simply clone and do a mvn package in the project root where the pom.xml resides.

## How do I execute it?

In the target directory where the package jar will be, run 

```java -jar thehound-1.0-SNAPSHOT.jar <path to template file> ```

The README.md file will be generated in the same directory as the YAML template file.

## Show me an example!

Ok then...

This:

![Image of template](https://github.com/paulmharwood/theHound/blob/master/template-screenshot.png)

Becomes this:

![Image of readme](https://github.com/paulmharwood/theHound/blob/master/readme-screenshot.png)
