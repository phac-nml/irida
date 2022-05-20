/*
 * This file is responsible for displaying the
 * advanced phylogenomics viewer for the analysis.
 */

import { Layout, PageHeader, Skeleton } from "antd";
import React, { useContext, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { ContentLoading } from "../../../components/loader";
import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { grey1 } from "../../../styles/colors";
import { SPACE_XS } from "../../../styles/spacing";
import { PhylocanvasTree } from "./components/PhylocanvasTree";
import { fetchTreeAndMetadata } from "./redux/treeSlice";

export default function AdvancedPhylo() {
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const { analysisName, loading } = analysisContext;
  const { error, fetching, treeProps } = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  // On load gets the newick string for the analysis
  useEffect(() => {
    if (analysisIdentifier) {
      dispatch(fetchTreeAndMetadata(analysisIdentifier));
    }
  }, [analysisIdentifier, dispatch]);

  return (
    <Layout style={{ height: `100%`, width: `100%` }}>
      <Skeleton loading={loading} active>
        <PageHeader title={i18n("visualization.phylogenomics.title", analysisName)}/>
        <Layout.Content
          style={{
            margin: 24,
            marginTop: 0,
            backgroundColor: grey1,
            display: "flex",
          }}
        >
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
      </Skeleton>
    </Layout>
  );
}