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
  const popupHeight = 400;
  const popupWidth = 400;

  // Fixes dual-screen position Most browsers Firefox
  const dualScreenLeft =
    typeof window.screenLeft != "undefined"
      ? window.screenLeft
      : window.screenX;
  const dualScreenTop =
    typeof window.screenTop != "undefined" ? window.screenTop : window.screenY;

  const width = window.innerWidth
    ? window.innerWidth
    : document.documentElement.clientWidth
    ? document.documentElement.clientWidth
    : screen.width;
  const height = window.innerHeight
    ? window.innerHeight
    : document.documentElement.clientHeight
    ? document.documentElement.clientHeight
    : screen.height;

  const left = (width - popupWidth) / 2 + dualScreenLeft;
  const top = (height - popupHeight) / 2 + dualScreenTop;

  return `top=${top},left=${left},width=${popupHeight},height=${popupHeight},scrollbars=no,status=1`;
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
    window.handleGalaxyError = () => reject("ERROR");
    window.handleClosing = () => reject("CLOSED");

    window.open(href, clientId, options);
  });
}
