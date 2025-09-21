package edu.eci.arep.taller5;

import edu.eci.arep.taller5.exception.BadRequestException;
import edu.eci.arep.taller5.exception.NotFoundException;
import edu.eci.arep.taller5.model.DTO.PropertyDTO;
import edu.eci.arep.taller5.model.Property;
import edu.eci.arep.taller5.repository.PropertyRepository;
import edu.eci.arep.taller5.service.Imp.PropertyServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class Taller5ApplicationTests {

	@Mock
	private PropertyRepository propertyRepository;


	@InjectMocks
	private PropertyServiceImp propertyServiceImp;


	private Property testProperty;
	private PropertyDTO testPropertyDTO;

	@BeforeEach
	void setUp() {
		testProperty = new Property();
		testProperty.setId(1L);
		testProperty.setAddress("Test Location");
		testProperty.setPrice(100000.0);
		testProperty.setSize(150.0);

		testPropertyDTO = new PropertyDTO();
		testPropertyDTO.setAddress("Test Location");
		testPropertyDTO.setPrice(100000.0);
		testPropertyDTO.setSize(150.0);
	}

	// Test 1: Test to obtain a property by ID (successful case)
	@Test
	void testGetPropertyById_Success() {
		Long propertyId = 1L;
		when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
		Property result = propertyServiceImp.getById(propertyId);
		assertNotNull(result);
		assertEquals(propertyId, result.getId());
		assertEquals("Test Location", result.getAddress());
		verify(propertyRepository, times(1)).findById(propertyId);
	}

	// Test 2: Test to get a property by ID (error case - not found)
	@Test
	void testGetPropertyById_NotFound() {
		Long propertyId = 999L;
		when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());
		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> propertyServiceImp.getById(propertyId));

		assertEquals("Property not found with id " + propertyId, exception.getMessage());
		verify(propertyRepository, times(1)).findById(propertyId);
	}

	// Test 3: Test to create a new property (successful case)
	@Test
	void testSaveProperty_Success() {
		Property newProperty = new Property();
		newProperty.setAddress("New Location");
		newProperty.setPrice(200000.0);
		newProperty.setSize(200.0);
		Property savedProperty = new Property();
		savedProperty.setId(2L);
		savedProperty.setAddress("New Location");
		savedProperty.setPrice(200000.0);
		savedProperty.setSize(200.0);

		when(propertyRepository.save(newProperty)).thenReturn(savedProperty);
		Property result = propertyServiceImp.save(newProperty);

		assertNotNull(result);
		assertEquals(2L, result.getId());
		assertEquals("New Location", result.getAddress());
		verify(propertyRepository, times(1)).save(newProperty);
	}

	// Test 4: Test to create property with ID (error case)
	@Test
	void testSaveProperty_WithId_ThrowsException() {
		Property propertyWithId = new Property();
		propertyWithId.setId(1L);
		propertyWithId.setAddress("Test Location");
		BadRequestException exception = assertThrows(BadRequestException.class,
				() -> propertyServiceImp.save(propertyWithId));

		assertEquals("Cannot create property with id " + propertyWithId.getId(), exception.getMessage());
		verify(propertyRepository, never()).save(any(Property.class));
	}

	// Test 5: Test to get paginated properties with filters
	@Test
	void testGetPaginatedProperties_WithFilters() {
		String location = "Test Address";
		Double price = 30000.0;
		Double sizeProperty = 300.0;
		Pageable pageable = PageRequest.of(0, 5);

		List<Property> properties = Arrays.asList(testProperty);
		Page<Property> propertyPage = new PageImpl<>(properties, pageable, 1);
		when(propertyRepository.findAllByFilter(location, price, sizeProperty, pageable))
				.thenReturn(propertyPage);
		Page<Property> result = propertyServiceImp.getPaginatedProperties(location, price, sizeProperty, pageable);
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals(1, result.getContent().size());
		assertEquals(testProperty.getId(), result.getContent().get(0).getId());
		assertEquals(testProperty.getAddress(), result.getContent().get(0).getAddress());

		verify(propertyRepository, times(1)).findAllByFilter(location, price, sizeProperty, pageable);
	}

	// Test 6: Test to update a property (successful case)
	@Test
	void testUpdateProperty_Success() {
		Long propertyId = 1L;
		Property updateProperty = new Property();
		updateProperty.setId(propertyId);
		updateProperty.setAddress("Updated Location");
		updateProperty.setPrice(250000.0);
		updateProperty.setSize(180.0);

		Property savedProperty = new Property();
		savedProperty.setId(propertyId);
		savedProperty.setAddress("Updated Location");
		savedProperty.setPrice(250000.0);
		savedProperty.setSize(180.0);

		when(propertyRepository.existsById(propertyId)).thenReturn(true);
		when(propertyRepository.save(updateProperty)).thenReturn(savedProperty);

		Property result = propertyServiceImp.update(propertyId, updateProperty);
		assertNotNull(result);
		assertEquals(propertyId, result.getId());
		assertEquals("Updated Location", result.getAddress());
		assertEquals(250000.0, result.getPrice());
		assertEquals(180.0, result.getSize());

		verify(propertyRepository, times(1)).existsById(propertyId);
		verify(propertyRepository, times(1)).save(updateProperty);
	}

	// Test 7: Test to update property that does not exist (error case)
	@Test
	void testUpdateProperty_NotFound() {
		Long propertyId = 999L;
		Property updateProperty = new Property();
		updateProperty.setId(propertyId);
		updateProperty.setAddress("Updated Location");

		when(propertyRepository.existsById(propertyId)).thenReturn(false);

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> propertyServiceImp.update(propertyId, updateProperty));

		assertEquals("Property not found with id " + propertyId, exception.getMessage());

		verify(propertyRepository, times(1)).existsById(propertyId);
		verify(propertyRepository, never()).save(any(Property.class));
	}

	// Test 8: Test to update property with inconsistent ID (error case)
	@Test
	void testUpdateProperty_InconsistentId() {
		Long pathId = 1L;
		Long propertyId = 2L;
		Property updateProperty = new Property();
		updateProperty.setId(propertyId);
		updateProperty.setAddress("Updated Location");
		BadRequestException exception = assertThrows(BadRequestException.class,
				() -> propertyServiceImp.update(pathId, updateProperty));

		assertEquals("The id property is not equals to param id", exception.getMessage());

		verify(propertyRepository, never()).existsById(any(Long.class));
		verify(propertyRepository, never()).save(any(Property.class));
	}

	// Test 9: Test to delete a property (successful case)
	@Test
	void testDeleteProperty_Success() {
		Long propertyId = 1L;
		when(propertyRepository.existsById(propertyId)).thenReturn(true);
		doNothing().when(propertyRepository).deleteById(propertyId);

		assertDoesNotThrow(() -> propertyServiceImp.delete(propertyId));
		verify(propertyRepository, times(1)).existsById(propertyId);
		verify(propertyRepository, times(1)).deleteById(propertyId);
	}

	// Test 10: Test to delete property that does not exist (error case)
	@Test
	void testDeleteProperty_NotFound() {
		Long propertyId = 999L;
		when(propertyRepository.existsById(propertyId)).thenReturn(false);

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> propertyServiceImp.delete(propertyId));

		assertEquals("Property not found with id " + propertyId, exception.getMessage());

		verify(propertyRepository, times(1)).existsById(propertyId);
		verify(propertyRepository, never()).deleteById(any(Long.class));
	}

}
