References:
  - https://docs.github.com/en/actions/use-cases-and-examples/building-and-testing/building-and-testing-java-with-maven

The test will fail because the tests need a Postgres database server. But in the build env in GitHub, there is no database server to connect to. Therefore, the `mvn package` need to run with the option `-DskipTests`.


