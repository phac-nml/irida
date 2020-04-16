import React, { useEffect, useReducer } from "react";
import { getProjectDetails } from "../../../apis/projects/projects";
import { Typography } from "antd";
import { BasicList } from "../../../components/lists";

function reducer(state, action) {
  switch (action.type) {
    case "LOADED":
      return { ...state, ...action.payload, loading: false };
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

  const details = [
    {
      title: i18n("ProjectDetails.label"),
      desc: state.label,
    },
    {
      title: i18n("ProjectDetails.description"),
      desc: state.description,
    },
    {
      title: i18n("ProjectDetails.id"),
      desc: state.id,
    },
  ];

  return (
    <>
      <h1>{i18n("ProjectDetails.header")}</h1>
      <BasicList dataSource={details} />
    </>
  );
}
