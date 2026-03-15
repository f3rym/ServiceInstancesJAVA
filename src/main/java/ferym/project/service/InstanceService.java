package ferym.project.service;

import ferym.project.dto.InstanceDto;
import ferym.project.dto.InstanceFilterDto;
import ferym.project.mapper.InstanceMapper;
import ferym.project.model.Instance;
import ferym.project.model.Datacenter;
import ferym.project.model.Software;
import ferym.project.repository.DatacenterRepository;
import ferym.project.repository.InstanceRepository;
import ferym.project.repository.SoftwareRepository;
import ferym.project.util.InstanceSearchKey;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstanceService {
    private final InstanceRepository instanceRepository;
    private final DatacenterRepository datacenterRepository;
    private final SoftwareRepository softwareRepository;
    private final InstanceMapper mapper;

    private static final String INSTANCE_NOT_FOUND = "Инстанс не найден";

    private final Map<InstanceSearchKey, Page<InstanceDto>> instanceCache;

    @Transactional(readOnly = true)
    public Page<InstanceDto> search(InstanceFilterDto filter, Pageable pageable) {

        InstanceSearchKey key = new InstanceSearchKey(
                filter.getPrice(),
                filter.getDatacenterLocation(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        if (instanceCache.containsKey(key)) {
            log.info("Using in-memory index");
            return instanceCache.get(key);
        }

        log.info("No cashed");
        Page<Instance> entityPage;

        if (filter.isUseNative()) {
            entityPage = instanceRepository.findByFilterNative(
                    filter.getPrice(), filter.getDatacenterLocation(), pageable);
        } else {
            entityPage = instanceRepository.findByFilterJPQL(
                    filter.getPrice(), filter.getDatacenterLocation(), pageable);
        }
        List<InstanceDto> instanceDto = entityPage.stream()
                .map(mapper::toDto)
                .toList();

        Page<InstanceDto> dtoPage = new PageImpl<>(instanceDto, pageable, entityPage.getTotalElements());

        instanceCache.put(key, dtoPage);

        return dtoPage;
    }

    @Transactional(readOnly = true)
    public List<InstanceDto> getAll() {
        return instanceRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public InstanceDto getById(Long id) {
        return instanceRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(INSTANCE_NOT_FOUND));
    }

    @Transactional
    public InstanceDto create(InstanceDto dto) {
        Instance entity = mapper.toEntity(dto);

        if (dto.getDatacenterIds() != null && !dto.getDatacenterIds().isEmpty()) {
            List<Datacenter> datacenters = datacenterRepository.findAllById(dto.getDatacenterIds());
            if (datacenters.size() != dto.getDatacenterIds().size()) {
                throw new EntityNotFoundException("Один или несколько дата-центров не найдены");
            }
            entity.setDatacenters(new HashSet<>(datacenters));
        }

        if (dto.getSoftwareIds() != null && !dto.getSoftwareIds().isEmpty()) {
            List<Software> softList = softwareRepository.findAllById(dto.getSoftwareIds());
            entity.setInstalledSoftware(new HashSet<>(softList));
        }
        instanceCache.clear();
        return mapper.toDto(instanceRepository.save(entity));
    }

    @Transactional
    public InstanceDto update(Long id, InstanceDto dto) {
        Instance entity = instanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(INSTANCE_NOT_FOUND));

        entity.setName(dto.getName());
        entity.setInstanceType(dto.getInstanceType());
        entity.setOs(dto.getOs());
        entity.setPrice(dto.getPrice());

        if (dto.getDatacenterIds() != null) {
            List<Datacenter> datacenters = datacenterRepository.findAllById(dto.getDatacenterIds());
            entity.setDatacenters(new HashSet<>(datacenters));
        }

        if (dto.getSoftwareIds() != null) {
            List<Software> softList = softwareRepository.findAllById(dto.getSoftwareIds());
            entity.setInstalledSoftware(new HashSet<>(softList));
        }
        instanceCache.clear();
        return mapper.toDto(instanceRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        instanceRepository.deleteById(id);
        instanceCache.clear();
    }
}