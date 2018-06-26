# training2018-final-project

## Docker images (sbt project)
**1. Create image**
`$ docker build -t restaurant-remote-reception . `

**2. Run docker container**
` $ docker run -it -p 9000:9000 --rm restaurant-remote-reception `

**3. Interact with container**
` $ curl -I localhost:9000 `
