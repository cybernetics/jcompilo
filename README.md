A pure Java 6+ build tool with advanced compiler features including:

  * Extremely fast (faster than all other Java build tools by between 20%-80%)
  * Post processing of output bytecode
  * Zero copy Jar creation
  * Tail-recursion call optimisation
  * Test support via [JUnit](http://www.junit.org/)
  * Dependency resolution via [shavenmaven](http://code.google.com/p/shavenmaven/) which gives us the following features
    * Pack200 support (10 x faster downloads)
    * Parallel downloads
    * No transitives
  * Supports 100% convention (no build file needed) or customisation in Java code
  * IntelliJ Plugin (For Compiler)

# Standard Convention #

The following shows the default folder structure for a JCompilo project. This will be very familiar to old school open source developers.

  * jcompilo.sh
  * Build.java (optional)
  * src
    * META-INF (optional)
      * MANIFEST.MF (optional)
    * com
      * example
        * HelloWorld.java
        * SomeResource.txt
  * test
    * com
      * example
        * HelloWorldTest.java

# Latest Releases and Repo #
http://repo.bodar.com/com/googlecode/jcompilo/jcompilo/


# Future exploration #

  * Run tests while compiling!
  * Even more compact console output
  * Remove JUnit/Hamcrest dependency
  * REPL
  * Use REPL as build server from IntelliJ