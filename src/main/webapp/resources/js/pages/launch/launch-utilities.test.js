import { formatDefaultPipelineName } from "./launch-utilities";

test("Should format the pipeline name based on its type and date", () => {
  expect(formatDefaultPipelineName("bio_hansel", 1590093988318)).toBe(
    "bio_hansel__5-21-2020"
  );
});
