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
  const { qc_status } = sistrResults;
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
            <Success message={qc_status} />
          </span>
        ) : qc_status === "FAIL" ? (
          <span>
            <Error message={qc_status} />
          </span>
        ) : (
          <span>
            <Warning message={qc_status} />
          </span>
        )
    }
  ];

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
