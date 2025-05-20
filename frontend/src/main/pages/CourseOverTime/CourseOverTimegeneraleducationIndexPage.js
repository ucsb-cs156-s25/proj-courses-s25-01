import { useState } from "react";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import CourseOverTimeGeneralEducationSearchForm from "main/components/BasicCourseSearch/CourseOverTimeGeneralEducationSearchForm";
import { useBackendMutation } from "main/utils/useBackend";
// import SingleGEDropdown from "main/components/GeneralEducation/SingleGEDropdown";

export default function CourseOverTimeGeneralEducationIndexPage() {
  // Stryker disable next-line all : Can't test state because hook is internal
  const [setCourseJSON] = useState([]);

  const objectToAxiosParams = (query) => ({
    url: "/api/public/courseovertime/generalEducationsearch",
    params: {
      startQtr: query.startQuarter,
      endQtr: query.endQuarter,
      generalEducation: query.generalEducation,
      lectureOnly: query.checkbox,
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

  async function fetchCourseOverTimeJSON(_event, query) {
    mutation.mutate(query);
  }

  return (
    <BasicLayout>
      <div className="pt-2">
        <h5>Welcome to the UCSB Course General Education Search!</h5>
        <CourseOverTimeGeneralEducationSearchForm
          fetchJSON={fetchCourseOverTimeJSON}
        />
      </div>
    </BasicLayout>
  );
}
