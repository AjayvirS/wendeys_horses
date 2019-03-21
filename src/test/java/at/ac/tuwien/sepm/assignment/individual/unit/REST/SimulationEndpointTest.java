package at.ac.tuwien.sepm.assignment.individual.unit.REST;

import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipant;
import at.ac.tuwien.sepm.assignment.individual.integration.dto.HorseTestDto;
import at.ac.tuwien.sepm.assignment.individual.integration.dto.JockeyTestDto;
import at.ac.tuwien.sepm.assignment.individual.rest.SimulationEndpoint;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationInputDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public class SimulationEndpointTest {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String BASE_URL = "http://localhost:";
    private static final String SIMULATION_URL = "/api/v1/simulations";
    private static final String HORSE_URL = "/api/v1/horses";
    private static final String JOCKEY_URL = "/api/v1/jockeys";
    private static final JockeyTestDto JOCKEY_1 = new JockeyTestDto("Jockey1", 30.5);
    private static final JockeyTestDto JOCKEY_2 = new JockeyTestDto("Jockey2", 0.0);
    private static final JockeyTestDto JOCKEY_3 = new JockeyTestDto("Jockey3", 503.1234);
    private static final HorseTestDto HORSE_1 = new HorseTestDto("Horse1", 45.2, 60.0);
    private static final HorseTestDto HORSE_2 = new HorseTestDto("Horse2", 49.6, 50.0);
    private static final HorseTestDto HORSE_3 = new HorseTestDto("Horse3", 55.55, 59.99);
    private static final SimulationParticipant SIMULATION_PARTICIPANT_1 = new SimulationParticipant(1, 1, 1.0f);
    private static final SimulationParticipant SIMULATION_PARTICIPANT_2 = new SimulationParticipant(2, 2, 0.95f);
    private static final SimulationParticipant SIMULATION_PARTICIPANT_3 = new SimulationParticipant(3, 1, 1.05f);
    private static final ArrayList<SimulationParticipant> SIMULATION_PARTICIPANTS= new ArrayList<>();
    private static final SimulationInputDto SIMULATION= new SimulationInputDto("Simulation 1", null);

    @Autowired
    SimulationEndpoint simulationEndpoint;

    @LocalServerPort
    private int port;

    @Before
    public void postAll(){
        postHorse(HORSE_1);
        postHorse(HORSE_2);
        postHorse(HORSE_3);

        postJockey(JOCKEY_1);
        postJockey(JOCKEY_2);
        postJockey(JOCKEY_3);
    }


    @Test(expected = ResponseStatusException.class)
    public void whenInsertOneWithDuplicateJockeys_getBadRequest(){
        SIMULATION_PARTICIPANTS.add(SIMULATION_PARTICIPANT_1);
        SIMULATION_PARTICIPANTS.add(SIMULATION_PARTICIPANT_2);
        SIMULATION_PARTICIPANTS.add(SIMULATION_PARTICIPANT_3);
        SIMULATION.setSimulationParticipants(SIMULATION_PARTICIPANTS);
        try {
            simulationEndpoint.insertOne(SIMULATION);
        } catch (ResponseStatusException e){
            assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST);
            throw e;
        }
    }

    private void postHorse(HorseTestDto horse) {
        REST_TEMPLATE.postForObject(BASE_URL + port + HORSE_URL, new HttpEntity<>(horse), HorseTestDto.class);
    }

    private void postJockey(JockeyTestDto jockey) {
        REST_TEMPLATE.postForObject(BASE_URL + port + JOCKEY_URL, new HttpEntity<>(jockey), JockeyTestDto.class);
    }


}
