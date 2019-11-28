/*
 * This file renders the tree component
 */

import React, { useContext, useEffect, useState } from "react";
import { getI18N } from "../../../../utilities/i18n-utilities";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";
import Phylocanvas from "react-phylocanvas";
import { Button } from "antd";
import { AnalysisContext } from "../../../../contexts/AnalysisContext";
import { getNewickTree } from "../../../../apis/analysis/analysis";
import { ContentLoading } from "../../../../components/loader/ContentLoading";

const ButtonGroup = Button.Group;

export default function Tree() {
  const [newickString, setNewickString] = useState(null);
  const [currTreeShape, setCurrTreeShape] = useState("rectangular");
  const { analysisContext } = useContext(AnalysisContext);

  // On load gets the newick string for the analysis
  useEffect(() => {
    getNewickTree(analysisContext.analysis.identifier).then(data => {
      setNewickString(data);
    });
  }, []);

  function getTree() {
    return <Phylocanvas data={newickString} treeType={currTreeShape} />;
  }

  function handleClick(e) {
    setCurrTreeShape(e.target.value);
    getTree();
  }

  /*
   * Returns the phylogenetic tree
   */
  return (
    <TabPaneContent title={getI18N("AnalysisPhylogeneticTree.tree")}>
      {newickString !== null ? (
        <div>
          <ButtonGroup>
            <Button value="rectangular" onClick={e => handleClick(e)}>
              Rectangular
            </Button>
            <Button value="circular" onClick={e => handleClick(e)}>
              Circular
            </Button>
            <Button value="radial" onClick={e => handleClick(e)}>
              Radial
            </Button>
            <Button value="diagonal" onClick={e => handleClick(e)}>
              Diagonal
            </Button>
            <Button value="hierarchical" onClick={e => handleClick(e)}>
              Hierarchical
            </Button>
          </ButtonGroup>
          {getTree()}
        </div>
      ) : (
        <ContentLoading />
      )}
    </TabPaneContent>
  );
}
