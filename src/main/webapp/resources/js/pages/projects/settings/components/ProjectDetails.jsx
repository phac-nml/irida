import { notification, Typography } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import { TAXONOMY } from "../../../../apis/ontology/taxonomy";
import {
  useGetProjectDetailsQuery,
  useUpdateProjectDetailsMutation,
} from "../../../../apis/projects/project";
import { EditableParagraph } from "../../../../components/ant.design";
import { BasicList } from "../../../../components/lists";
import { OntologyInput } from "../../../../components/ontology";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";

const { Paragraph, Title } = Typography;

/**
 * React component for render the basic information about a project.
 * @returns {*}
 * @constructor
 */
export default function ProjectDetails() {
  const params = useParams();
  const {
    data: project = {},
    isLoading,
    error: loadingError,
  } = useGetProjectDetailsQuery(params.projectId);
  const [updateProjectDetails] = useUpdateProjectDetailsMutation();

  React.useEffect(() => {
    if (loadingError) {
      // Should only hit this is the project cannot be found
      notification.error({ message: loadingError.data.error });
    }
  }, [loadingError]);

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

    updateProjectDetails({
      projectId: params.projectId,
      field,
      value: value || "",
    })
      .then((response) =>
        notification.success({ message: response.data.message })
      )
      .catch((message) => notification.error({ message }));
  };

  const details = isLoading
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
              <OntologyInput
                term={project.organism}
                ontology={TAXONOMY}
                onTermSelected={(term) => updateField("organism", term)}
              />
            </EditableParagraph>
          ) : (
            <span className="t-project-organism">
              <span>{project.organism}</span>
            </span>
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
