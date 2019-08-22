import React, { useContext } from "react";
import { Tabs } from "antd";

//import analysis components required by page
import { AnalysisDetails } from "./AnalysisDetails";
import { AnalysisSteps } from "./AnalysisSteps";
import { AnalysisProvenance } from "./AnalysisProvenance";
import { AnalysisOutputFiles } from "./AnalysisOutputFiles";
import { AnalysisPhylogeneticTree } from "./AnalysisPhylogeneticTree";
import { AnalysisBioHansel } from "./AnalysisBioHansel";
import { AnalysisSistr } from "./AnalysisSistr";
import { AnalysisError } from "./AnalysisError";

import { AnalysisContext, AnalysisProvider } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsProvider } from "../../../contexts/AnalysisDetailsContext";

const TabPane = Tabs.TabPane;

const analysisTypesWithAdditionalPage = [
  "BIO_HANSEL",
  "SISTR_TYPING",
  "PHYLOGENOMICS",
  "MLST_MENTALIST"
];

export default function Analysis() {
  const { analysisContext, analysisContextUpdateSubmissionName } = useContext(AnalysisContext);

  return (
    <>
      <div
        style={{ marginLeft: "15px", marginRight: "15px", marginTop: "15px" }}
      >
        <h1>{analysisContext.analysisName}</h1>
        <div>
          <AnalysisSteps />
        </div>
        <Tabs
          defaultActiveKey={
            analysisContext.isError ||
            (analysisTypesWithAdditionalPage.indexOf(analysisContext.analysisType.type) > -1 &&
              analysisContext.isCompleted)
              ? "0"
              : "3"
          }
          animated={false}
        >
          {analysisContext.isCompleted ? (
            [
              analysisContext.analysisType.type === "BIO_HANSEL" ? (
                <TabPane tab="bio_hansel" key="0">
                  <AnalysisBioHansel />
                </TabPane>
              ) : null,

              analysisContext.analysisType.type === "SISTR_TYPING" ? (
                <TabPane tab="sistr" key="0">
                  <AnalysisSistr />
                </TabPane>
              ) : null,

              analysisContext.analysisType.type === "PHYLOGENOMICS" ||
              analysisContext.analysisType.type === "MLST_MENTALIST" ? (
                <TabPane tab="Phylogenetic Tree" key="0">
                  <AnalysisPhylogeneticTree />
                </TabPane>
              ) : null,

              <TabPane tab="Output Files" key="1">
                <AnalysisOutputFiles />
              </TabPane>,

              <TabPane tab="Provenance" key="2">
                <AnalysisProvenance />
              </TabPane>
            ]
          ) : analysisContext.isError ? (
            <TabPane tab="Job Error" key="0">
              <AnalysisError />
            </TabPane>
          ) : null}
          <TabPane tab="Settings" key="3">
            <AnalysisDetailsProvider>
                <AnalysisDetails />
            </AnalysisDetailsProvider>
          </TabPane>
        </Tabs>
      </div>
    </>
  );
}
