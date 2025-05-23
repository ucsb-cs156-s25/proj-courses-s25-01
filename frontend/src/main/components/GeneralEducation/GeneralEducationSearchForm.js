import { useState } from "react";
import { Form, Button, Container, Row, Col } from "react-bootstrap";

import { quarterRange } from "main/utils/quarterUtilities";
import { useSystemInfo } from "main/utils/systemInfo";
import SingleQuarterDropdown from "../Quarters/SingleQuarterDropdown";
import SingleGEDropdown from "./SingleGEDropdown";
import { useBackend } from "main/utils/useBackend";

const GeneralEducationSearchForm = ({ fetchJSON }) => {
  const { data: systemInfo } = useSystemInfo();

 // Stryker disable OptionalChaining
 const sqtr = systemInfo?.startQtrYYYYQ || "20211";
  const eqtr = systemInfo?.endQtrYYYYQ || "20214";
 // Stryker restore OptionalChaining

 const quarters = quarterRange(sqtr, eqtr);

 // Stryker disable all : not sure how to test/mock local storage
 const localStartQuarter = localStorage.getItem(
   "GeneralEducationSearch.StartQuarter",
 );
 const localEndQuarter = localStorage.getItem(
   "GeneralEducationSearch.EndQuarter",
 );
 const localgeCode = localStorage.getItem(
   "GeneralEducationSearch.geCode",
 );

 const [startQuarter, setStartQuarter] = useState(
   localStartQuarter || quarters[0].yyyyq,
 );
 const [endQuarter, setEndQuarter] = useState(
   localEndQuarter || quarters[0].yyyyq,
 );
 const [geCode, setgeCode] = useState(localgeCode || "A1");

   // Stryker restore all

  const {
    data: geAreas,
    error: _error,
    status: _status,
  } = useBackend(
    // Stryker disable next-line all : don't test internal caching of React Query
    ["/api/public/generalEducationInfo"],
    { method: "GET", url: "/api/public/generalEducationInfo" },
    [],
  );

  console.log("geCode:", geCode);
  const handleSubmit = (event) => {
    event.preventDefault();
    console.log("Submitting with:", { startQuarter, endQuarter, geCode });
    fetchJSON(event, { startQuarter, endQuarter, geCode});
  };

  return (
    <Form onSubmit={handleSubmit}>
      <Container>
        <Row>
          <Col md="auto">
            <SingleQuarterDropdown
              quarters={quarters}
              quarter={startQuarter}
              setQuarter={setStartQuarter}
              controlId={"GeneralEducationSearch.StartQuarter"}
              label={"Start Quarter"}
            />
          </Col>
          <Col md="auto">
            <SingleQuarterDropdown
              quarters={quarters}
              quarter={endQuarter}
              setQuarter={setEndQuarter}
              controlId={"GeneralEducationSearch.EndQuarter"}
              label={"End Quarter"}
            />
          </Col>
        </Row>
        <Form.Group controlId="GeneralEducationSearch.GEArea">
          <SingleGEDropdown
            areas={geAreas}
            area={geCode}
            setArea={setgeCode}
            controlId="GeneralEducationSearch.GEArea"
            label="GE Area"
          />
        </Form.Group>
        <Row
          data-testid="GeneralEducationSearchForm-submit-row"
          style={{ paddingTop: 10, paddingBottom: 10 }}
        >
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

export default GeneralEducationSearchForm;


