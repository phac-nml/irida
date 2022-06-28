import { FAKE_CONTEXT_PATH } from "../../../setUpTests";
import { projectSamplesUrl } from "./routes";

test("Should add projectId to the string", async () => {
  const expected = `${FAKE_CONTEXT_PATH}/projects/4`;
  expect(projectSamplesUrl({ projectId: 4 })).toBe(expected);
});
