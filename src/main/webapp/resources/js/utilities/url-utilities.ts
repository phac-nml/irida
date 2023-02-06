declare let window: IridaWindow;

/**
 * Since IRIDA can be served within a container, all requests need to have
 * the correct base path.
 * This adds, if required, the base path.
 *
 * NOTE: THIS ONLY NEEDS TO BE CALLED FOR LINKS, ASYNCHRONOUS REQUESTS WILL
 * BE AUTOMATICALLY HANDLED.
 *
 * @param path
 * @return {string|*}
 */
export function setBaseUrl(path: string) {
  /*
    Get the base path which is set via the thymeleaf template engine.
     */
  const BASE_URL: string = window.TL?.BASE_URL || "/";

  /*
    Check to make sure that the given path has not already been given the
    base path.
     */
  if (path.startsWith(BASE_URL) || path.startsWith("http")) {
    return path;
  }

  /*
    Create the new path
     */
  return `${BASE_URL}${path.replace(/^\/+/, "")}`;
}

/**
 * Get the project id from the URL.
 * @param url
 * @returns the project id or throws error
 */
export function getProjectIdFromUrl(url = window.location.href) {
  const projectIdRegex = /\/projects\/(?<projectId>\d+)/;

  const found = url.match(projectIdRegex);
  if (found) {
    if (found.groups) {
      return found.groups.projectId;
    }
  }
  throw new Error("Could not find project id in url");
}
