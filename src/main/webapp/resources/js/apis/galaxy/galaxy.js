import axios from "axios";

export const getGalaxyClientAuthentication = clientId =>
  axios
    .get(
      `${window.TL.BASE_URL}ajax/galaxy-export/authorized?clientId=${clientId}`
    )
    .then(({ data }) => data);

export const getGalaxySamples = () =>
  axios
    .get(`${window.TL.BASE_URL}ajax/galaxy-export/samples`)
    .then(({ data }) => data);

export const exportToGalaxy = (
  email,
  makepairedcollection,
  oauthCode,
  oauthRedirect,
  samples
) => {
  const name = `IRIDA-${Date.now()}`;

  /*
  This structure is expected by galaxy.
   */
  const params = {
    _embedded: {
      library: { name },
      user: { email },
      addtohistory: true, // Default according to Phil Mabon
      makepairedcollection,
      oauth2: {
        code: oauthCode,
        redirect: oauthRedirect
      },
      samples
    }
  };

  const form = document.forms["js-galaxy-form"];
  if (typeof form === "undefined") {
    throw new Error(`Expecting the galaxy form with name "js-galaxy-form"`);
  } else {
    const input = form.elements["js-query"];
    input.value = JSON.stringify(params);
    form.submit();
  }
};
