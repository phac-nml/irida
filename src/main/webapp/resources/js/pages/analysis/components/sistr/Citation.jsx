/*
 * This file renders the citation component for SISTR
 */

import React from "react";
import { getI18N } from "../../../../utilities/i18n-utilties";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function Citation() {
  /*
   * Returns the citation for the SISTR workflow
   */
  return (
    <TabPaneContent title={getI18N("AnalysisSistr.mash")}>
      <a href="https://doi.org/10.1371/journal.pone.0147101">
        {getI18N("AnalysisSistr.citationLinkText")}
        <cite>{getI18N("AnalysisSistr.plosOne")}</cite>
      </a>
    </TabPaneContent>
  );
}
