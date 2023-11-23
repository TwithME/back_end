package com.example.twithme.controller.destination;

import com.example.twithme.common.model.ApiResponse;
import com.example.twithme.model.dto.destination.DestinationRes;
import com.example.twithme.model.entity.destination.Continent;
import com.example.twithme.model.entity.destination.Nation;
import com.example.twithme.model.entity.destination.Region;
import com.example.twithme.service.destination.DestinationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags={"06.Destination"})
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/destination", produces = MediaType.APPLICATION_JSON_VALUE)
public class DestinationController {
    private final DestinationService destinationService;

    @ApiOperation(value = "대륙보여주기", notes = "대륙 6개를 보여줍니다. ")
    @GetMapping("/continent")
    public ApiResponse<List<DestinationRes.DestinationDto>> getContinentList(){
        List<Continent> continents = destinationService.getContinentList();
        List<DestinationRes.DestinationDto> continentDtoList = new ArrayList<>();

        for (Continent continent : continents){
            DestinationRes.DestinationDto continentListDto = DestinationRes.DestinationDto.builder()
                    .id(continent.getId())
                    .name(continent.getName())
                    .build();
            continentDtoList.add(continentListDto);
        }

        return new ApiResponse<>(continentDtoList);

    }

    @ApiOperation(value = "해당 나라들 보여주기", notes = "대륙을 선택하면 해당하는 나라 리스트를 보여줍니다.")
    @GetMapping("/nation")
    public ApiResponse<List<DestinationRes.DestinationDto>> getNationList(@RequestParam(name = "continentId") Long continentId) {
        List<Nation> nations = destinationService.getNationList(continentId);
        List<DestinationRes.DestinationDto> nationDtoList = new ArrayList<>();

        for (Nation nation : nations) {
            DestinationRes.DestinationDto nationDto = DestinationRes.DestinationDto.builder()
                    .id(nation.getId())
                    .name(nation.getName())
                    .build();
            nationDtoList.add(nationDto);
        }

        return new ApiResponse<>(nationDtoList);
    }

    @ApiOperation(value = "해당 지역들 보여주기", notes = "나라를 선택하면 해당하는 지역 리스트를 보여줍니다.")
    @GetMapping("/region")
    public ApiResponse<List<DestinationRes.DestinationDto>> getRegionList(@RequestParam(name = "nationId") Long nationId) {
        List<Region> regions = destinationService.getRegionList(nationId);
        List<DestinationRes.DestinationDto> regionDtoList = new ArrayList<>();

        for (Region region : regions) {
            DestinationRes.DestinationDto regionDto = DestinationRes.DestinationDto.builder()
                    .id(region.getId())
                    .name(region.getName())
                    .build();
            regionDtoList.add(regionDto);
        }

        return new ApiResponse<>(regionDtoList);
    }
}