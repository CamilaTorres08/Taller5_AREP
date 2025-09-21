package edu.eci.arep.taller5.repository;

import edu.eci.arep.taller5.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    @Query(
            """
            SELECT p FROM Property p
            WHERE (:location IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%',:location,'%')))
            AND (:price IS NULL OR p.price >= :price)
            AND (:sizeProperty IS NULL OR p.size >= :sizeProperty)
            """
    )
    Page<Property> findAllByFilter(@Param("location") String location, @Param("price") Double price,
                                          @Param("sizeProperty") Double sizeProperty,Pageable pageable);

}
