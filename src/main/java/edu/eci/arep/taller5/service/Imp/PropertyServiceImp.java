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

    /**
     * Gets all properties
     */
    public List<Property> getAll() {
        return propertyRepository.findAll();
    }

    /**
     * Gets property by Id
     * @param id the id to search
     * @throws NotFoundException When the property does not exist
     */
    public Property getById(Long id) {
        Optional<Property> p = propertyRepository.findById(id);
        if(p.isPresent()){
            return p.get();
        }
        throw new NotFoundException("Property not found with id " + id);
    }
    /**
     * Save new property
     * @param property the new property
     * @throws BadRequestException When there's a property with an existing id
     * @return the new property
     */
    public Property save(Property property) {
        if(property.getId() != null){
            throw new BadRequestException("Cannot create property with id " + property.getId());
        }
        return propertyRepository.save(property);
    }

    /**
     * Update an existing property
     * @param id property ID to update
     * @param property property values
     * @throws NotFoundException when the property does not exist
     * @return the updated property
     */
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

    /**
     * Delete an existing property
     * @param id the ID to delete
     */
    public void delete(Long id) {
        if(!propertyRepository.existsById(id)){
            throw new NotFoundException("Property not found with id " + id);
        }
        propertyRepository.deleteById(id);
    }

    /**
     * Gets the property by filter and pagination
     * @param location
     * @param price
     * @param sizeProperty
     * @param pageable
     * @return The Page object with the filtered properties
     */
    public Page<Property> getPaginatedProperties(String location, Double price, Double sizeProperty,Pageable pageable){
        return propertyRepository.findAllByFilter(location,price,sizeProperty,pageable);
    }
}
