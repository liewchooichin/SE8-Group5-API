package sg.edu.ntu.bus_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Must start field names with small letters

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="bus_stop")
public class BusStop {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="BusStopId")
  private Long busStopId;

  @Column(name="BusStopCode")
  @Pattern(regexp = "\\d{5,7}", message = "Bus stop code must be numeric and between 5 and 7 digits long")
  private String busStopCode;

  @Column(name="RoadName")
  @NotBlank(message = "Road name is mandatory")
  private String roadName;

  @Column(name="Description")
  @NotBlank(message = "Description name of bus stop is mandatory")
  private String description;

  @Column(name="Latitude")
  private Double latitude;

  @Column(name="Longitude")
  private Double longitude;
}
