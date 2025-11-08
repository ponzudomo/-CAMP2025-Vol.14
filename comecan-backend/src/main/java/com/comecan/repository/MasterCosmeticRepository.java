package com.comecan.repository;

import com.comecan.model.MasterCosmetic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MasterCosmeticRepository extends JpaRepository<MasterCosmetic, Long> {
    Optional<MasterCosmetic> findByJanCode(String janCode);
}