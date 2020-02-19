/**
 * @file Use when needing to add a link to a JSX component.
 */
import React from "react";

/**
 * Create a link to a specific project.
 * @param {number} id - project identifier
 * @param {string} label - project label
 * @param {Object} props - any remaining props to pass to the anchor tag.
 * @returns {*}
 */
export const createProjectLink = ({ id, label, ...props }) => (
  <a href={`${window.TL.BASE_URL}projects/${id}`} {...props}>
    {label}
  </a>
);
