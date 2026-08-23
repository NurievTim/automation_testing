package api.dao.comparison;

import api.models.BaseModel;
import org.assertj.core.api.AbstractAssert;

public class DaoAndModelAssertions {

    private static final DaoComparator daoComparator = new DaoComparator();

    public static DaoModelAssert assertThat(BaseModel dto, Object dao) {
        return new DaoModelAssert(dto, dao);
    }

    public static class DaoModelAssert extends AbstractAssert<DaoModelAssert, Object> {
        private final BaseModel dto;
        private final Object dao;

        public DaoModelAssert(BaseModel dto, Object dao) {
            super(dto, DaoModelAssert.class);
            this.dto = dto;
            this.dao = dao;
        }

        public DaoModelAssert match() {
            if (dto == null) {
                failWithMessage("DTO should not be null");
            }

            if (dao == null) {
                failWithMessage("DAO model should not be null");
            }

            // Use configurable comparison
            try {
                daoComparator.compare(dto, dao);
            } catch (AssertionError e) {
                failWithMessage(e.getMessage());
            }

            return this;
        }
    }
}