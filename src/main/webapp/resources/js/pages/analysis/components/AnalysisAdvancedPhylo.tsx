/*
 * This file is responsible for displaying the
 * advanced phylogenomics viewer for the analysis.
 */

import { TreeTypes } from "@phylocanvas/phylocanvas.gl";
import { Button, Layout, PageHeader } from "antd";
import React, { useContext, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { WarningAlert } from "../../../components/alerts";
import { ContentLoading } from "../../../components/loader";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { SPACE_MD, SPACE_XS } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";
import {
  fetchTreeAndMetadata,
  LoadingState,
  updateTreeType,
} from "../redux/treeSlice";
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
    border: 1px solid var(--grey-4);
  }
`;

export default function AnalysisAdvancedPhylo(): JSX.Element {
  const { analysisIdentifier } = useContext(AnalysisContext);
  const { error, loadingState } = useSelector((state) => state.tree.state);
  const dispatch = useDispatch();

  // On load gets the newick string for the analysis
  useEffect(() => {
    dispatch(fetchTreeAndMetadata(analysisIdentifier));
  }, [analysisIdentifier, dispatch]);

  let content = null;
  switch (loadingState) {
    case LoadingState["error-loading"]:
      content = (
        <WarningAlert message={error} style={{ marginBottom: SPACE_XS }} />
      );
      break;
    case LoadingState.empty:
      content = (
        <WarningAlert
          message={i18n("AnalysisPhylogeneticTree.noPreviewAvailable")}
        />
      );
      break;
    case LoadingState.complete:
      console.log("LOADING LAYOUT");
      content = <LayoutComponent />;
      break;
    default:
      content = <ContentLoading />;
  }

  return <Header title="Tree Viewer">{content}</Header>;
}
