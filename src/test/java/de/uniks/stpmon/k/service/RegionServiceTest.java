package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.Region;
import de.uniks.stpmon.k.rest.RegionApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {
    @Mock
    RegionApiService regionApiService;
    @InjectMocks
    RegionService regionService;

    @Test
    void getRegions() {
        Region region = new Region(
                "1",
                "Test",
                null,
                null
        );
        ArrayList<String> regions = new ArrayList<>();
        regions.add(region._id());
        ArrayList<Region> regionsAsRegion = new ArrayList<>();
        regionsAsRegion.add(region);


        when(regionApiService.getRegions())
                .thenReturn(Observable.just(regionsAsRegion));

        //action
        List<Region> regionList = regionService.getRegions().blockingFirst();

        //check values
        assertEquals(1, regionList.size());
        assertEquals("1", regionList.get(0)._id());
        //check mock
        verify(regionApiService).getRegions();
    }

    @Test
    void getRegion() {
        Region region = new Region(
                "1",
                "Test",
                null,
                null
        );
        ArrayList<String> regions = new ArrayList<>();
        regions.add(region._id());


        when(regionApiService.getRegion("1"))
                .thenReturn(Observable.just(region));

        //action
        Region regionList = regionService.getRegion("1").blockingFirst();

        //check values
        assertEquals("Test", regionList.name());
        assertEquals("1", regionList._id());
        //check mock
        verify(regionApiService).getRegion("1");
    }

}
