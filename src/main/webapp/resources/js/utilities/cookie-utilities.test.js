import { getCookieByName } from "./cookie-utilities";

beforeEach(() => {
  Object.defineProperty(window.document, "cookie", {
    writable: true,
    value: "fred=johnson;locale=en",
  });
});

test("Should be able to handle an empty name", () => {
  expect(getCookieByName("")).toBe(undefined);
});

test("Should be able to handle non-existing cookies", () => {
  expect(getCookieByName("barney")).toBe(undefined);
});

test("Should be get different values from the cookies", () => {
  expect(getCookieByName("fred")).toBe("johnson");
  expect(getCookieByName("locale")).toBe("en");
});
