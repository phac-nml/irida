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
  const [current, setCurrent] = React.useState(() => {
    if (type !== "output") return type;
    const regex = /analysis\/\d+\/(?<path>\w+)/;
    const found = location.pathname.match(regex);
    if (found) {
      return found.groups.path;
    }
    return "output";
  });

  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const DEFAULT_URL = setBaseUrl(`/analysis/${analysisIdentifier}`);
  const handleMenu = (e) => setCurrent(e.key);

  return (
    <Menu selectedKeys={[current]} mode="horizontal" onClick={handleMenu}>
      {type === "sistr" && (
        <Item key="sistr">
          <Link to={DEFAULT_URL}>{i18n("Analysis.sistr")}</Link>
        </Item>
      )}
      {type === "biohansel" && (
        <Item key="biohansel">
          <Link to={DEFAULT_URL}>{i18n("Analysis.biohansel")}</Link>
        </Item>
      )}
      {type === "tree" && analysisContext.treeDefault && (
        <Item key="tree">
          <Link to={DEFAULT_URL}>{i18n("Analysis.phylogeneticTree")}</Link>
        </Item>
      )}
      {type === "output" ? (
        <Item key="output">
          <Link to={DEFAULT_URL}>{i18n("Analysis.outputFiles")}</Link>
        </Item>
      ) : (
        <Item key="output">
          <Link to={`${DEFAULT_URL}/${ANALYSIS.OUTPUT}`}>
            {i18n("Analysis.outputFiles")}
          </Link>
        </Item>
      )}
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
