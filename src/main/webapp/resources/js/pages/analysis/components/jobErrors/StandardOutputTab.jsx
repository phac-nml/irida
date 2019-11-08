/*
 * This file renders the 'Standard Output' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays the standard output
 * in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { StandardOutput } from "./StandardOutput";
import { getI18N } from "../../../../utilities/i18n-utilities";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function StandardOutputTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors
}) {
  return (
    <TabPaneContent title={getI18N("AnalysisError.standardOutput")}>
      {galaxyJobErrors.length > 1 ? (
        <PassTabs
          tabName="standard-out"
          currActiveKey={currActiveKey}
          updateActiveKey={updateActiveKey}
          galaxyJobErrors={galaxyJobErrors}
        />
      ) : (
        <StandardOutput galaxyJobErrors={galaxyJobErrors} />
      )}
    </TabPaneContent>
  );
}
