import { Typography } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { getProjectRoles } from "../../../../apis/projects/projects";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { ProjectMembersTable } from "../../../../components/project-members";
import { RolesProvider } from "../../../../contexts/roles-context";
import { setBaseUrl } from "../../../../utilities/url-utilities";

const { Title } = Typography;

/**
 * React component that displays the Project > Members page.
 * @returns {*}
 * @constructor
 */
export default function ProjectMembersPage() {
  const { id: projectId } = useSelector((state) => state.project);

  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/members?projectId=${projectId}`)}
    >
      <RolesProvider rolesFn={getProjectRoles}>
        <>
          <Title level={2}>{i18n("project.settings.page.title.members")}</Title>
          <ProjectMembersTable />
        </>
      </RolesProvider>
    </PagedTableProvider>
  );
}
