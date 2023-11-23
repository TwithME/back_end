package com.example.twithme.service.destination;

import com.example.twithme.model.entity.destination.Continent;
import com.example.twithme.model.entity.destination.Nation;
import com.example.twithme.model.entity.destination.Region;
import com.example.twithme.repository.destination.ContinentRepository;
import com.example.twithme.repository.destination.NationRepository;
import com.example.twithme.repository.destination.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DestinationService {
    private final ContinentRepository continentRepository;
    private final NationRepository nationRepository;
    private final RegionRepository regionRepository;

    public List<Continent> getContinentList(){
        return continentRepository.findAll();
    }

    public List<Nation> getNationList(Long continentId){
        return nationRepository.findByContinentId(continentId);
    }

    public List<Region> getRegionList(Long nationId){
        return regionRepository.findByNationId(nationId);
    }
}
