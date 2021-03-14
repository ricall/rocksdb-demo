# rocksdb-demo
Simple demo of rocksdb based on https://levelup.gitconnected.com/using-rocksdb-with-spring-boot-and-java-99cb1c43a834

## Getting Started
```bash
./gradlew bootRun
```

This will start the application on port 8181, the following rest endpoints are available:

### Add Key/Value
```http request
POST http://localhost:8181/api/1234
Content-Type: application/json
Accept: application/json

{
	"field": "value",
	"another-field": "another value"
}
```

### Get Value associated with Key
```http request
GET http://localhost:8181/api/1234
Accept: application/json
```

### Delete Key/Value
```http request
DELETE http://localhost:8181/api/1234
Accept: application/json
```

