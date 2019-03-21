package at.ac.tuwien.sepm.assignment.individual.integration.REST;

import at.ac.tuwien.sepm.assignment.individual.integration.dto.HorseTestDto;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public class HorseEndpointTest {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String BASE_URL = "http://localhost:";
    private static final String HORSE_URL = "/api/v1/horses";
    private static final HorseTestDto HORSE_1 = new HorseTestDto(" ", "Breed1", 45.0, 55.0);
    private static final HorseTestDto HORSE_2 = new HorseTestDto("Horse2", 40.0, 60.0);

    @LocalServerPort
    private int port;
    @Autowired
    private DBConnectionManager dbConnectionManager;

    /**
     * It is important to close the database connection after each test in order to clean the in-memory database
     */
    @After
    public void afterEachTest() throws PersistenceException {
        dbConnectionManager.closeConnection();
    }

    @Test
    public void whenSaveOneHorsewithInvalidName_thenGetBadRequest() {
        HttpEntity<HorseTestDto> request = new HttpEntity<>(HORSE_1);

        try {
            ResponseEntity<HorseTestDto> response = REST_TEMPLATE
                .exchange(BASE_URL + port + HORSE_URL, HttpMethod.POST, request, HorseTestDto.class);
        }catch (HttpClientErrorException e){
            assertEquals(e.getStatusCode(), HttpStatus.BAD_REQUEST);
        }
    }


}
