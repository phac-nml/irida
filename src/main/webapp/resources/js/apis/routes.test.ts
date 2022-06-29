import { FAKE_CONTEXT_PATH } from "../../../setUpTests";
import {
  activities_project_route,
  activities_recent_route,
  activities_user_route,
} from "./routes";

test("Test activities routes", async () => {
  expect(
    activities_project_route({ queryParams: { projectId: `3`, page: `4` } })
  ).toBe(`${FAKE_CONTEXT_PATH}/ajax/activities/project?projectId=3&page=4`);
  expect(activities_recent_route({ queryParams: { page: `55` } })).toBe(
    `${FAKE_CONTEXT_PATH}/ajax/activities/all?page=55`
  );
  expect(activities_user_route({ queryParams: { page: `12` } })).toBe(
    `${FAKE_CONTEXT_PATH}/ajax/activities/user?page=12`
  );
});
