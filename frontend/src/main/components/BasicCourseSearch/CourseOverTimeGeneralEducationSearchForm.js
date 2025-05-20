import { useState } from "react";
import { Form, Button, Container, Row, Col } from "react-bootstrap";
import { quarterRange } from "main/utils/quarterUtilities";
import { useSystemInfo } from "main/utils/systemInfo";
import SingleQuarterDropdown from "../Quarters/SingleQuarterDropdown";
import SingleGEDropdown from "../GeneralEducation/SingleGEDropdown";

const CourseOverTimeGeneralEducationSearchForm = ({ fetchJSON }) => {
  const { data: systemInfo } = useSystemInfo();

  // Stryker disable OptionalChaining
  const startQtr = systemInfo?.startQtrYYYYQ || "20211";
  const endQtr = systemInfo?.endQtrYYYYQ || "20214";
  // Stryker restore OptionalChaining

  const quarters = quarterRange(startQtr, endQtr);

  // Stryker disable all : not sure how to test/mock local storage
  const localStartQuarter = localStorage.getItem(
    "CourseOverTimeGeneralEducationSearch.StartQuarter",
  );
  const localEndQuarter = localStorage.getItem(
    "CourseOverTimeGeneralEducationSearch.EndQuarter",
  );
  const localGeneralEducation = localStorage.getItem(
    "CourseOverTimeGeneralEducationSearch.GeneralEducation",
  );

  const [startQuarter, setStartQuarter] = useState(
    localStartQuarter || quarters[0].yyyyq,
  );
  const [endQuarter, setEndQuarter] = useState(
    localEndQuarter || quarters[0].yyyyq,
  );
  const [selectedGEArea, setSelectedGEArea] = useState(
    localGeneralEducation || "",
  );
  // Stryker restore all

  const geAreas = [
    { geCode: "A1", description: "Area A1: English Reading and Composition" },
    { geCode: "B1", description: "Area B1: Physical Science" },
    { geCode: "C1", description: "Area C1: Arts" },
    // Add more GE areas as needed
  ];

  const handleSubmit = (event) => {
    event.preventDefault();
    fetchJSON(event, {
      startQuarter,
      endQuarter,
      generalEducation: selectedGEArea,
    });
  };

  //   const testid = "CourseOverTimeGeneralEducationSearchForm";

  return (
    <Form onSubmit={handleSubmit}>
      <Container>
        <Row>
          <Col md="auto">
            <SingleQuarterDropdown
              quarters={quarters}
              quarter={startQuarter}
              setQuarter={setStartQuarter}
              controlId={"CourseOverTimeGeneralEducationSearch.StartQuarter"}
              label={"Start Quarter"}
            />
          </Col>
          <Col md="auto">
            <SingleQuarterDropdown
              quarters={quarters}
              quarter={endQuarter}
              setQuarter={setEndQuarter}
              controlId={"CourseOverTimeGeneralEducationSearch.EndQuarter"}
              label={"End Quarter"}
            />
          </Col>
        </Row>
        <Form.Group controlId="CourseOverTimeGeneralEducationSearch.GEArea">
          <SingleGEDropdown
            areas={geAreas || []}
            area={selectedGEArea}
            setArea={setSelectedGEArea}
            controlId="CourseOverTimeGeneralEducationSearch.GEArea"
            label="GE Area"
          />
        </Form.Group>
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

export default CourseOverTimeGeneralEducationSearchForm;
