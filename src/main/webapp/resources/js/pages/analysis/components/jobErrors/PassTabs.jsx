/*
 * This file renders the `Pass` tabs if there are
 * multiple job errors.
 */

import React from "react";
import { Tabs } from "antd";
import { GalaxyJobInfo } from "./GalaxyJobInfo";
import { GalaxyParameters } from "./GalaxyParameters";
import { StandardError } from "./StandardError";
import { StandardOutput } from "./StandardOutput";

const TabPane = Tabs.TabPane;

export function PassTabs({ tabName, galaxyJobErrors, galaxyUrl }) {
  /* This function will display 'Pass' tabs within the tabpanes if there is
   * more than one error is returned for an analysis.
   */
  function showPassTabs(tabName) {
    // Since we only need the jobError indexes to generate the tabs we iterate '
    // over the keys instead of the whole galaxyJobErrors object
    return (
      <Tabs animated={false}>
        {Object.keys(galaxyJobErrors).map(key => {
          const index = parseInt(key);
          if (tabName === "galaxy-parameters") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <GalaxyParameters
                  galaxyJobErrors={galaxyJobErrors}
                  currIndex={index}
                />
              </TabPane>
            );
          } else if (tabName === "job-error-info") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <GalaxyJobInfo
                  galaxyJobErrors={galaxyJobErrors}
                  galaxyUrl={galaxyUrl}
                  currIndex={index}
                />
              </TabPane>
            );
          } else if (tabName === "standard-error") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <StandardError
                  galaxyJobErrors={galaxyJobErrors}
                  currIndex={index}
                />
              </TabPane>
            );
          } else if (tabName === "standard-out") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <StandardOutput
                  galaxyJobErrors={galaxyJobErrors}
                  currIndex={index}
                />
              </TabPane>
            );
          }
        })}
      </Tabs>
    );
  }

  return <>{showPassTabs(tabName)}</>;
}
