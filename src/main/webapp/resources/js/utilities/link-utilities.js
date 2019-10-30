import React from "react";

export const createProjectLink = ({ id, label }) => (
  <a href={`${window.TL.BASE_URL}projects/${id}`}>{label}</a>
);
