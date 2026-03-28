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

import java.util.Collections;
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
    void getAll_ShouldReturnMappedList() {
        Datacenter entity = new Datacenter();
        DatacenterDto dto = new DatacenterDto();

        when(datacenterRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        List<DatacenterDto> result = datacenterService.getAll();

        assertThat(result).hasSize(1).containsExactly(dto);
        verify(datacenterRepository).findAll();
    }

    @Test
    void getById_ShouldReturnDto_WhenFound() {
        Datacenter entity = new Datacenter();
        DatacenterDto dto = new DatacenterDto();

        when(datacenterRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        DatacenterDto result = datacenterService.getById(1L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(datacenterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datacenterService.getById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Дата-центр не найден");
    }


    @Test
    void create_ShouldSaveWithoutInstances_WhenIdsNullOrEmpty() {
        DatacenterDto request = new DatacenterDto();
        request.setInstanceIds(null);

        Datacenter entity = new Datacenter();
        when(mapper.toEntity(request)).thenReturn(entity);
        when(datacenterRepository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(new DatacenterDto());

        datacenterService.create(request);

        verify(instanceRepository, never()).findAllById(any());
        verify(datacenterRepository).save(entity);
    }

    @Test
    void create_ShouldSkipInstances_WhenIdsEmpty() {
        DatacenterDto request = new DatacenterDto();
        request.setInstanceIds(Collections.emptySet());

        Datacenter entity = new Datacenter();
        when(mapper.toEntity(request)).thenReturn(entity);
        when(datacenterRepository.save(entity)).thenReturn(entity);
        when(mapper.toDto(any())).thenReturn(new DatacenterDto());

        datacenterService.create(request);

        verify(instanceRepository, never()).findAllById(any());
    }
    @Test
    void create_ShouldAttachInstances_WhenIdsValid() {
        DatacenterDto request = new DatacenterDto();
        request.setInstanceIds(Set.of(10L, 20L));
        Datacenter entity = new Datacenter();

        when(mapper.toEntity(request)).thenReturn(entity);
        when(instanceRepository.findAllById(request.getInstanceIds()))
                .thenReturn(List.of(new Instance(), new Instance()));
        when(datacenterRepository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(new DatacenterDto());

        datacenterService.create(request);

        assertThat(entity.getInstances()).hasSize(2);
        verify(datacenterRepository).save(entity);
    }

    @Test
    void create_ShouldThrow_WhenInstancesIncomplete() {
        DatacenterDto request = new DatacenterDto();
        request.setInstanceIds(Set.of(1L, 2L));

        when(mapper.toEntity(any())).thenReturn(new Datacenter());
        when(instanceRepository.findAllById(any())).thenReturn(List.of(new Instance()));

        assertThatThrownBy(() -> datacenterService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Некоторые инстансы не найдены");
    }


    @Test
    void update_ShouldModifyFieldsAndInstances() {
        Long id = 1L;
        Datacenter existingEntity = new Datacenter();
        existingEntity.setName("Old Name");

        DatacenterDto updateDto = new DatacenterDto();
        updateDto.setName("New Name");
        updateDto.setLocation("New Location");
        updateDto.setInstanceIds(Set.of(100L));

        Instance newInstance = new Instance();

        when(datacenterRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(instanceRepository.findAllById(updateDto.getInstanceIds())).thenReturn(List.of(newInstance));
        when(mapper.toDto(any(Datacenter.class))).thenReturn(updateDto);

        DatacenterDto result = datacenterService.update(id, updateDto);

        assertThat(existingEntity.getName()).isEqualTo("New Name");
        assertThat(existingEntity.getLocation()).isEqualTo("New Location");
        assertThat(existingEntity.getInstances()).containsExactly(newInstance);
        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    void update_ShouldNotUpdateInstances_WhenIdsNull() {
        Long id = 1L;
        Datacenter entity = new Datacenter();
        DatacenterDto dto = new DatacenterDto();
        dto.setInstanceIds(null);

        when(datacenterRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDto(any(Datacenter.class))).thenReturn(new DatacenterDto());

        datacenterService.update(id, dto);

        verify(instanceRepository, never()).findAllById(any());
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        when(datacenterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datacenterService.update(1L, new DatacenterDto()))
                .isInstanceOf(EntityNotFoundException.class);
    }


    @Test
    void delete_ShouldCallRepository() {
        datacenterService.delete(1L);
        verify(datacenterRepository).deleteById(1L);
    }
}