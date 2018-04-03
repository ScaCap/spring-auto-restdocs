# Spring Auto REST Docs
[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Build Status](https://travis-ci.org/ScaCap/spring-auto-restdocs.svg?branch=master)](https://travis-ci.org/ScaCap/spring-auto-restdocs)
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

[Current 2.0.1 release](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v2.0.1/docs/index.html) reference guide (based on Spring REST Docs 2.x).

[Current 1.0.13 release](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.13/docs/index.html) reference guide (based on Spring REST Docs 1.x).

Latest master [2.0.2-SNAPSHOT](https://scacap.github.io/spring-auto-restdocs) reference guide.

Older releases:
[2.0.0](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v2.0.0/docs/index.html),
[1.0.12](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.12/docs/index.html),
[1.0.11](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.11/docs/index.html),
[1.0.10](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.10/docs/index.html),
[1.0.9](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.9/docs/index.html),
[1.0.8](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.8/docs/index.html),
[1.0.7](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.7/docs/index.html), 
[1.0.6](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.6/docs/index.html), 
[1.0.5](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.5/docs/index.html), 
[1.0.4](https://htmlpreview.github.io/?https://github.com/ScaCap/spring-auto-restdocs/blob/v1.0.4/docs/index.html).

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

## Building from source

See the [Building from source](https://scacap.github.io/spring-auto-restdocs/#contributing-building) section in the documentation.

## Contributing

- Submit a Pull Request for any enhancement you made.
- Create an issue describing your particular problem.
- Ask and answer questions on Stack Overflow using the [spring-auto-restdocs](https://stackoverflow.com/tags/spring-auto-restdocs) tag.

## License

Spring Auto REST Docs is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
