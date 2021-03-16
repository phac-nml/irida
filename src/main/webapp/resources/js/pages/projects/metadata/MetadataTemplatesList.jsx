import React from "react";
import { Button, List, notification, Popconfirm, Tag } from "antd";
import {
  IconDownloadFile,
  IconIsDefault,
  IconRemove,
  IconSetDefault,
} from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Link } from "@reach/router";
import { blue6 } from "../../../styles/colors";
import { useDispatch, useSelector } from "react-redux";
import {
  getDefaultTemplateForProject,
  removeDefaultTemplateForProject,
  removeTemplateFromProject,
  setDefaultTemplateForProject,
} from "./redux/templates/templatesSlice";
import { unwrapResult } from "@reduxjs/toolkit";
import { SPACE_XS } from "../../../styles/spacing";

export function MetadataTemplatesList({ projectId }) {
  const { templates, loading, defaultTemplate } = useSelector(
    (state) => state.templates
  );
  const dispatch = useDispatch();
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${projectId}/metadata-templates`)
  );

  React.useEffect(() => {
    dispatch(getDefaultTemplateForProject({ projectId }));
  }, []);

  const deleteTemplate = async (templateId) => {
    const { payload } = await dispatch(
      removeTemplateFromProject({ projectId, templateId })
    );
    console.log(payload);
    // .then(unwrapResult)
    // .then(({ message }) => notification.success({ message }))
    // .catch((message) => notification.error({ message }));
  };

  const setDefaultTemplate = async (templateId) => {
    if (templateId !== defaultTemplate) {
      await dispatch(setDefaultTemplateForProject({ projectId, templateId }))
        .then(unwrapResult)
        .then(({ message }) => {
          notification.success({ message });
        })
        .catch((message) => notification.error({ message }));
    }
  };

  const removeDefaultTemplate = async () => {
    await dispatch(removeDefaultTemplateForProject({ projectId }))
      .then(unwrapResult)
      .then(({ message }) => {
        notification.success({ message });
      })
      .catch((message) => notification.error({ message }));
  };

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
            <Tag key={`fields-${item.identifier}`}>
              {i18n("ProjectMetadataTemplates.fields", item.fields.length)}
            </Tag>,
            defaultTemplate == item.identifier ? (
              <Button
                shape="circle"
                size="small"
                icon={<IconIsDefault />}
                key="remove-default-template"
                onClick={() => removeDefaultTemplate()}
              />
            ) : (
              <Button
                shape="circle"
                size="small"
                icon={<IconSetDefault />}
                key="set-default-template"
                onClick={() => setDefaultTemplate(item.identifier)}
              />
            ),
            <Button
              shape="circle"
              size="small"
              icon={<IconDownloadFile />}
              href={`${BASE_URL}/${item.identifier}/excel`}
              key={`download-${item.identifier}`}
            />,
            window.project.canManage ? (
              <Popconfirm
                key={`remove-${item.id}`}
                placement="bottomRight"
                title={"Delete this template?"}
                onConfirm={() => deleteTemplate(item.identifier)}
              >
                <Button shape="circle" size="small" icon={<IconRemove />} />
              </Popconfirm>
            ) : null,
          ]}
        >
          <List.Item.Meta
            title={
              <Link style={{ color: blue6 }} to={`${item.identifier}`}>
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
