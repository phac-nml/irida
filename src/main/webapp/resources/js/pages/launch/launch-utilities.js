import { formatInternationalizedDateTime } from "../../utilities/date-utilities";

export function formatDefaultPipelineName(type, date) {
  return `${type.replace(" ", "_")}__${formatInternationalizedDateTime(date, {
    year: "numeric",
    month: "numeric",
    day: "numeric",
  }).replace(/\//g, "-")}`;
}

/**
 * Helper function to determine if the option should be rendered as a
 * checkbox because it is either true or false.
 * @param {array} options
 * @returns {boolean}
 */
export function isTruthy(options) {
  if (options.length > 2) return false;
  return (
    (typeof options[0].value === "boolean" &&
      typeof options[1].value === "boolean") ||
    options[0].value === "true" ||
    options[1].value === "true"
  );
}

export function formatParametersWithOptions(parameters) {
  return parameters.map((p) => {
    const parameter = { ...p };
    if (isTruthy(parameter.options)) {
      // Need to update to be actually boolean values
      parameter.type = "checkbox";
      parameter.options[0].value = parameter.options[0].value === "true";
      parameter.options[1].value = parameter.options[0].value === "true";
      parameter.value = parameter.value === "true";
    } else if (parameter.options.length < 7) {
      parameter.type = "radio";
    } else {
      parameter.type = "select";
    }
    return parameter;
  });
}
