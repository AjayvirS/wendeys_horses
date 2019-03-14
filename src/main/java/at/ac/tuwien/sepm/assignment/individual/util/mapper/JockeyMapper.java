package at.ac.tuwien.sepm.assignment.individual.util.mapper;
import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.JockeyDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class JockeyMapper {
    public JockeyDto entityToDto(Jockey jockey) {
        return new JockeyDto(jockey.getId(), jockey.getName(), jockey.getSkill(), jockey.getCreated(), jockey.getUpdated());
    }

    public Jockey dtoToEntity(JockeyDto jockeyDto){
        return new Jockey(jockeyDto.getId(), jockeyDto.getName(), jockeyDto.getSkill(), jockeyDto.getCreated(), jockeyDto.getUpdated());
    }
    public JockeyDto[] entitiesToDto(ArrayList<Jockey> jockeys){
        JockeyDto[]jockeyDtos= new JockeyDto[jockeys.size()];

        for (int i = 0; i < jockeys.size(); i++) {
            jockeyDtos[i]= new JockeyDto(jockeys.get(i).getId(),jockeys.get(i).getName(),jockeys.get(i).getSkill(), jockeys.get(i).getCreated(), jockeys.get(i).getUpdated());
        }
        return jockeyDtos;
    }
}
