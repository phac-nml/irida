import React from "react";
import {
  Button,
  Empty,
  List,
  notification,
  Popconfirm,
  Tag,
  Tooltip,
  Typography,
} from "antd";
import { IconDownloadFile, IconRemove } from "../../../components/icons/Icons";

import { setBaseUrl } from "../../../utilities/url-utilities";
import { Link } from "@reach/router";
import { blue6 } from "../../../styles/colors";
import { useDispatch, useSelector } from "react-redux";
import {
  removeTemplateFromProject,
  setDefaultTemplateForProject,
} from "../../projects/redux/templatesSlice";

import { unwrapResult } from "@reduxjs/toolkit";
import styled from "styled-components";

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

  const [currDefault, setCurrDefault] = React.useState(0);

  const dispatch = useDispatch();
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${projectId}/metadata-templates`)
  );

  const HoverItem = styled(List.Item)`
    button.ant-btn-link {
      opacity: 0;
      transition: opacity 0.35s ease-in-out;
    }
    &:hover button.ant-btn-link {
      opacity: 1;
    }
  `;

  const setDefaultTemplate = async (templateId) => {
    if (templateId !== defaultTemplate) {
      await dispatch(setDefaultTemplateForProject({ projectId, templateId }))
        .then(unwrapResult)
        .then(({ message }) => {
          setCurrDefault(templateId);
          notification.success({ message });
        })
        .catch((message) => notification.error({ message }));
    }
  };

  const isTemplateDefault = (template) => {
    return template.default || currDefault == template.identifier;
  };

  /**
   * This creates the "actions" that appear at the right of every row in
   * the table: default tag, set default, field count, download template,
   * and remove template (if applicable).
   *
   * @param {Object} template
   * @returns {JSX.Element[]}
   */
  const getActionsForItem = (template) => {
    let defaultTemplate = isTemplateDefault(template);

    const actions = [
      <Button
        size="small"
        icon={<IconDownloadFile />}
        href={`${BASE_URL}/${template.identifier}/excel`}
        key={`download-${template.identifier}`}
      >
        {i18n("MetadataTemplatesList.download")}
      </Button>,
    ];
    if (canManage) {
      actions.push(
        <Tooltip
          placement="topLeft"
          title={
            defaultTemplate &&
              i18n("MetadataTemplatesList.cannot-remove-default")
          }
          arrowPointAtCenter
          key={`remove-tooltip-${template.identifier}`}
        >
          <Popconfirm
            key={`remove-${template.id}`}
            title={i18n("MetadataTemplatesList.delete-confirm")}
            onConfirm={() => deleteTemplate(template.identifier)}
            okButtonProps={{
              className: "t-t-confirm-remove",
            }}
            disabled={defaultTemplate}
          >
            <Button
              className="t-t-remove-button"
              size="small"
              icon={<IconRemove />}
              disabled={defaultTemplate}
            >
              {i18n("MetadataTemplatesList.remove")}
            </Button>
          </Popconfirm>
        </Tooltip>
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
      loading={loading}
      bordered
      itemLayout="vertical"
      size="large"
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
        <HoverItem className="t-m-template" actions={getActionsForItem(item)}>
          <List.Item.Meta
            title={
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <Link
                  className="t-t-name"
                  style={{ color: blue6, display: "block" }}
                  to={`${item.identifier}`}
                >
                  {item.name}
                </Link>
                <div
                  style={{
                  display: "flex",
                  justifyContent: "flex-end",
                  alignItems: "center",
                  }}
                >
                  {
                    isTemplateDefault(item) ? (
                      <Tag key={`default-${item.identifier}`} color={blue6}>
                        {i18n("MetadataTemplatesList.default")}
                      </Tag>
                    ) : (
                      <Button
                        size="small"
                        key={`set-default-${item.identifier}`}
                        onClick={() => setDefaultTemplate(item.identifier)}
                        type="link"
                      >
                        {i18n("MetadataTemplatesList.set-as-default")}
                      </Button>
                    )
                  }
                  <Tag key={`fields-${item.identifier}`}>
                    {i18n("ProjectMetadataTemplates.fields", item.fields.length)}
                  </Tag>
                </div>
              </div>
            }
          />
          {item.description && (
            <Typography.Paragraph
              ellipsis={{
                rows: 2,
                expandable: true,
              }}
            >
              {item.description}
            </Typography.Paragraph>
          )}
        </HoverItem>
      )}
    />
  );
}
