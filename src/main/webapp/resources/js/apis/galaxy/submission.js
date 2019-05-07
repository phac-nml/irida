/**
 * Exporting samples to galaxy occurs via a form post.  This function creates
 * the structure of data that galaxy is expecting and turns it into a JSON
 * string that is put into an input on the form.  The form is then submitted,
 * which will redirect the user to the galaxy instance.
 * @param {string} email - user's galaxy email address
 * @param {boolean} makepairedcollection - if true, it organizes imported data by putting it into paired collections
 * @param {string} oauthCode - Oauth2 authentication code
 * @param {string} oauthRedirect - galaxy oauth redirect
 * @param {object} samples - sample links in format required for galaxy.
 */
export const exportToGalaxy = ({
  email,
  makepairedcollection,
  oauthCode,
  oauthRedirect,
  samples
}) => {
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
  will get populated with the action (galaxy url).  It has a hidden input (#js-query)
  that we set the stringified version of the galaxy parameters to.  After updating
  the value, the form is submitted, which will redirect us back to Galaxy.
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
