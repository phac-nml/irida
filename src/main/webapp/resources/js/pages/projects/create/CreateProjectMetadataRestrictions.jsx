import { Alert, Table, Tag } from "antd";
import React from "react";

import { getColourForRestriction } from "../../../utilities/restriction-utilities";
import { TargetMetadataRestriction } from "../share/TargetMetadataRestriction";

import { useDispatch, useSelector } from "react-redux";
import { useGetMetadataFieldsForProjectQuery } from "../../../apis/metadata/field";
import { setNewProjectMetadataRestrictions } from "./metadataRestrictionSlice";
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

  const { metadataRestrictions } = useSelector(
    (state) => state.metadataRestrictionReducer
  );

  const { data: sourceFields = {} } = useGetMetadataFieldsForProjectQuery(1);

  React.useEffect(() => {
    getMetadataRestrictions().then((data) => {
      setRestrictions(data);
      if (sourceFields?.length) {
        console.log(sourceFields);
        dispatch(setNewProjectMetadataRestrictions(sourceFields));
      }
    });
  }, [dispatch, sourceFields]);

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
          <TargetMetadataRestriction field={item} restrictions={restrictions} />
        );
      },
    },
  ];

  return metadataRestrictions.length ? (
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
  );
}
