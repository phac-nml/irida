import React from "react";
import { InputWithOptions } from "../../components/form/InputWithOptions";

/**
 * React Component to display workflow "parameters with options", this would include
 * any pipeline specific parameters that have multiple options, not just fill in the
 * blank.
 * <ul>
 *   <li>If the context has assigned the parameter as "truthy" then it is render as a checkbox</li>
 *   <li>Else if it has less then 7 options, it is rendered as regular radio group</li>
 *   <li>Else it will be rendered as a select dropdown</li>
 * </ul>
 *
 * @param {array} parameters - list of all parameters that have options.
 * @returns {*}
 * @constructor
 */
export function LaunchParametersWithOptions({ parameters }) {
  const [params] = React.useState(() => {
    function isTruthy(options) {
      if (options.length > 2) return false;
      return (
        (typeof options[0].value === "boolean" &&
          typeof options[1].value === "boolean") ||
        options[0].value === "true" ||
        options[1].value === "true"
      );
    }
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
  });

  const content = params.map((parameter) => (
    <InputWithOptions key={parameter.name} item={parameter} />
  ));

  return <section>{content}</section>;
}
