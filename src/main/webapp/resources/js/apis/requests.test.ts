import axios from "axios";
import { get } from "./requests";

jest.mock("axios");
const mockedAxios = axios as jest.MockedFunction<typeof axios>;

test(`Fetches successfully`, async () => {
  const data = { foo: `bar` };
  mockedAxios.get.mockResolvedValueOnce({
    data,
    status: 200,
    statusText: "Ok",
    headers: {},
    config: {},
  });

  const response = await get("/foobar");
  expect(axios.get).toHaveBeenCalledWith("/foobar");
  expect(response).toEqual(data);
});
