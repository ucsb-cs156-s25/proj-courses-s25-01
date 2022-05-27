package edu.ucsb.cs156.courses.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import edu.ucsb.cs156.courses.documents.ConvertedSection;
import edu.ucsb.cs156.courses.documents.Course;
import edu.ucsb.cs156.courses.documents.CourseInfo;
import edu.ucsb.cs156.courses.documents.CoursePage;
import edu.ucsb.cs156.courses.documents.Section;

/**
 * Service object that wraps the UCSB Academic Curriculum API
 */
@Service
public class UCSBCurriculumService {

    @Autowired
    private ObjectMapper objectMapper;

    private Logger logger = LoggerFactory.getLogger(UCSBCurriculumService.class);

    @Value("${app.ucsb.api.consumer_key}")
    private String apiKey;

    private RestTemplate restTemplate = new RestTemplate();

    public UCSBCurriculumService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public static final String CURRICULUM_ENDPOINT = "https://api.ucsb.edu/academics/curriculums/v1/classes/search";

    public static final String SUBJECTS_ENDPOINT = "https://api.ucsb.edu/students/lookups/v1/subjects";

    public String getJSON(String subjectArea, String quarter, String courseLevel) {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("ucsb-api-version", "1.0");
        headers.set("ucsb-api-key", this.apiKey);

        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        String params = String.format(
                "?quarter=%s&subjectCode=%s&objLevelCode=%s&pageNumber=%d&pageSize=%d&includeClassSections=%s", quarter,
                subjectArea, courseLevel, 1, 100, "true");
        String url = CURRICULUM_ENDPOINT + params;

        if (courseLevel.equals("A")) {
            params = String.format(
                    "?quarter=%s&subjectCode=%s&pageNumber=%d&pageSize=%d&includeClassSections=%s",
                    quarter, subjectArea, 1, 100, "true");
            url = CURRICULUM_ENDPOINT + params;
        }

        logger.info("url=" + url);

        String retVal = "";
        MediaType contentType = null;
        HttpStatus statusCode = null;
        try {
            ResponseEntity<String> re = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            contentType = re.getHeaders().getContentType();
            statusCode = re.getStatusCode();
            retVal = re.getBody();
        } catch (HttpClientErrorException e) {
            retVal = "{\"error\": \"401: Unauthorized\"}";
        }
        logger.info("json: {} contentType: {} statusCode: {}", retVal, contentType, statusCode);
        return retVal;
    }

    public List<ConvertedSection> getConvertedSections(String subjectArea, String quarter, String courseLevel)
            throws JsonProcessingException {
        String json = getJSON(subjectArea, quarter, courseLevel);
        CoursePage coursePage = objectMapper.readValue(json, CoursePage.class);
        List<ConvertedSection> result = coursePage.convertedSections();       
        return result;
    }

    public String getSubjectsJSON() {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("ucsb-api-version", "1.0");
        headers.set("ucsb-api-key", this.apiKey);

        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        logger.info("url=" + SUBJECTS_ENDPOINT);

        String retVal = "";
        MediaType contentType = null;
        HttpStatus statusCode = null;
        try {
            ResponseEntity<String> re = restTemplate.exchange(SUBJECTS_ENDPOINT, HttpMethod.GET, entity, String.class);
            contentType = re.getHeaders().getContentType();
            statusCode = re.getStatusCode();
            retVal = re.getBody();
        } catch (HttpClientErrorException e) {
            retVal = "{\"error\": \"401: Unauthorized\"}";
        }
        logger.info("json: {} contentType: {} statusCode: {}", retVal, contentType, statusCode);
        return retVal;
    }

}