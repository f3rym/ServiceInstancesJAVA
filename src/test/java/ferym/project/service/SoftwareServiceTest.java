package ferym.project.service;

import ferym.project.dto.SoftwareDto;
import ferym.project.mapper.SoftwareMapper;
import ferym.project.model.Software;
import ferym.project.repository.SoftwareRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftwareServiceTest {

    @Mock private SoftwareRepository softwareRepository;
    @Mock private SoftwareMapper mapper;

    @InjectMocks private SoftwareService softwareService;

    @Test
    void getAll_ShouldReturnList() {
        when(softwareRepository.findAll()).thenReturn(List.of(new Software()));
        when(mapper.toDto(any())).thenReturn(new SoftwareDto());
        assertThat(softwareService.getAll()).isNotEmpty();
    }

    @Test
    void getById_ShouldReturnDto() {
        when(softwareRepository.findById(1L)).thenReturn(Optional.of(new Software()));
        when(mapper.toDto(any())).thenReturn(new SoftwareDto());
        assertThat(softwareService.getById(1L)).isNotNull();
    }

    @Test
    void create_ShouldSaveEntity() {
        when(mapper.toEntity(any())).thenReturn(new Software());
        when(softwareRepository.save(any())).thenReturn(new Software());
        when(mapper.toDto(any())).thenReturn(new SoftwareDto());

        softwareService.create(new SoftwareDto());
        verify(softwareRepository).save(any());
    }

    @Test
    void update_ShouldChangeFields() {
        Software entity = new Software();
        SoftwareDto dto = new SoftwareDto();
        dto.setName("Nginx");
        dto.setVersion("1.20");

        when(softwareRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        softwareService.update(1L, dto);

        assertThat(entity.getName()).isEqualTo("Nginx");
        assertThat(entity.getVersion()).isEqualTo("1.20");
    }

    @Test
    void delete_ShouldCallRepo() {
        softwareService.delete(1L);
        verify(softwareRepository).deleteById(1L);
    }
}