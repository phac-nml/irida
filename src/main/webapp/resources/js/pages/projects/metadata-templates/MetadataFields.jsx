import React from "react";
import { PageHeader, Table } from "antd";
import { getMetadataFieldsForProject } from "../../../apis/metadata/field";

export function MetadataFields({ projectId }) {
  const [fields, setFields] = React.useState();

  React.useEffect(() => {
    getMetadataFieldsForProject(projectId).then((data) => {
      setFields(data.map((f) => ({ ...f, key: `field-${f.id}` })));
    });
  }, []);
  return (
    <PageHeader title={"METADATA FIELDS"}>
      <Table
        dataSource={fields}
        columns={[{ title: "Field", dataIndex: "label", key: "label" }]}
      />
    </PageHeader>
  );
}
