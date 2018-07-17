# training2018-final-project

## Docker image (sbt project: restaurant_remote_reception)
**1. Build image**
```
$ sbt docker:publishLocal
```

**2. Run docker container**
```
 $ docker run --env APPLICATION_SECRET={} --env ALLOWED_HOST_DOMAIN={} --env MYSQL_DATABASE_URL={} --env MYSQL_DATABASE_USER={} --env MYSQL_DATABASE_PWD={} --env ALLOWED_HOST_DOMAIN={} -p 9000:9000 restaurant-remote-reception:latest
 ```
 docker run --env APPLICATION_SECRET='6lcsVk73>cZ9gJN=4Fvo4[iA[f_Hi>LNC7m_SNv?7H8InM68fzOgSI@8rGuH' --env ALLOWED_HOST_DOMAIN_1="localhost:4200" --env MYSQL_DATABASE_URL="jdbc:mysql://localhost:3306/restaurant_remote_reception" --env MYSQL_DATABASE_USER="root" --env MYSQL_DATABASE_PWD="mysql" -p 9000:9000 restaurant-remote-reception:latest

 docker run --env APPLICATION_SECRET='6lcsVk73>cZ9gJN=4Fvo4[iA[f_Hi>LNC7m_SNv?7H8InM68fzOgSI@8rGuH' --env ALLOWED_HOST_DOMAIN_1='localhost:9000' ALLOWED_HOST_DOMAIN_2='localhost:4200' -p 9000:9000 restaurant-remote-reception:latest

 docker run --env APPLICATION_SECRET='6lcsVk73>cZ9gJN=4Fvo4[iA[f_Hi>LNC7m_SNv?7H8InM68fzOgSI@8rGuH' --env ALLOWED_HOST_DOMAIN_1='localhost:9000' --env ALLOWED_HOST_DOMAIN_2='localhost:9000' --env ALLOWED_HOST_DOMAIN_3='localhost:9000' --env ALLOWED_HOST_DOMAIN_4='localhost:9000' -p 9000:9000 restaurant-remote-reception:latest
"ec2-18-182-40-10.ap-northeast-1.compute.amazonaws.com",


**3. Interact with container**
```
 $ curl -I localhost:9000
```

## Sending request to API
Assumption: Running MySQL server at port 3306 initialised with `create.sql`
**1. List user**
```
$ curl -XGET http://localhost:9000/api/users
```
**2. Add user**
```
$ curl -H "Content-type: application/json" -XPOST -d '{"full_name":"Your Fullname", "email":"youremail@email.com", "password": "password"}' http://localhost:9000/api/users
```

## Angular project: restaurant_remote_reception-app
Assumption: node and npm installed
**1. Run project**
Run the following command and open your browser at http://localhost:4200
```
$ ng serve --proxy-config proxy.conf.json
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
$ docker image build -t restaurant-remote-reception-app .
```
**2. Run docker container**
```
$ docker run -p 4200:80 --rm restaurant-remote-reception-app
```
