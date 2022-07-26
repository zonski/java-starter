package au.com.roaringventures.util.dto;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DtoBuilder {

    private static final Logger log = LoggerFactory.getLogger(DtoBuilder.class);

    private DtoBuilder baseDtoBuilder;
    private List<MappedProperty> mappedProperties;

    public DtoBuilder() {
        this.mappedProperties = new ArrayList<>();
    }

    public DtoBuilder(DtoBuilder base) {
        this();
        this.baseDtoBuilder = base;
    }

    public DtoBuilder add(String property) {
        this.mappedProperties.add(new MappedProperty(property));
        return this;
    }

    public DtoBuilder add(String property, String subClassName) {
        this.mappedProperties.add(new MappedProperty(property, subClassName));
        return this;
    }

    public DtoBuilder add(String property, DtoBuilder builder) {
        this.mappedProperties.add(new MappedProperty(property, builder));
        return this;
    }

    public List<Map<String, Object>> buildList(Collection<?> sourceList) {
        if (sourceList != null) {
            List<Map<String, Object>> results = new ArrayList<>();
            try {
                for (Object source : sourceList) {
                    results.add(build(source));
                }
            } catch (Exception e) {
                throw new DtoBuilderException("Unable to initialize hibernate proxy for list " + sourceList.getClass(), e);
            }
            return results;
        } else {
            return null;
        }
    }

    public List<Map<String, Object>> buildArray(Object sourceArray) {
        if (sourceArray != null && sourceArray.getClass().isArray()) {
            List<Map<String, Object>> results = new ArrayList<>();
            try {
                for(int i = 0; i < Array.getLength(sourceArray); i++){
                    results.add(build(Array.get(sourceArray, i)));
                }
            } catch (Exception e) {
                log.warn("*** DTO-ERROR: unable to initialize hibernate proxy for list " + sourceArray.getClass());
            }
            return results;
        } else {
            return null;
        }
    }

    public Map<String, Object> buildPage(Page<?> page) {
        if (page != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("totalElements", page.getTotalElements());
            result.put("totalPages", page.getTotalPages());
            result.put("number", page.getNumber());
            result.put("numberOfElements", page.getNumberOfElements());
            result.put("hasNext", page.hasNext());
            result.put("hasPrevious", page.hasPrevious());
            result.put("content", buildList(page.getContent()));
            return result;
        } else {
            return null;
        }
    }

    public Map<String, Object> build(Object source) {
        Map<String, Object> map = null;

        if (source != null) {
            if (source instanceof HibernateProxy) {
                try {
                    Hibernate.initialize(source);
                    source = ((HibernateProxy) source).getHibernateLazyInitializer().getImplementation();
                } catch (Exception e) {
                    throw new DtoBuilderException("*** DTO-ERROR: unable to initialize hibernate proxy for " + source.getClass());
                }
            }

            if (this.baseDtoBuilder != null) {
                map = this.baseDtoBuilder.build(source);
            } else {
                map = new HashMap<>();
            }

            for (MappedProperty property : mappedProperties) {
                mapProperty(property, source, map);
            }
        }

        return map;
    }

    private void mapProperty(MappedProperty property, Object source, Map<String, Object> map) {
        Object value = null;
        if (source != null && (property.subClassName == null || property.subClassName == source.getClass().getName())) {
            try {
                value = PropertyUtils.getProperty(source, property.name);
            } catch (IllegalAccessException e) {
                throw new DtoBuilderException("Unable to access property '" + property.name
                        + "' on " + source.getClass().getSimpleName(), e);
            } catch (InvocationTargetException e) {
                throw new DtoBuilderException("Error while accessing property '" + property.name
                        + "' on " + source.getClass().getSimpleName(), e);
            } catch (NoSuchMethodException e) {
                throw new DtoBuilderException("Property '" + property.name
                        + "' does not exist on " + source.getClass().getSimpleName(), e);
            }
        }

        if (value != null) {
            if (property.builder != null) {
                if (value instanceof Collection) {
                    map.put(property.name, property.builder.buildList((Collection<?>) value));
                } else if (value.getClass().isArray()){
                    map.put(property.name, property.builder.buildArray(value));
                } else {
                    map.put(property.name, property.builder.build(value));
                }
            } else {
                map.put(property.name, value);
            }
        }
    }

    private static final class MappedProperty {

        String name;
        String subClassName;
        DtoBuilder builder;

        MappedProperty(String name) {
            this.name = name;
        }

        MappedProperty(String name, String subClassName) {
            this.name = name;
            this.subClassName = subClassName;
        }

        MappedProperty(String name, DtoBuilder builder) {
            this.name = name;
            this.builder = builder;
        }
    }
}
