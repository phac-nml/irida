import React from "react";
import { render } from "react-dom";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";
import {
  PagedTable,
  PagedTableProvider,
} from "../../../components/ant.design/PagedTable";
import { fetchTemplates } from "../../../apis/metadata/templates";
const projectId = getProjectIdFromUrl();

export const FIELDS_TRANSLATOR = {
  "icons": "owner",
  "irida-static-sample-name": "name",
  "irida-static-sample-id": "id",
  "irida-static-modifed": "modifiedDate",
  "irida-static-created": "createdDate",
  "irida-static-project-name": "project.name",
  "irida-static-project-id": "project.id",
};

function transformTemplateField(field) {
  if (field.field in FIELDS_TRANSLATOR) {
    return FIELDS_TRANSLATOR[field.field];
  }
  else {
    return ["metadata", field.field];
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
        dataIndex: transformTemplateField(field),
        title: field.headerName,
        sorter: true,
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
    <PagedTable search={false} loading={loading} columns={templates[current]} />
  );
}

render(
  <PagedTableProvider url={`/ajax/linelist/entries?projectId=${projectId}`}>
    <LineListBeta />
  </PagedTableProvider>,
  document.getElementById("root")
);
