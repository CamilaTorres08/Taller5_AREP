package edu.eci.arep.taller5.mapper;

import edu.eci.arep.taller5.model.DTO.PropertyDTO;
import edu.eci.arep.taller5.model.Property;

public class PropertyMapper {
    public static Property toProperty(PropertyDTO dto){
        if(dto.getId() != null){
            return new Property(dto.getId(),dto.getAddress(),dto.getPrice(),dto.getSize(),dto.getDescription());
        }
        return new Property(dto.getAddress(),dto.getPrice(),dto.getSize(),dto.getDescription());
    }
}
