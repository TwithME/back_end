package com.example.twithme.repository.destination;

import com.example.twithme.model.entity.destination.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NationRepository extends JpaRepository<Nation, Long> {

    Nation findNationById(Long id);

    List<Nation> findByContinentId(Long id);
}
