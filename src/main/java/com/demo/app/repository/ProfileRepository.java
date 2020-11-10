package com.demo.app.repository;

import com.demo.app.domain.Profile;
import com.demo.app.service.dto.ProfileDTO;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Profile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

	Profile findByName(String name);
}
