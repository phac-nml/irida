/*
 * This file renders the 'Standard Output' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays the standard output
 * in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { StandardErrorOutput } from "./StandardErrorOutput";

import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function StandardOutputTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors
}) {
  return (
    <TabPaneContent title={i18n("AnalysisError.standardOutput")}>
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
    </TabPaneContent>
  );
}
