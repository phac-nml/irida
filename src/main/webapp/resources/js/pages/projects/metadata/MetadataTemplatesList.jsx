import React from "react";
import { Button, List, notification, Popconfirm, Tag } from "antd";
import { IconDownloadFile, IconRemove } from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Link } from "@reach/router";
import { blue6 } from "../../../styles/colors";
import { useDispatch, useSelector } from "react-redux";
import { removeTemplateFromProject } from "./templates/templatesSlice";
import { unwrapResult } from "@reduxjs/toolkit";

export function MetadataTemplatesList({ projectId }) {
  const { templates, loading } = useSelector((state) => state.templates);
  const dispatch = useDispatch();
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${projectId}/metadata-templates`)
  );

  const deleteTemplate = (templateId) =>
    dispatch(removeTemplateFromProject({ projectId, templateId }))
      .then(unwrapResult)
      .then(({ message }) => notification.success({ message }))
      .catch((message) => notification.error({ message }));

  return (
    <List
      loading={loading}
      bordered
      itemLayout="horizontal"
      dataSource={templates}
      renderItem={(item) => (
        <List.Item
          className="t-template"
          actions={[
            <Tag key={`fields-${item.id}`}>
              {i18n("ProjectMetadataTemplates.fields", item.fields.length)}
            </Tag>,
            <Button
              shape="circle"
              size="small"
              icon={<IconDownloadFile />}
              href={`${BASE_URL}/${item.id}/excel`}
              key={`download-${item.id}`}
            />,
            window.project.canManage ? (
              <Popconfirm
                key={`remove-${item.id}`}
                placement="bottomRight"
                title={"Delete this template?"}
                onConfirm={() => deleteTemplate(item.id)}
              >
                <Button shape="circle" size="small" icon={<IconRemove />} />
              </Popconfirm>
            ) : null,
          ]}
        >
          <List.Item.Meta
            title={
              <Link style={{ color: blue6 }} to={`${item.id}`}>
                {item.label}
              </Link>
            }
            description={item.description}
          />
        </List.Item>
      )}
    />
  );
}
