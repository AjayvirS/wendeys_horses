package at.ac.tuwien.sepm.assignment.individual.unit.REST;

import at.ac.tuwien.sepm.assignment.individual.integration.dto.JockeyTestDto;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import at.ac.tuwien.sepm.assignment.individual.rest.JockeyEndpoint;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public class JockeyEndpointTest {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String BASE_URL = "http://localhost:";
    private static final String JOCKEY_URL = "/api/v1/jockeys";
    private static final JockeyTestDto JOCKEY_1 = new JockeyTestDto("Jockey1", 50.0);


    @LocalServerPort
    private int port;
    @Autowired
    JockeyEndpoint jockeyEndpoint;

    @Autowired
    private DBConnectionManager dbConnectionManager;

    /**
     * It is important to close the database connection after each test in order to clean the in-memory database
     */
    @After
    public void afterEachTest() throws PersistenceException {
        dbConnectionManager.closeConnection();
    }


    @Test(expected= ResponseStatusException.class)
    public void whenDeleteAndFindJockey_getResponseStatusException(){
        postJockey1();
        jockeyEndpoint.deleteOneById(1);
        jockeyEndpoint.getOneById(1);
    }




    private void postJockey1() {
        REST_TEMPLATE.postForObject(BASE_URL + port + JOCKEY_URL, new HttpEntity<>(JOCKEY_1), JockeyTestDto.class);
    }

}
