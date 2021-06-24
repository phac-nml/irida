import { navigate } from "@reach/router";
import { Table } from "antd";
import React from "react";
import { getTextSearchProps } from "../../../../components/ant.design/table-search-props";

export function ShareProjects({
  projectId,
  setShareProjectId,
  shareProjectId,
  projects,
}) {
  const [selected, setSelected] = React.useState();

  if (!projectId) navigate("./projects");

  const onChange = (_, selectedRows) =>
    setShareProjectId(selectedRows[0].identifier);

  React.useEffect(() => {
    setSelected([`project-${shareProjectId}`]);
  }, [shareProjectId]);

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
