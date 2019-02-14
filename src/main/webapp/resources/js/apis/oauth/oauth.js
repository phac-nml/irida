const createHref = (clientId, redirectUrl) =>
  `${
    window.TL.BASE_URL
  }api/oauth/authorize?client_id=${clientId}&response_type=code&scope=read&redirect_uri=${redirectUrl}`;

/**
 * Create a DOMString containing a comma-separated list of window features for the pop up window.
 * @returns {string}
 */
const getWindowFeatures = () => {
  /*
  Center the opened window within the current browser.
   */
  const height = 400;
  const width = 400;
  const left = window.innerWidth / 2 - width / 2;
  const top = window.innerHeight / 2 - height / 2;

  return `top=${top},left=${left},width=${width},height=${height},scrollbars=no,status=1`;
};

export async function authenticateOauthClient(clientId, redirectUrl) {
  const href = createHref(clientId, redirectUrl);
  const options = getWindowFeatures();

  // Create a promise since we do not know how long the user will have the window open for.
  return new Promise((resolve, reject) => {
    // Create a global handler to accept the oath code.  This code is in
    // this is called from the galaxy_auth_code.tmpl.html page after authorization
    // is granted.
    window.handleGalaxyCode = code => resolve(code);
    window.handleGalaxyError = () => reject();

    window.open(href, clientId, options);
  });
}
