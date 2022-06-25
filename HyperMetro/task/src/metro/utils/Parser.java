package metro.utils;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import io.vavr.collection.List;
import io.vavr.control.Try;
import metro.model.MetroLine;
import metro.model.Station;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Parser {

    public static List<MetroLine> parse(String[] args) {
        List<MetroLine> metroLineList = List.empty();
        try {
            String json_string = Files.readString(Paths.get(args[0]));

//            System.out.println(json_string);

            try (JsonReader reader = new JsonReader(new StringReader(json_string))) {
                String lineName = "";
                List<Station> stations = List.empty();
                List<Station> connectedStationsList = List.empty();
                Station.StationBuilder connectionStation = Station.builder();
                int stNo = 0;
                boolean parse = true;
                String currentTokenName = "";
                String currentStationName = "";

                while (parse) {

                    Try<JsonToken> token = Try.of(reader::peek);

                    if (token.isFailure()) break;

                    JsonToken nextToken = token.get();
//                    System.out.println("PEEK: " + nextToken.name());

                    switch (nextToken) {
                        case BEGIN_OBJECT:
//                            System.out.println("begin");
                            reader.beginObject();
                            break;
                        case NAME:
                            currentTokenName = reader.nextName();
//                            System.out.println("nm is " + nm);
                            if (NumberUtils.isParsable(currentTokenName)) {
                                stNo = Integer.parseInt(currentTokenName);
//                                System.out.println(stNo);
                                break;
                            }
                            else if (currentTokenName.equals("name")
                                    || currentTokenName.equals("transfer")
                                    || currentTokenName.equals("line")
                                    || currentTokenName.equals("station")
                                    || currentTokenName.equals("time")){
                                break;
                            }
                            else if (!lineName.equals("")){
                                metroLineList = metroLineList.append(new MetroLine(lineName, stations));
                                stations = List.empty();
                            }
                            lineName = currentTokenName;
//                            System.out.println(name);
                            break;
                        case NUMBER:
                            reader.nextInt();
                            break;
                        case NULL:
                            reader.nextNull();
                            break;
                        case STRING:
                            if (currentTokenName.equals("name")) {
                                currentStationName = reader.nextString();
                                break;
                            }
                            else if (currentTokenName.equals("line")){
                                connectionStation = Station.builder();
                                connectionStation.connectionStation(true)
                                        .lineName(reader.nextString());
                                break;
                            }
                            else if (currentTokenName.equals("station")){
                                connectionStation.name(reader.nextString());
                                connectedStationsList = connectedStationsList.append(connectionStation.build());
                                break;
                            }
                            reader.nextString();
//                            System.out.println("SS " + stations.last().getName());
                            break;
                        case BEGIN_ARRAY:
                            reader.beginArray();
                            break;
                        case END_ARRAY:
                            Station s = Station.builder()
                                    .name(currentStationName)
                                    .lineName(lineName)
                                    .number(stNo)
                                    .connectedStationsList(connectedStationsList)
                                    .connected(!connectedStationsList.isEmpty())
                                    .connectionStation(false)
                                    .build();
                            stations = stations.append(s);
                            currentStationName = "";
                            stNo = 0;
                            connectedStationsList = List.empty();
                            reader.endArray();
                            break;
                        case END_OBJECT:
                            reader.endObject();
//                            System.out.println("end object");
                            break;
                        case END_DOCUMENT:
                            metroLineList = metroLineList.append(new MetroLine(lineName, stations));
                            parse = false;
                            break;
                    }
                }
//                metroLineList = metroLineList.append(new MetroLine(name,stations));

            } catch (Exception ex) {
                System.out.println("Incorrect file.");
            }


        } catch (IOException e) {
            System.out.printf("Error! Such a file doesn't exist!%n");
        }

//        metroLineList.forEach(n -> System.out.println(n.getName()));
//        metroLineList.forEach(n -> System.out.println(n.toString()));


        return metroLineList;

    }
}
