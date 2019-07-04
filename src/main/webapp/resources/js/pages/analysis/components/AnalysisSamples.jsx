import React from "react";
import { Button, Collapse, Icon } from "antd";
import { getI18N } from "../../../utilities/i18n-utilties";

const Panel = Collapse.Panel;

export default function AnalysisSamples() {
  return (
    <>
      <h2 style={{ fontWeight: "bold" }}>{getI18N("analysis.tab.samples")}</h2>
      <br />
      <br />
    </>
  );
}
