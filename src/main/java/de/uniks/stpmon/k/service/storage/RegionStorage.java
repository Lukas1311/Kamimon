package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RegionStorage {

    private final Subject<RegionEvent> regionEvents = PublishSubject.create();
    private Region region;
    private Area area;

    @Inject
    public RegionStorage() {
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
        if (region == null) {
            area = null;
            regionEvents.onNext(RegionEvent.EMPTY);
        }
    }

    public void setArea(Area area) {
        regionEvents.onNext(new RegionEvent(region, area, this.area));
        this.area = area;
    }

    public Observable<RegionEvent> onEvents() {
        return regionEvents;
    }

    public Area getArea() {
        return area;
    }

    public boolean isEmpty() {
        return region == null || area == null;
    }

    public record RegionEvent(Region region, Area area, Area oldArea) {

        public static final RegionEvent EMPTY = new RegionEvent(null, null, null);

        public boolean isEmpty() {
            return region == null || area == null;
        }

        public boolean changedArea() {
            return oldArea != null && !oldArea.equals(area);
        }

    }

}
