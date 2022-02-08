import { Menu } from "antd";
import React, { useContext } from "react";
import { Link, useLocation } from "react-router-dom";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ANALYSIS } from "../routes";

const { Item } = Menu;

/**
 * React component to render the menu for the analysis
 * @returns {React.ReactElement}
 */
export default function AnalysisMenu({ type }) {
  const location = useLocation();
  const [current, setCurrent] = React.useState("");
  const regex = /analysis\/\d+\/(?<path>\w+)/;
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const DEFAULT_URL = setBaseUrl(`/analysis/${analysisIdentifier}`);
  const handleMenu = (e) => setCurrent(e.key);

  React.useEffect(() => {
    const found = location.pathname.match(regex);

    if (found) {
      setCurrent(found.groups.path);
    } else {
      if (type === "output") {
        setCurrent("output");
      } else {
        setCurrent(type);
      }
    }
  }, [location.pathname, analysisContext.isCompleted]);

  return (
    <Menu
      selectedKeys={[current]}
      mode="horizontal"
      onClick={handleMenu}
      className="t-analysis-menu"
    >
      {type === "sistr" && (
        <Item key="sistr">
          <Link to={`${DEFAULT_URL}/${ANALYSIS.SISTR}`}>
            {i18n("Analysis.sistr")}
          </Link>
        </Item>
      )}
      {type === "biohansel" && (
        <Item key="biohansel">
          <Link to={`${DEFAULT_URL}/${ANALYSIS.BIOHANSEL}`}>
            {i18n("Analysis.biohansel")}
          </Link>
        </Item>
      )}
      {type === "tree" && analysisContext.treeDefault && (
        <Item key="tree">
          <Link to={`${DEFAULT_URL}/${ANALYSIS.TREE}`}>
            {i18n("Analysis.phylogeneticTree")}
          </Link>
        </Item>
      )}
      <Item key="output">
        <Link to={`${DEFAULT_URL}/${ANALYSIS.OUTPUT}`}>
          {i18n("Analysis.outputFiles")}
        </Link>
      </Item>
      <Item key="provenance">
        <Link to={`${DEFAULT_URL}/${ANALYSIS.PROVENANCE}`}>
          {i18n("Analysis.provenance")}
        </Link>
      </Item>
      <Item key="settings">
        <Link to={`${DEFAULT_URL}/${ANALYSIS.SETTINGS}/`}>
          {i18n("Analysis.settings")}
        </Link>
      </Item>
    </Menu>
  );
}
