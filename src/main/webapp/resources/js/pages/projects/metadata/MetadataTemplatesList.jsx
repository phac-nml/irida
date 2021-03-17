import React from "react";
import { Button, Empty, List, notification, Popconfirm, Tag } from "antd";
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
} from "../../projects/redux/templatesSlice";

import { unwrapResult } from "@reduxjs/toolkit";
import { SPACE_MD } from "../../../styles/spacing";

/**
 * Component to display all metadata templates associated with a project.
 *
 * @param projectId
 * @returns {JSX.Element}
 * @constructor
 */
export function MetadataTemplatesList({ projectId }) {
  const { templates, loading, defaultTemplate } = useSelector(
    (state) => state.templates
  );
  const { canManage } = useSelector((state) => state.project);

  const dispatch = useDispatch();
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${projectId}/metadata-templates`)
  );

  React.useEffect(() => {
    dispatch(getDefaultTemplateForProject({ projectId }));
  }, []);

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

  /**
   * This crates the "actions" that appear at the right of every row in
   * the table: field count, download template, and remove template (if applicable).
   *
   * @param {Object} template
   * @returns {JSX.Element[]}
   */
  const getActionsForItem = (template) => {
    const actions = [
      <Tag key={`fields-${template.identifier}`}>
        {i18n("ProjectMetadataTemplates.fields", template.fields.length)}
      </Tag>,
      <Button
        shape="circle"
        size="small"
        icon={<IconDownloadFile />}
        href={`${BASE_URL}/${template.identifier}/excel`}
        key={`download-${template.identifier}`}
      />,
    ];
    if (canManage) {
      actions.push(
        defaultTemplate == template.identifier ? (
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
            onClick={() => setDefaultTemplate(template.identifier)}
          />
        ),
        <Popconfirm
          key={`remove-${template.id}`}
          placement="bottomRight"
          title={i18n("MetadataTemplatesList.delete-confirm")}
          onConfirm={() => deleteTemplate(template.identifier)}
        >
          <Button shape="circle" size="small" icon={<IconRemove />} />
        </Popconfirm>
      );
    }
    return actions;
  };

  /**
   * Delete a metadata template.
   *
   * @param {number} templateId - identifier for the metadata template to delete
   */
  const deleteTemplate = async (templateId) =>
    dispatch(removeTemplateFromProject({ projectId, templateId }))
      .then(unwrapResult)
      .then(({ message }) => notification.success({ message }))
      .catch((message) => notification.error({ message }));

  return (
    <List
      style={{ marginTop: SPACE_MD }}
      loading={loading}
      bordered
      itemLayout="horizontal"
      locale={{
        emptyText: (
          <Empty
            description={i18n("MetadataTemplatesList.empty")}
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          />
        ),
      }}
      dataSource={templates}
      renderItem={(item) => (
        <List.Item className="t-template" actions={getActionsForItem(item)}>
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
