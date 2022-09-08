/*
 * This file renders the tree component
 */

import React, { useContext, useEffect, useState } from "react";

import { TabPaneContent } from "../../../components/tabs";
import { PhylocanvasComponent } from "../../../components/PhylocanvasComponent";
import { Layout, Radio } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getNewickTree } from "../../../apis/analysis/analysis";
import { ContentLoading } from "../../../components/loader";
import { WarningAlert } from "../../../components/alerts";
import styled from "styled-components";
import { SPACE_MD, SPACE_XS } from "../../../styles/spacing";
import { BORDERED_LIGHT } from "../../../styles/borders";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { grey1 } from "../../../styles/colors";
import { ANALYSIS } from "../routes";
import { Link } from "react-router-dom";

const CANVAS_HEIGHT = 600;
const URL = setBaseUrl("analysis");

const VisualizationWrapper = styled.div`
  height: ${CANVAS_HEIGHT}px;
  border: ${BORDERED_LIGHT};
`;

const ButtonGroupWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${SPACE_MD};
`;

export default function AnalysisPhylogeneticTree() {
  const [newickString, setNewickString] = useState(null);
  const [serverMsg, setServerMsg] = useState(null);
  const [currTreeShape, setCurrTreeShape] = useState("rectangular");
  const { analysisIdentifier } = useContext(AnalysisContext);

  // On load gets the newick string for the analysis
  useEffect(() => {
    getNewickTree(analysisIdentifier).then((data) => {
      if (data.newick === null) {
        //Empty tree
        setNewickString("");
      } else {
        setNewickString(data.newick);
      }

      if (data.message !== null) {
        setServerMsg(data.message);
      }
    });
  }, []);

  function handleClick(e) {
    setCurrTreeShape(e.target.value);
  }

  /*
   * Returns the phylogenetic tree
   */
  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent
        title={i18n("AnalysisPhylogeneticTree.tree")}
        xl={24}
        xxl={24}
      >
        {serverMsg !== null ? (
          <WarningAlert
            message={serverMsg}
            style={{ marginBottom: SPACE_XS }}
          />
        ) : null}
        {newickString !== null ? (
          newickString === "" ? (
            <WarningAlert
              message={i18n("AnalysisPhylogeneticTree.noPreviewAvailable")}
            />
          ) : (
            <div>
              <ButtonGroupWrapper>
                <Radio.Group
                  value={currTreeShape}
                  onChange={handleClick}
                  className="t-tree-shape-tools"
                >
                  <Radio.Button value="rectangular">
                    {i18n("AnalysisPhylogeneticTree.rectangular")}
                  </Radio.Button>
                  <Radio.Button value="circular">
                    {i18n("AnalysisPhylogeneticTree.circular")}
                  </Radio.Button>
                  <Radio.Button value="radial">
                    {i18n("AnalysisPhylogeneticTree.radial")}
                  </Radio.Button>
                  <Radio.Button value="diagonal">
                    {i18n("AnalysisPhylogeneticTree.diagonal")}
                  </Radio.Button>
                  <Radio.Button value="hierarchical">
                    {i18n("AnalysisPhylogeneticTree.hierarchical")}
                  </Radio.Button>
                </Radio.Group>
                <Link
                  to={`${URL}/${analysisIdentifier}/${ANALYSIS.ADVANCED_PHYLO}`}
                  key="advphylo"
                  className="ant-btn ant-btn-primary t-advanced-phylo-btn"
                >
                  {i18n("AnalysisPhylogeneticTree.viewAdvVisualization")}
                </Link>
              </ButtonGroupWrapper>
              <VisualizationWrapper className="t-phylocanvas-wrapper">
                <PhylocanvasComponent
                  data={newickString}
                  treeType={currTreeShape}
                  style={{ height: "100%" }}
                />
              </VisualizationWrapper>
            </div>
          )
        ) : (
          <ContentLoading />
        )}
      </TabPaneContent>
    </Layout>
  );
}
