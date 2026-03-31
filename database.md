# Setup instructions

## MySql

### Installation

Either
- Install [MySql](https://dev.mysql.com/doc/mysql-getting-started/en/) locally
- Or use a docker container : ` docker compose up`.

### Enter the database (when using docker)

```
docker exec -it mysql-db mysql -u root -p
```

Password : `root`

Then you can execute SQL commands. Ex : `SELECT * FROM mydb.user;`

### Add a new table

- Update `resources/schema.sql`
- Add an entity file in the folder `entity`
- Add a repository file in the folder `repository` (interface of `CrudRepository`)
- Add a service file in the folder `service`
- Add a controller file in the folder `controller`
You can just adapt copy and adapt the existing files.