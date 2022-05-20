/*
 * This file is responsible for displaying the
 * advanced phylogenomics viewer for the analysis.
 */

import { Layout, PageHeader } from "antd";
import React, { useContext, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { ContentLoading } from "../../../components/loader";
import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { grey1 } from "../../../styles/colors";
import { SPACE_MD, SPACE_XS } from "../../../styles/spacing";
import { PhylocanvasTree } from "./phylocanvas/PhylocanvasTree";
import { fetchTreeAndMetadata } from "../redux/treeSlice";

export default function AnalysisAdvancedPhylo() {
  const { analysisIdentifier } = useContext(AnalysisContext);
  const { error, fetching, treeProps } = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  // On load gets the newick string for the analysis
  useEffect(() => {
    if (analysisIdentifier) {
      dispatch(fetchTreeAndMetadata(analysisIdentifier));
    }
  }, [analysisIdentifier, dispatch]);

  return (
    <Layout
      style={{
        height: `100%`,
        width: `100%`,
        backgroundColor: grey1,
        paddingLeft: SPACE_MD
      }}
    >
      <Layout.Content
        style={{
          margin: 0,
          backgroundColor: grey1,
          display: "flex",
          flexDirection: "column"
        }}
      >
        <PageHeader
          style={{ padding: 0, paddingBottom: SPACE_MD, flex: "0 1 auto" }}
          title="Tree Viewer"
        />
        {error && (
          <WarningAlert
            message={error}
            style={{ marginBottom: SPACE_XS }}
          />
        )}
        {!fetching ? (
          treeProps.source === "" ? (
            <WarningAlert
              message={i18n("AnalysisPhylogeneticTree.noPreviewAvailable")}
            />
          ) : (
            <PhylocanvasTree/>
          )
        ) : (
          <ContentLoading/>
        )}
      </Layout.Content>
    </Layout>
  );
}