package metro.model;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@AllArgsConstructor
@Setter
@Getter
@Builder
public class Station {
    private int number;
    private final String name;
    private final String lineName;
    private boolean connected;
    private final boolean connectionStation;
    private List<Station> connectedStationsList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return getName().equals(station.getName()) && getLineName().equals(station.getLineName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getLineName());
    }
}
