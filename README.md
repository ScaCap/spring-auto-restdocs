# End-of-life Notice

This project is officially end-of-life. Users are welcome to maintain it in a
fork, but there will be no more Scalable-authored changes published. While Spring
Auto REST Docs helped us over the years to maintain our API documentation,
we decided to move to [OpenAPI] and phased out the usage internally. At Scalable,
[springdoc-openapi] helps us now to automate the generation of API documentation.

[OpenAPI]: https://www.openapis.org/
[springdoc-openapi]: https://springdoc.org/

---

# Spring Auto REST Docs
[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Build status](https://github.com/ScaCap/spring-auto-restdocs/actions/workflows/build.yaml/badge.svg)](https://github.com/ScaCap/spring-auto-restdocs/actions/workflows/build.yaml)
[![Maven Central status](https://img.shields.io/maven-central/v/capital.scalable/spring-auto-restdocs-core.svg)](https://search.maven.org/#search%7Cga%7C1%7Ccapital.scalable%20spring-auto-restdocs)

The aim of this [Spring REST Docs](https://projects.spring.io/spring-restdocs/)
extension is to help you write even less - both code and documentation.
You still get the same nice documentation as with Spring REST Docs itself.
The main benefit is that writing less and moving the documentation closer
to the code increases the maintainability of the documentation.

In Spring REST Docs you have to add the documentation for your JSON with
a DSL in your test. We moved this documentation to the POJO that represents
your JSON object. You just add Javadoc to the fields and it will end
up in the documentation.

Learn more in the [Introducing Spring Auto REST Docs](https://dzone.com/articles/introducing-spring-auto-rest-docs) article.
The [slides](https://www.slideshare.net/fbenz/introducing-spring-auto-rest-docs)
and the
[video recording](https://www.youtube.com/watch?v=M7GEN6Jh6CQ)
from the Introducing Spring Auto REST Docs talk at Spring IO 2017 are also available.

## Documentation

[Current 2.0.11 release](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v2.0.11/docs/index.html) reference guide (based on Spring REST Docs 2.x).

[Legacy 1.0.15 release](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.15/docs/index.html) reference guide (based on Spring REST Docs 1.x).

Latest master [2.0.12-SNAPSHOT](https://scacap.github.io/spring-auto-restdocs) reference guide.

## Main features

* Automatic documentation of
[request](https://scacap.github.io/spring-auto-restdocs/#snippets-request-fields) and
[response](https://scacap.github.io/spring-auto-restdocs/#snippets-response-fields) fields,
[path](https://scacap.github.io/spring-auto-restdocs/#snippets-path-parameters),
[query parameters](https://scacap.github.io/spring-auto-restdocs/#snippets-request-parameters) and
[request headers](https://scacap.github.io/spring-auto-restdocs/#snippets-request-headers)
using Jackson and Javadoc/KDoc
* Automatic documentation of field and parameter
[optionality](https://scacap.github.io/spring-auto-restdocs/#constraints-optionality) and
[constraints](https://scacap.github.io/spring-auto-restdocs/#constraints) using JSR 303 annotations
* Automatic documentation of [entire endpoint](https://scacap.github.io/spring-auto-restdocs/#snippets-section) with [customizations](https://scacap.github.io/spring-auto-restdocs/#snippets-section-customization)
* A helper to document [authentication](https://scacap.github.io/spring-auto-restdocs/#snippets-authorization)
* Support for [paging](https://scacap.github.io/spring-auto-restdocs/#paging)
* Convenient [preprocessors](https://scacap.github.io/spring-auto-restdocs/#preprocessors)

## Usage

See the [Getting started](https://scacap.github.io/spring-auto-restdocs/#gettingstarted) section in the documentation.

## Sample projects

* [Java WebMVC](https://github.com/ScaCap/spring-auto-restdocs/tree/master/samples/java-webmvc): Spring Boot 2.3, WebMVC, JUnit 4, Java 8/11/14, Maven/Gradle
* [Java WebFlux](https://github.com/ScaCap/spring-auto-restdocs/tree/master/samples/java-webflux): Spring Boot 2.3, WebFlux, JUnit 4, Java 8/11/14, Maven
* [Kotlin WebMVC](https://github.com/ScaCap/spring-auto-restdocs/tree/master/samples/kotlin-webmvc): Spring Boot 2.3, WebMVC, JUnit 5, Kotlin 1.4, Maven/Gradle

All sample projects use [shared POJOs](https://github.com/ScaCap/spring-auto-restdocs/tree/master/samples/shared)
to demonstarte the usage across projects.

## Building from source

See the [Building from source](https://scacap.github.io/spring-auto-restdocs/#contributing-building) section in the documentation.

## Contributing

- Submit a Pull Request for any enhancement you made.
- Create an issue describing your particular problem.
- Ask and answer questions on Stack Overflow using the [spring-auto-restdocs](https://stackoverflow.com/tags/spring-auto-restdocs) tag.

## License

Spring Auto REST Docs is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
