package metro.model;


import io.vavr.collection.List;
import lombok.Getter;

import java.util.Comparator;

@Getter
public class MetroLine {
    private final String name;
    private List<Station> stations;
    private List<String> connectedLines;

    public MetroLine(String name, List<Station> stations) {
        this.name = name;
        this.stations = stations.sorted(Comparator.comparingInt(Station::getNumber));
        addStartingDepot();
        addTrailingDepot();
        updateConnectedLines();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < stations.size(); i++) {
            Station s = stations.get(i);
            sb.append("\n");
            sb.append(s.getName());
            if (!s.getConnectedStationsList().isEmpty()) {
                sb.append(" - ");
                sb.append(s.getConnectedStationsList().get(0).getName());
                sb.append(" (");
                sb.append(s.getConnectedStationsList().get(0).getLineName());
                sb.append(")");
            }
        }

        return sb.toString().strip();

    }

    public void print() {
        System.out.println(this);
    }

    private void addStartingDepot() {
        Station station = Station.builder()
                .name("depot")
                .number(0)
                .connectionStation(false)
                .connected(false)
                .lineName(this.name)
                .connectedStationsList(List.empty())
                .build();
        this.stations = stations.prepend(station);
        renumberStations();
    }

    private void dropStartingDepot() {
        this.stations = stations.pop();
    }

    private void renumberStations() {
        stations.forEach(n -> {
            n.setNumber(stations.indexOf(n) + 1);
        });
    }

    private void addTrailingDepot() {
        Station station = Station.builder()
                .name("depot")
                .number(stations.last().getNumber() + 1)
                .connectionStation(false)
                .connected(false)
                .lineName(this.name)
                .connectedStationsList(List.empty())
                .build();
        this.stations = stations.append(station);
    }

    private void dropTrailingDepot() {
        this.stations = stations.dropRight(1);
    }

    public void append(List<String> params) {
        int size = stations.size();
        dropTrailingDepot();
        Station station = Station.builder()
                .name(params.get(1))
                .number(size)
                .connectionStation(false)
                .connected(false)
                .lineName(this.name)
                .connectedStationsList(List.empty())
                .build();
        this.stations = stations.append(station);
        addTrailingDepot();
        updateConnectedLines();
    }

    public void addHead(List<String> params) {
        dropStartingDepot();
        Station station = Station.builder()
                .name(params.get(1))
                .number(1)
                .connectionStation(false)
                .connected(false)
                .lineName(this.name)
                .connectedStationsList(List.empty())
                .build();
        this.stations = stations.prepend(station);
        addStartingDepot();
        updateConnectedLines();
    }

    public void remove(List<String> params) {
        this.stations = stations.removeFirst(n -> n.getName().equals(params.get(1)));
        renumberStations();
        updateConnectedLines();
    }

    public void connect(List<String> params) {
        if (this.name.equalsIgnoreCase(params.get(0))) {
            connectionHelper(params.get(1), params.get(2), params.get(3));
        } else {
            connectionHelper(params.get(3), params.get(0), params.get(1));
        }
        updateConnectedLines();
    }

    private void connectionHelper(String stationName, String lineName, String newStationName) {
        Station newConnectedStation = Station.builder()
                .connectionStation(true)
                .name(newStationName)
                .lineName(lineName)
                .build();

        this.stations
                .filter(n -> n.getName().equalsIgnoreCase(stationName))
                .forEach(n -> {
                    n.setConnected(true);
                    n.setConnectedStationsList(n.getConnectedStationsList().append(newConnectedStation));
                });
    }

    private void updateConnectedLines() {
        List<String> newconnectedLines = List.empty();

        for (Station s : this.stations) {
            if (s.isConnected()) {
                for (Station conSta : s.getConnectedStationsList()) {
                    newconnectedLines = newconnectedLines.append(conSta.getLineName());
                }
            }
        }
        newconnectedLines = newconnectedLines.distinct();
        this.connectedLines = newconnectedLines;
    }

}


//Old impl:

//package metro.model;
//
//
//        import java.util.List;
//
//public class MetroLine {
//    private final List<String> stations;
//
//    public MetroLine(List<String> stations) {
//        this.stations = stations;
//        stations.add("depot");
//        stations.add(0, "depot");
//    }
//
//    public List<String> getStations() {
//        return stations;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i <= stations.size() - 3; i++) {
//            sb.append("\n");
//            sb.append(stations.get(i));
//            sb.append(" - ");
//            sb.append(stations.get(i + 1));
//            sb.append(" - ");
//            sb.append(stations.get(i + 2));
//        }
//
//        return sb.toString();
//
//    }
//}
