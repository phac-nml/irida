import { Button, Space } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import {
  useGetPotentialProjectsToShareToQuery
} from "../../../apis/projects/projects";
import {
  useGetSampleIdsForProjectQuery,
  useShareSamplesWithProjectMutation,
} from "../../../apis/projects/samples";
import { IconShare } from "../../../components/icons/Icons";
import { ShareError } from "./ShareError";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";
import { ShareSuccess } from "./ShareSuccess";

/**
 * React component to layout the components for sharing/moving samples between
 * projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareLayout({ redirect }) {
  const [result, setResult] = React.useState();

  const {
    originalSamples,
    currentProject,
    locked,
    projectId,
    remove,
  } = useSelector((state) => state.shareReducer);

  const [
    shareSamplesWithProject,
    { isLoading, isError, error },
  ] = useShareSamplesWithProjectMutation();

  /*
  This fetches a list of the projects that the user has access to.
   */
  const { data: projects } = useGetPotentialProjectsToShareToQuery(
    currentProject,
    {
      skip: !currentProject,
    }
  );

  const { data: existingIds = [] } = useGetSampleIdsForProjectQuery(projectId, {
    skip: !projectId,
  });

  /*
  originalSamples contains all samples, here we are filtering it
  to only show samples that are not in the target project.
   */
  let samples = originalSamples.filter(
    (sample) => !existingIds.includes(sample.id)
  );

  /**
   * Server call to actually share samples with another project.
   */
  const shareSamples = () => {
    shareSamplesWithProject({
      sampleIds: samples.map((s) => s.id),
      locked,
      currentId: currentProject,
      targetId: projectId,
      remove,
    }).then(({ data }) => {
      setResult(data.message);
    });
  };

  const SHOW_BUTTON = samples.length > 0;
  const DISABLED = samples.length === 0 || typeof projectId === "undefined";

  return (
    <Space direction="vertical" style={{ display: "block" }} size="large">
      {typeof result === "string" ? (
        <ShareSuccess
          removed={remove}
          samples={samples}
          project={projects.find((project) => project.identifier === projectId)}
        />
      ) : typeof projectId !== "undefined" && isError ? (
        <ShareError error={error} redirect={redirect} />
      ) : (
        <>
          <ShareProject currentProject={currentProject} />
          <ShareSamples samples={samples} redirect={redirect} />
          {SHOW_BUTTON && (
            <div style={{ display: "flex", flexDirection: "row-reverse" }}>
              <Button
                type="primary"
                className="t-share-button"
                disabled={DISABLED}
                onClick={() => shareSamples()}
                loading={isLoading}
                icon={<IconShare />}
              >
                {i18n("ShareButton.button")}
              </Button>
            </div>
          )}
        </>
      )}
    </Space>
  );
}
