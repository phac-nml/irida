/*
 * This file renders the Project Statistics component
 */

import React from "react";

import AdvancedStatistics from "./AdvancedStatistics";

export default function ProjectStats() {
  return (<>
    <AdvancedStatistics statType={"projects"} />
  </>);
}