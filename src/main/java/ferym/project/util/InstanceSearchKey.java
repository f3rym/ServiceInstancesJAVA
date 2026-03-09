package ferym.project.util;

import java.util.Objects;

public class InstanceSearchKey {
    private final Double price;
    private final String datacenterLocation;
    private final int pageNumber;
    private final int pageSize;

    public InstanceSearchKey(Double price, String datacenterLocation, int pageNumber, int pageSize) {
        this.price = price;
        this.datacenterLocation = datacenterLocation;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InstanceSearchKey that = (InstanceSearchKey) o;
        return pageNumber == that.pageNumber
                && pageSize == that.pageSize
                && Objects.equals(price, that.price)
                && Objects.equals(datacenterLocation, that.datacenterLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, datacenterLocation, pageNumber, pageSize);
    }
}
