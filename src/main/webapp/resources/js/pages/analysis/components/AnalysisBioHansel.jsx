/**
 * @File component renders the bio hansel results.
 */

import React, { useContext, useEffect, useState } from "react";
import { Layout } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import {
  getBioHanselResults,
  getOutputInfo
} from "../../../apis/analysis/analysis";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { BasicList } from "../../../components/lists/BasicList";
import { Error } from "../../../components/icons/Error";
import { Success } from "../../../components/icons/Success";
import { Warning } from "../../../components/icons/Warning";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { getI18N } from "../../../utilities/i18n-utilities";
import { WarningAlert } from "../../../components/alerts/WarningAlert";

const { Content } = Layout;

export default function AnalysisBioHansel() {
  const { analysisContext } = useContext(AnalysisContext);
  const [bioHanselResults, setBioHanselResults] = useState(null);

  // On load gets the bio hansel results. If the file is not found then set to undefined
  useEffect(() => {
    getOutputInfo(analysisContext.analysis.identifier).then(data => {
      const outputInfo = data.find(output => {
        return output.filename === "bio_hansel-results.json";
      });

      if (outputInfo !== undefined) {
        getBioHanselResults({
          submissionId: analysisContext.analysis.identifier,
          fileId: outputInfo.id,
          seek: 0,
          chunk: outputInfo.fileSizeBytes
        }).then(data => {
          const { text } = data;
          const parsedResults = JSON.parse(text);
          setBioHanselResults(parsedResults[0]);
        });
      } else {
        setBioHanselResults(undefined);
      }
    });
  }, []);

  const biohanselResults = [
    {
      title: getI18N("AnalysisBioHansel.sampleName"),
      desc: bioHanselResults ? bioHanselResults.sample : ""
    },
    {
      title: `${getI18N("AnalysisBioHansel.schemeName")} (v${getI18N(
        "AnalysisBioHansel.schemeVersion"
      )})`,
      desc: bioHanselResults
        ? `${bioHanselResults.scheme} (${bioHanselResults.scheme_version})`
        : ""
    },
    {
      title: getI18N("AnalysisBioHansel.subtype"),
      desc: bioHanselResults ? bioHanselResults.subtype : ""
    },
    {
      title: getI18N("AnalysisBioHansel.averageTileFrequency"),
      desc: bioHanselResults
        ? bioHanselResults.avg_tile_coverage.toString()
        : ""
    },
    {
      title: getI18N("AnalysisBioHansel.qualityControlStatus"),
      desc: bioHanselResults ? (
        bioHanselResults.qc_status === "PASS" ? (
          <Success
            message={formatQcMessage(
              bioHanselResults.qc_status,
              bioHanselResults.qc_message
            )}
          />
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
      )
    }
  ];

  function formatQcMessage(qc_status, qc_message) {
    const msgs = qc_message.trim().split("|");

    return (
      <>
        <span>{qc_status}</span>
        <br />
        {msgs.length > 1 ? (
          <ul>
            {msgs.map(msg => {
              return <li key={msg}>{msg}</li>;
            })}
          </ul>
        ) : null}
      </>
    );
  }

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: "white" }}>
      <Content>
        {bioHanselResults === undefined ? (
          <WarningAlert message="Bio Hansel results are unavailable" />
        ) : bioHanselResults !== null ? (
          <TabPaneContent title={getI18N("AnalysisBioHansel.bioHansel")}>
            <BasicList dataSource={biohanselResults}></BasicList>
          </TabPaneContent>
        ) : (
          <ContentLoading />
        )}
      </Content>
    </Layout>
  );
}
