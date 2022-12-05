import React, { useMemo } from "react";
import { Link, Outlet, useMatches, useParams } from "react-router-dom";
import { Menu, PageHeader } from "antd";
import { useGetProjectDetailsQuery } from "../redux/endpoints/project";
import { MenuItem } from "../types/ant-design";
import { renderMenuItem } from "../components/ant.design/menu-utilities";

export default function ProjectLayout(): JSX.Element {
  const { projectId } = useParams();
  const matches = useMatches();

  const path = useMemo(() => {
    const match = matches.find((match) => match.id.startsWith(`project-`));
    return match?.id;
  }, [matches]);

  const { data: details = {} } = useGetProjectDetailsQuery(projectId);

  const menuItems: MenuItem[] = [
    {
      key: `project-samples`,
      label: <Link to={``}>{i18n("project.nav.samples")}</Link>,
    },
    {
      key: `project-linelist`,
      label: <Link to={`linelist`}>{i18n("project.nav.linelist")}</Link>,
    },
    {
      key: `project-analyses`,
      label: <Link to={`analyses`}>{i18n("project.nav.analysis")}</Link>,
    },
    {
      key: `project-exports`,
      label: <Link to={`exports`}>{i18n("project.nav.exports")}</Link>,
    },
    {
      key: `project-activity`,
      label: <Link to={`activity`}>{i18n("project.nav.activity")}</Link>,
    },
    {
      key: `project-settings`,
      label: <Link to={`settings`}>{i18n("project.nav.settings")}</Link>,
    },
  ];

  return (
    <PageHeader
      title={details.label}
      subTitle={details.description}
      style={{ margin: `0 25px` }}
    >
      <div style={{ backgroundColor: `#ffffff` }}>
        <Menu mode={"horizontal"} theme={"light"} selectedKeys={[path]}>
          {menuItems.map(renderMenuItem)}
        </Menu>
        <div style={{ padding: 20 }}>
          <Outlet />
        </div>
      </div>
    </PageHeader>
  );
}
