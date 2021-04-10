package ua.com.tracktor.kombine.data;

import org.springframework.data.repository.CrudRepository;
import ua.com.tracktor.kombine.entity.Property;

public interface PropertyRepository extends CrudRepository<Property, String> {
}
