import { Link } from "@reach/router";

import { unwrapResult } from "@reduxjs/toolkit";
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
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import styled from "styled-components";
import {
  useDeleteTemplateMutation,
  useGetTemplatesForProjectQuery,
} from "../../../../../apis/metadata/metadata-templates";
import {
  IconDownloadFile,
  IconRemove,
} from "../../../../../components/icons/Icons";
import { blue6 } from "../../../../../styles/colors";

import { setBaseUrl } from "../../../../../utilities/url-utilities";
import { fetchFieldsForProject } from "../../../redux/fieldsSlice";

import { setDefaultTemplateForProject } from "../../../redux/projectSlice";

const { Text } = Typography;

const ALL_FIELDS_TEMPLATE_ID = 0;

const HoverItem = styled(List.Item)`
  button.ant-btn-link {
    opacity: 0;
    transition: opacity 0.35s ease-in-out;
  }
  &:hover button.ant-btn-link {
    opacity: 1;
  }
`;

/**
 * Component to display all metadata templates associated with a project.
 *
 * @param projectId
 * @returns {JSX.Element}
 * @constructor
 */
export function MetadataTemplatesList({ projectId }) {
  const { canManage, defaultMetadataTemplateId } = useSelector(
    (state) => state.project
  );

  const { data: templates, error, isLoading } = useGetTemplatesForProjectQuery(
    projectId
  );
  const [deleteMetadataTemplate] = useDeleteTemplateMutation();

  const dispatch = useDispatch();
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${projectId}/metadata-templates`)
  );

  React.useEffect(() => {
    dispatch(fetchFieldsForProject(projectId));
  }, [dispatch, projectId]);

  /**
   * Set default metadata template for project.
   *
   * @param {number} templateId - identifier for the metadata template to set as default
   */
  const setDefaultTemplate = async (templateId) => {
    dispatch(setDefaultTemplateForProject({ projectId, templateId }))
      .then(unwrapResult)
      .then(({ message }) => {
        notification.success({ message });
      })
      .catch((message) => notification.error({ message }));
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
    let isDefaultTemplateForProject =
      template.identifier == defaultMetadataTemplateId;
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
            isDefaultTemplateForProject &&
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
            disabled={isDefaultTemplateForProject}
          >
            <Button
              className="t-t-remove-button"
              size="small"
              icon={<IconRemove />}
              disabled={isDefaultTemplateForProject}
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
    deleteMetadataTemplate({ projectId, templateId })
      .then(({ data }) => notification.success({ message: data }))
      .catch((message) => notification.error({ message }));

  return (
    <>
      <Typography.Title level={2}>
        {i18n("ProjectMetadataTemplates.title")}
      </Typography.Title>
      <List
        loading={isLoading}
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
        renderItem={(item) => {
          const isNotAllFieldsTemplate =
            item.identifier != ALL_FIELDS_TEMPLATE_ID;
          return (
            <HoverItem
              className="t-m-template"
              actions={isNotAllFieldsTemplate && getActionsForItem(item)}
            >
              <List.Item.Meta
                title={
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    {isNotAllFieldsTemplate ? (
                      <Link
                        className="t-t-name"
                        style={{ color: blue6, display: "block" }}
                        to={`${item.identifier}`}
                      >
                        {item.name}
                      </Link>
                    ) : (
                      <Text className="t-t-name" style={{ display: "block" }}>
                        {item.name}
                      </Text>
                    )}
                    <div>
                      {canManage &&
                        (item.identifier == defaultMetadataTemplateId ? (
                          <Tag
                            key={`default-${item.identifier}`}
                            color={blue6}
                            className="t-t-default-tag"
                          >
                            {i18n("MetadataTemplatesList.default")}
                          </Tag>
                        ) : (
                          <Button
                            size="small"
                            key={`set-default-${item.identifier}`}
                            onClick={() => setDefaultTemplate(item.identifier)}
                            type="link"
                            className="t-t-set-default-button"
                          >
                            {i18n("MetadataTemplatesList.set-as-default")}
                          </Button>
                        ))}
                      <Tag key={`fields-${item.identifier}`}>
                        {i18n(
                          "ProjectMetadataTemplates.fields",
                          item.fields ? item.fields.length : 0
                        )}
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
          );
        }}
      />
    </>
  );
}
