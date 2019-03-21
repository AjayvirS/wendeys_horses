package at.ac.tuwien.sepm.assignment.individual.unit.REST;

import at.ac.tuwien.sepm.assignment.individual.integration.dto.HorseTestDto;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import at.ac.tuwien.sepm.assignment.individual.rest.HorseEndpoint;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.HorseDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public class HorseEndpointTest {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String BASE_URL = "http://localhost:";
    private static final String HORSE_URL = "/api/v1/horses";


    private static final HorseTestDto HORSE_1 = new HorseTestDto(" ", "Breed1", 45.0, 55.0);
    private static final HorseTestDto HORSE_2 = new HorseTestDto("Horse2", "Breed2",40.0, 60.0);
    private static final HorseDto HORSE_DTO= new HorseDto(null,null,null,50.0,null,null,null);

    @LocalServerPort
    private int port;
    @Autowired
    HorseEndpoint horseEndpoint;

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
        try {
            postHorse1();
        }catch (HttpClientErrorException e){
            assertEquals(e.getStatusCode(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void whenUpdateHorse_thengetUpdatedHorse(){
        postHorse2();
        HorseDto horseDto=horseEndpoint.updateOneById(1, HORSE_DTO);
        assertEquals(horseDto.getMinSpeed(), HORSE_DTO.getMinSpeed());
    }



    private void postHorse1() {
        REST_TEMPLATE.postForObject(BASE_URL + port + HORSE_URL, new HttpEntity<>(HORSE_1), HorseTestDto.class);
    }

    private void postHorse2() {
        REST_TEMPLATE.postForObject(BASE_URL + port + HORSE_URL, new HttpEntity<>(HORSE_2), HorseTestDto.class);
    }




}
