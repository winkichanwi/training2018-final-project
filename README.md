# training2018-final-project

## Docker images (sbt project)
**1. Create image**
`$ sbt docker:publishLocal`

**2. Run docker container**
` $ docker run --env APPLICATION_SECRET={application secret} -p 9000:9000 restaurant-remote-reception:1.0-SNAPSHOT`

**3. Interact with container**
` $ curl -I localhost:9000 `
