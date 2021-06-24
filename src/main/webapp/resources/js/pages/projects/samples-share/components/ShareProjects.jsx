import { navigate } from "@reach/router";
import { Button, Space, Table } from "antd";
import React from "react";
import { getTextSearchProps } from "../../../../components/ant.design/table-search-props";
import { IconLinkOut } from "../../../../components/icons/Icons";
import { setBaseUrl } from "../../../../utilities/url-utilities";

export function ShareProjects({
  projectId,
  setDestinationId,
  destinationId,
  projects,
}) {
  const [selected, setSelected] = React.useState();

  if (!projectId) navigate("./projects");

  const onChange = (_, selectedRows) =>
    setDestinationId(selectedRows[0].identifier);

  React.useEffect(() => {
    setSelected([`project-${destinationId}`]);
  }, [destinationId]);

  return (
    <Table
      scroll={{
        y: 600,
      }}
      pagination={{ hideOnSinglePage: true, pageSize: projects?.length }}
      rowSelection={{
        type: "radio",
        selectedRowKeys: selected,
        onChange,
      }}
      dataSource={projects}
      rowKey={(item) => `project-${item.identifier}`}
      columns={[
        {
          dataIndex: "name",
          title: "Project Name",
          ...getTextSearchProps("name"),
          onFilter: (value, record) => 0 < record.name.indexOf(value),
          render: (text, project) => (
            <Button
              type="link"
              target="_blank"
              href={setBaseUrl(`/projects/${project.identifier}`)}
              rel="noreferrer"
            >
              <Space>
                {text}
                <IconLinkOut />{" "}
              </Space>
            </Button>
          ),
        },
        {
          dataIndex: "organism",
          title: "Organism",
          filters: projects
            ?.filter((project) => project.organism)
            .map((project) => ({
              text: project.organism,
              value: project.organism,
            })),
          onFilter: (value, record) => record.organism === value,
        },
      ]}
    />
  );
}
