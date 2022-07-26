package au.com.roaringventures.util.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PagedRequest {

    private Integer size;
    private Integer page;
    private List<SortField> sort;

    public PagedRequest() {
        this.size = 25;
        this.page = 0;
        this.sort = Collections.singletonList(new SortField("id", Direction.ASC));
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<SortField> getSort() {
        return sort;
    }

    public void setSort(List<SortField> sort) {
        this.sort = sort;
    }

    public PageRequest getPageRequest() {
        if (sort == null) {
            return PageRequest.of(
                    page != null ? page : 0,
                    size != null ? size : 25);
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            for (SortField field : sort) {
                orders.add(new Sort.Order(
                        field.getDirection(),
                        field.getProperty()
                        //,field.isNullsFirst() ? Sort.NullHandling.NULLS_FIRST : Sort.NullHandling.NULLS_LAST
                ));
            }
            Sort sort = Sort.by(orders);

            return PageRequest.of(
                    page != null ? page : 0,
                    size != null ? size : 25,
                    sort
            );
        }
    }

    //-------------------------------------------------------------------------

    public static final class SortField {

        private Direction direction;
        private String property;
        private boolean nullsFirst;

        public SortField() {
        }

        public SortField(String property,
                         Direction direction) {
            this.direction = direction;
            this.property = property;
        }

        public Direction getDirection() {
            return direction;
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public boolean isNullsFirst() {
            return nullsFirst;
        }

        public void setNullsFirst(boolean nullsFirst) {
            this.nullsFirst = nullsFirst;
        }
    }
}
