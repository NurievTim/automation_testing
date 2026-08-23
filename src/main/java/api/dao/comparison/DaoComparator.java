package api.dao.comparison;

import api.models.BaseModel;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class DaoComparator {

    private final DaoComparisonConfigLoader configLoader;

    public DaoComparator() {
        this.configLoader = new DaoComparisonConfigLoader("dao-comparison.properties");
    }

    public void compare(BaseModel dto, Object dao) {
        DaoComparisonConfigLoader.DaoComparisonRule rule = configLoader.getRuleFor(dto.getClass());

        if (rule == null) {
            throw new RuntimeException("No comparison rule found for " + dto.getClass().getSimpleName());
        }

        Map<String, String> fieldMappings = rule.getFieldMappings();

        for (Map.Entry<String, String> mapping : fieldMappings.entrySet()) {
            String dtoFieldName = mapping.getKey();
            String daoFieldName = mapping.getValue();

            Object dtoValue = getFieldValue(dto, dtoFieldName);
            Object daoValue = getFieldValue(dao, daoFieldName);

            if (!valuesMatch(dtoValue, daoValue)) {
                throw new AssertionError(String.format(
                        "Field mismatch for %s: DTO=%s, DAO=%s",
                        dtoFieldName, dtoValue, daoValue));
            }
        }
    }

    /**
     * DTO и DAO хранят одно и то же значение в разных типах: double в модели против
     * BigDecimal/Long из БД. Числа сравниваем по значению, остальное - обычным equals.
     */
    private boolean valuesMatch(Object dtoValue, Object daoValue) {
        if (dtoValue instanceof Number && daoValue instanceof Number) {
            return toBigDecimal((Number) dtoValue).compareTo(toBigDecimal((Number) daoValue)) == 0;
        }
        return Objects.equals(dtoValue, daoValue);
    }

    private BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        // через toString, чтобы 0.01 не превратилось в 0.010000000000000000208...
        return new BigDecimal(number.toString());
    }

    private Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get field value: " + fieldName, e);
        }
    }
}
