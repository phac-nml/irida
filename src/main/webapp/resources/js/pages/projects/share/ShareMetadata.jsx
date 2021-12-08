import { Table, Tag } from "antd";
import React, { useEffect } from "react";
import { useSelector } from "react-redux";
import {
  getMetadataRestrictions,
  useGetMetadataFieldsForProjectQuery,
} from "../../../apis/metadata/field";
import {
  compareRestrictionLevels,
  getColourForRestriction,
} from "../../../utilities/restriction-utilities";
import { TargetMetadataRestriction } from "../settings/components/metadata/TargetMetadataRestriction";

export function ShareMetadata() {
  /**
   * Available restrictions for metadata fields
   */
  const [restrictions, setRestrictions] = React.useState([]);
  const [targetFields, setTargetFields] = React.useState([]);

  const { currentProject, projectId } = useSelector(
    (state) => state.shareReducer
  );

  /**
   * Get the fields for the current project.  The restrictions from these fields
   * will act as a base for the restriction level when the fields are shared.
   */
  const {
    data: sourceFields,
    isLoading: loadingFields,
  } = useGetMetadataFieldsForProjectQuery(currentProject);

  /**
   * Target project metadata fields. Needed to determine which fields will be
   * on both projects so that the new restriction for a field should start at
   * the highest level of restriction.
   */
  const {
    data: targetExistingFields = [],
  } = useGetMetadataFieldsForProjectQuery(projectId, {
    skip: !projectId,
  });

  useEffect(() => {
    if (sourceFields) {
      if (targetExistingFields.length) {
        const existing = targetExistingFields.reduce(
          (fields, field) => ({
            ...fields,
            [field.fieldKey]: field,
          }),
          {}
        );
        const newFields = sourceFields.map((field) => {
          if (existing[field.fieldKey]) {
            const difference = compareRestrictionLevels(
              field.restriction,
              existing[field.fieldKey].restriction
            );
            if (difference <= 0) {
              return { ...field, difference };
            } else {
              return {
                ...field,
                difference,
                restriction: existing[field.fieldKey].restriction,
              };
            }
          } else {
            return { ...field, new: true };
          }
        });
        setTargetFields(newFields);
      } else {
        // Set all the difference to 0 since the fields do not exist in the
        // target project
        const newFields = sourceFields.map((f) => ({
          ...f,
          difference: 0,
          new: true,
        }));
        setTargetFields(newFields);
      }
    }
  }, [loadingFields, sourceFields, targetExistingFields]);

  /**
   * On load, get metadata restrictions that are possible for a project.
   * These are formatted for Select inputs ({label, value}).
   */
  React.useEffect(() => {
    getMetadataRestrictions().then(setRestrictions);
  }, []);

  const updateRestrictionForField = (field, level) => {
    // Find the field
    const fieldIndex = targetFields.findIndex(
      (f) => f.fieldKey === field.fieldKey
    );
    if (fieldIndex >= 0) {
      field.restriction = level;
      field.difference = compareRestrictionLevels(
        sourceFields[fieldIndex].restriction,
        field.restriction
      );
      const updatedFields = [...targetFields];
      updatedFields[fieldIndex] = field;
      setTargetFields(updatedFields);
    }
  };

  const columns = [
    {
      title: "Field",
      key: "label",
      dataIndex: "label",
    },
    {
      title: "Current Restriction",
      key: "current",
      dataIndex: "restriction",
      render(text, item, index) {
        const field = restrictions.find(
          (r) => r.value === sourceFields[index].restriction
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
      render(currentRestriction, item, index) {
        if (typeof targetFields !== "undefined") {
          return (
            <TargetMetadataRestriction
              field={item}
              onChange={updateRestrictionForField}
              restrictions={restrictions}
            />
          );
        }
        return "---";
      },
    },
    {
      title: "",
      key: "new",
      dataIndex: "fieldKey",
      width: 100,
      render(restriction, item, index) {
        console.log({ NEW: item });
        return item.new ? <Tag>NEW</Tag> : <></>;
      },
    },
  ];

  console.log(targetFields);
  return (
    <Table
      columns={columns}
      dataSource={targetFields}
      scroll={{ y: 600 }}
      pagination={false}
    />
  );
}
