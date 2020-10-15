/*
 * This file renders the User Statistics component
 */

import React from "react";

import AdvancedStatistics from "./AdvancedStatistics";

export default function UserStats() {
  return (<>
    <AdvancedStatistics statType={"users"} />
  </>);
}