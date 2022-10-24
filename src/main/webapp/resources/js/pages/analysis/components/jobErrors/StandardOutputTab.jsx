/*
 * This file renders the 'Standard Output' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays the standard output
 * in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { StandardErrorOutput } from "./StandardErrorOutput";

import { TabPanelContent } from "../../../../components/tabs/TabPanelContent";

export default function StandardOutputTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors,
}) {
  return (
    <TabPanelContent title={i18n("AnalysisError.standardOutput")}>
      {galaxyJobErrors.length > 1 ? (
        <PassTabs
          tabName="standard-out"
          currActiveKey={currActiveKey}
          updateActiveKey={updateActiveKey}
          galaxyJobErrors={galaxyJobErrors}
        />
      ) : (
        <StandardErrorOutput galaxyError={galaxyJobErrors[0].standardOutput} />
      )}
    </TabPanelContent>
  );
}
