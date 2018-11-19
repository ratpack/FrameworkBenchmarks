# Ratpack Benchmarking Test

This is the Ratpack portion of a [benchmarking test suite](../) comparing a variety of web development platforms.

Ratpack apps can be written in either Groovy or Java. This project includes benchmarks written in both languages,
under the [ratpack-groovy](ratpack-groovy) and [ratpack-java](ratpack-java) directories respectively.

The `ratpack-groovy` module focuses mostly on developer productivity utilizing Ratpack's idiomatic Groovy DSL, rather 
than trying to squeeze every last bit of performance out of it by using pure Java or other performance-tuning
techniques. `ratpack-java` on the other hand, focuses on raw performance using Java top to bottom and taking advantage 
of low-level optimizations where possible.

Currently, there are two test permutations, the "default" one exercising the required test types using the Groovy DSL, 
and the "java" one exercising the same test types using the Java DSL, both running on Linux (see [benchmark_config](benchmark_config) 
for further details). More will follow soon, along with the implementation of the [remaining test types](#todo).

### JSON Serialization Test

* [Groovy JSON test source](ratpack-groovy/src/ratpack/Ratpack.groovy)
* [Java JSON test source](ratpack-java/src/main/java/ratpack/benchmarks/techempower/java/HandlerFactory.java)

### Single Database Query Test

* [Groovy Single DB Query test source](ratpack-groovy/src/ratpack/Ratpack.groovy)

### Multiple Database Queries Test

* [Groovy Multiple DB Queries test source](ratpack-groovy/src/ratpack/Ratpack.groovy)

### Fortunes Test

* [Groovy Fortunes test source](ratpack-groovy/src/ratpack/Ratpack.groovy)

### Database Updates Test

* [Groovy DB Updates test source](ratpack-groovy/src/ratpack/Ratpack.groovy)

### Plaintext Test

* [Groovy Plaintext test source](ratpack-groovy/src/ratpack/Ratpack.groovy)
* [Java Plaintext test source](ratpack-java/src/main/java/ratpack/benchmarks/techempower/java/HandlerFactory.java)

## Infrastructure Software Versions
The tests were run with:
* [Ratpack 0.9.4-SNAPSHOT](http://ratpack.io)
* [Groovy 2.2.1](http://groovy.codehaus.org)
* [Netty 4.0.18.FINAL](http://netty.io)
* [Jackson 2.3.1](https://github.com/FasterXML/jackson)
* [HikariCP 1.3.0](https://github.com/brettwooldridge/HikariCP)
* [MySQL Connector/J 5.1.28](http://dev.mysql.com/downloads/connector/j/)

## Test URLs
### JSON Serialization Test

http://localhost:5050/json

### Single Database Query Test

http://localhost:5050/db

### Multiple Database Queries Test

http://localhost:5050/queries

### Fortunes Test

http://localhost:5050/fortunes

### Database Updates Test

http://localhost:5050/updates

### Plaintext Test

http://localhost:5050/plaintext

##TODO
* Implement Java version of "Single database query" test type
* Implement Java version of "Multiple database queries" test type
* Implement Java version of "Fortunes" test type
* Implement Java version of "Database updates" test type

