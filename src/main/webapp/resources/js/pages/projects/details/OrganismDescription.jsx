import React, { useEffect, useState } from "react";
import { OntologySelect } from "../../../components/ontology/OntologySelect";
import { Tooltip } from "antd";
import { SPACE_XS } from "../../../styles/spacing";
import { IconEdit } from "../../../components/icons/Icons";
import { blue6 } from "../../../styles/colors";
import { TAXONOMY } from "../../../apis/ontology/query";

/**
 * Show the organism along with an edit button.  Allow the user to enter
 * edit mode.
 *
 * @param {string} organism
 * @param {function} setEditing
 * @returns {*}
 * @constructor
 */
const OrganismTextDescription = ({ organism, setEditing }) => (
  <span>
    {organism}
    <Tooltip title={"Edit"}>
      <button
        style={{
          border: "none",
          margin: 0,
          padding: 0,
          backgroundColor: "transparent",
          marginLeft: SPACE_XS,
        }}
        onClick={() => setEditing(true)}
      >
        <IconEdit style={{ color: blue6 }} />
      </button>
    </Tooltip>
  </span>
);

export function OrganismDescription({ organism, setOrganism }) {
  const [editing, setEditing] = useState(false);

  const setUpdatedOrganism = (newOrganism) => {
    setEditing(false);
    setOrganism(newOrganism);
  };

  useEffect(() => {}, [editing]);

  return editing ? (
    <OntologySelect
      term={organism}
      onTermSelected={setUpdatedOrganism}
      ontology={TAXONOMY}
    />
  ) : (
    <OrganismTextDescription organism={organism} setEditing={setEditing} />
  );
}
