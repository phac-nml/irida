import { Menu } from "antd";
import React, { useContext } from "react";
import { Link, useLocation } from "react-router-dom";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ANALYSIS } from "../routes";

const { Item } = Menu;

const pathRegx = /\/analysis\/\d+\/(?<path>[a-zA-Z_0-9]+)/;

/*
 * The following code sets the key which should
 * be highlighted by default on page load if the
 * analysis has completed. If an analysis is a type
 * of sistr, bio_hansel, phylogenomics, or mentalist,
 * then a special view is add as the default,
 * otherwise the output files tab key is set.
 * If the analysis is not completed and not errored
 * the the settings tab is the default key. If the
 * job has errored then the error tab key is set as
 * the default.
 */
const getDefaultKey = ({ isCompleted, treeDefault, analysisViewer, isError }) =>
  isCompleted
    ? analysisViewer === "sistr"
      ? ANALYSIS.SISTR
      : analysisViewer === "biohansel"
      ? ANALYSIS.BIOHANSEL
      : analysisViewer === "tree" && treeDefault
      ? ANALYSIS.TREE
      : ANALYSIS.OUTPUT
    : isError
    ? ANALYSIS.ERROR
    : ANALYSIS.SETTINGS;

/**
 * React component to render the menu for the analysis
 * @returns {React.ReactElement}
 */
export default function AnalysisMenu() {
  const location = useLocation();
  const [current, setCurrent] = React.useState();
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const DEFAULT_URL = setBaseUrl(`/analysis/${analysisIdentifier}`);

  const type = analysisContext.analysisViewer;

  React.useEffect(() => {
    const found = location.pathname.match(pathRegx);
    const path = found ? found.groups.path : getDefaultKey(analysisContext);
    setCurrent(path);
  }, [location.pathname]);

  return (
    <Menu selectedKeys={[current]} mode="horizontal">
      {analysisContext.isError && (
        <Item key="error">
          <Link to={`${DEFAULT_URL}/${ANALYSIS.ERROR}/`}>
            {i18n("Analysis.jobError")}
          </Link>
        </Item>
      )}
      {analysisContext.isCompleted && (
        <>
          {type === "sistr" && (
            <Item key="sistr">
              <Link to={`${DEFAULT_URL}/${ANALYSIS.SISTR}/`}>
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
        </>
      )}
      <Item key="settings">
        <Link to={`${DEFAULT_URL}/${ANALYSIS.SETTINGS}/details`}>
          {i18n("Analysis.settings")}
        </Link>
      </Item>
    </Menu>
  );
}
