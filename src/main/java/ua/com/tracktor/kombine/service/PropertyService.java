package ua.com.tracktor.kombine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ua.com.tracktor.kombine.data.PropertyRepository;
import ua.com.tracktor.kombine.entity.Property;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class PropertyService {
    @Autowired
    private Environment env;

    @Autowired
    private PropertyRepository propertyRepository;

    private Properties properties = new Properties();

    public Map<Object, Object> getAllProperties() {
        Properties result = new Properties();
        MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();

        StreamSupport.stream(propertyRepository.findAll().spliterator(), false)
                .forEach(prop -> properties.put(prop.getKey(), prop.getValue()));

        StreamSupport.stream(propSrcs.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                .flatMap(Arrays::<String>stream)
                .forEach(propName -> result.setProperty(propName, properties.getProperty(propName, env.getProperty(propName))));

        return result;
    }

    @Nullable
    public String getProperty(String key) {
        String value = null;

        if (env.containsProperty(key)) {
            if (properties.containsKey(key)) {
                value = properties.getProperty(key);
            } else {
                Optional<Property> valueFromDb = propertyRepository.findById(key);
                if (valueFromDb.isPresent()) {
                    value = valueFromDb.get().getValue();
                } else {
                    value = env.getProperty(key);
                }

                properties.setProperty(key, value);
            }
        }

        return value;
    }

    public boolean setProperty(String key, String value) {
        boolean success = true;
        if (env.containsProperty(key)) {
            propertyRepository.save(new Property(key, value));
            properties.setProperty(key, value);
        } else {
            success = false;
        }

        return success;
    }

    public boolean resetProperty(String key) {
        boolean success = true;
        if (env.containsProperty(key)) {
            String value = env.getProperty(key);

            propertyRepository.save(new Property(key, value));
            properties.setProperty(key, value);
        } else {
            success = false;
        }

        return success;
    }
}
