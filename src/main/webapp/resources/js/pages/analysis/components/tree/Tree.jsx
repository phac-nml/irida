/*
 * This file renders the tree component
 */

import React, { useContext, useEffect, useState } from "react";
import { getI18N } from "../../../../utilities/i18n-utilities";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";
import Phylocanvas from "react-phylocanvas";
import { Button, Radio } from "antd";
import { AnalysisContext } from "../../../../contexts/AnalysisContext";
import { getNewickTree } from "../../../../apis/analysis/analysis";
import { ContentLoading } from "../../../../components/loader/ContentLoading";
import { WarningAlert } from "../../../../components/alerts/WarningAlert";
import styled from "styled-components";
import { SPACE_MD, SPACE_XS } from "../../../../styles/spacing";
import { BORDER_LIGHT } from "../../../../styles/borders";

const CANVAS_HEIGHT = 600;

const VisualizationWrapper = styled.div`
  height: ${CANVAS_HEIGHT}px;
  border: ${BORDER_LIGHT};
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
  const [currTreeShape, setCurrTreeShape] = useState("circular");
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
      <Phylocanvas
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
    <TabPaneContent title={getI18N("AnalysisPhylogeneticTree.tree")}>
      {serverMsg !== null ? (
        <WarningAlert message={serverMsg} style={{ marginBottom: SPACE_XS }} />
      ) : null}
      {newickString !== null ? (
        newickString === "" ? (
          <WarningAlert
            message={getI18N("AnalysisPhylogeneticTree.noPreviewAvailable")}
          />
        ) : (
          <div>
            <ButtonGroupWrapper>
              <Radio.Group value={currTreeShape} onChange={e => handleClick(e)}>
                <Radio.Button value="rectangular">
                  {getI18N("AnalysisPhylogeneticTree.rectangular")}
                </Radio.Button>
                <Radio.Button value="circular">
                  {getI18N("AnalysisPhylogeneticTree.circular")}
                </Radio.Button>
                <Radio.Button value="radial">
                  {getI18N("AnalysisPhylogeneticTree.radial")}
                </Radio.Button>
                <Radio.Button value="diagonal">
                  {getI18N("AnalysisPhylogeneticTree.diagonal")}
                </Radio.Button>
                <Radio.Button value="hierarchical">
                  {getI18N("AnalysisPhylogeneticTree.hierarchical")}
                </Radio.Button>
              </Radio.Group>
              <Button
                type="primary"
                href={`${window.TL.BASE_URL}analysis/${analysisContext.analysis.identifier}/advanced-phylo`}
                target="_blank"
                key="advphylo"
              >
                {getI18N("AnalysisPhylogeneticTree.viewAdvVisualization")}
              </Button>
            </ButtonGroupWrapper>
            <VisualizationWrapper>{getTree()}</VisualizationWrapper>
          </div>
        )
      ) : (
        <ContentLoading />
      )}
    </TabPaneContent>
  );
}
