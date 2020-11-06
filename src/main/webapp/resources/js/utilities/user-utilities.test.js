import {
  getSystemRole,
  getUserIdentifier,
  getUserLocale,
  getUsername,
  isUserAdmin,
} from "./user-utilities";

beforeEach(() => {
  Object.defineProperty(window.document, "cookie", {
    writable: true,
    value: "username=barney; locale=en; user_id=222; system_role=ROLE_ADMIN",
  });
});

test("Should get the correct user information from the document cookies", () => {
  expect(getUserLocale()).toBe("en");
  expect(getUsername()).toBe("barney");
  expect(getUserIdentifier()).toBe(222);
  expect(getSystemRole()).toBe("ROLE_ADMIN");
  expect(isUserAdmin()).toBe(true);
});
