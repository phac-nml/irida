import {formatDate, formatTimeForNow, getHumanizedDuration} from "./date-utilities";

const currentTime = 1498678028321;

test('Testing creating a humanized duration', () => {
  expect(getHumanizedDuration({date: 86400000})).toBe("a day");
  expect(getHumanizedDuration({date: 64800000})).toBe("18 hours");
});

test('Formatting dates', () => {
  expect(formatDate({date: currentTime})).toBe("June 28, 2017 2:27 PM");
});

test('Creating the time from now', () => {
  expect(formatTimeForNow({now: currentTime, date: 1497179364693})).toBe("17 days ago");
});
