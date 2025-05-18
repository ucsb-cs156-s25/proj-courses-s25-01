import {
  onDeleteSuccess,
  cellToAxiosParamsDelete,
  schedulesFilter,
  sectionToEvents,
} from "main/utils/PersonalScheduleUtils";
import mockConsole from "jest-mock-console";

const mockToast = jest.fn();
jest.mock("react-toastify", () => {
  const originalModule = jest.requireActual("react-toastify");
  return {
    __esModule: true,
    ...originalModule,
    toast: (x) => mockToast(x),
  };
});

describe("PersonalScheduleUtils", () => {
  describe("onDeleteSuccess", () => {
    test("It puts the message on console.log and in a toast", () => {
      // arrange
      const restoreConsole = mockConsole();

      // act
      onDeleteSuccess("abc");

      // assert
      expect(mockToast).toHaveBeenCalledWith("abc");
      expect(console.log).toHaveBeenCalled();
      const message = console.log.mock.calls[0][0];
      expect(message).toMatch("abc");

      restoreConsole();
    });
  });
  describe("cellToAxiosParamsDelete", () => {
    test("It returns the correct params", () => {
      // arrange
      const cell = { row: { values: { id: 17 } } };

      // act
      const result = cellToAxiosParamsDelete(cell);

      // assert
      expect(result).toEqual({
        url: "/api/personalschedules",
        method: "DELETE",
        params: { id: 17 },
      });
    });
  });

  describe("schedulesFilter", () => {
    test("schedulesFilter", () => {
      // arrange
      const schedules = [
        { id: 1, quarter: "20241", name: "Schedule 1" },
        { id: 2, quarter: "20242", name: "Schedule 2" },
      ];

      //assert
      expect(schedulesFilter(schedules, "20241")).toEqual([
        { id: 1, quarter: "20241", name: "Schedule 1" },
      ]);
    });
  });

  describe("sectionToEvents", () => {
    test("converts a single class section into multiple calendar events", () => {
      // arrange: example personal‐section object
      const section = {
        courseId: "CMPSC 130A",
        title: "Data Structures and Algorithms",
        classSections: [
          {
            section: "0100",
            timeLocations: [
              { days: "MWF", beginTime: "09:00", endTime: "09:50" },
            ],
          },
        ],
      };

      // act
      const events = sectionToEvents(section);

      // assert
      expect(events).toHaveLength(3); // Monday, Wednesday, Friday
      expect(events).toEqual(
        expect.arrayContaining([
          expect.objectContaining({
            id: "CMPSC 130A-0100-M",
            title: "CMPSC 130A (0100)",
            day: "Monday",
            startTime: "9:00AM",
            endTime: "9:50AM",
          }),
          expect.objectContaining({ day: "Wednesday" }),
          expect.objectContaining({ day: "Friday" }),
        ]),
      );
    });
  });


  test("formats afternoon times with PM", () => {
    // arrange: section that starts after noon and contains whitespace in days/courseId
    const section = {
      courseId: "  MATH 4A ",
      title: "Calculus",
      classSections: [
        {
          section: "0200",
          timeLocations: [
            { days: " T ", beginTime: "13:30", endTime: "14:45" },
          ],
        },
      ],
    };

    // act
    const events = sectionToEvents(section);

    // assert: one event, day mapped correctly, start/end times end in PM
    expect(events).toHaveLength(1);
    expect(events[0]).toMatchObject({
      id: "MATH 4A-0200-T",
      title: "MATH 4A (0200)",
      day: "Tuesday",
      startTime: "1:30PM",
      endTime: "2:45PM",
    });
  });
});
