import { setBaseUrl } from "./url-utilities";
import { getCookieByName } from "./cookie-utilities";

// Check the current session to make sure it is correct
export async function checkSession() {
  const currentSession = getCookieByName("JSESSIONID");
  const prevSession = sessionStorage.getItem("JSESSIONID");
  if (currentSession !== prevSession) {
    fetch(setBaseUrl(`/ajax/session`))
      .then((response) => response.json())
      .then(({ locale, username, userid, userRole }) => {
        sessionStorage.setItem("JSESSIONID", currentSession);
        sessionStorage.setItem("locale", locale);
        sessionStorage.setItem("username", username);
        sessionStorage.setItem("userid", userid);
        sessionStorage.setItem("userRole", userRole);
      });
  }
}

export async function getSessionValue(term) {
  await checkSession();
  const value = sessionStorage.getItem(term);
  if (!term) {
    throw new Error(`Cannot find [${term}] in session storage.`);
  }
  return value;
}
