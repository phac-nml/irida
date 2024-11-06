/*
 * This file returns a list of the `Galaxy Job Information` for
 * a given index. If there is no index provided then just the
 * information from the first index in the galaxyJobErrors object
 * is returned.
 */

import React, { useContext } from "react";
import { Button, Typography } from "antd";
import { Monospace } from "../../../../components/typography";

import { formatDate } from "../../../../utilities/date-utilities";
import { BasicList } from "../../../../components/lists/BasicList";
import { AnalysisContext } from "../../../../contexts/AnalysisContext";

const { Text } = Typography;

export function GalaxyJobInfo({ galaxyJobErrors, galaxyUrl, currIndex }) {
  const { analysisContext } = useContext(AnalysisContext);

  // Returns the galaxy job details for the given index
  function galaxyJobDetails(index) {
    let jobError = galaxyJobErrors[index];
    return [
      {
        title: i18n("AnalysisError.createdDate"),
        desc: formatDate({
          date: jobError.createdDate,
        }),
      },
      {
        title: i18n("AnalysisError.updatedDate"),
        desc: formatDate({
          date: jobError.updatedDate,
        }),
      },
      {
        title: i18n("AnalysisError.commandLine"),
        desc: <Text code>{jobError.commandLine.trim()}</Text>,
      },
      {
        title: i18n("AnalysisError.exitCode"),
        desc: <Monospace>{jobError.exitCode}</Monospace>,
      },
      {
        title: i18n("AnalysisError.toolId"),
        desc: jobError.toolId,
      },
      {
        title: i18n("AnalysisError.toolName"),
        desc: jobError.toolName,
      },
      {
        title: i18n("AnalysisError.toolVersion"),
        desc: <Monospace>{jobError.toolVersion}</Monospace>,
      },
      {
        title: i18n("AnalysisError.toolDescription"),
        desc: jobError.toolDescription,
      },
      {
        title: i18n("AnalysisError.provenanceId"),
        desc: <Monospace>{jobError.provenanceId}</Monospace>,
      },
      {
        title: i18n("AnalysisError.provenanceUUID"),
        desc: <Monospace>{jobError.provenanceUUID}</Monospace>,
      },
      {
        title: i18n("AnalysisError.historyId"),
        desc: analysisContext.isAdmin ? (
          <Button
            type="link"
            style={{ paddingLeft: 0 }}
            href={`${galaxyUrl}/histories/view?id=${jobError.historyId}`}
            target="_blank"
          >
            <Monospace>{jobError.historyId}</Monospace>
          </Button>
        ) : (
          <Monospace>{jobError.historyId}</Monospace>
        ),
      },
      {
        title: i18n("AnalysisError.jobId"),
        desc: <Monospace>{jobError.jobId}</Monospace>,
      },
      {
        title: i18n("AnalysisError.identifier"),
        desc: <Monospace>{jobError.identifier}</Monospace>,
      },
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
