package edu.ucsb.cs156.courses.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucsb.cs156.courses.collections.ConvertedSectionCollection;
import edu.ucsb.cs156.courses.documents.ConvertedSection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/courseovertime")
public class CourseOverTimeBuildingController {

  private ObjectMapper mapper = new ObjectMapper();

  @Autowired ConvertedSectionCollection convertedSectionCollection;

  @Operation(summary = "Get a list of courses over time, filtered by (abbreviated) building code")
  @GetMapping(value = "/buildingsearch", produces = "application/json")
  public ResponseEntity<String> search(
      @Parameter(
              name = "startQtr",
              description =
                  "Starting quarter in yyyyq format, e.g. 20231 for W23, 20232 for S23, etc. (1=Winter, 2=Spring, 3=Summer, 4=Fall)",
              example = "20231",
              required = true)
          @RequestParam
          String startQtr,
      @Parameter(
              name = "endQtr",
              description =
                  "Ending quarter in yyyyq format, e.g. 20231 for W23, 20232 for S23, etc. (1=Winter, 2=Spring, 3=Summer, 4=Fall)",
              example = "20231",
              required = true)
          @RequestParam
          String endQtr,
      @Parameter(
              name = "buildingCode",
              description = "Building code such as PHELP for Phelps, GIRV for Girvetz",
              example = "GIRV",
              required = true)
          @RequestParam
          String buildingCode)
      throws JsonProcessingException {
    List<ConvertedSection> courseResults =
        convertedSectionCollection.findByQuarterRangeAndBuildingCode(
            startQtr, endQtr, buildingCode);
    String body = mapper.writeValueAsString(courseResults);
    return ResponseEntity.ok().body(body);
  }

  @Operation(summary = "Get a list of classroom numbers for a building within a quarter range")
  @GetMapping(value = "/classrooms", produces = "application/json")
  public ResponseEntity<String> getClassrooms(
      @Parameter(
              name = "startQtr",
              description =
                  "Starting quarter in yyyyq format, e.g. 20231 for W23, 20232 for S23, etc. (1=Winter, 2=Spring, 3=Summer, 4=Fall)",
              example = "20231",
              required = true)
          @RequestParam
          String startQtr,
      @Parameter(
              name = "endQtr",
              description =
                  "Ending quarter in yyyyq format, e.g. 20231 for W23, 20232 for S23, etc. (1=Winter, 2=Spring, 3=Summer, 4=Fall)",
              example = "20231",
              required = true)
          @RequestParam
          String endQtr,
      @Parameter(
              name = "buildingCode",
              description = "Building code such as PHELP for Phelps, GIRV for Girvetz",
              example = "GIRV",
              required = true)
          @RequestParam
          String buildingCode)
      throws JsonProcessingException {

    List<ConvertedSection> sections =
        convertedSectionCollection.findByQuarterRangeAndBuildingCode(
            startQtr, endQtr, buildingCode);

    List<String> rooms =
        sections.stream()
            .flatMap(cs -> cs.getSection().getTimeLocations().stream())
            .filter(tl -> buildingCode.equals(tl.getBuilding()))
            .map(tl -> tl.getRoom())
            .distinct()
            .sorted()
            .collect(Collectors.toList());

    String body = mapper.writeValueAsString(rooms);
    return ResponseEntity.ok().body(body);
  }
}
