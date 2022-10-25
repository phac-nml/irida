/*
 * This file renders the Sistr Information component
 */

import React from "react";

import { Error } from "../../../../components/icons/Error";
import { Success } from "../../../../components/icons/Success";
import { Warning } from "../../../../components/icons/Warning";
import { BasicList } from "../../../../components/lists/BasicList";

import { TabPanelContent } from "../../../../components/tabs/TabPanelContent";

export default function SistrInfo({ sistrResults, sampleName }) {
  const { qc_status, qc_messages } = sistrResults;
  const sistrInfo = [
    {
      title: i18n("AnalysisSistr.sampleName"),
      desc: sampleName,
    },
    {
      title: i18n("AnalysisSistr.qualityControlStatus"),
      desc:
        qc_status === "PASS" ? (
          <span>
            <Success message={qc_status} />
          </span>
        ) : qc_status === "FAIL" ? (
          <span>
            <Error message={formatQcMessage(qc_status, qc_messages)} />
          </span>
        ) : (
          <span>
            <Warning message={formatQcMessage(qc_status, qc_messages)} />
          </span>
        ),
    },
    {
      title: i18n("AnalysisSistr.overallSerovar"),
      desc: sistrResults.serovar,
    },
    {
      title: i18n("AnalysisSistr.antigenSerovar"),
      desc: sistrResults.serovar_antigen,
    },
    {
      title: i18n("AnalysisSistr.cgmlstSerovar"),
      desc: sistrResults.serovar_cgmlst,
    },
    {
      title: i18n("AnalysisSistr.serogroup"),
      desc: sistrResults.serogroup,
    },
    {
      title: i18n("AnalysisSistr.h1"),
      desc: sistrResults.h1,
    },
    {
      title: i18n("AnalysisSistr.h2"),
      desc: sistrResults.h2,
    },
    {
      title: i18n("AnalysisSistr.oAntigen"),
      desc: sistrResults.o_antigen,
    },
  ];

  /*
   * Formats the QC status and QC messages to display
   * in a list.
   */
  function formatQcMessage(qc_status, qc_messages) {
    let msgs = [];
    if (qc_messages) {
      msgs = qc_messages.trim().split("|");
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

  /*
   * Returns a simple list which displays labels and values
   * for the sistr information
   */
  return (
    <TabPanelContent title={i18n("AnalysisSistr.sistrInformation")}>
      <BasicList dataSource={sistrInfo} />
    </TabPanelContent>
  );
}
