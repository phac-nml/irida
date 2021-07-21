import { Result } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { useShareSamplesToProjectMutation } from "../../../../apis/projects/projects";
import { IconCheck, IconLoading } from "../../../../components/icons/Icons";

/**
 * React component to submit copy samples to the server.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareSubmit({ projectId }) {
  const loading = false;
  const { target, fields, samples, locked } = useSelector(
    (state) => state.reducer
  );

  console.log({ target, fields, samples, locked });

  const [
    shareSamplesToProject,
    { isLoading, isUpdating },
  ] = useShareSamplesToProjectMutation();

  React.useEffect(() => {
    shareSamplesToProject({
      original: projectId,
      destination: target.identifier,
      sampleIds: samples.map((s) => s.id),
      owner: !locked,
      fields: fields.target,
    });
  }, [
    fields,
    locked,
    projectId,
    samples,
    shareSamplesToProject,
    target.identifier,
  ]);

  return loading ? (
    <Result
      icon={<IconLoading />}
      title={`Submitting samples ${samples.length} to be copied to ${target.label}`}
    />
  ) : (
    <Result
      icon={<IconCheck />}
      title={`You are about to submit ${samples.length} to be copied to ${target.label}`}
    />
  );
}
