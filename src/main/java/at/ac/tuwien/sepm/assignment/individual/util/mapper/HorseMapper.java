package at.ac.tuwien.sepm.assignment.individual.util.mapper;

import at.ac.tuwien.sepm.assignment.individual.rest.dto.HorseDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class HorseMapper {

    public HorseDto entityToDto(Horse horse) {
        return new HorseDto(horse.getId(), horse.getName(), horse.getBreed(), horse.getMinSpeed(), horse.getMaxSpeed(), horse.getCreated(), horse.getUpdated());
    }

    public Horse dtoToEntity(HorseDto horseDto){
        return new Horse(horseDto.getId(), horseDto.getName(), horseDto.getBreed(), horseDto.getMinSpeed(), horseDto.getMaxSpeed(), horseDto.getCreated(), horseDto.getUpdated());
    }
    public HorseDto[] entitiesToDto(ArrayList<Horse> horses){
        HorseDto[]horseDtos= new HorseDto[horses.size()];

        for (int i = 0; i < horses.size(); i++) {
            horseDtos[i]= new HorseDto(horses.get(i).getId(),horses.get(i).getName(),horses.get(i).getBreed(), horses.get(i).getMinSpeed(), horses.get(i).getMaxSpeed(),horses.get(i).getCreated(), horses.get(i).getUpdated());
        }
        return horseDtos;
    }

}
