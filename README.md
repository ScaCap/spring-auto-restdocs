# Spring Auto REST Docs
[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Build Status](https://travis-ci.org/ScaCap/spring-auto-restdocs.svg?branch=master)](https://travis-ci.org/ScaCap/spring-auto-restdocs)

**Full documentation at https://scacap.github.io/spring-auto-restdocs**

The aim of this extension to Spring REST Docs is to help you to write
even less -- both code and documentation.
You still get the same nice documentation as with Spring REST Docs itself.
The main benefit is that writing less and moving the documentation closer
to the code increases the maintainability of the documentation.

In Spring REST Docs you have to add the documentation for your JSON with
a DSL in your test. We moved this documentation to the POJO that represents
your JSON object. You just add Javadoc to the fields and it will end
up in the documentation.

## Features:

* Jackson visitor that gathers the whole JSON structure and includes Javadoc
and constraint annotations on the fields. It works for both request and
response bodies. In addition to the constraint documentation support that
is already Spring REST Docs, we automatically include the constraint message
in the documentation and also added support for documenting constraint groups.
* Path and query parameters can be documented automatically.
* A helper to document authentication.
* A snippet that includes all other snippets of this extension and thus helps
you write even less.

## Usage

See [Getting started section](https://scacap.github.io/spring-auto-restdocs/#gettingstarted) in documentation.

## Building from source

See [Building from source section](https://scacap.github.io/spring-auto-restdocs/#contributing-building) in documentation.

