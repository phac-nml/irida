import type { MenuProps } from "antd";
import { Menu } from "antd";
import React, { useContext, useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ANALYSIS } from "../routes";
import HorizontalMenu from "../../../components/ant.design/HorizontalMenu";

/**
 * React component to render the menu for the analysis
 * @returns {React.ReactElement}
 */
export default function AnalysisMenu({ type }: { type: string }) {
  const location = useLocation();
  const navigate = useNavigate();
  const [current, setCurrent] = React.useState("");
  const regex = /analysis\/\d+\/(?<path>\w+)/;
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const DEFAULT_URL = setBaseUrl(`/analysis/${analysisIdentifier}`);

  React.useEffect(() => {
    const found = location.pathname.match(regex);

    if (found && found.groups) {
      setCurrent(found.groups.path);
    } else {
      if (type === "output") {
        setCurrent("analysis:output");
      } else {
        setCurrent(`analysis:${type}`);
      }
    }
  }, [location.pathname, analysisContext.isCompleted]);

  const menuItems: MenuProps["items"] = useMemo(() => {
    return [
      ...(type === "sistr"
        ? [
            {
              key: "analysis:sistr",
              label: i18n("Analysis.sistr"),
            },
          ]
        : []),
      ...(type === "biohansel"
        ? [
            {
              key: "analysis:biohansel",
              label: i18n("Analysis.biohansel"),
            },
          ]
        : []),
      ...(type === "tree" && analysisContext.treeDefault
        ? [
            {
              key: "analysis:tree",
              label: i18n("Analysis.phylogeneticTree"),
            },
          ]
        : []),
      {
        key: "analysis:output",
        label: i18n("Analysis.outputFiles"),
      },
      {
        key: "analysis:provenance",
        label: i18n("Analysis.provenance"),
      },
      {
        key: "analysis:settings",
        label: i18n("Analysis.settings"),
      },
    ];
  }, [analysisContext.treeDefault, type]);

  const onClick: MenuProps["onClick"] = ({ key }) => {
    if (key === "analysis:sistr") {
      navigate(`${DEFAULT_URL}/${ANALYSIS.SISTR}`);
    } else if (key === "analysis:biohansel") {
      navigate(`${DEFAULT_URL}/${ANALYSIS.BIOHANSEL}`);
    } else if (key === "analysis:tree") {
      navigate(`${DEFAULT_URL}/${ANALYSIS.TREE}`);
    } else if (key === "analysis:output") {
      navigate(`${DEFAULT_URL}/${ANALYSIS.OUTPUT}`);
    } else if (key === "analysis:provenance") {
      navigate(`${DEFAULT_URL}/${ANALYSIS.PROVENANCE}`);
    } else if (key === "analysis:settings") {
      navigate(`${DEFAULT_URL}/${ANALYSIS.SETTINGS}/`);
    } else {
      throw new Error(`Cannot find route: ${key}`);
    }
  };

  return (
    <HorizontalMenu
      className="t-analysis-menu"
      items={menuItems}
      onClick={onClick}
      selectedKeys={[current]}
    />
  );
}
