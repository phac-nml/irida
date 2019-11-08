/*
 * This file returns a list of the `Galaxy Job Information` for
 * a given index. If there is no index provided then just the
 * information from the first index in the galaxyJobErrors object
 * is returned.
 */

import React from "react";
import { Button, Typography } from "antd";
import { Monospace } from "../../../../components/typography";
import { getI18N } from "../../../../utilities/i18n-utilities";
import { formatDate } from "../../../../utilities/date-utilities";
import { BasicList } from "../../../../components/lists/BasicList";

const { Text } = Typography;

export function GalaxyJobInfo({ galaxyJobErrors, galaxyUrl, currIndex }) {
  // Returns the galaxy job details for the given index
  function galaxyJobDetails(index) {
    let jobError = galaxyJobErrors[index];
    return [
      {
        title: getI18N("AnalysisError.createdDate"),
        desc: formatDate({
          date: jobError.createdDate
        })
      },
      {
        title: getI18N("AnalysisError.updatedDate"),
        desc: formatDate({
          date: jobError.updatedDate
        })
      },
      {
        title: getI18N("AnalysisError.commandLine"),
        desc: <Text code>{jobError.commandLine.trim()}</Text>
      },
      {
        title: getI18N("AnalysisError.exitCode"),
        desc: <Monospace>{jobError.exitCode}</Monospace>
      },
      {
        title: getI18N("AnalysisError.toolId"),
        desc: jobError.toolId
      },
      {
        title: getI18N("AnalysisError.toolName"),
        desc: jobError.toolName
      },
      {
        title: getI18N("AnalysisError.toolVersion"),
        desc: <Monospace>{jobError.toolVersion}</Monospace>
      },
      {
        title: getI18N("AnalysisError.toolDescription"),
        desc: jobError.toolDescription
      },
      {
        title: getI18N("AnalysisError.provenanceId"),
        desc: <Monospace>{jobError.provenanceId}</Monospace>
      },
      {
        title: getI18N("AnalysisError.provenanceUUID"),
        desc: <Monospace>{jobError.provenanceUUID}</Monospace>
      },
      {
        title: getI18N("AnalysisError.historyId"),
        desc: (
          <Button
            type="link"
            style={{ paddingLeft: 0 }}
            href={`${galaxyUrl}/histories/view?id=${jobError.historyId}`}
            target="_blank"
          >
            <Monospace>{jobError.historyId}</Monospace>
          </Button>
        )
      },
      {
        title: getI18N("AnalysisError.jobId"),
        desc: <Monospace>{jobError.jobId}</Monospace>
      },
      {
        title: getI18N("AnalysisError.identifier"),
        desc: <Monospace>{jobError.identifier}</Monospace>
      }
    ];
  }

  /* Returns a list of the galaxy job information for the provided index
   * which is pulled from the galaxyJobDetails function above
   */
  function getGalaxyJobInfo(index = 0) {
    return <BasicList dataSource={galaxyJobDetails(index)} />;
  }

  return <>{getGalaxyJobInfo(currIndex)}</>;
}
