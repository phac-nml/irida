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
  const content = parameters.map((parameter) => (
    <InputWithOptions
      key={parameter.name}
      item={parameter}
      initialValue={parameters[0].value}
    />
  ));
  return <section>{content}</section>;
}
