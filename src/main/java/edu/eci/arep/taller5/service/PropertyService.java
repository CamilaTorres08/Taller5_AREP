package edu.eci.arep.taller5.service;

import edu.eci.arep.taller5.exception.NotFoundException;
import edu.eci.arep.taller5.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PropertyService {
    List<Property> getAll();
    Property getById(Long id);
    Property save(Property property);
    Property update(Long id, Property property);
    void delete(Long id);
    Page<Property> getPaginatedProperties(Pageable pageable);
}
