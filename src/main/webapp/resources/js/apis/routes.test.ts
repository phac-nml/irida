import { activitiesRoute } from "./routes";

test("Should add projectId to the string", async () => {
  expect(activitiesRoute()).toBe(`/ajax/activities`);
});
