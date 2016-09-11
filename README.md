# Spring Auto REST Docs

The aim of this extension to Spring REST Docs is to help you to write
even less -- both code and documentation.
You still get the same nice documentation as with Spring REST Docs itself.
The main benefit is that writing less and moving the documentation closer
to the code increases the maintainability of the documentation.

In Spring REST Docs you have to add the documentation for your JSON with
a DSL in your test. We moved this documentation to the POJO that represents
your JSON object. You just add JavaDoc to the fields and it will end
up in the documentation.

## Features:

* Jackson visitor that gathers the whole JSON structure and includes JavaDoc
and constraint annotations on the fields. It works for both request and
response bodies. In addition to the constraint documentation support that
is already Spring REST Docs, we automatically include the constraint message
in the documentation and also added support for documenting constraint groups.
* Path and query parameters can be documented automatically.
* A helper to document authentication.
* A snippet that includes all other snippets of this extension and thus helps
you write even less.

## Building from source

### Get spring-restdocs-core test JAR

The test JAR is not published, but this project relies on it.
If you want to build this project yourself, you first have to build and copy the test JAR on your system.

We use version v1.1.1 of Spring REST Docs in this example.

You find the currently required version in `restdocs-jackson/pom.xml`:

```
<dependency>
    <groupId>org.springframework.restdocs</groupId>
    <artifactId>spring-restdocs-core</artifactId>
    <version>v1.1.1.RELEASE</version>
    <classifier>test</classifier>
    <scope>test</scope>
</dependency>
```

Clone and build a specific version of Spring Restdocs:
```
git@github.com:spring-projects/spring-restdocs.git
cd spring-restdocs
git fetch --tags
git checkout tags/v1.1.1.RELEASE
./gradlew build
```

Afterwards you copy
`spring-restdocs/spring-restdocs-core/build/libs/spring-restdocs-core-1.1.1.RELEASE-test.jar`
to
`~/.m2/repository/org/springframework/restdocs/spring-restdocs-core/1.1.1.RELEASE`
so that the test JAR is available to Maven.

### Build

```
mvn install -f json-doclet/pom.xml
mvn install -f spring-auto-restdocs/pom.xml
```

## TODO

* Move to a new GitHub repo: ScaCap/spring-auto-restdocs
  * one initial commit
  * make public
* Release Maven packages: spring-auto-restdocs, json-doclet
  * https://maven.apache.org/guides/mini/guide-central-repository-upload.html
  * http://central.sonatype.org/pages/ossrh-guide.html
  * Example pom.xml: https://github.com/codecentric/spring-boot-admin/blob/master/pom.xml
* more documentation, but not required for initial release

## TODO impl
* use json parser for javadocs
* shared data model section
* maven logger?
