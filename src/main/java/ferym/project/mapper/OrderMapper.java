package ferym.project.mapper;

import ferym.project.dto.OrderDto;
import ferym.project.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderDto toDto(Order entity) {
        if (entity == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        if (entity.getInstance() != null) {
            dto.setInstanceId(entity.getInstance().getId());
        }
        return dto;
    }

}