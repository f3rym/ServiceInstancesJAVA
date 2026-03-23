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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatacenterServiceTest {

    @Mock private DatacenterRepository datacenterRepository;
    @Mock private InstanceRepository instanceRepository;
    @Mock private DatacenterMapper mapper;

    @InjectMocks private DatacenterService datacenterService;

    @Test
    void getAll_ShouldReturnList() {
        when(datacenterRepository.findAll()).thenReturn(List.of(new Datacenter()));
        when(mapper.toDto(any())).thenReturn(new DatacenterDto());

        List<DatacenterDto> result = datacenterService.getAll();

        assertThat(result).hasSize(1);
        verify(datacenterRepository).findAll();
    }

    @Test
    void getById_ShouldReturnDto_WhenFound() {
        when(datacenterRepository.findById(1L)).thenReturn(Optional.of(new Datacenter()));
        when(mapper.toDto(any())).thenReturn(new DatacenterDto());

        assertThat(datacenterService.getById(1L)).isNotNull();
    }

    @Test
    void create_ShouldAttachInstances_WhenIdsProvided() {
        DatacenterDto request = new DatacenterDto();
        request.setInstanceIds(Set.of(10L, 20L));
        Datacenter entity = new Datacenter();

        when(mapper.toEntity(request)).thenReturn(entity);
        when(instanceRepository.findAllById(request.getInstanceIds()))
                .thenReturn(List.of(new Instance(), new Instance()));
        when(datacenterRepository.save(any())).thenReturn(entity);
        when(mapper.toDto(any())).thenReturn(new DatacenterDto());

        datacenterService.create(request);

        verify(instanceRepository).findAllById(request.getInstanceIds());
        verify(datacenterRepository).save(entity);
    }

    @Test
    void update_ShouldThrowException_WhenNotFound() {
        Long id = 1L;
        DatacenterDto dto = new DatacenterDto();

        when(datacenterRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datacenterService.update(id, dto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(datacenterRepository, never()).save(any());
        verifyNoInteractions(mapper);
    }
    @Test
    void delete_ShouldCallRepository() {
        datacenterService.delete(1L);
        verify(datacenterRepository).deleteById(1L);
    }
}