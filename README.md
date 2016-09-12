# TODO-restdocs

Preparation for open sourcing our Spring REST Dos extensions.

## Description

This project is an extensions of spring-restdocs that allows automatic documentation all JSON objects
by getting the structure and types via Jackson and the description from JavaDoc on the fields.
In addition, it includes modifiers that shorten the documented data by limiting JSON arrays to three elements
and by replacing binary data with a short description.

## Building from source

### Get spring-restdocs-core test JAR

The test JAR is not published, but this project relies on it.
If you want to build this project yourself, you first have to build and copy the test JAR on your system.

We use version v1.1.2 of Spring REST Docs in this example.

You find the currently required version in `restdocs-jackson/pom.xml`:

```
<dependency>
    <groupId>org.springframework.restdocs</groupId>
    <artifactId>spring-restdocs-core</artifactId>
    <version>v1.1.2.RELEASE</version>
    <classifier>test</classifier>
    <scope>test</scope>
</dependency>
```

Clone and build a specific version of Spring Restdocs:
```
git@github.com:spring-projects/spring-restdocs.git
cd spring-restdocs
git fetch --tags
git checkout tags/v1.1.2.RELEASE
./gradlew build
```

Afterwards you copy
`spring-restdocs/spring-restdocs-core/build/libs/spring-restdocs-core-1.1.2.RELEASE-test.jar`
to
`~/.m2/repository/org/springframework/restdocs/spring-restdocs-core/1.1.2.RELEASE`
so that the test JAR is available to Maven.

### Build

```
mvn install -f json-doclet/pom.xml
mvn install -f restdocs-jackson/pom.xml
```

## TODO

* Move to a new GitHub repo: ScaCap/scalable-TODO-restdocs
  * one initial commit
  * make public
* Release Maven packages: TODO-restdocs, json-doclet
  * https://maven.apache.org/guides/mini/guide-central-repository-upload.html
  * http://central.sonatype.org/pages/ossrh-guide.html
  * Example pom.xml: https://github.com/codecentric/spring-boot-admin/blob/master/pom.xml
* more documentation, but not required for initial release

## TODO impl
* use json parser for javadocs
* shared data model section
* maven logger?
