import React from "react";

import { useStateValue } from "./GalaxyState";
import { Checkbox, Form, Input } from "antd";
import { actions } from "./reducer";
import { FONT_WEIGHT_HEAVY } from "../../styles/fonts";

/**
 * Component to display a form containing all required and user modifiable fields.
 * @returns {*}
 */
export function GalaxyDetailsForm() {
  const [
    {
      email,
      validEmail,
      makepairedcollection,
      includeAssemblies,
      includeFast5,
    },
    dispatch,
  ] = useStateValue();

  const emailModified = (e) => dispatch(actions.setEmail(e.target.value));

  const makePairedCollectionModified = (e) =>
    dispatch(actions.setMakePairedCollection(e.target.checked));

  const includeAssembliesModified = (e) =>
    dispatch(actions.setIncludeAssemblies(e.target.checked));

  const includeFast5Modified = (e) =>
    dispatch(actions.setIncludeFast5(e.target.checked));

  const galaxyUrl = window
    .decodeURI(window.GALAXY.URL)
    .split("/tool_runner")[0];
  return (
    <div>
      <p>
        <span style={{ fontWeight: FONT_WEIGHT_HEAVY }}>
          {i18n("ExportToGalaxyForm.galaxy")}
        </span>{" "}
        <a target="_blank" rel="noreferrer noopener" href={galaxyUrl}>
          {galaxyUrl}
        </a>
      </p>
      <Form layout="vertical" hideRequiredMark>
        <Form.Item
          label={i18n("ExportToGalaxyForm.email")}
          validateStatus={validEmail ? "success" : "error"}
          help={i18n("ExportToGalaxyForm.email.help")}
        >
          <Input onChange={emailModified} value={email} />
        </Form.Item>
        <Form.Item help={i18n("ExportToGalaxyForm.assemblies.help")}>
          <Checkbox
            onChange={includeAssembliesModified}
            checked={includeAssemblies}
          >
            {i18n("ExportToGalaxyForm.assemblies")}
          </Checkbox>
        </Form.Item>
        <Form.Item help={i18n("ExportToGalaxyForm.fast5.help")}>
          <Checkbox onChange={includeFast5Modified} checked={includeFast5}>
            {i18n("ExportToGalaxyForm.fast5")}
          </Checkbox>
        </Form.Item>
        <Form.Item help={i18n("ExportToGalaxyForm.makepairedcollection.help")}>
          <Checkbox
            onChange={makePairedCollectionModified}
            checked={makepairedcollection}
          >
            {i18n("ExportToGalaxyForm.makepairedcollection")}
          </Checkbox>
        </Form.Item>
      </Form>
    </div>
  );
}
