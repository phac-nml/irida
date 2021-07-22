import { Result } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { useShareSamplesToProjectMutation } from "../../../../apis/projects/projects";
import { IconLoading } from "../../../../components/icons/Icons";
import { ShareSuccess } from "./ShareSuccess";

/**
 * React component to submit copy samples to the server.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareSubmit({ projectId }) {
  const { target, fields, samples, locked } = useSelector(
    (state) => state.reducer
  );

  const [
    shareSamplesToProject,
    { isLoading, isSuccess, ...rest },
  ] = useShareSamplesToProjectMutation();

  console.log(rest);

  /*
  When this component is rendered, it is time to submit the request to copy these
  samples to the target project.
   */
  React.useEffect(() => {
    shareSamplesToProject({
      original: projectId,
      destination: target.identifier,
      sampleIds: samples.map((s) => s.id),
      owner: !locked,
      fields,
    });
  }, [
    fields,
    locked,
    projectId,
    samples,
    shareSamplesToProject,
    target.identifier,
  ]);

  return isLoading ? (
    <Result
      icon={<IconLoading />}
      title={`Submitting samples ${samples.length} to be copied to ${target.label}`}
    />
  ) : isSuccess ? (
    <ShareSuccess count={samples.length} target={target} />
  ) : null;
}
