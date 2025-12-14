# Planning Poker

A Hypermedia-driven Scrum Poker web application built with Kotlin, HTMX, and Ktor SSE.

Try it out online: https://planningpoker.hendriks.ca/

## Requirements

- [JDK v21](https://openjdk.org/projects/jdk/21/)
- [Git](https://www.atlassian.com/git/tutorials/install-git)
- [Gradle 9](https://gradle.org)
- [Optional] [Heroku CLI client](https://devcenter.heroku.com/articles/heroku-cli)

## Installation

1. Download the [source code](https://github.com/jasonhendriks/planningpoker) to your workstation
    ```
    git clone https://github.com/jasonhendriks/planningpoker
    ```
3. [Optional] If you installed the Heroku CLI, connect your local repository to Heroku:
    ```
    heroku git:remote --app planningpoker
    ```

## Running Locally

### Build the application and run with the Maven Spring Boot plug-in

```
$ ./gradlew build
```

Then access the application in your web browser: http://localhost:8080

### Build the application and run with the Heroku CLI

```
$ ./gradlew build
$ heroku local
```

Then access the application in your web browser: http://localhost:5001

## Deployment to Production

### Continuous Integration

After committing and pushing any changes, GitHub will run the tests. If successful, Heroku will automatically retrieve
the changes, build and deploy.

### Manual Deploy with Heroku CLI

Push to the Heroku GIT remote to manually trigger a deployment:

```
git push heroku
```

## Production Support

### Debugging

View the production logs:

```
heroku logs --tail --app planningpoker
```

## Development Resources

HTMX

- [What is Hypermedia?](https://hypermedia.systems/introduction/)
- [HTMX](https://htmx.org/)
- [You can do so much with hx-boost, hx-indicator, and simple forms / links @ Reddit](https://www.reddit.com/r/htmx/comments/196gwsa/you_can_do_so_much_with_hxboost_hxindicator_and/)

Heroku

- https://devcenter.heroku.com/articles/java-session-handling-on-heroku

## Application Endpoints

### Web URLs:

| Endpoint                | Description             |
|-------------------------|-------------------------|
| `GET /`                 | home page/lobby         |
| `GET /room/{room-name}` | show homepage with room |

## HTMX API:

| Endpoint                                                       | Description                                                |
|----------------------------------------------------------------|------------------------------------------------------------|
| `POST /assignment?room-name={room-name}&user-name={user-name}` | assign current user to room / return SSE room fragment     |
| `GET /assignments/{id}/sse`                                    | connect to SSE stream for current user / return SSE stream |
| `DELETE /assignments/{id}`                                     | remove current user from room / return lobby fragment      |
| `POST /room/{room-name}/voting`                                |                                                            |                                                              
| `DELETE /room/{room-name}/voting`                              |                                                            |                                                              
| `POST /vote/{id}`                                              |                                                            |                                                              

## JSON API:

| Endpoint                 | Description                             |
|--------------------------|-----------------------------------------|
| `GET /rooms`             | list all rooms                          |
| `GET /rooms/{room-name}` | show details of specified room          |
| `GET /users`             | list all users                          |
| `GET /users/{room-name}` | show details of specified user          |
| `GET /assignments/`      | list all users                          |
| `GET /assignments/{id}`  | list particular User-to-Room assignment |
