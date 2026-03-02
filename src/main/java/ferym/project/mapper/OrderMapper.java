package ferym.project.mapper;

import ferym.project.dto.OrderDto;
import ferym.project.model.CloudOrder;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderDto toDto(CloudOrder entity) {
        if (entity == null) {
            return null;
        }
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setUser(entity.getUser());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setInstance(entity.getInstance());
        return dto;
    }
    public CloudOrder toEntity(OrderDto dto) {
        if (dto == null) {
            return null;
        }
        CloudOrder entity = new CloudOrder();
        entity.setUser(dto.getUser());
        entity.setInstance(dto.getInstance());
        entity.setCreatedAt(dto.getCreatedAt());
        return entity;
    }
}
