package edu.ucsb.cs156.courses.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.ucsb.cs156.courses.documents.ConvertedSection;
import edu.ucsb.cs156.courses.documents.CoursePage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Service object that wraps the UCSB Academic Curriculum API */
@Service
@Slf4j
public class UCSBCurriculumService {

  @Autowired private ObjectMapper objectMapper;

  @Value("${app.ucsb.api.consumer_key}")
  private String apiKey;

  private RestTemplate restTemplate = new RestTemplate();

  public UCSBCurriculumService(RestTemplateBuilder restTemplateBuilder) throws Exception {
    restTemplate = restTemplateBuilder.build();
  }

  public static final String CURRICULUM_ENDPOINT =
      "https://api.ucsb.edu/academics/curriculums/v1/classes/search";

  public static final String SUBJECTS_ENDPOINT =
      "https://api.ucsb.edu/students/lookups/v1/subjects";

  public static final String SECTION_ENDPOINT =
      "https://api.ucsb.edu/academics/curriculums/v1/classsection/{quarter}/{enrollcode}";

  public static final String ALL_SECTIONS_ENDPOINT =
      "https://api.ucsb.edu/academics/curriculums/v3/classes/{quarter}/{enrollcode}";

  public static final String FINALS_ENDPOINT =
      "https://api.ucsb.edu/academics/curriculums/v3/finals";

  private static final String[] GE_CODES = {
    "A1", "A2", "AMH", "B", "C", "D", "E", "E1", "E2", "ETH", "EUR", "F", "G", "H", "NWC", "QNT",
    "SUB", "WRT"
  };

  public static final String GE_ENDPOINT = "https://api.ucsb.edu/students/lookups/v1/requirements";

  public String getJSON(String subjectArea, String quarter, String courseLevel) throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("ucsb-api-version", "1.0");
    headers.set("ucsb-api-key", this.apiKey);

    HttpEntity<String> entity = new HttpEntity<>("body", headers);

    String params =
        String.format(
            "?quarter=%s&subjectCode=%s&objLevelCode=%s&pageNumber=%d&pageSize=%d&includeClassSections=%s",
            quarter, subjectArea, courseLevel, 1, 100, "true");
    String url = CURRICULUM_ENDPOINT + params;

    if (courseLevel.equals("A")) {
      params =
          String.format(
              "?quarter=%s&subjectCode=%s&pageNumber=%d&pageSize=%d&includeClassSections=%s",
              quarter, subjectArea, 1, 100, "true");
      url = CURRICULUM_ENDPOINT + params;
    }

    log.info("url=" + url);

    String retVal = "";
    MediaType contentType = null;
    HttpStatus statusCode = null;

    ResponseEntity<String> re = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    contentType = re.getHeaders().getContentType();
    statusCode = (HttpStatus) re.getStatusCode();
    retVal = re.getBody();

