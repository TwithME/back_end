package com.example.twithme.repository.destination;

import com.example.twithme.model.entity.destination.Continent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContinentRepository extends JpaRepository<Continent, Long> {

    List<Continent> findAll();

    Continent findByName(String continentName);
    Continent findContinentById(Long id);

}
