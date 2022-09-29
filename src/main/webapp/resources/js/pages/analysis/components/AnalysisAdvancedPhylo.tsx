/*
 * This file is responsible for displaying the
 * advanced phylogenomics viewer for the analysis.
 */

import React, { useContext, useEffect } from "react";
import { WarningAlert } from "../../../components/alerts";
import { ContentLoading } from "../../../components/loader";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { SPACE_XS } from "../../../styles/spacing";
import { fetchTreeAndMetadataThunk, LoadingState } from "../redux/treeSlice";
import { useAppDispatch, useAppSelector } from "../store";
import LayoutComponent from "./phylocanvas/LayoutComponent";
import styled from "styled-components";
import { PageHeader } from "antd";

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
  const { error, loadingState } = useAppSelector((state) => state.tree.state);
  const dispatch = useAppDispatch();

  // On load gets the newick string for the analysis
  useEffect(() => {
    if (analysisIdentifier) {
      dispatch(fetchTreeAndMetadataThunk(analysisIdentifier));
    }
  }, [analysisIdentifier, dispatch]);

  let content;
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
      content = <LayoutComponent />;
      break;
    default:
      content = <ContentLoading />;
  }

  return <Header title="Tree Viewer">{content}</Header>;
}
