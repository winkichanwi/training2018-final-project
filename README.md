# training2018-final-project

## Docker images (sbt project: restaurant_remote_reception)
**1. Create image**
```
$ sbt docker:publishLocal
```

**2. Run docker container**
```
 $ docker run --env APPLICATION_SECRET={application secret} --env ALLOWED_HOST_DOMAIN={domain} -p 9000:9000 restaurant-remote-reception:1.0-SNAPSHOT
 ```

**3. Interact with container**
```
 $ curl -I localhost:9000
```

## Sending request to API
Assumption: Running MySQL server at port 3306 initialised with `create.sql`
**1. List user**
```
$ curl -XGET http://localhost:9000/users
```
**2. Add user**
```
$ curl -H "Content-type: application/json" -XPOST -d '{"full_name":"Your Fullname", "email":"youremail@email.com", "password": "password"}' http://localhost:9000/users
```

## Angular project: restaurant_remote_reception-app
Assumption: node and npm installed
**1. Run project**
Run the following command and open your browser at http://localhost:4200
```
$ ng serve
```
