import React, { useEffect, useState } from "react";
import { Tag, Tooltip } from "antd";
import { fetchAnalysesQueueCounts } from "./../apis/analysis/analysis";
import { SPACE_XS } from "../styles/spacing";
import styled from "styled-components";
import { formatNumber } from "../utilities/number-utilities";
import { blue4 } from "../styles/colors";
import { SyncOutlined } from "@ant-design/icons";

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
      <Tag color={"blue"}>
        <div
          style={{
            width: 150,
            display: "flex",
            justifyContent: "space-between",
            alignContent: "center"
          }}
        >
          <SyncOutlined
            spin
            style={{
              fontSize: 20,
              display: "inline-block",
              margin: `10px ${SPACE_XS}`
            }}
          />

          <div style={{ display: "flex", flexDirection: "column", flex: 1 }}>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <Label>{i18n("AnalysesQueue.running")}</Label>
              <Value className="t-running-counts">
                {formatNumber(running)}
              </Value>
            </div>
            <div
              style={{
                height: 1,
                borderTop: `1px solid ${blue4}`,
                width: "100%"
              }}
            />
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <Label>{i18n("AnalysesQueue.queued")}</Label>
              <Value className="t-queue-counts">{formatNumber(queued)}</Value>
            </div>
          </div>
        </div>
      </Tag>
    </Tooltip>
  );
}
