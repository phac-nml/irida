import test from "ava";
import {formatDate, formatTimeForNow, getHumanizedDuration} from "./date-utilities";

const currentTime = 1498678028321;

test("Testing creating a humanized duration", t => {
  t.is("a day", getHumanizedDuration({date: 86400000}));
  t.is("18 hours", getHumanizedDuration({date: 64800000}));
});

test("Formatting dates", t => {
  t.is("June 28, 2017 2:27 PM", formatDate({date: currentTime}));
});

test("Creating the time from now", t => {
  t.is("17 days ago", formatTimeForNow({now: currentTime, date: 1497179364693}));
});
