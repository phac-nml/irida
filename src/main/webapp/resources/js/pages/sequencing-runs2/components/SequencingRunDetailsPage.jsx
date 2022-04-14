import React from "react";
import { useParams } from "react-router-dom";
import { useGetSequencingRunDetailsQuery } from "../../../apis/sequencing-runs/sequencing-runs";

/**
 * React component to display the sequencing run details page.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunDetailsPage() {
  const { runId } = useParams();
  const { data: run, isLoading } = useGetSequencingRunDetailsQuery(runId);

  console.log("runID = " + runId);
  console.log("run = " + JSON.stringify(run));

  return (<div>Details</div>);
}