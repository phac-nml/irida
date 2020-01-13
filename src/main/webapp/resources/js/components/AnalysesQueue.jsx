import React, { useEffect, useState } from "react";
import { Tag } from "antd";
import { fetchAnalysesQueueCounts } from "./../apis/analysis/analysis";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faRunning } from "@fortawesome/free-solid-svg-icons";
import { SPACE_XS } from "../styles/spacing";
import styled from "styled-components";

const LabelTD = styled.td`
  font-weight: bold;
`;

const ValueTD = styled.td`
  width: 40px;
  text-align: right;
  font-family: monospace;
`;

/**
 * React component for rendering the current server status
 * for running analyses.
 * @return {*}
 * @constructor
 */
export function AnalysesQueue({}) {
  const [running, setRunning] = useState(0);
  const [queued, setQueued] = useState(0);

  useEffect(() => {
    fetchAnalysesQueueCounts().then(data => {
      setRunning(data.running);
      setQueued(data.queued);
    });
  }, [fetchAnalysesQueueCounts]);

  return (
    <Tag color={"blue"}>
      <div
        style={{
          width: 140,
          display: "flex",
          justifyContent: "space-between",
          alignContent: "center"
        }}
      >
        <FontAwesomeIcon
          icon={faRunning}
          size="2x"
          fixedWidth
          style={{ flex: 1, display: "inline-block", margin: `${SPACE_XS} 0` }}
        />

        <table>
          <tbody>
            <tr>
              <LabelTD>{i18n("AnalysesQueue.running")}</LabelTD>
              <ValueTD>{running}</ValueTD>
            </tr>
            <tr>
              <LabelTD>{i18n("AnalysesQueue.queued")}</LabelTD>
              <ValueTD>{queued}</ValueTD>
            </tr>
          </tbody>
        </table>
      </div>
    </Tag>
  );
}
