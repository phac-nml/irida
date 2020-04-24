import React, { useEffect, useState } from "react";
import { Alert, Tooltip } from "antd";
import { fetchAnalysesQueueCounts } from "./../apis/analysis/analysis";
import { SPACE_XS } from "../styles/spacing";
import styled from "styled-components";
import { formatNumber } from "../utilities/number-utilities";
import { blue4, blue6 } from "../styles/colors";
import { IconCloudServer } from "./icons/Icons";

const Label = styled.span`
  font-weight: bold;
`;

const Value = styled.span`
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
      <Alert
        style={{ padding: 0 }}
        type="info"
        message={
          <div
            style={{
              width: 160,
              display: "flex",
              alignContent: "center",
              color: blue6
            }}
          >
            <IconCloudServer
              style={{ fontSize: "2em", flex: 1, padding: SPACE_XS }}
            />
            <div
              style={{
                display: "inline-block",
                width: 130,
                marginRight: SPACE_XS
              }}
            >
              <div
                style={{
                  borderBottom: `1px solid ${blue4}`,
                  display: "flex",
                  justifyContent: "space-between"
                }}
              >
                <Label>{i18n("AnalysesQueue.running")}</Label>
                <Value className="t-running-counts">
                  {formatNumber(running)}
                </Value>
              </div>
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between"
                }}
              >
                <Label>{i18n("AnalysesQueue.queued")}</Label>
                <Value className="t-queue-counts">{formatNumber(queued)}</Value>
              </div>
            </div>
          </div>
        }
      />
    </Tooltip>
  );
}
