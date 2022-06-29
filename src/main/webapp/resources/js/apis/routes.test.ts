import { FAKE_CONTEXT_PATH } from "../../../setUpTests";
import {
  activities_project,
  activities_recent,
  activities_user,
} from "./routes";

test("Test activities routes", async () => {
  expect(activities_project(undefined, { projectId: `3`, page: `4` })).toBe(
    `${FAKE_CONTEXT_PATH}/ajax/activities/project?projectId=3&page=4`
  );
  expect(activities_recent(undefined, { page: `55` })).toBe(
    `${FAKE_CONTEXT_PATH}/ajax/activities/all?page=55`
  );
  expect(activities_user(undefined, { page: `12` })).toBe(
    `${FAKE_CONTEXT_PATH}/ajax/activities/user?page=12`
  );
});
