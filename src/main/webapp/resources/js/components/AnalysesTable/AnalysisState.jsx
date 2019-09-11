import React from "react";
import { Badge, Icon } from "antd";
import { SPACE_XS } from "../../styles/spacing";
import { green6 } from "../../styles/colors";

/**
 * Display the state of an analysis
 * @param {string} state
 * @param {string} type a non-internationalized state
 * @returns {*}
 * @constructor
 */
export function AnalysisState({ state }) {
  switch (state.value) {
    case "NEW":
      return <Badge status="default" text={state.text} />;
    case "ERROR":
      return <Badge status="error" text={state.text} />;
    case "COMPLETED":
      return <Badge status="success" text={state.text} />;
    default:
      return (
        <span>
          <Icon
            type="sync"
            style={{ marginRight: SPACE_XS, color: green6 }}
            spin
          />
          {state.text}
        </span>
      );
  }
}
