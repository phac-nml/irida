import { Menu } from "antd";
import React from "react";
import type { MenuProps } from "antd";
import { useNavigate } from "react-router-dom";

const menuItems: MenuProps["items"] = [
  {
    key: "pa:project-analyses",
    label: i18n("AnalysesNav.projectAnalyses"),
  },
  {
    key: "ps:shared-output",
    label: i18n("AnalysesNav.sharedOutputs"),
  },
  {
    key: "pa:automated-outputs",
    label: i18n("AnalysesNav.automatedOutputs"),
  },
];

/**
 * Component to handle navigation within the project analyses page
 * @param  path - the current page name
 * @constructor
 */
export default function AnalysesNav({ path }: { path: string }): JSX.Element {
  const navigate = useNavigate();

  const onClick: MenuProps["onClick"] = ({ key }) => {
    if (key === "pa:project-analyses") {
      navigate("project-analyses");
    } else if (key === "ps:shared-output") {
      navigate("shared-output");
    } else if (key === "pa:automated-outputs") {
      navigate("automated-outputs");
    } else {
      throw new Error("Unknown navigation endpoint");
    }
  };

  return (
    <Menu selectedKeys={[`pa:${path}`]} items={menuItems} onClick={onClick} />
  );
}
