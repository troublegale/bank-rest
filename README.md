# Bank REST Test Assignment

### Features

- Bank cards CRUD with filters & pagination
- Card operations (activating, blocking via tickets, transfers)
- JWT authorization with access & refresh tokens
- User management

### Launch

1. Clone repo: `git clone https://github.com/troublegale/bank-rest`
2. Change values in `.env`
3. Build with Maven: `mvn clean package`
4. Launch containers: `docker compose up -d --build`
5. Access the API via `https://localhost:8443/api/v1` (make sure to trust the Caddy certificate!).
Both HTTP/1.1 & HTTP/2 are available.
6. See Swagger UI documentation at `http://localhost:8080/`
