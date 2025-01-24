
# Directory structure

Our directory is like this:

```
SE-Group5-API/
├── .github/workflows/<action_name>.yml 
├── bus-api/
│   ├── Dockerfile_Actions
|   ├── src
|   ├── target
│   ├── ... other files ...
└── ... other files ...
```

When building the Dockerfile, the directory must be correct.

# Create the Repo secrets

In **Project - Settings - Security - Secrets and Variables - Actions.**

In the page **Repository Secrets **, create **New repository secrets** for 
- **DOCKER_USERNAME**: Your Docker Hub username,
- **DOCKER_PASSWORD**: Your Docker Hub password.

# Actions

On **push** and **pull-request**, the action **Java CI with Maven** (package.yml) will run.

On **release**, the action **Package JAR and Publish Docker image** (package_publish.yml) will run.

# Test and Postgres

The integration test will fail because the tests need a **Postgres** database server. But in the build env in GitHub, there is no database server to connect to. Therefore, the `mvn package` need to run with the option `-DskipTests`.

# Explanation on this part of directory

Because of the way the directory is strucutered, we need to set the source files and Docker_Actions file to be in the **bus-api** directory.

The **ls -a $GITHUB_WORKSPACE** is not needed for the action. It is for myself to see the files in the directory.

GITHUB_WORKSPACE is a built-in environment variable in GitHub Actions. It represents the root directory where your workflow runs. Essentially, it's the top-level folder for your entire repository within the GitHub Actions runner environment.

The line `context` and `file` will let the build action to get files from the correct directory.

```
- name: List workspace directory
        run: |
          ls -a $GITHUB_WORKSPACE
      - name: Build and push Docker image
        id: push
        uses: docker/build-push-action@3b5e8027fcad23fda98b2e3ac259d8d67585f671
        with:
          context: ./bus-api
          file: ./bus-api/Dockerfile_Actions
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
```



# References:
  - https://docs.github.com/en/actions/use-cases-and-examples/building-and-testing/building-and-testing-java-with-maven
  - https://docs.github.com/en/actions/sharing-automations/creating-actions/dockerfile-support-for-github-actions
  - https://spring.io/guides/gs/spring-boot-docker

