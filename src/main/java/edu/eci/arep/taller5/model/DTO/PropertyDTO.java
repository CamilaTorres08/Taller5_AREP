package edu.eci.arep.taller5.model.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PropertyDTO {
    Long id;
    @NotBlank(message="Address is required")
    String address;
    @NotNull(message="Price is required")
    @Min(value=0, message="Price must be greater than 0")
    Double price;
    @NotNull(message="Size is required")
    @Min(value=1, message="Size must be greater than 0")
    Double size;
    String description;

    public String getAddress() {
        return address;
    }

    public Double getPrice() {
        return price;
    }

    public Double getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }
    public Long getId() {
        return id;
    }

}
