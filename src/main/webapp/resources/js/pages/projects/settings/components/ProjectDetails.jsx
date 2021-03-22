import React from "react";
import { notification, Typography } from "antd";
import { BasicList } from "../../../../components/lists";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { EditableParagraph } from "../../../../components/ant.design";
import { OntologySelect } from "../../../../components/ontology";
import { TAXONOMY } from "../../../../apis/ontology/taxonomy";
import { useDispatch, useSelector } from "react-redux";
import { updateProjectDetails } from "../../redux/projectSlice";
import { unwrapResult } from "@reduxjs/toolkit";

const { Paragraph, Title } = Typography;

/**
 * React component for render the basic information about a project.
 * @returns {*}
 * @constructor
 */
export default function ProjectDetails() {
  const project = useSelector((state) => state.project);
  const dispatch = useDispatch();

  /**
   * When a field is updated, submitted it to the server to be saved.
   * Display a notification on the result of the update, and then update the
   * UI accordingly.
   *
   * @param {string} field to be updated
   * @param {string} value to be store in the field
   */
  const updateField = (field, value) => {
    /*
    Make sure the value actually changed, if it didn't, don't update it.
     */
    if (project[field] === value) return;

    dispatch(updateProjectDetails({ field, value }))
      .then(unwrapResult)
      .then(({ message }) => notification.success({ message }))
      .catch((message) => notification.error({ message }));
  };

  const details = project.loading
    ? []
    : [
        {
          title: i18n("ProjectDetails.label"),
          desc: project.canManage ? (
            <Paragraph
              className="t-project-name"
              editable={{ onChange: (value) => updateField("label", value) }}
            >
              {project.label}
            </Paragraph>
          ) : (
            <span className="t-project-name">{project.label}</span>
          ),
        },
        {
          title: i18n("ProjectDetails.description"),
          desc: project.canManage ? (
            <Paragraph
              className="t-project-desc"
              editable={{
                onChange: (value) => updateField("description", value),
              }}
            >
              {project.description}
            </Paragraph>
          ) : (
            <span className="t-project-desc">{project.description}</span>
          ),
        },
        {
          title: i18n("ProjectDetails.id"),
          desc: <span className="t-project-id">{project.id}</span>,
        },
        {
          title: i18n("ProjectDetails.organism"),
          desc: project.canManage ? (
            <EditableParagraph
              value={project.organism}
              valueClassName="t-project-organism"
            >
              <OntologySelect
                term={project.organism}
                ontology={TAXONOMY}
                onTermSelected={(term) => updateField("organism", term)}
              />
            </EditableParagraph>
          ) : (
            <span className="t-project-organism">{project.organism}</span>
          ),
        },
        {
          title: i18n("ProjectDetails.createdDate"),
          desc: formatInternationalizedDateTime(project.createdDate),
        },
        {
          title: i18n("ProjectDetails.modifiedDate"),
          desc: formatInternationalizedDateTime(project.modifiedDate),
        },
      ];

  return (
    <>
      <Title level={2}>{i18n("ProjectDetails.header")}</Title>
      <BasicList dataSource={details} />
    </>
  );
}
