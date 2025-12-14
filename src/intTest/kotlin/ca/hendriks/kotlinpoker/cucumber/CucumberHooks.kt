package ca.hendriks.kotlinpoker.cucumber

import ca.hendriks.planningpoker.assignment.AssignmentRepository
import ca.hendriks.planningpoker.ktor
import ca.hendriks.planningpoker.room.RoomRepository
import io.cucumber.java.AfterAll
import io.cucumber.java.BeforeAll
import io.cucumber.java.en.Given
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.runBlocking

class CucumberHooks {

    companion object {
        const val PORT = 8888
    }

    @Given("my application")
    fun rootRouteRespondsWithHelloWorldString(): Unit = runBlocking {

//        // Initiate Webdriver
//        val driver: WebDriver = SafariDriver()
//
//        // adding implicit wait of 15 seconds
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15))
//
//        // URL launch
//        driver.get("http://localhost:$PORT/")
//
//        // get browser title after browser launch
//        println("Browser title: " + driver.title)
    }

    object TestDependices {

        val roomRepository: RoomRepository = RoomRepository()
        val usersToRoom: AssignmentRepository = AssignmentRepository()

        private var embeddedServer: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

        @BeforeAll
        @JvmStatic
        fun startServer() {
            embeddedServer = ktor(PORT, roomRepository, usersToRoom)
                .start()
        }

        @AfterAll
        @JvmStatic
        fun stopServer(): Unit = runBlocking {
            embeddedServer?.stop(1000)
        }

    }

}
