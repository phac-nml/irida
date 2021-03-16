import React from "react";
import { Button, List, notification, Popconfirm, Tag } from "antd";
import { IconDownloadFile, IconRemove } from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Link } from "@reach/router";
import { blue6 } from "../../../styles/colors";
import { useDispatch, useSelector } from "react-redux";
import { removeTemplateFromProject } from "../redux/templatesSlice";
import { SPACE_MD } from "../../../styles/spacing";
import { unwrapResult } from "@reduxjs/toolkit";

/**
 * Component to display all metadata templates associated with a project.
 *
 * @param projectId
 * @returns {JSX.Element}
 * @constructor
 */
export function MetadataTemplatesList({ projectId }) {
  const { canManage } = useSelector((state) => state.project);
  const { templates, loading } = useSelector((state) => state.templates);
  const dispatch = useDispatch();
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${projectId}/metadata-templates`)
  );

  const getActionsForItem = (item) => {
    const actions = [
      <Tag key={`fields-${item.identifier}`}>
        {i18n("ProjectMetadataTemplates.fields", item.fields.length)}
      </Tag>,
      <Button
        shape="circle"
        size="small"
        icon={<IconDownloadFile />}
        href={`${BASE_URL}/${item.identifier}/excel`}
        key={`download-${item.identifier}`}
      />,
    ];
    if (canManage) {
      actions.push(
        <Popconfirm
          key={`remove-${item.id}`}
          placement="bottomRight"
          title={i18n("MetadataTemplatesList.delete-confirm")}
          onConfirm={() => deleteTemplate(item.identifier)}
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
