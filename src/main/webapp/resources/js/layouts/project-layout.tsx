import React, { Suspense } from "react";
import {
  LoaderFunction,
  LoaderFunctionArgs,
  Outlet,
  useLoaderData,
} from "react-router-dom";
import { PageHeader } from "antd";
import ProjectNavigation from "../components/project/project-navigation";
import { ContentLoading } from "../components/loader";
import { CONTEXT_PATH } from "../data/routes";
import type { ProjectDetails } from "../redux/endpoints/project";

export const projectLoader: LoaderFunction = async (
  args: LoaderFunctionArgs
) => {
  const response = await fetch(
    `${CONTEXT_PATH}/ajax/project/details?projectId=${args.params.projectId}`
  );
  const details = await response.json();

  if (response.status === 404) {
    throw new Response(details.error, { status: 404 });
  }

  return details;
};

/**
 * React component for the layout of the project specific pages
 * @constructor
 */
export default function ProjectLayout(): JSX.Element {
  const details = useLoaderData() as ProjectDetails;

  return (
    <PageHeader
      title={details.name}
      subTitle={details.description}
      style={{ margin: `0 25px` }}
    >
      <div style={{ backgroundColor: `#ffffff` }}>
        <ProjectNavigation />
        <div style={{ padding: 20 }}>
          <Suspense
            fallback={
              <ContentLoading message={i18n("ProjectLayout.loading")} />
            }
          >
            <Outlet />
          </Suspense>
        </div>
      </div>
    </PageHeader>
  );
}
