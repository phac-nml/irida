import React, { useEffect, useReducer } from "react";
import {
  getProjectDetails,
  updateProjectAttribute,
} from "../../../apis/projects/projects";
import { notification, PageHeader, Tooltip, Typography } from "antd";
import { BasicList } from "../../../components/lists";
import {
  IconEdit,
  IconFolder,
  IconLoading,
} from "../../../components/icons/Icons";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { blue6 } from "../../../styles/colors";
import { SPACE_XS } from "../../../styles/spacing";
import { OrganismDescription } from "./OrganismDescription";

const { Paragraph } = Typography;

function reducer(state, action) {
  switch (action.type) {
    case "LOADED":
      return { ...state, ...action.payload, loading: false };
    case "UPDATE":
      return { ...state, ...action.payload };
  }
}

const initialState = {
  loading: true,
};

export function ProjectDetails() {
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    getProjectDetails(window.project.id).then((data) =>
      dispatch({ type: "LOADED", payload: data })
    );
  }, []);

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
          desc: (
            <Paragraph
              editable={{ onChange: (value) => updateField("label", value) }}
            >
              {state.label}
            </Paragraph>
          ),
        },
        {
          title: i18n("ProjectDetails.description"),
          desc: (
            <Paragraph
              editable={{
                onChange: (value) => updateField("description", value),
              }}
            >
              {state.description}
            </Paragraph>
          ),
        },
        {
          title: i18n("ProjectDetails.id"),
          desc: state.id,
        },
        {
          title: i18n("ProjectDetails.organism"),
          desc: (
            <OrganismDescription
              organism={state.organism}
              setOrganism={(organism) => updateField("organism", organism)}
            />
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
