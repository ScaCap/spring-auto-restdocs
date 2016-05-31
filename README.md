# restdocs-jackson

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

We use version 1.1.0.RC1 of Spring Restdocs in this example.

You find the currently required version in `restdocs-jackson/pom.xml`:

```
        <dependency>
            <groupId>org.springframework.restdocs</groupId>
            <artifactId>spring-restdocs-core</artifactId>
            <version>1.1.0.RC1</version>
            <classifier>test</classifier>
            <scope>test</scope>
        </dependency>
```

Clone and build a specific version of Spring Restdocs:
```
git@github.com:spring-projects/spring-restdocs.git
cd spring-restdocs
git checkout tags/v1.1.0.RC1
./gradlew build
```

Afterwards you copy
`spring-restdocs/spring-restdocs-core/build/libs/spring-restdocs-core-1.1.0.RC1-test.jar`
to
`~/.m2/repository/org/springframework/restdocs/spring-restdocs-core/1.1.0.RC1`
so that the test JAR is available to Maven.

### Build

```
mvn install -f json-doclet/pom.xml
mvn install -f restdocs-jackson/pom.xml
```

## TODO

* Move to a new GitHub repo: ScaCap/scalable-restdocs-jackson
  * one initial commit
  * make public
* Release Maven packages
  * restdocs-jackson
  * json-doclet
* more documentation, but not required for initial release

## TODO impl
* add authentication section
* use json parser for javadocs
* pageable section
* shared data model section
* log warn for unresolvable comment
* maven logger?
