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

import java.util.List;

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
    //PAGINATION IMPLEMENTATION
    @GetMapping
    public ResponseEntity<Page<Property>> getPaginatedProperties(Pageable pageable) {
        return ResponseEntity.ok(propertyService.getPaginatedProperties(pageable));
    }
    @GetMapping("{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getById(id));
    }
    @PostMapping
    public ResponseEntity<Property> createProperty(@Valid @RequestBody PropertyDTO property) {

        return ResponseEntity.ok(propertyService.save(toProperty(property)));
    }
    @PutMapping("{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @Valid @RequestBody PropertyDTO property) {
        return ResponseEntity.ok(propertyService.update(id, toProperty(property)));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
