import { formatInternationalizedDateTime } from "../../utilities/date-utilities";

/*
IRIDA Workflow identifier can be found as a query parameter within the URL.
Here we grab it and hold onto it so that we can use it to gather all the
details about the pipeline.
 */
export const PIPELINE_ID = (() => {
  const params = new URLSearchParams(window.location.search);
  return params.get("id");
})();

/**
 * Format the name of a pipeline to be unique.  The user can modify it at any point.
 *
 * @param type the type of pipeline
 * @param date the current date in milliseconds
 * @returns {string}
 */
export function formatDefaultPipelineName(type, date) {
  return `${type.replace(" ", "_")}__${formatInternationalizedDateTime(date, {
    year: "numeric",
    month: "numeric",
    day: "numeric",
  }).replace(/\//g, "-")}`;
}

export function formatSavedParameterSets(sets = []) {
  return sets.map((set) => ({ ...set, key: `set-${set.id}` }));
}

/**
 * Use to copy objects and arrays, breaking any references.
 *
 * @param {array|object} object - item to make a deep copy of
 * @returns {any}
 */
export function deepCopy(object) {
  return JSON.parse(JSON.stringify(object));
}
