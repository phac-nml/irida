import { Menu } from "antd";
import React from "react";
import { Link } from "react-router-dom";

/**
 * Component to handle navigation within the project analyses page
 * @param {string} path - the current page name
 * @returns {JSX.Element}
 * @constructor
 */
export default function AnalysesNav({ path: key }) {
  return (
    <Menu selectedKeys={[key]} items={[
      {
        key: "project-analyses",
        label: <Link to="project-analyses">{i18n("AnalysesNav.projectAnalyses")}</Link>
      },
      {
        key: "shared-outputs",
        label: <Link to="shared-outputs">{i18n("AnalysesNav.sharedOutputs")}</Link>
      },
      {
        key: "automated-outputs",
        label: <Link to="automated-outputs">{i18n("AnalysesNav.automatedOutputs")}</Link>
      }
    ]} />
  );
}
