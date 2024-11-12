import React, { useEffect, useState } from "react";
import { Alert, notification, Tooltip } from "antd";
import { fetchAnalysesQueueCounts } from "./../apis/analysis/analysis";
import { SPACE_XS } from "../styles/spacing";
import styled from "styled-components";
import { formatNumber } from "../utilities/number-utilities";
import { blue6 } from "../styles/colors";
import { IconCloudServer } from "./icons/Icons";
import { useInterval } from "../hooks";

const Label = styled.span`
  font-weight: bold;
`;

const Value = styled.span`
  width: 50px;
  text-align: right;
  font-family: monospace;
  border-bottom: none;
  background-color: transparent;
`;

const UPDATE_QUEUE_COUNT_DELAY = 60000;

/**
 * React component for rendering the current server status
 * for running analyses.
 * @return {*}
 * @constructor
 */
export function AnalysesQueue() {
  const [running, setRunning] = useState(null);
  const [queued, setQueued] = useState(null);

  useEffect(() => {
    fetchAnalysesQueueCounts().then((data) => {
      setRunning(data.running);
      setQueued(data.queued);
    });
  }, []);

  // Update the analysis duration using polling
  const intervalId = useInterval(() => {
    fetchAnalysesQueueCounts()
      .then((data) => {
        if (data.running !== running) {
          setRunning(data.running);
        }
        if (data.queued !== queued) {
          setQueued(data.queued);
        }
      })
      .catch((message) => {
        notification.error({ message });
        clearInterval(intervalId);
      });
  }, UPDATE_QUEUE_COUNT_DELAY);

  return (
    <Tooltip title={i18n("AnalysesQueue.title")} placement={"left"}>
      <Alert
        style={{ padding: 0 }}
        message={
          <div
            style={{
              width: 160,
              display: "flex",
              alignContent: "center",
              color: blue6,
            }}
          >
            <IconCloudServer
              style={{ fontSize: "2em", flex: 1, padding: SPACE_XS }}
            />
            <div
              style={{
                display: "inline-block",
                width: 130,
                marginRight: SPACE_XS,
              }}
            >
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
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
                  justifyContent: "space-between",
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
