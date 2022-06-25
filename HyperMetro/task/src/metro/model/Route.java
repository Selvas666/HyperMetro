package metro.model;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class Route {
    List<Station> stations;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.stations.size(); i++){
            sb.append(stations.get(i).getName());
            sb.append("\n");
            if (i+1 < this.stations.size()){
                if (!stations.get(i).getLineName().equals(stations.get(i+1).getLineName())){
                    sb.append("Transition to line ");
                    sb.append(stations.get(i+1).getLineName());
                    sb.append("\n");
                }
            }
        }

        return sb.toString().strip();


    }
}
