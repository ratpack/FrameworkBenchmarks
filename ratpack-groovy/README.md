# Ratpack/Groovy Benchmarking Test

This is the Ratpack/Groovy portion of a [benchmarking test suite](../) comparing a variety of web development platforms.

Ratpack apps can be written in Groovy or Java. This variation focuses mostly on developer productivity
utilizing Ratpack's idiomatic Groovy DSL, rather than trying to squeeze every last bit of performance out of it by
using straight Java or other low-level performance-tuning techniques. Another variation should follow shortly
(e.g. Ratpack/Java), which will focus on raw performance.

Currently, there are two test permutations, the "default" one exercising the JSON Serialization and Plaintext
test types on Linux and the "default-windows" one, exercising the same test types on Windows
(see [benchmark_config](benchmark_config) for further details). More will follow soon, along with the implementation
of the [remaining test types](#todo).

### JSON Serialization Test

* [JSON test source](src/ratpack/Ratpack.groovy)

### Plaintext Test

* [Plaintext test source](src/ratpack/Ratpack.groovy)

## Infrastructure Software Versions
The tests were run with:
* [Ratpack 0.9.0](http://ratpack.io)
* [Groovy 2.2.1](http://groovy.codehaus.org)
* [Netty 4.0.12.FINAL](http://netty.io)
* [Jackson 2.2.2](https://github.com/FasterXML/jackson)

## Test URLs
### JSON Serialization Test

http://localhost:5050/json

### Plaintext Test

http://localhost:5050/plaintext

##TODO
* Implement "Single database query" test type
* Implement "Multiple database queries" test type
* Implement "Fortunes" test type
* Implement "Database updates" test type