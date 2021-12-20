import React from "react";
import { useSelector } from "react-redux";
import {
  useGetPotentialProjectsToShareToQuery
} from "../../../apis/projects/projects";
import {
  useGetSampleIdsForProjectQuery,
  useShareSamplesWithProjectMutation,
} from "../../../apis/projects/samples";
import { ShareMetadata } from "./ShareMetadata";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";

/**
 * React component to layout the components for sharing/moving samples between
 * projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareLayout({ redirect, step, nextStep, previousStep }) {
  const [result, setResult] = React.useState();

  const {
    originalSamples,
    currentProject,
    locked,
    projectId,
    remove,
    metadataRestrictions,
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
  params.remoteId contains all samples, here we are filtering it
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
      restrictions: metadataRestrictions.map(({ restriction, id }) => ({
        restriction,
        identifier: id,
      })),
    }).then(({ data }) => {
      setResult(data.message);
    });
  };

  const SHOW_BUTTON = samples.length > 0;
  const DISABLED = samples.length === 0 || typeof projectId === "undefined";

  let component;

  if (step === 0) {
    return <ShareProject />;
  } else if (step === 1) {
    return <ShareSamples samples={samples} redirect={redirect} />;
  } else if (step === 2) {
    return <ShareMetadata />;
  } else if (step === 3) {
    return <h1>REVIEW</h1>;
  }
}

// return (
//   <>
//     {typeof result === "string" ? (
//       <ShareSuccess
//         removed={remove}
//         samples={samples}
//         currentProject={currentProject}
//         project={projects.find((project) => project.identifier === projectId)}
//       />
//     ) : typeof projectId !== "undefined" && isError ? (
//       <ShareError error={error} redirect={redirect} />
//     ) : (
//       <Space direction="vertical" style={{ width: `100%` }}>
//         <ShareProject currentProject={currentProject} />
//         <Tabs>
//           <Tabs.TabPane tab={i18n("ShareLayout.samples")} key="samples">
//             <ShareSamples samples={samples} redirect={redirect} />
//           </Tabs.TabPane>
//           <Tabs.TabPane tab={i18n("ShareLayout.fields")} key="metadata">
//             <ShareMetadata />
//           </Tabs.TabPane>
//         </Tabs>
//         {SHOW_BUTTON && (
//           <div style={{ display: "flex", flexDirection: "row-reverse" }}>
//             <Button
//               type="primary"
//               className="t-share-button"
//               disabled={DISABLED}
//               onClick={() => shareSamples()}
//               loading={isLoading}
//               icon={<IconShare />}
//             >
//               {i18n("ShareButton.button")}
//             </Button>
//           </div>
//         )}
//       </Space>
//     )}
//   </>
// );
// }
