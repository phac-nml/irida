/*
 * This file is responsible for displaying the
 * advanced phylogenomics viewer for the analysis.
 */

import { TreeTypes } from "@phylocanvas/phylocanvas.gl";
import { Layout, PageHeader } from "antd";
import React, { useContext, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { WarningAlert } from "../../../components/alerts";
import { ContentLoading } from "../../../components/loader";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { SPACE_MD, SPACE_XS } from "../../../styles/spacing";
import { fetchTreeAndMetadata, updateTreeType } from "../redux/treeSlice";
import LayoutComponent from "./phylocanvas/LayoutComponent";
import { PhylocanvasTree } from "./phylocanvas/PhylocanvasTree";
import styled from "styled-components";

const Header = styled(PageHeader)`
  height: 100%;
  width: 100%;
  position: relative;
  .ant-page-header-content {
    left: 0;
    top: 70px;
    right: 0;
    bottom: 0;
    position: absolute;
  }
`;

export default function AnalysisAdvancedPhylo(): JSX.Element {
  const { analysisIdentifier } = useContext(AnalysisContext);
  const { error, fetching, treeProps } = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  // On load gets the newick string for the analysis
  useEffect(() => {
    dispatch(fetchTreeAndMetadata(analysisIdentifier));
  }, [analysisIdentifier, dispatch]);

  return (
    <Header
      title="Tree Viewer"
      extra={[
        <button
          key="changer"
          onClick={() =>
            dispatch(updateTreeType({ treeType: TreeTypes.Radial }))
          }
        >
          CHANGE
        </button>,
      ]}
    >
      {error && (
        <WarningAlert message={error} style={{ marginBottom: SPACE_XS }} />
      )}
      {!fetching ? (
        treeProps.source === "" ? (
          <WarningAlert
            message={i18n("AnalysisPhylogeneticTree.noPreviewAvailable")}
          />
        ) : (
          <LayoutComponent />
        )
      ) : (
        <ContentLoading />
      )}
    </Header>
  );
}
