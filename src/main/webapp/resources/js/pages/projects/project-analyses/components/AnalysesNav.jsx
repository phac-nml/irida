import { Link } from "@reach/router";
import { Menu } from "antd";
import React from "react";

/**
 * Component to handle navigation within the project analyses page
 * @param {string} path - the current page name
 * @returns {JSX.Element}
 * @constructor
 */
export default function AnalysesNav({ path }) {
  const [key, setKey] = React.useState();

  React.useEffect(() => {
    setKey(path.split("/")[0]);
  }, [path]);

  return (
    <Menu selectedKeys={[key]}>
      <Menu.Item key="project-analyses">
        <Link to="project-analyses">Project Analyses</Link>
      </Menu.Item>
      <Menu.Item key="shared-outputs">
        <Link to="shared-outputs" style={{ overflow: "auto" }}>
          Shared Single Sample Analysis Outputs
        </Link>
      </Menu.Item>
      <Menu.Item key="automated-outputs">
        <Link to="automated-outputs">
          Automated Single Sample Analysis Outputs
        </Link>
      </Menu.Item>
    </Menu>
  );
}
