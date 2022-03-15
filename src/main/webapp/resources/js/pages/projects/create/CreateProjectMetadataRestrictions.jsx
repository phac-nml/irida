import { Alert, Table, Tag } from "antd";
import React from "react";

import { getColourForRestriction } from "../../../utilities/restriction-utilities";
import { TargetMetadataRestriction } from "../share/TargetMetadataRestriction";

import { useDispatch, useSelector } from "react-redux";
import { getAllMetadataFieldsForProjects } from "../../../apis/metadata/field";
import { setNewProjectMetadataRestrictions } from "./newProjectSlice";
import { getMetadataRestrictions } from "../../../apis/metadata/field";

/**
 * Component to render metadata restrictions for samples that are in the cart (if any).
 * User can update the new project restrictions as required
 * @param {Object} form - Ant Design form API
 * @returns {JSX.Element}
 * @constructor
 */
export function CreateProjectMetadataRestrictions({ form }) {
  const dispatch = useDispatch();

  /**
   * Available restrictions for metadata fields
   */
  const [restrictions, setRestrictions] = React.useState([]);
  const [sourceFields, setSourceFields] = React.useState({});
  const { samples, metadataRestrictions } = useSelector(
    (state) => state.newProjectReducer
  );

  React.useEffect(() => {
    if (samples?.length) {
      getAllMetadataFieldsForProjects({ projectIds: [1, 2] }).then((data) => {
        setSourceFields(data);
        dispatch(setNewProjectMetadataRestrictions(data));
      });
    } else {
      dispatch(setNewProjectMetadataRestrictions([]));
    }

    getMetadataRestrictions().then((data) => {
      setRestrictions(data);
    });
  }, [samples]);

  const columns = [
    {
      title: "Field",
      key: "label",
      dataIndex: "label",
      render: (label, field) => <span className="t-field-label">{label}</span>,
    },
    {
      title: "Current Restriction",
      key: "current",
      dataIndex: "current",
      render(text, item, index) {
        const field = restrictions.find(
          (restriction) => restriction.value === sourceFields[index].restriction
        );
        if (field) {
          return (
            <Tag color={getColourForRestriction(field.value)}>
              {field.label}
            </Tag>
          );
        }
        return text;
      },
    },
    {
      title: "Target Restriction",
      key: "target",
      dataIndex: "restriction",
      render(currentRestriction, item) {
        return (
          <TargetMetadataRestriction
            field={item}
            restrictions={restrictions}
            newProject={true}
          />
        );
      },
    },
  ];

  return samples.length ? (
    metadataRestrictions.length ? (
      <Table
        className="t-meta-table"
        columns={columns}
        dataSource={metadataRestrictions}
        scroll={{ y: 300 }}
        pagination={false}
      />
    ) : (
      <Alert
        message="No metadata restrictions to share"
        description="The samples to be shared don't contain any metadata and therefore no restrictions will be shared."
        type="info"
        showIcon
      />
    )
  ) : (
    <Alert
      message="No metadata restrictions to apply to new project"
      description="No samples were selected therefore no restrictions will be applied to the new project"
      type="info"
      showIcon
    />
  );
}
