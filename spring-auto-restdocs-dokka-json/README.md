# KDoc and Javadoc to JSON converter for Spring Auto REST Docs

This library is a [Dokka](https://github.com/Kotlin/dokka) extension.
Dokka is a documentation engine for Kotlin, performing the same function as Javadoc for Java.
Mixed-language Java/Kotlin projects are fully supported.
Dokka understands standard Javadoc comments in Java files and KDoc comments in Kotlin files.
The same holds true for this Dokka extension.

## Usage with Maven

This Dokka extension can be used with the `dokka-maven-plugin`.
To avoid any incompatibilities, the Dokka version of the `dokka-maven-plugin` and of this extension should be the same.
If this extension is included as a dependency of the plugin, the output format `auto-restdocs-json` can be used.

Example usage:
```xml
            <plugin>
                <groupId>org.jetbrains.dokka</groupId>
                <artifactId>dokka-maven-plugin</artifactId>
                <version>${dokka.version}</version>
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <goals>
                            <goal>dokka</goal>
                        </goals>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>capital.scalable</groupId>
                        <artifactId>spring-auto-restdocs-dokka-json</artifactId>
                        <version>${spring-auto-restdocs-dokka-json.version}</version>
                    </dependency>
                </dependencies>
                <configuration>
                    <outputFormat>auto-restdocs-json</outputFormat>
                    <outputDir>${jsonDirectory}</outputDir>
                </configuration>
            </plugin>
```
[Full example](https://github.com/ScaCap/spring-auto-restdocs/blob/master/samples/kotlin-webmvc/pom.xml)

## Usage with Gradle

Dokka extension can be used with the `dokka-gradle-plugin`.
To avoid any incompatibilities, the Dokka version of the `dokka-gradle-plugin` and of this extension should be the same.
If this extension is set as the `dokkaFatJar` in the dokka task, the output format `auto-restdocs-json` can be used.

```groovy
buildscript {
    ext {
        springAutoRestDocsVersion = "2.0.2"
        dokkaVersion = "0.9.16"
    }
    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion"
    }
}
apply plugin: "org.jetbrains.dokka"

dokka {
    outputFormat = "auto-restdocs-json"
    dokkaFatJar = "capital.scalable:spring-auto-restdocs-dokka-json:$springAutoRestDocsVersion"
}
```
[Full example](https://github.com/ScaCap/spring-auto-restdocs/blob/master/samples/kotlin-webmvc/build.gradle)

