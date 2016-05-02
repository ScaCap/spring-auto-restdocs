# restdocs-jackson

Preparation for open sourcing our Spring REST Dos extensions.

## Description

This project is an extensions of spring-restdocs that allows automatic documentation all JSON objects
by getting the structure and types via Jackson and the description from JavaDoc on the fields.
In addition, it includes modifiers that shorten the documented data by limiting JSON arrays to three elements
and by replacing binary data with a short description.

## TODO

* Move to a new GitHub repo: ScaCap/scalable-restdocs-jackson
  * one initial commit
  * make public
* Release Maven packages
  * restdocs-jackson
  * json-doclet
* more documentation, but not required for initial release

## TODO impl
* make things more robust, stable, generic, commented
* add autodoc of endpoint URL (`GET /items` section)
* add autodoc of endpoint text (Endpoint documentation section - from method javadoc)
* add autodoc of entire endpoint docs (joining all sections into one file: url, params, request, response, example) so index.adoc would only need to include that one
* add authentication section
