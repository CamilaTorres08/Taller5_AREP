package edu.eci.arep.taller5.controller;

import edu.eci.arep.taller5.model.DTO.PropertyDTO;
import edu.eci.arep.taller5.model.Property;
import edu.eci.arep.taller5.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static edu.eci.arep.taller5.mapper.PropertyMapper.toProperty;

@RestController
@RequestMapping("/properties")
@CrossOrigin("*")
public class PropertyController {
    private final PropertyService propertyService;
    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }
//    @GetMapping
//    public ResponseEntity<List<Property>> getAllProperties() {
//        return ResponseEntity.ok(propertyService.getAll());
//    }
    /**
    * Get Properties with filters by location, price and size
     * make pagination sending current page and size
     */
    @GetMapping
    public ResponseEntity<Page<Property>> getPaginatedProperties(@RequestParam(required = false) String location,
                                                                 @RequestParam(required = false) Double price,
                                                                 @RequestParam(required = false) Double sizeProperty,
                                                                 Pageable pageable) {
        return ResponseEntity.ok(propertyService.getPaginatedProperties(location, price, sizeProperty, pageable));
    }

    /**
     * Get Property by id
     * @param id Property ID
     * @return Property
     */
    @GetMapping("{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getById(id));
    }

    /**
     * Save new property
     * @param property DTO Object with the values of the new property
     * @return The new property
     */
    @PostMapping
    public ResponseEntity<Property> createProperty(@Valid @RequestBody PropertyDTO property) {

        return ResponseEntity.ok(propertyService.save(toProperty(property)));
    }

    /**
     * Updates an existing property
     * @param id the property to update
     * @param property the values of the property
     * @return The updated property
     */
    @PutMapping("{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @Valid @RequestBody PropertyDTO property) {
        return ResponseEntity.ok(propertyService.update(id, toProperty(property)));
    }

    /**
     * Delete an existing property
     * @param id The property ID to delete
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
