/*
 * This file is responsible for displaying the
 * advanced phylogenomics viewer for the analysis.
 */

import { Layout, PageHeader, Skeleton } from "antd";
import React, { useContext, useEffect, useState } from "react";
import { ContentLoading } from "../../../components/loader";
import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getMetadata, getMetadataTemplates, getNewickTree } from "../../../apis/analysis/analysis";
import { grey1 } from "../../../styles/colors";
import { SPACE_XS } from "../../../styles/spacing";
import { PhylocanvasTreeComponent } from "./components/PhylocanvasTreeComponent";

export default function AdvancedPhylo() {
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const { analysisName, loading } = analysisContext;
  const [treeState, setTreeState] = useState(null)
  const [serverMsg, setServerMsg] = useState(null);

  // On load gets the newick string for the analysis
  useEffect(() => {
    if (analysisIdentifier) {
      const promises = [getNewickTree(analysisIdentifier), getMetadata(analysisIdentifier), getMetadataTemplates(analysisIdentifier)];

      Promise.all(promises).then(([newickData, metadataData, metadataTemplateData]) => {
        // Check for errors
        if (!newickData.newick) {
          setServerMsg(newickData.message ? newickData.message : newickData.error.message);
          return;
        }


        setTreeState({
          source: newickData.newick,
          metadata: metadataData.metadata,
          fields: metadataData.terms,
          templates: metadataTemplateData.templates,
        });

      });

    }
  }, [analysisIdentifier]);

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
          {serverMsg && (
            <WarningAlert
              message={serverMsg}
              style={{ marginBottom: SPACE_XS }}
            />
          )}
          {treeState !== null ? (
            treeState.source === "" ? (
              <WarningAlert
                message={i18n("AnalysisPhylogeneticTree.noPreviewAvailable")}
              />
            ) : (
              <PhylocanvasTreeComponent
                {...treeState}
              />
            )
          ) : (
            <ContentLoading/>
          )}
        </Layout.Content>
      </Skeleton>
    </Layout>
  );
}