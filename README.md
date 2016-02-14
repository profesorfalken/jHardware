![](https://img.shields.io/maven-central/v/org.jhardware/jHardware.svg)
![](https://img.shields.io/github/license/profesorfalken/jHardware.svg)

# jHardware
Get crossplatform hardware details with Java

## Installation ##

To install jHardware you can add the dependecy to your software project management tool: http://mvnrepository.com/artifact/org.jhardware/jHardware/0.2

For example, for Maven you have just to add to your pom.xml: 

     <dependency>
          <groupId>org.jhardware</groupId>
          <artifactId>jHardware</artifactId>
          <version>0.3</version>
     </dependency>


Instead, you can direct download the JAR file and add it to your classpath. 
http://central.maven.org/maven2/org/jhardware/jHardware/0.3/jHardware-0.3.jar

## Basic Usage ##

To retrieve hardware data we only have to call an static method. This method will return a bean with all the basic and crossbrowser data and it will also include a map with all the data retrieved in the system.

#### Get CPU details ####
```java
    ProcessorInfo info = HardwareInfo.getProcessorInfo();
    //Get named info
    System.out.println("Cache size: " + info.getCacheSize());        
    System.out.println("Family: " + info.getFamily());
    System.out.println("Speed (Mhz): " + info.getMhz());
    //[...]

    //Get all info for map
    Set<Entry<String, String>> fullInfos = info.getFullInfo().entrySet();
        
    for (final Entry<String, String> fullInfo : fullInfos) {
        System.out.println(fullInfo.getKey() + ": " + fullInfo.getValue());
    }
```

Same for other hardware components as Memory, Motherboard, Bios...
    
## More info ##

Webpage: http://www.jhardware.org
