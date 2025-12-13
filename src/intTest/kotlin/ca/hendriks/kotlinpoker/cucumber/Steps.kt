package ca.hendriks.kotlinpoker.cucumber

import ca.hendriks.planningpoker.assignment.AssignmentRepository
import ca.hendriks.planningpoker.ktor
import ca.hendriks.planningpoker.room.RoomRepository
import io.cucumber.java.AfterAll
import io.cucumber.java.BeforeAll
import io.cucumber.java.en.Given
import io.kotest.matchers.shouldNotBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.runBlocking

class Steps {

    @Given("my application")
    fun rootRouteRespondsWithHelloWorldString(): Unit = runBlocking {
        val response: String = HttpClient()
            .get("http://localhost:8080/")
            .body()
        response shouldNotBe null
    }

    object TestDependices {

        val roomRepository: RoomRepository = RoomRepository()
        val usersToRoom: AssignmentRepository = AssignmentRepository()

        private var embeddedServer: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

        @BeforeAll
        @JvmStatic
        fun startServer() {
            embeddedServer = ktor(8080, roomRepository, usersToRoom)
                .start()
        }

        @AfterAll
        @JvmStatic
        fun stopServer(): Unit = runBlocking {
            embeddedServer?.stop(1000)
        }

    }

}
