import {
  formatDate,
  formatInternationalizedDateTime,
  fromNow,
  getDurationFromSeconds,
  getHumanizedDuration,
  isDate,
} from "./date-utilities";

const MILLISECOND_DATE_1 = 1590093988318;
const STRING_DATE_1 = "May 21, 2020, 3:46 PM";
const STRING_DATE_2 = "04 Dec 1995 00:12:00 GMT";
const SOME_RANDOM_STRING = "Some other string";

window.TL = {
  LANGUAGE_TAG: "en",
};

test("Should format to internationalized date and time", () => {
  expect(formatInternationalizedDateTime(MILLISECOND_DATE_1)).toBe(
    STRING_DATE_1
  );
});

test("Should format a date as instructed", () => {
  expect(formatDate({ date: MILLISECOND_DATE_1 })).toBe(STRING_DATE_1);
  expect(
    formatDate({
      date: MILLISECOND_DATE_1,
      format: {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
      },
    })
  ).toBe("Thursday, May 21, 2020");
  expect(formatDate({ date: SOME_RANDOM_STRING })).toBe("");
});

test("Test converting a duration to time", () => {
  expect(getHumanizedDuration({ date: 4 * 60 * 60 * 1000 })).toBe("4 hours");
  expect(getHumanizedDuration({ date: 30 * 60 * 1000 })).toBe("30 minutes");
  expect(getHumanizedDuration({ date: 0 })).toBe("a few seconds");
});

test("Should format the difference between 2 dates in human readable form", () => {
  expect(fromNow({ date: subtractHoursFromDate(4) })).toBe("4 hours ago");
  expect(fromNow({ date: subtractHoursFromDate(24) })).toBe("a day ago");
  expect(fromNow({ date: subtractHoursFromDate(-2) })).toBe("in 2 hours");
});

test("Should be able to determine if an input is a date or something else", () => {
  expect(isDate(MILLISECOND_DATE_1)).toBe(true);
  expect(isDate(STRING_DATE_1)).toBe(true);
  expect(isDate(STRING_DATE_2)).toBe(true);
  expect(isDate(SOME_RANDOM_STRING)).toBe(false);
});

test("", () => {
  expect(getDurationFromSeconds(604800)).toBe("7 days");
  expect(getDurationFromSeconds(2592000)).toBe("a month");
  expect(getDurationFromSeconds(2592000)).toBe("a month");
  expect(getDurationFromSeconds(7776000)).toBe("3 months");
  expect(getDurationFromSeconds(15552000)).toBe("6 months");
});

function subtractHoursFromDate(hours) {
  return new Date().getTime() - hours * 60 * 60 * 1000;
}
