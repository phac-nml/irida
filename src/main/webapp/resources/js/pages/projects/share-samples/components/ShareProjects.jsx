import { Table } from "antd";
import React from "react";
import { getTextSearchProps } from "../../../../components/ant.design/table-search-props";
import { setBaseUrl } from "../../../../utilities/url-utilities";

export function ShareProjects({ projectId }) {
  const [projects, setProjects] = React.useState();
  const [selected, setSelected] = React.useState();

  React.useEffect(() => {
    fetch(setBaseUrl(`/ajax/projects/share-samples?current=${projectId}`))
      .then((response) => response.json())
      .then(setProjects);
  }, [projectId]);
  return (
    <Table
      scroll={{
        y: 600,
      }}
      pagination={{ hideOnSinglePage: true, pageSize: projects?.length }}
      rowSelection={{
        type: "radio",
        onChange: (_, selectedRows) => setSelected(selectedRows[0]),
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
