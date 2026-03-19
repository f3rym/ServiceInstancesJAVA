package ferym.project.service;

import ferym.project.dto.DatacenterDto;
import ferym.project.mapper.DatacenterMapper;
import ferym.project.model.Datacenter;
import ferym.project.model.Instance;
import ferym.project.repository.DatacenterRepository;
import ferym.project.repository.InstanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatacenterServiceTest {

    @Mock
    private DatacenterRepository datacenterRepository;
    @Mock
    private InstanceRepository instanceRepository;
    @Mock
    private DatacenterMapper mapper;

    @InjectMocks
    private DatacenterService datacenterService;

    @Test
    void create_WithValidInstances_SavesAndReturnsDto() {
        DatacenterDto inputDto = new DatacenterDto();
        inputDto.setInstanceIds(Set.of(1L, 2L));

        Datacenter entity = new Datacenter();
        Instance inst1 = new Instance(); inst1.setId(1L);
        Instance inst2 = new Instance(); inst2.setId(2L);

        when(mapper.toEntity(inputDto)).thenReturn(entity);
        when(instanceRepository.findAllById(inputDto.getInstanceIds())).thenReturn(List.of(inst1, inst2));
        when(datacenterRepository.save(any(Datacenter.class))).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(new DatacenterDto());

        DatacenterDto result = datacenterService.create(inputDto);

        assertNotNull(result);
        assertEquals(2, entity.getInstances().size());
        verify(datacenterRepository).save(entity);
    }

    @Test
    void create_WithMissingInstances_ThrowsException() {
        DatacenterDto inputDto = new DatacenterDto();
        inputDto.setInstanceIds(Set.of(1L, 2L));

        Datacenter entity = new Datacenter();
        Instance inst1 = new Instance(); inst1.setId(1L);

        when(mapper.toEntity(inputDto)).thenReturn(entity);
        when(instanceRepository.findAllById(inputDto.getInstanceIds())).thenReturn(List.of(inst1));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> datacenterService.create(inputDto));

        assertEquals("Некоторые инстансы не найдены", exception.getMessage());
        verify(datacenterRepository, never()).save(any());
    }
}