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

  /*
  On the cart.html page, there is a form with the name "js-galaxy-form" that
  get populated with the action (galaxy url).  It has a hidden input (#js-query)
  that we set the stringified version of the galaxy parameters to.  After updating
  the value, we submit the form, which will redirect us back to Galaxy.
   */
  const form = document.forms["js-galaxy-form"];
  if (typeof form === "undefined") {
    throw new Error(`Expecting the galaxy form with name "js-galaxy-form"`);
  } else {
    const input = form.elements["js-query"];
    input.value = JSON.stringify(params);
    form.submit();
  }
};
