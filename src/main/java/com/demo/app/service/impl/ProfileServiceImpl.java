package com.demo.app.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.app.domain.Profile;
import com.demo.app.repository.ProfileRepository;
import com.demo.app.service.ProfileService;
import com.demo.app.service.dto.ProfileDTO;
import com.demo.app.service.mapper.ProfileMapper;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * Service Implementation for managing {@link Profile}.
 */
@Service
@Transactional
@CacheConfig(cacheNames = "cache")
public class ProfileServiceImpl implements ProfileService {

    private final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final ProfileRepository profileRepository;

    private final ProfileMapper profileMapper;

    private HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
    
    public ProfileServiceImpl(ProfileRepository profileRepository, ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
    }

    /**
     * Save a profile.
     *
     * @param profileDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
//    @Caching(evict = {@CacheEvict(value = "cache",allEntries = true)})
//    @CachePut(cacheNames = {"cache"})
   @CachePut(cacheNames = "cache",key="#profileDTO.id")
    public ProfileDTO save(ProfileDTO profileDTO) {
        log.debug("Request to save Profile : {}", profileDTO);
        
        Profile profile = profileMapper.toEntity(profileDTO);
        
//        ConcurrentMap<String, Profile> cacheProfile = hazelcastInstance.getMap("cache");
//        cacheProfile.replace(profile.getId().toString(), profile);
        
        profile = profileRepository.save(profile);
        return profileMapper.toDto(profile);
    }

    /**
     * Get all the profiles.
     *
     * @return the list of entities.
     */
    @Override
    @Cacheable(cacheNames = "cache")
    public List<ProfileDTO> findAll() {
//        ConcurrentMap<String,Profile> profileDTO = hazelcastInstance.getMap("cache");

        log.debug("Request to get all Profiles");
        List<Profile> list = profileRepository.findAll();
//        for(Profile info : list) {
//        	profileDTO.putIfAbsent(info.getId().toString(), info);
//        }
//   return     list.stream().map(profileMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
        return profileRepository.findAll().stream()
            .map(profileMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one profile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = { "cache" },key = "#id")
    public Optional<ProfileDTO> findOne(Long id) {
        log.debug("Request to get Profile : {}", id);
        return profileRepository.findById(id)
            .map(profileMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = {"cache"},key = "#id")
    public Profile findByOne(Long id) {
    	return profileRepository.findByName("Naina");
    }

    /**
     * Delete the profile by id.
     *
     * @param id the id of the entity.
     */
    @Override
    @CacheEvict(cacheNames  = {"cache"},key = "#id",allEntries = true)
    public void delete(Long id) {
        log.debug("Request to delete Profile : {}", id);
        profileRepository.deleteById(id);
    }

	@Override
	public Profile findByOne(String name) {
		// TODO Auto-generated method stub
		return null;
	}
}
