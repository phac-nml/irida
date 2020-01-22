import React, { useEffect, useState } from "react";
import { Tag, Tooltip } from "antd";
import { fetchAnalysesQueueCounts } from "./../apis/analysis/analysis";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faRunning } from "@fortawesome/free-solid-svg-icons";
import { SPACE_XS } from "../styles/spacing";
import styled from "styled-components";
import { formatNumber } from "../utilities/number-utilities";
import { blue4 } from "../styles/colors";

const LabelTD = styled.td`
  font-weight: bold;
`;

const ValueTD = styled.td`
  width: 50px;
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
    <Tooltip title={i18n("AnalysesQueue.title")} placement={"left"}>
      <Tag color={"blue"}>
        <div
          style={{
            width: 150,
            display: "flex",
            justifyContent: "space-between",
            alignContent: "center"
          }}
        >
          <FontAwesomeIcon
            icon={faRunning}
            size="2x"
            fixedWidth
            style={{
              flex: 1,
              display: "inline-block",
              margin: `${SPACE_XS} 0`
            }}
          />

          <table>
            <tbody>
              <tr style={{ borderBottom: `1px solid ${blue4}` }}>
                <LabelTD>{i18n("AnalysesQueue.running")}</LabelTD>
                <ValueTD className="t-running-counts">
                  {formatNumber(running)}
                </ValueTD>
              </tr>
              <tr>
                <LabelTD>{i18n("AnalysesQueue.queued")}</LabelTD>
                <ValueTD className="t-queue-counts">
                  {formatNumber(queued)}
                </ValueTD>
              </tr>
            </tbody>
          </table>
        </div>
      </Tag>
    </Tooltip>
  );
}
