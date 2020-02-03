/**
 * Since IRIDA can be served within a container, all requests need to have
 * the correct base url.  This will add, if required, the base url.
 *
 * NOTE: THIS ONLY NEEDS TO BE CALLED FOR LINKS, ASYNCHRONOUS REQUESTS WILL
 * BE AUTOMATICALLY HANDLED.
 *
 * @param {string} url
 * @return {string|*}
 */
export function setBaseUrl(url) {
  /*
  Get the base url which is set via the thymeleaf template engine.
   */
  const BASE_URL = window.TL?._BASE_URL || "/";

  /*
  Check to make sure that the given url has not already been given the
  base url.
   */
  if (url.startsWith(BASE_URL) || url.startsWith("http")) {
    return url;
  }

  // Remove any leading slashes
  url = url.replace(/^\/+/, "");

  /*
  Create the new url
   */
  return `${BASE_URL}${url}`;
}
