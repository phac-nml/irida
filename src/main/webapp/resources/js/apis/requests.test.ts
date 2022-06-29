import axios from "axios";
import { get } from "./requests";

jest.mock("axios");

test(`Fetches successfully`, async () => {
  const data = { foo: `bar` };
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  axios.get.mockImplementation(() => Promise.resolve({ data }));

  const response = await get("/foobar");
  expect(axios.get).toHaveBeenCalledWith("/foobar");
  expect(response).toEqual(data);
});
