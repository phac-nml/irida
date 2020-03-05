/*
 * This file renders the Sistr Information component
 */

import React from "react";

import { Error } from "../../../../components/icons/Error";
import { Success } from "../../../../components/icons/Success";
import { Warning } from "../../../../components/icons/Warning";
import { BasicList } from "../../../../components/lists/BasicList";

import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function SistrInfo({ sistrResults, sampleName }) {
  const { qc_status, qc_messages } = sistrResults;
  const sistrInfo = [
    {
      title: i18n("AnalysisSistr.sampleName"),
      desc: sampleName
    },
    {
      title: i18n("AnalysisSistr.qualityControlStatus"),
      desc:
        qc_status === "PASS" ? (
          <span>
            <Success
              message={qc_status}
            />
          </span>
        ) : qc_status === "FAIL" ? (
          <span>
            <Error
              message={
                formatQcMessage(
                  qc_status,
                  qc_messages
                )}
            />
          </span>
        ) : (
          <span>
            <Warning
              message={
                formatQcMessage(
                  qc_status,
                  qc_messages
                )}
            />
          </span>
        )
    }
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
            {msgs.map(msg => {
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
    <TabPaneContent title={i18n("AnalysisSistr.sistrInformation")}>
      <BasicList dataSource={sistrInfo} />
    </TabPaneContent>
  );
}
