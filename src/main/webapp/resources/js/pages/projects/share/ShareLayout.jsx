import { Button, Modal, Space } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { useGetPotentialProjectsToShareToQuery } from "../../../apis/projects/projects";
import {
  useGetSampleIdsForProjectQuery,
  useShareSamplesWithProjectMutation,
} from "../../../apis/projects/samples";
import { IconShare } from "../../../components/icons/Icons";
import { ShareError } from "./ShareError";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";
import { ShareSuccess } from "./ShareSuccess";
import ShareLarge from "./ShareLarge";

/**
 * React component to layout the components for sharing/moving samples between
 * projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareLayout({ redirect }) {
  const [result, setResult] = React.useState();
  const [shareLarge, setShareLarge] = React.useState(false);

  const { originalSamples, currentProject, locked, projectId, remove } =
    useSelector((state) => state.shareReducer);

  const [shareSamplesWithProject, { isLoading, isError, error }] =
    useShareSamplesWithProjectMutation();

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
    // Just do a bulk share if only 500.
    if (samples.length < 500) {
      shareSamplesWithProject({
        sampleIds: samples.map((sample) => sample.id),
        locked,
        currentId: currentProject,
        targetId: projectId,
        remove,
      }).then(({ data }) => {
        setResult(data.message);
      });
    } else {
      setShareLarge(true);
    }
  };

  const SHOW_BUTTON = samples.length > 0;
  const DISABLED = samples.length === 0 || typeof projectId === "undefined";

  const onShareLargeComplete = () => {
    setShareLarge(false);
    setResult("done");
  };

  return (
    <>
      {typeof result === "string" ? (
        <ShareSuccess
          removed={remove}
          samples={samples}
          currentProject={currentProject}
          project={projects.find((project) => project.identifier === projectId)}
        />
      ) : typeof projectId !== "undefined" && isError ? (
        <ShareError error={error} redirect={redirect} />
      ) : shareLarge ? (
        <ShareLarge
          samples={samples}
          current={currentProject}
          target={projectId}
          locked={locked}
          remove={remove}
          onComplete={onShareLargeComplete}
        />
      ) : (
        <Space direction="vertical" style={{ width: `100%` }}>
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
        </Space>
      )}
    </>
  );
}
