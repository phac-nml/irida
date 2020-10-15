/*
 * This file renders the Sample Statistics component
 */

import React from "react";

import AdvancedStatistics from "./AdvancedStatistics";

export default function SampleStats() {
  return (<>
    <AdvancedStatistics statType={"samples"} />
  </>);
}