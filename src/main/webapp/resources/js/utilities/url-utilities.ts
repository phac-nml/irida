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

/**
 * Get the project id from the url.
 * @param {string} url
 * @returns the project id or null if not found
 */
export function getProjectIdFromUrl(url = window.location.href) {
  const projectIdRegex = /\/projects\/(?<projectId>\d+)/;

  const found = url.match(projectIdRegex);
  if (found) {
    if (found.groups) {
      return found.groups.projectId;
    }
  }
}

/**
 * Get the context path for IRIDA.  This expects the body element to have a data attribute
 * of context (`data-context`) with the context path.
 */
export function getContextPath(): string {
  const element = document.querySelector("body");
  if (element && element.dataset && element.dataset.context) {
    return element.dataset.context;
  } else {
    console.error("The body element is missing the attribute `data-context`");
    return "/";
  }
}
