/*
 * This file renders the tree component
 */

import React, { useContext, useEffect, useState } from "react";

import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { PhylocanvasComponent } from "../../../components/PhylocanvasComponent"
import { Button, Layout, Radio } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getNewickTree } from "../../../apis/analysis/analysis";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { WarningAlert } from "../../../components/alerts/WarningAlert";
import styled from "styled-components";
import { SPACE_MD, SPACE_XS } from "../../../styles/spacing";
import { BORDERED_LIGHT } from "../../../styles/borders";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { grey1 } from "../../../styles/colors";

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

export default function Tree() {
  const [newickString, setNewickString] = useState(null);
  const [serverMsg, setServerMsg] = useState(null);
  const [currTreeShape, setCurrTreeShape] = useState("rectangular");
  const { analysisContext } = useContext(AnalysisContext);

  // On load gets the newick string for the analysis
  useEffect(() => {
    getNewickTree(analysisContext.analysis.identifier).then(data => {
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

  function getTree() {
    return (
      <PhylocanvasComponent
        data={newickString}
        treeType={currTreeShape}
        style={{ height: "100%" }}
      />
    );
  }

  function handleClick(e) {
    setCurrTreeShape(e.target.value);
  }

  /*
   * Returns the phylogenetic tree
   */
  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent title={i18n("AnalysisPhylogeneticTree.tree")}>
      {serverMsg !== null ? (
        <WarningAlert message={serverMsg} style={{ marginBottom: SPACE_XS }} />
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
                id="t-tree-shape-tools"
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
              <Button
                type="primary"
                href={`${URL}/${analysisContext.analysis.identifier}/advanced-phylo`}
                target="_blank"
                key="advphylo"
                id="t-advanced-phylo-btn"
              >
                {i18n("AnalysisPhylogeneticTree.viewAdvVisualization")}
              </Button>
            </ButtonGroupWrapper>
            <VisualizationWrapper id="t-phylocanvas-wrapper">
              {getTree()}
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
