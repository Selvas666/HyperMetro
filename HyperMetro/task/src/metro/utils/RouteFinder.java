package metro.utils;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import metro.model.MetroLine;
import metro.model.Route;
import metro.model.Station;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class RouteFinder {
    public static Route findRoute(List<MetroLine> metroLineList, String line1, String station1, String line2, String station2) {
        Station head = findStation(metroLineList, station1, line1).getOrElseThrow(() -> new RuntimeException("No such station " + station1 + " from line " + line1));
        Station tail = findStation(metroLineList, station2, line2).getOrElseThrow(() -> new RuntimeException("No such station " + station2 + " from line " + line2));

        boolean connected = false;
        if (metroLineList.find(n -> n.getName().equals(head.getLineName())).get().getConnectedLines().contains(tail.getLineName())
                || line1.equals(line2))
            connected = true;

        if (!connected) {
            if (!areLinesConnected(metroLineList, head, tail)) throw new RuntimeException("Lines are not connected!");
        }

        ArrayList<Station> visitedStationsList = new ArrayList<Station>();

        HashMap<Station, List<Station>> stationConectionsMap = generateStationConectionsMap(metroLineList);

        ArrayList<ArrayList<Station>> routes = new ArrayList<ArrayList<Station>>();

        List<Station> toBeVistted = visitNeighbours(stationConectionsMap, head, routes, visitedStationsList);
        List<Station> newToBeVisited = List.empty();


        while (!visitedStationsList.containsAll(stationConectionsMap.keySet().toJavaList())) {
            for (Station station : toBeVistted) {
                newToBeVisited = newToBeVisited.appendAll(visitNeighbours(stationConectionsMap, station, routes, visitedStationsList));
            }
            toBeVistted = newToBeVisited;
            newToBeVisited = List.empty();
            if (routes.stream().anyMatch(n -> n.contains(tail))) break;
        }


        List<ArrayList<Station>> properRoutes = List.ofAll(routes.stream().filter(n -> n.contains(tail)).collect(Collectors.toList()));
        int shortest = 0;
        for (ArrayList<Station> list : properRoutes) {
            if (shortest == 0) {
                shortest = list.size();
            }
            if (list.size() < shortest) shortest = list.size();
        }

        int finalShortest = shortest;
        ArrayList<Station> shortestRoute = properRoutes.find(n -> n.size() == finalShortest).get();
        return new Route(List.ofAll(shortestRoute));
    }

    private static List<Station> visitNeighbours(HashMap<Station, List<Station>> stationConectionsMap, Station station, ArrayList<ArrayList<Station>> routes, ArrayList<Station> visitedStationsList) {
        List<Station> toBeVisited = List.empty();
        if (!visitedStationsList.contains(station)) {
            visitedStationsList.add(station);
            if (routes.isEmpty()) {
                for (Station conStat : stationConectionsMap.get(station).get()) {
                    ArrayList<Station> newRoute = new ArrayList<Station>();
                    newRoute.add(station);
                    newRoute.add(conStat);
                    routes.add(newRoute);
                    toBeVisited = toBeVisited.append(conStat);
                }
                return toBeVisited;
            }
            ArrayList<Station> route = routes.stream().filter(n -> n.get(n.size() - 1).equals(station)).findFirst().get();
            if (stationConectionsMap.get(station).get().size() == 2) {
                Station toBeAdded = stationConectionsMap.get(station).get().find(n -> !(n.equals(route.get(route.size() - 2)))).get();
                route.add(toBeAdded);
                toBeVisited = toBeVisited.append(toBeAdded);
            } else {
                routes.remove(route);
                for (Station conStat : stationConectionsMap.get(station).get().filter(n -> !(n.equals(route.get(route.size() - 2))))) {
                    ArrayList<Station> newRoute = new ArrayList<Station>(route);
                    newRoute.add(conStat);
                    routes.add(newRoute);
                    toBeVisited = toBeVisited.append(conStat);
                }
            }
        }
        return toBeVisited;
    }

    private static HashMap<Station, List<Station>> generateStationConectionsMap(List<MetroLine> metroLineList) {
        HashMap<Station, List<Station>> stationConectionsMap = HashMap.empty();

        for (MetroLine ml : metroLineList) {
            for (Station s : ml.getStations()) {
                List<Station> conected = List.empty();
                for (Station conSta : s.getConnectedStationsList()) {
                    MetroLine metroLine = metroLineList.find(n -> n.getName().equals(conSta.getLineName())).get();
                    conected = conected.append(metroLine.getStations().find(n -> n.getName().equals(s.getName())).get());
                }

                Try<Station> prevStat = Try.of(() -> ml.getStations().get(ml.getStations().indexOf(s) - 1));
                Try<Station> nextStat = Try.of(() -> ml.getStations().get(ml.getStations().indexOf(s) + 1));

                if (prevStat.isSuccess()) {
                    if (!prevStat.get().getName().equalsIgnoreCase("Depot")) {
                        conected = conected.append(prevStat.get());
                    }
                }

                if (nextStat.isSuccess()) {
                    if (!nextStat.get().getName().equalsIgnoreCase("Depot")) {
                        conected = conected.append(nextStat.get());
                    }
                }

                stationConectionsMap = stationConectionsMap.put(s, conected);
            }
        }
        return stationConectionsMap;
    }

    private static Option<Station> findStation(List<MetroLine> metroLineList, String station, String line) {
        for (MetroLine ml : metroLineList) {
            if (ml.getName().equals(line)) {
                for (Station s : ml.getStations()) {
                    if (s.getName().equals(station)) {
                        return Option.of(s);
                    }
                }
            }
        }
        return Option.none();
    }

    private static boolean areLinesConnected(List<MetroLine> metroLineList, Station head, Station tail) {
        HashMap<MetroLine, List<MetroLine>> lineConectionMap = createMetroLineMap(metroLineList);

        MetroLine tailLine = metroLineList.find(n -> n.getName().equals(tail.getLineName())).get();

        List<MetroLine> headConnected = lineConectionMap.get(metroLineList.find(n -> n.getName().equals(head.getLineName())).get()).get();
        for (int i = 0; i == metroLineList.size(); i++) {
            for (MetroLine ml : lineConectionMap.keySet()) {
                if (headConnected.contains(ml)) {
                    for (String lineName : ml.getConnectedLines()) {
                        MetroLine metroLine = metroLineList.find(n -> n.getName().equals(lineName)).get();
                        headConnected = headConnected.append(metroLine);
                    }
                }
            }
            if (headConnected.contains(tailLine)) return true;
        }
        if (headConnected.contains(tailLine)) return true;
        return false;
    }

    private static HashMap<MetroLine, List<MetroLine>> createMetroLineMap(List<MetroLine> metroLineList) {
        HashMap<MetroLine, List<MetroLine>> lineConectionMap = HashMap.empty();

        for (MetroLine ml : metroLineList) {
            List<MetroLine> connectedLines = List.empty();
            for (String lineName : ml.getConnectedLines()) {
                MetroLine metroLine = metroLineList.find(n -> n.getName().equals(lineName)).get();
                connectedLines = connectedLines.append(metroLine);
            }
            lineConectionMap = lineConectionMap.put(ml, connectedLines);
        }
        return lineConectionMap;
    }
}