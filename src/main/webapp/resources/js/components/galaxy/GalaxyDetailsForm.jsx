import React from "react";
import { useStateValue } from "./GalaxyState";
import { Checkbox, Form, Input } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { actions } from "./reducer";
import { FONT_WEIGHT_HEAVY } from "../../styles/fonts";

/**
 * Component to display a form containing all required and user modifiable fields.
 * @param {string} email - email for galaxy
 * @param  {boolean} makepairedcollection - whether to organize data into a library
 * @param {function} updateEmail - handles updates to the email address
 * @param {function} updateMakePairedCollection - handles update to makepairedcollection
 * @returns {*}
 */
export function GalaxyDetailsForm() {
  const [{ email, validEmail, makepairedcollection }, dispatch] = useStateValue();

  const emailModified = e => dispatch(actions.setEmail(e.target.value));

  const makePairedCollectionModified = e =>
    updateMakePairedCollection(e.target.checked);


  const galaxyUrl = window
    .decodeURI(window.GALAXY.URL)
    .split("/tool_runner")[0];
  return (
    <div>
      <p>
        <span style={{ fontWeight: FONT_WEIGHT_HEAVY }}>
          {getI18N("ExportToGalaxyForm.galaxy")}
        </span>{" "}
        <a target="_blank" rel="noreferrer noopener" href={galaxyUrl}>
          {galaxyUrl}
        </a>
      </p>
      <Form layout="vertical" hideRequiredMark>
        <Form.Item
          label={getI18N("ExportToGalaxyForm.email")}
          validateStatus={validEmail ? "success" : "error"}
          help={getI18N("ExportToGalaxyForm.email.help")}
        >
          <Input onChange={emailModified} value={email} />
        </Form.Item>
        <Form.Item
          help={getI18N("ExportToGalaxyForm.makepairedcollection.help")}
        >
          <Checkbox
            onChange={makePairedCollectionModified}
            checked={makepairedcollection}
          >
            {getI18N("ExportToGalaxyForm.makepairedcollection")}
          </Checkbox>
        </Form.Item>
      </Form>
    </div>
  );
}
