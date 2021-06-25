import { Link } from "@reach/router";
import { Menu } from "antd";
import React from "react";

/**
 * Component to handle navigation within the project analyses page
 * @param {string} path - the current page name
 * @returns {JSX.Element}
 * @constructor
 */
export default function AnalysesNav({ path: key }) {
  return (
    <Menu mode="horizontal" theme="dark" selectedKeys={[key]}>
      <Menu.Item key="project-analyses">
        <Link to="project-analyses">{i18n("AnalysesNav.projectAnalyses")}</Link>
      </Menu.Item>
      <Menu.Item key="shared-outputs">
        <Link to="shared-outputs">{i18n("AnalysesNav.sharedOutputs")}</Link>
      </Menu.Item>
      <Menu.Item key="automated-outputs">
        <Link to="automated-outputs">
          {i18n("AnalysesNav.automatedOutputs")}
        </Link>
      </Menu.Item>
    </Menu>
  );
}
