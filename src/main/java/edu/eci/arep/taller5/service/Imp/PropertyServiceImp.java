package edu.eci.arep.taller5.service.Imp;

import edu.eci.arep.taller5.exception.BadRequestException;
import edu.eci.arep.taller5.exception.NotFoundException;
import edu.eci.arep.taller5.model.Property;
import edu.eci.arep.taller5.repository.PropertyRepository;
import edu.eci.arep.taller5.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PropertyServiceImp implements PropertyService {

    private PropertyRepository propertyRepository;
    @Autowired
    public PropertyServiceImp(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }
    public List<Property> getAll() {
        return propertyRepository.findAll();
    }
    public Property getById(Long id) {
        Optional<Property> p = propertyRepository.findById(id);
        if(p.isPresent()){
            return p.get();
        }
        throw new NotFoundException("Property not found with id " + id);
    }
    public Property save(Property property) {
        if(property.getId() != null){
            throw new BadRequestException("Cannot create property with id " + property.getId());
        }
        return propertyRepository.save(property);
    }
    public Property update(Long id, Property property) {
        if(property.getId() != null && !property.getId().equals(id)){
            throw new BadRequestException("The id property is not equals to param id");
        }
        if(propertyRepository.existsById(id)){
            if(property.getId() == null) property.setId(id);
            return propertyRepository.save(property);
        }
        throw new NotFoundException("Property not found with id " + id);

    }
    public void delete(Long id) {
        if(!propertyRepository.existsById(id)){
            throw new NotFoundException("Property not found with id " + id);
        }
        propertyRepository.deleteById(id);
    }
    public Page<Property> getPaginatedProperties(Pageable pageable){
        return propertyRepository.findAll(pageable);
    }
}
