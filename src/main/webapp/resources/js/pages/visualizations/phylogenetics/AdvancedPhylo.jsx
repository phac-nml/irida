/*
 * This file is responsible for displaying the
 * advanced phylogenomics viewer for the analysis.
 */

import { Layout, PageHeader, Skeleton } from "antd";
import React, { useContext, useEffect, useState } from "react";
import { ContentLoading } from "../../../components/loader";
import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getNewickTree } from "../../../apis/analysis/analysis";
import { grey1 } from "../../../styles/colors";
import { SPACE_XS } from "../../../styles/spacing";
import { PhylocanvasTreeComponent } from "./components/PhylocanvasTreeComponent";

export default function AdvancedPhylo() {
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const { analysisName, loading } = analysisContext;
  const [newickString, setNewickString] = useState(null);
  const [serverMsg, setServerMsg] = useState(null);

  // On load gets the newick string for the analysis
  useEffect(() => {
    if (analysisIdentifier) {
      getNewickTree(analysisIdentifier).then((data) => {
        console.log({"THIS ERRIR": data})
        if (data.newick === null) {
          //Empty tree
          setNewickString("");
        } else {
          setNewickString(data.newick);
        }

        if (data.message !== null) {
          setServerMsg(data.message);
        }

        if (data.error !== undefined) {
          setServerMsg(data.error.message);
        }
      });
    }
  }, [analysisIdentifier]);

  function getTree() {
    return (
      <PhylocanvasTreeComponent
        source={newickString}
      />
    )
  }

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
          {newickString !== null ? (
            newickString === "" ? (
              <WarningAlert
                message={i18n("AnalysisPhylogeneticTree.noPreviewAvailable")}
              />
            ) : (
              getTree()
            )
          ) : (
            <ContentLoading/>
          )}
        </Layout.Content>
      </Skeleton>
    </Layout>
  );
}