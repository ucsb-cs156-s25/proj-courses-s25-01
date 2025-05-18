import { toast } from "react-toastify";

export function onDeleteSuccess(message) {
  console.log(message);
  toast(message);
}

export function cellToAxiosParamsDelete(cell) {
  return {
    url: "/api/personalschedules",
    method: "DELETE",
    params: {
      id: cell.row.values.id,
    },
  };
}

export function schedulesFilter(schedules, quarter) {
  return schedules.filter((schedule) => schedule.quarter === quarter);
}

// export function sectionToEvents(section) {
//   // helper functions defined inside keep file count down

//   const dayLetterToName = (c) =>
//     ({
//       M: "Monday",
//       T: "Tuesday",
//       W: "Wednesday",
//       R: "Thursday",
//       F: "Friday",
//       S: "Saturday",
//       U: "Sunday",
//     })[c];

//   const to12hr = (hhmm) => {
//     const [h, m] = hhmm.split(":").map(Number);
//     const hour12 = ((h + 11) % 12) + 1;
//     return `${hour12}:${m.toString().padStart(2, "0")}${h < 12 ? "AM" : "PM"}`;
//   };

//   return section.classSections.flatMap((cs) =>
//     cs.timeLocations.flatMap((tl) =>
//       [...tl.days.trim()].map((d) => ({
//         id: `${section.courseId.trim()}-${cs.section}-${d}`,
//         title: `${section.courseId.trim()} (${cs.section})`,
//         description: section.title,
//         day: dayLetterToName(d),
//         startTime: to12hr(tl.beginTime),
//         endTime: to12hr(tl.endTime),
//       })),
//     ),
//   );
// }
