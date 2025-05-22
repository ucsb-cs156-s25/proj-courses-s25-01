import { useState } from "react";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import GeneralEducationSearchForm from "main/components/GeneralEducation/GeneralEducationSearchForm";
import { useBackendMutation } from "main/utils/useBackend";
import BasicCourseTable from "main/components/Courses/BasicCourseTable";

export default function GeneralEducationSearchPage() {
  // Stryker disable next-line all : Can't test state because hook is internal
  const [courseJSON, setCourseJSON] = useState([]);

  const objectToAxiosParams = (query) => ({
    url: "/api/public/generalEducation/gesearch",
    params: {
      startQtr: query.quarter,
      endQtr: query.quarter,
      geCode: query.geArea,
    },
  });

  const onSuccess = (courses) => {
    setCourseJSON(courses);
  };

  const mutation = useBackendMutation(
    objectToAxiosParams,
    { onSuccess },
    // Stryker disable next-line all : hard to set up test for caching
    [],
  );

  async function fetchJSON(_event, query) {
    mutation.mutate(query);
  }
  console.log("courseJSON:", courseJSON);

  return (
    <BasicLayout>
      <div className="pt-2">
        <h5>Welcome to the UCSB Course General Education Search!</h5>
        <GeneralEducationSearchForm
          fetchJSON={fetchJSON}
        />
        <BasicCourseTable courses={courseJSON}
        />
      </div>
    </BasicLayout>
  );
}