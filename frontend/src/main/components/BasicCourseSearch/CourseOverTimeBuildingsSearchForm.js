// frontend/src/main/components/BasicCourseSearch/CourseOverTimeBuildingsSearchForm.js

import { useState } from "react";
import { Form, Button, Container, Row, Col } from "react-bootstrap";

import { allBuildings } from "fixtures/buildingFixtures";
import { quarterRange } from "main/utils/quarterUtilities";
import { useSystemInfo } from "main/utils/systemInfo";

import SingleQuarterDropdown from "../Quarters/SingleQuarterDropdown";
import SingleBuildingDropdown  from "../Buildings/SingleBuildingDropdown";

const CourseOverTimeBuildingsSearchForm = ({ fetchJSON, availableClassrooms }) => {
  const { data: systemInfo } = useSystemInfo();

  // Stryker disable OptionalChaining
  const startQtr = systemInfo?.startQtrYYYYQ || "20211";
  const endQtr   = systemInfo?.endQtrYYYYQ   || "20214";
  // Stryker restore OptionalChaining

  const quarters = quarterRange(startQtr, endQtr);

  // Stryker disable all : not sure how to test/mock local storage
  const localStartQuarter  = localStorage.getItem("CourseOverTimeBuildingsSearch.StartQuarter");
  const localEndQuarter    = localStorage.getItem("CourseOverTimeBuildingsSearch.EndQuarter");
  const localBuildingCode  = localStorage.getItem("CourseOverTimeBuildingsSearch.BuildingCode");
  const localClassroom     = localStorage.getItem("CourseOverTimeBuildingsSearch.Classroom");
  // Stryker restore all

  const [startQuarter, setStartQuarter] = useState(localStartQuarter  || quarters[0].yyyyq);
  const [endQuarter,   setEndQuarter]   = useState(localEndQuarter    || quarters[0].yyyyq);
  const [buildingCode, setBuildingCode] = useState(localBuildingCode  || "");
  const [classroom,    setClassroom]    = useState(localClassroom     || "");

  const handleSubmit = (event) => {
    event.preventDefault();

    // persist for next time
    localStorage.setItem("CourseOverTimeBuildingsSearch.StartQuarter", startQuarter);
    localStorage.setItem("CourseOverTimeBuildingsSearch.EndQuarter",   endQuarter);
    localStorage.setItem("CourseOverTimeBuildingsSearch.BuildingCode", buildingCode);
    localStorage.setItem("CourseOverTimeBuildingsSearch.Classroom",    classroom);

    fetchJSON(event, { startQuarter, endQuarter, buildingCode, classroom });
  };

  return (
    <Form onSubmit={handleSubmit}>
      <Container>
        <Row>
          {/* Start Quarter */}
          <Col md="auto">
            <SingleQuarterDropdown
              quarters={quarters}
              quarter={startQuarter}
              setQuarter={setStartQuarter}
              controlId="CourseOverTimeBuildingsSearch.StartQuarter"
              label="Start Quarter"
            />
          </Col>
          {/* End Quarter */}
          <Col md="auto">
            <SingleQuarterDropdown
              quarters={quarters}
              quarter={endQuarter}
              setQuarter={setEndQuarter}
              controlId="CourseOverTimeBuildingsSearch.EndQuarter"
              label="End Quarter"
            />
          </Col>
          {/* Building */}
          <Col md="auto">
            <SingleBuildingDropdown
              buildings={allBuildings}
              building={buildingCode}
              setBuilding={setBuildingCode}
              controlId="CourseOverTimeBuildingsSearch.BuildingCode"
              label="Building Name"
            />
          </Col>
          {/* Classroom */}
          <Col md="auto">
            <Form.Group controlId="CourseOverTimeBuildingsSearch.Classroom">
              <Form.Label>Classroom</Form.Label>
              <Form.Control
                as="select"
                value={classroom}
                onChange={e => setClassroom(e.target.value)}
              >
                <option value="">All</option>
                {availableClassrooms.map(r => (
                  <option key={r} value={r}>{r}</option>
                ))}
              </Form.Control>
            </Form.Group>
          </Col>
        </Row>

        <Row style={{ paddingTop: 10, paddingBottom: 10 }}>
          <Col md="auto">
            <Button variant="primary" type="submit">
              Submit
            </Button>
          </Col>
        </Row>
      </Container>
    </Form>
  );
};

export default CourseOverTimeBuildingsSearchForm;

