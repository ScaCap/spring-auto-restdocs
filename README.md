# restdocs-jackson

Preparation for open sourcing our Spring REST Dos extensions.

This project is an extensions of spring-restdocs that allows automatic documentation all JSON objects
 by getting the structure and types via Jackson and the description from JavaDoc on the fields.
 In addition, it includes modifiers that shorten the documented data by limiting JSON arrays to three elements
 and by replacing binary data with a short description.

## Still to discuss

* Name of the project, currently `restdocs-jackson`. Alternatives: `restdocs-reflection`
* Maven group ID / package names, currently `capital.scalable`. Is usually the inverse of a company URL. Thus, `com.scacap` is also possible.
* Do we keep three test files copied from spring-restdocs-core? Alternatives write our own helpers or include spring-restdocs-core test jar (gradle build exists, but not published).