    log.trace("json: {}", retVal);
    log.info("contentType: {} statusCode: {}", contentType, statusCode);
    return retVal;
  }

  public List<ConvertedSection> getConvertedSections(
      String subjectArea, String quarter, String courseLevel) throws Exception {
    String json = getJSON(subjectArea, quarter, courseLevel);
    CoursePage coursePage = objectMapper.readValue(json, CoursePage.class);
    List<ConvertedSection> result = coursePage.convertedSections();
    return result;
  }

  public String getSectionJSON(String subjectArea, String quarter, String courseLevel)
      throws Exception {
    List<ConvertedSection> l = getConvertedSections(subjectArea, quarter, courseLevel);

    String arrayToJson = objectMapper.writeValueAsString(l);

    return arrayToJson;
  }

  public String getSubjectsJSON() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("ucsb-api-version", "1.0");
    headers.set("ucsb-api-key", this.apiKey);

    HttpEntity<String> entity = new HttpEntity<>("body", headers);

    log.info("url=" + SUBJECTS_ENDPOINT);

    String retVal = "";
    MediaType contentType = null;
    HttpStatus statusCode = null;

    ResponseEntity<String> re =
        restTemplate.exchange(SUBJECTS_ENDPOINT, HttpMethod.GET, entity, String.class);
    contentType = re.getHeaders().getContentType();
    statusCode = (HttpStatus) re.getStatusCode();
    retVal = re.getBody();

    log.info("json: {} contentType: {} statusCode: {}", retVal, contentType, statusCode);
    return retVal;
  }

  /**
   * This method retrieves exactly one section matching the enrollCode and quarter arguments, if
   * such a section exists.
   */
  public String getSection(String enrollCode, String quarter) throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("ucsb-api-version", "1.0");
    headers.set("ucsb-api-key", this.apiKey);

    HttpEntity<String> entity = new HttpEntity<>("body", headers);

    String url = SECTION_ENDPOINT;

    log.info("url=" + url);

    Map<String, String> params = new HashMap<>();
    params.put("quarter", quarter);
    params.put("enrollcode", enrollCode);

    String retVal = "";
    MediaType contentType = null;
    HttpStatus statusCode = null;

    ResponseEntity<String> re =
        restTemplate.exchange(url, HttpMethod.GET, entity, String.class, params);
    contentType = re.getHeaders().getContentType();
    statusCode = (HttpStatus) re.getStatusCode();
    retVal = re.getBody();

    if (retVal.equals("null")) {
      retVal = "{\"error\": \"Enroll code doesn't exist in that quarter.\"}";
    }

    log.info("json: {} contentType: {} statusCode: {}", retVal, contentType, statusCode);
    return retVal;
  }

  /**
   * This method retrieves all of the sections related to a certain enroll code. For example, if the
   * enrollCode is for a discussion section, the lecture section and all related discussion sections
   * will also be returned.
   */
  public String getAllSections(String enrollCode, String quarter) throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("ucsb-api-version", "3.0");
    headers.set("ucsb-api-key", this.apiKey);

    HttpEntity<String> entity = new HttpEntity<>("body", headers);

    String url = ALL_SECTIONS_ENDPOINT;

    log.info("url=" + url);

    Map<String, String> params = new HashMap<>();
    params.put("quarter", quarter);
    params.put("enrollcode", enrollCode);

    String retVal = "";
    MediaType contentType = null;
    HttpStatus statusCode = null;

    ResponseEntity<String> re =
        restTemplate.exchange(url, HttpMethod.GET, entity, String.class, params);
    contentType = re.getHeaders().getContentType();
    statusCode = (HttpStatus) re.getStatusCode();
    retVal = re.getBody();

    if (retVal.equals("null")) {
      retVal = "{\"error\": \"Enroll code doesn't exist in that quarter.\"}";
    }

    log.info("json: {} contentType: {} statusCode: {}", retVal, contentType, statusCode);
    return retVal;
  }

  public String getJSONbyQtrEnrollCd(String quarter, String enrollCd) throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("ucsb-api-version", "1.0");
    headers.set("ucsb-api-key", this.apiKey);

    HttpEntity<String> entity = new HttpEntity<>("body", headers);

    String url =
        "https://api.ucsb.edu/academics/curriculums/v3/classsection/" + quarter + "/" + enrollCd;

    log.info("url=" + url);

    String retVal = "";
    MediaType contentType = null;
    HttpStatus statusCode = null;

    ResponseEntity<String> re = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    contentType = re.getHeaders().getContentType();
    statusCode = (HttpStatus) re.getStatusCode();
    retVal = re.getBody();

    log.info("json: {} contentType: {} statusCode: {}", retVal, contentType, statusCode);
    return retVal;
  }

  public String getFinalsInfo(String quarter, String enrollCd) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.set("ucsb-api-version", "3.0");
    headers.set("ucsb-api-key", this.apiKey);

    HttpEntity<String> entity = new HttpEntity<>("body", headers);

    String params = String.format("?quarter=%s&enrollCode=%s", quarter, enrollCd);
    String url = FINALS_ENDPOINT + params;

    log.info("url=" + url);

    String retVal;
    MediaType contentType;
    HttpStatus statusCode;

    ResponseEntity<String> re = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    contentType = re.getHeaders().getContentType();
    statusCode = (HttpStatus) re.getStatusCode();
    retVal = re.getBody();

    log.info("json: {} contentType: {} statusCode: {}", retVal, contentType, statusCode);
    return retVal;
  }

  public String[] getStaticGeInfo() {
    return GE_CODES;
  }

  public String getGeInfo() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.set("ucsb-api-version", "3.0");
    headers.set("ucsb-api-key", this.apiKey);

    HttpEntity<String> entity = new HttpEntity<>("body", headers);

    String url = GE_ENDPOINT;

    log.info("url=" + url);

    ResponseEntity<String> re = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    MediaType contentType = re.getHeaders().getContentType();
    HttpStatus statusCode = (HttpStatus) re.getStatusCode();
    String fullResponse = re.getBody();

    log.info("json: {} contentType: {} statusCode: {}", fullResponse, contentType, statusCode);

    // Parse the full JSON response
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(fullResponse);

    // Create a new array with just teh requirement coes as strings
    Set<String> uniqueCodes = new TreeSet<>();
    for (JsonNode item : rootNode) {
      uniqueCodes.add(item.get("requirementCode").asText());
    }

    // Convert the Set to a JSON array
    ArrayNode resultArray = objectMapper.createArrayNode();
    for (String code : uniqueCodes) {
      resultArray.add(code);
    }

    // Convert the filtered array back to JSON string
    return objectMapper.writeValueAsString(resultArray);
  }
}
