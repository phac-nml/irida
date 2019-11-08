/*
 * This file renders the Sistr Information component
 */

import React from "react";

import { Success } from "../../../../components/icons/Success";
import { BasicList } from "../../../../components/lists/BasicList";
import { getI18N } from "../../../../utilities/i18n-utilities";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function SistrInfo({ sistrResults, sampleName }) {
  const { qc_status } = sistrResults;
  const sistrInfo = [
    {
      title: getI18N("AnalysisSistr.sampleName"),
      desc: sampleName
    },
    {
      title: getI18N("AnalysisSistr.qualityControlStatus"),
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
    <TabPaneContent title={getI18N("AnalysisSistr.sistrInformation")}>
      <BasicList dataSource={sistrInfo} />
    </TabPaneContent>
  );
}
