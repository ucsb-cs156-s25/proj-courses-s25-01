import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useParams } from "react-router-dom";
import PersonalSchedulesTable from "main/components/PersonalSchedules/PersonalSchedulesTable";
import PersonalSectionsTable from "main/components/PersonalSections/PersonalSectionsTable";
import { useBackend, _useBackendMutation } from "main/utils/useBackend";
import { Button } from "react-bootstrap";

export default function PersonalSchedulesDetailsPage() {
  let { id } = useParams();

  const {
    data: personalSchedule,
    _error,
    _status,
  } = useBackend(
    // Stryker disable all : hard to test for query caching
    [`/api/personalschedules?id=${id}`],
    {
      // Stryker disable next-line all : GET is the default, so changing this to "" doesn't introduce a bug
      method: "GET",
      url: `/api/personalschedules?id=${id}`,
      params: {
        id,
      },
    },
  );
  const createButton = () => {
    return (
      <Button variant="primary" href="/personalschedules/list" style={{}}>
        Back
      </Button>
    );
  };

  const { data: personalSection } = useBackend(
    // Stryker disable all : hard to test for query caching
    [`/api/personalSections/all?psId=${id}`],
    {
      method: "GET",
      url: `/api/personalSections/all?psId=${id}`,
      params: {
        id,
      },
    },
  );

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Personal Schedules Details</h1>
        {personalSchedule && (
          <PersonalSchedulesTable
            personalSchedules={[personalSchedule]}
            showButtons={false}
          />
        )}
        <p>
          <h2>Sections in Personal Schedule</h2>
          {personalSection && (
            <PersonalSectionsTable personalSections={personalSection} />
          )}
        </p>
        {createButton()}
      </div>
    </BasicLayout>
  );
}
