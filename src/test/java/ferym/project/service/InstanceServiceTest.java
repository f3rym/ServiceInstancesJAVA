package ferym.project.service;

import ferym.project.dto.InstanceDto;
import ferym.project.dto.InstanceFilterDto;
import ferym.project.mapper.InstanceMapper;
import ferym.project.model.Datacenter;
import ferym.project.model.Instance;
import ferym.project.model.Software;
import ferym.project.repository.DatacenterRepository;
import ferym.project.repository.InstanceRepository;
import ferym.project.repository.SoftwareRepository;
import ferym.project.util.InstanceSearchKey;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstanceServiceTest {

    @Mock private InstanceRepository instanceRepository;
    @Mock private DatacenterRepository datacenterRepository;
    @Mock private SoftwareRepository softwareRepository;
    @Mock private InstanceMapper mapper;

    private Map<InstanceSearchKey, Page<InstanceDto>> instanceCache;
    private InstanceService instanceService;

    @BeforeEach
    void setUp() {
        instanceCache = new HashMap<>();
        instanceService = new InstanceService(
                instanceRepository,
                datacenterRepository,
                softwareRepository,
                mapper,
                instanceCache
        );
    }


    @Test
    void search_ShouldReturnFromCache_WhenKeyExists() {
        InstanceFilterDto filter = new InstanceFilterDto();
        Pageable pageable = PageRequest.of(0, 10);
        InstanceSearchKey key = new InstanceSearchKey(null, null, 0, 10);

        Page<InstanceDto> cachedPage = new PageImpl<>(List.of(new InstanceDto()));
        instanceCache.put(key, cachedPage);

        Page<InstanceDto> result = instanceService.search(filter, pageable);

        assertThat(result).isSameAs(cachedPage);
        verifyNoInteractions(instanceRepository);
    }

    @Test
    void search_ShouldCallNativeAndCache_WhenUseNativeTrue() {
        InstanceFilterDto filter = new InstanceFilterDto();
        filter.setUseNative(true);
        Pageable pageable = PageRequest.of(0, 10);

        Instance entity = new Instance();
        InstanceDto dto = new InstanceDto();
        Page<Instance> entityPage = new PageImpl<>(List.of(entity));

        when(instanceRepository.findByFilterNative(any(), any(), any())).thenReturn(entityPage);
        when(mapper.toDto(entity)).thenReturn(dto);

        Page<InstanceDto> result = instanceService.search(filter, pageable);

        assertThat(result.getContent()).containsExactly(dto);
        assertThat(instanceCache).isNotEmpty();
        verify(instanceRepository).findByFilterNative(any(), any(), eq(pageable));
    }

    @Test
    void search_ShouldCallJPQLAndCache_WhenUseNativeFalse() {
        InstanceFilterDto filter = new InstanceFilterDto();
        filter.setUseNative(false);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Instance> entityPage = new PageImpl<>(Collections.emptyList());
        when(instanceRepository.findByFilterJPQL(any(), any(), any())).thenReturn(entityPage);

        instanceService.search(filter, pageable);

        verify(instanceRepository).findByFilterJPQL(any(), any(), eq(pageable));
    }


    @Test
    void getAll_ShouldReturnList() {
        Instance entity = new Instance();
        when(instanceRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(new InstanceDto());

        List<InstanceDto> result = instanceService.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_ShouldReturnDto_WhenFound() {
        Instance entity = new Instance();
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(new InstanceDto());

        assertThat(instanceService.getById(1L)).isNotNull();
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> instanceService.getById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Инстанс не найден");
    }


    @Test
    void create_ShouldSaveWithoutRelations_WhenIdsNullOrEmpty() {
        InstanceDto dto = new InstanceDto();
        dto.setDatacenterIds(null);
        dto.setSoftwareIds(Collections.emptyList());

        Instance entity = new Instance();
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(instanceRepository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(new InstanceDto());

        instanceCache.put(new InstanceSearchKey(null, null, 0, 0), Page.empty());

        instanceService.create(dto);

        assertThat(instanceCache).isEmpty();
        verify(datacenterRepository, never()).findAllById(any());
    }

    @Test
    void create_ShouldSaveWithFullRelations_WhenIdsProvided() {
        InstanceDto dto = new InstanceDto();
        dto.setDatacenterIds(Set.of(1L));
        dto.setSoftwareIds(List.of(2L));

        Instance entity = new Instance();
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(datacenterRepository.findAllById(any())).thenReturn(List.of(new Datacenter()));
        when(softwareRepository.findAllById(any())).thenReturn(List.of(new Software()));
        when(instanceRepository.save(entity)).thenReturn(entity);

        instanceService.create(dto);

        verify(instanceRepository).save(entity);
        assertThat(entity.getDatacenters()).hasSize(1);
        assertThat(entity.getInstalledSoftware()).hasSize(1);
    }

    @Test
    void create_ShouldThrow_WhenDatacentersNotFound() {
        InstanceDto dto = new InstanceDto();
        dto.setDatacenterIds(Set.of(1L, 2L));

        when(mapper.toEntity(dto)).thenReturn(new Instance());
        when(datacenterRepository.findAllById(any())).thenReturn(List.of(new Datacenter()));

        assertThatThrownBy(() -> instanceService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("дата-центров не найдены");
    }


    @Test
    void update_ShouldThrow_WhenInstanceNotFound() {

        InstanceDto dto = new InstanceDto();
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instanceService.update(1L, dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_ShouldModifyAllFieldsAndClearCache() {
        Instance entity = new Instance();
        InstanceDto dto = new InstanceDto();
        dto.setName("New");
        dto.setDatacenterIds(Set.of(1L));
        dto.setSoftwareIds(List.of(2L));

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(datacenterRepository.findAllById(any())).thenReturn(List.of(new Datacenter()));
        when(softwareRepository.findAllById(any())).thenReturn(List.of(new Software()));
        when(instanceRepository.save(entity)).thenReturn(entity);

        instanceCache.put(new InstanceSearchKey(null, null, 0, 0), Page.empty());

        instanceService.update(1L, dto);

        assertThat(entity.getName()).isEqualTo("New");
        assertThat(instanceCache).isEmpty();
        verify(instanceRepository).save(entity);
    }

    @Test
    void update_ShouldNotUpdateRelations_WhenIdsAreNull() {
        Instance entity = new Instance();
        InstanceDto dto = new InstanceDto();
        dto.setDatacenterIds(null);
        dto.setSoftwareIds(null);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(instanceRepository.save(entity)).thenReturn(entity);

        instanceService.update(1L, dto);

        verify(datacenterRepository, never()).findAllById(any());
        verify(softwareRepository, never()).findAllById(any());
    }


    @Test
    void delete_ShouldCallRepoAndClearCache() {
        instanceCache.put(new InstanceSearchKey(null, null, 0, 0), Page.empty());

        instanceService.delete(1L);

        verify(instanceRepository).deleteById(1L);
        assertThat(instanceCache).isEmpty();
    }
    @Test
    void create_ShouldSkipDatacenters_WhenIdsEmpty() {
        InstanceDto dto = new InstanceDto();
        dto.setDatacenterIds(Collections.emptySet());

        Instance entity = new Instance();
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(instanceRepository.save(entity)).thenReturn(entity);
        when(mapper.toDto(any())).thenReturn(new InstanceDto());

        instanceService.create(dto);

        verify(datacenterRepository, never()).findAllById(any());
    }

    @Test
    void create_ShouldSkipSoftware_WhenIdsEmpty() {
        InstanceDto dto = new InstanceDto();
        dto.setSoftwareIds(Collections.emptyList());

        Instance entity = new Instance();
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(instanceRepository.save(entity)).thenReturn(entity);
        when(mapper.toDto(any())).thenReturn(new InstanceDto());

        instanceService.create(dto);

        verify(softwareRepository, never()).findAllById(any());
    }
}