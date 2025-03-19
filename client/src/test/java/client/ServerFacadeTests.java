package client;

import org.junit.jupiter.api.*;
import server.Server;
import serverFacade.ServerFacade;
import static dataaccess.DatabaseManager.freshStart;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() { freshStart();}

    @Test
    void testRegisterPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void testRegisterNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        try {
            facade.register("player1", "password", "p1@email.com");
        } catch (Exception ex) {
            assertNotNull(ex);
        }
    }

}
