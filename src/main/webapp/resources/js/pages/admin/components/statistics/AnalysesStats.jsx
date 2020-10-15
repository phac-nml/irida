/*
 * This file renders the Analyses Statistics component
 */

import React from "react";

import AdvancedStatistics from "./AdvancedStatistics";

export default function AnalysesStats() {
  return (<>
    <AdvancedStatistics statType={"analyses"} />
  </>);
}