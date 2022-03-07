import React from "react";
import { render } from "react-dom";
import { Space } from "antd";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";
import {
  PagedTable,
  PagedTableProvider,
} from "../../../components/ant.design/PagedTable";
import { fetchTemplates } from "../../../apis/metadata/templates";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { IconLocked, IconUnlocked } from "../../../components/icons/Icons";
import { blue6, red6 } from "../../../styles/colors";
const projectId = getProjectIdFromUrl();

const dateColumn = {
  render(text) {
    return formatInternationalizedDateTime(text);
  },
};

export const FIELDS_TRANSLATOR = {
  icons: {
    dataIndex: "owner",
    sorter: false,
    fixed: "left",
    render(text) {
      return text === "true" ? (
        <IconUnlocked style={{ color: blue6 }} />
      ) : (
        <IconLocked style={{ color: red6 }} />
      );
    },
  },
  "irida-static-sample-name": { dataIndex: "sampleName", fixed: "left" },
  "irida-static-sample-id": { dataIndex: "id" },
  "irida-static-modified": { dataIndex: "modifiedDate", ...dateColumn },
  "irida-static-created": { dataIndex: "createdDate", ...dateColumn },
  "irida-static-project-name": { dataIndex: "project.name" },
  "irida-static-project-id": { dataIndex: "project.id" },
};

function transformTemplateField(field) {
  if (field.field in FIELDS_TRANSLATOR) {
    return FIELDS_TRANSLATOR[field.field];
  } else {
    return {
      dataIndex: ["metadata", field.field],
      ...(field.type === "date" && dateColumn),
    };
  }
}

function transformTemplateResponse(data) {
  const templates = {};
  // All templates should have name
  data.forEach((item) => {
    const newTemplate = [];

    // Lets add the metadata fields
    item.fields.forEach((field) => {
      newTemplate.push({
        title: field.headerName,
        sorter: true,
        ...transformTemplateField(field),
      });
    });

    templates[item.name] = newTemplate;
  });
  return templates;
}

function LineListBeta() {
  const [loading, setLoading] = React.useState(true);
  const [templates, setTemplates] = React.useState({});
  const [current, setCurrent] = React.useState(undefined);
  const [selectedRowKeys, setSelectedRowKeys] = React.useState([]);

  function onSelectChange(selectedRowKeys) {
    console.log("selectedRowKeys changed: ", selectedRowKeys);
    setSelectedRowKeys(selectedRowKeys);
  }

  React.useEffect(() => {
    fetchTemplates(projectId).then(({ data }) => {
      console.log(data);
      // This is older formatted data so let's quickly modify it to match the new format
      const newTemplates = transformTemplateResponse(data);
      setTemplates(newTemplates);
      setCurrent(data[0].name);
      setLoading(false);
    });
  }, []);

  return (
    <Space direction="vertical" style={{ width: `100%` }}>
      <PagedTable
        search={false}
        loading={loading}
        rowSelection={{
          selectedRowKeys,
          preserveSelectedRowKeys: true,
          onChange: onSelectChange,
        }}
        columns={templates[current]}
      />
    </Space>
  );
}

render(
  <PagedTableProvider url={`/ajax/linelist/entries?projectId=${projectId}`}>
    <LineListBeta />
  </PagedTableProvider>,
  document.getElementById("root")
);
