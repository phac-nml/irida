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
  if (
    (BASE_URL !== "/" && url.startsWith(BASE_URL)) ||
    url.startsWith("http")
  ) {
    return url;
  }
  /*
  Create the new url and remove the possibility of any "//"
   */
  return `${BASE_URL}${url}`.replace(/\/{2}/g, "/");
}
