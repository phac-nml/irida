import React from "react";
import { useParams } from "react-router-dom";
import { useGetSequencingRunFilesQuery } from "../../../apis/sequencing-runs/sequencing-runs";

/**
 * React component to display the sequencing run files page.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunFilesPage() {
const { runId } = useParams();
  const { data: files, isLoading } = useGetSequencingRunFilesQuery(runId);

  console.log("runID = " + runId);
  console.log("run = " + JSON.stringify(files));

  return (<div>Files</div>);
}