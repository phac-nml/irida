/**
 * @File component renders the bio hansel results.
 */

import React, { useContext, useEffect, useState } from "react";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getDataViaChunks } from "../../../apis/analysis/analysis";
import { AnalysisOutputsContext } from "../../../contexts/AnalysisOutputsContext";

import { TabPanelContent } from "../../../components/tabs";
import { Error, Success } from "../../../components/icons";
import { Warning } from "../../../components/icons/Warning";
import { BasicList } from "../../../components/lists";
import ScrollableSection from "./ScrollableSection";

export default function AnalysisBioHansel() {
  const { analysisIdentifier } = useContext(AnalysisContext);
  const { analysisOutputsContext, getAnalysisOutputs } = useContext(
    AnalysisOutputsContext
  );
  const [bioHanselResults, setBioHanselResults] = useState(null);

  // On load gets the bio hansel results. If the file is not found then set to undefined
  useEffect(() => {
    if (analysisOutputsContext.outputs === null) {
      getAnalysisOutputs();
    }
  }, []);

  useEffect(() => {
    getBioHanselResults();
  }, [analysisOutputsContext.outputs]);

  function getBioHanselResults() {
    if (analysisOutputsContext.outputs !== null) {
      const outputInfo = analysisOutputsContext.outputs.find((output) => {
        return output.filename === "bio_hansel-results.json";
      });

      if (outputInfo !== undefined) {
        getDataViaChunks({
          submissionId: analysisIdentifier,
          fileId: outputInfo.id,
          seek: 0,
          chunk: outputInfo.fileSizeBytes,
        }).then(({ text }) => {
          const parsedResults = JSON.parse(text);
          setBioHanselResults(parsedResults[0]);
        });
      } else {
        setBioHanselResults(undefined);
      }
    }
  }

  const biohanselResults = [
    {
      title: i18n("AnalysisBioHansel.sampleName"),
      desc: bioHanselResults ? bioHanselResults.sample : "",
    },
    {
      title: `${i18n("AnalysisBioHansel.schemeName")} (v${i18n(
        "AnalysisBioHansel.schemeVersion"
      )})`,
      desc: bioHanselResults
        ? `${bioHanselResults.scheme} (${bioHanselResults.scheme_version})`
        : "",
    },
    {
      title: i18n("AnalysisBioHansel.subtype"),
      desc: bioHanselResults ? bioHanselResults.subtype : "",
    },
    {
      title: i18n("AnalysisBioHansel.averageTileFrequency"),
      desc:
        bioHanselResults && bioHanselResults.avg_tile_coverage
          ? bioHanselResults.avg_tile_coverage.toString()
          : "",
    },
    {
      title: i18n("AnalysisBioHansel.qualityControlStatus"),
      desc: bioHanselResults ? (
        bioHanselResults.qc_status === "PASS" ? (
          <Success message={bioHanselResults.qc_status} />
        ) : bioHanselResults.qc_status === "FAIL" ? (
          <Error
            message={formatQcMessage(
              bioHanselResults.qc_status,
              bioHanselResults.qc_message
            )}
          />
        ) : (
          <Warning
            message={formatQcMessage(
              bioHanselResults.qc_status,
              bioHanselResults.qc_message
            )}
          />
        )
      ) : (
        ""
      ),
    },
  ];

  /*
   * Formats the QC status and QC messages to display
   * in a list.
   */
  function formatQcMessage(qc_status, qc_message) {
    let msgs = [];
    if (qc_message) {
      msgs = qc_message.trim().split("|");
    }

    return (
      <>
        <span>{qc_status}</span>
        <br />
        {msgs.length > 0 ? (
          <ul>
            {msgs.map((msg) => {
              return <li key={msg}>{msg}</li>;
            })}
          </ul>
        ) : null}
      </>
    );
  }

  return (
    <ScrollableSection>
      <TabPanelContent title={i18n("AnalysisBioHansel.bioHanselInformation")}>
        <BasicList dataSource={biohanselResults}></BasicList>
      </TabPanelContent>
    </ScrollableSection>
  );
}
