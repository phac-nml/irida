/**
 * Parse the url for Metadata Template Details.  This allows for getting the
 * current url, current template id and the current path in the application
 * @param href
 * @returns {(string|*|number)[]}
 */
import { setBaseUrl } from "../../utilities/url-utilities";

export function parseMetadataTemplateUrl(href) {
  let [, url, templateId, path] = href.match(
    /(projects\/\d+\/metadata-templates\/(\d+))\/?(\w+)?/
  );

  /*
  The root path to this application does not actually have a sub-path
  We named it details, so if it is not set, set it :)
   */
  path = path || "details";

  return [setBaseUrl(url), Number.parseInt(templateId), path];
}
