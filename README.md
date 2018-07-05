# training2018-final-project

## Docker image (sbt project: restaurant_remote_reception)
**1. Build image**
```
$ sbt docker:publishLocal
```

**2. Run docker container**
```
 $ docker run --env APPLICATION_SECRET={} --env ALLOWED_HOST_DOMAIN={} --env MYSQL_DATABASE_URL={} --env MYSQL_DATABASE_USER={} --env MYSQL_DATABASE_PWD={} -p 9000:9000 restaurant-remote-reception:1.0-SNAPSHOT
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
**2. Build project**
Assumption: API is exposed to localhost:9000
```
$ ng build --prod
```

## Docker image (Angular project: restaurant_remote_reception-app)
Assumption: Angular project has been built
**1. Build image**
```
$ docker image build -t restaurant_remote_reception-app .
```
**2. Run docker container**
```
$ docker run -p 4200:80 --rm restaurant_remote_reception-app
```
