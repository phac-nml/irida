import React, { useEffect, useReducer } from "react";
import {
  getProjectDetails,
  updateProjectAttribute,
} from "../../../apis/projects/projects";
import { notification, PageHeader, Typography } from "antd";
import { BasicList } from "../../../components/lists";
import { IconFolder, IconLoading } from "../../../components/icons/Icons";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { EditableParagraph } from "../../../components/ant.design";
import { OntologySelect } from "../../../components/ontology";
import { TAXONOMY } from "../../../apis/ontology/taxonomy";

const { Paragraph } = Typography;

/**
 * Reducer for the state of the project details.
 *
 * @param {object} state - current state of the project details
 * @param {object} action - include type and payload
 * @returns {object}
 */
function reducer(state, action) {
  switch (action.type) {
    case "LOADED":
      return { ...state, ...action.payload, loading: false };
    case "UPDATE":
      return { ...state, ...action.payload };
    default:
      return { ...state };
  }
}

/**
 * React component for render the basic information about a project.
 * @returns {*}
 * @constructor
 */
export function ProjectDetails() {
  const [state, dispatch] = useReducer(reducer, { loading: true });

  /*
  When this component is rendered, query the api for the specific details
  about this project.
   */
  useEffect(() => {
    getProjectDetails(window.project.id).then((data) =>
      dispatch({ type: "LOADED", payload: data })
    );
  }, []);

  /**
   * When a field is updated, submitted it to the server to be saved.
   * Display a notification on the result of the update, and then update the
   * UI accordingly.
   *
   * @param {string} field to be updated
   * @param {string} value to be store in the field
   */
  const updateField = (field, value) => {
    updateProjectAttribute({
      projectId: window.project.id,
      field,
      value,
    })
      .then((message) => {
        notification.success({ message });
        dispatch({ type: "UPDATE", payload: { [field]: value } });
      })
      .catch((message) => {
        notification.error({ message });
      });
  };

  const details = state.loading
    ? []
    : [
        {
          title: i18n("ProjectDetails.label"),
          desc: window.project.canManage ? (
            <Paragraph
              editable={{ onChange: (value) => updateField("label", value) }}
            >
              {state.label}
            </Paragraph>
          ) : (
            state.label
          ),
        },
        {
          title: i18n("ProjectDetails.description"),
          desc: window.project.canManage ? (
            <Paragraph
              editable={{
                onChange: (value) => updateField("description", value),
              }}
            >
              {state.description}
            </Paragraph>
          ) : (
            state.description
          ),
        },
        {
          title: i18n("ProjectDetails.id"),
          desc: state.id,
        },
        {
          title: i18n("ProjectDetails.organism"),
          desc: window.project.canManage ? (
            <EditableParagraph value={state.organism}>
              <OntologySelect
                term={state.organism}
                ontology={TAXONOMY}
                onTermSelected={(term) => updateField("organism", term)}
              />
            </EditableParagraph>
          ) : (
            state.organism
          ),
        },
        {
          title: i18n("ProjectDetails.createdDate"),
          desc: formatInternationalizedDateTime(state.createdDate),
        },
        {
          title: i18n("ProjectDetails.modifiedDate"),
          desc: formatInternationalizedDateTime(state.modifiedDate),
        },
      ];

  return (
    <PageHeader
      title={i18n("ProjectDetails.header")}
      avatar={{ icon: state.loading ? <IconLoading /> : <IconFolder /> }}
    >
      <BasicList dataSource={details} />
    </PageHeader>
  );
}
