package com.andd.DoDangAn.DoDangAn.repository;

import com.andd.DoDangAn.DoDangAn.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
}