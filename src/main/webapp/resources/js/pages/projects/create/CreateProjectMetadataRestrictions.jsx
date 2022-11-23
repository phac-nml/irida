import { Alert, Table, Tag } from "antd";
import React from "react";

import { getColourForRestriction } from "../../../utilities/restriction-utilities";
import { TargetMetadataRestriction } from "../../../components/metadata/TargetMetadataRestriction";

import { useDispatch, useSelector } from "react-redux";
import {
  getAllMetadataFieldsForProjects,
  getMetadataRestrictions,
} from "../../../apis/metadata/field";
import { setNewProjectMetadataRestrictions } from "./newProjectSlice";

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

  /*
   On load if samples are selected in the previous step then
   we get the metadata template fields for all the projects
   that the selected samples are from.
   */

  React.useEffect(() => {
    if (samples?.length) {
      let projectIds = samples.map((s) => s.projectId);
      getAllMetadataFieldsForProjects(projectIds).then((data) => {
        setSourceFields(data);
        dispatch(
          setNewProjectMetadataRestrictions(
            data.sort((a, b) => a.label.localeCompare(b.label))
          )
        );
      });
    } else {
      dispatch(setNewProjectMetadataRestrictions([]));
    }

    // Get the available field restrictions
    getMetadataRestrictions().then((data) => {
      setRestrictions(data);
    });
  }, [samples]);

  const columns = [
    {
      title: i18n("CreateProjectMetadataRestrictions.field"),
      key: "label",
      dataIndex: "label",
      render: (label) => <span className="t-field-label">{label}</span>,
    },
    {
      title: i18n("CreateProjectMetadataRestrictions.currentRestriction"),
      key: "current",
      dataIndex: "current",
      render(text, item, index) {
        const field = restrictions.find(
          (restriction) => restriction.value === sourceFields[index].restriction
        );
        if (field) {
          return (
            <Tag
              color={getColourForRestriction(field.value)}
              className={`t-current-restriction-${item.label}`}
            >
              {field.label}
            </Tag>
          );
        }
        return text;
      },
    },
    {
      title: i18n("CreateProjectMetadataRestrictions.targetRestriction"),
      key: "target",
      dataIndex: "restriction",
      render(currentRestriction, item) {
        return (
          <TargetMetadataRestriction
            field={item}
            restrictions={restrictions}
            newProject={true}
            className={item.label}
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
        tableLayout="auto"
      />
    ) : (
      <Alert
        message={i18n(
          "CreateProjectMetadataRestrictions.noSampleMetadata.title"
        )}
        description={i18n(
          "CreateProjectMetadataRestrictions.noSampleMetadata.description"
        )}
        type="info"
        showIcon
        className="t-no-sample-metadata"
      />
    )
  ) : (
    <Alert
      message={i18n(
        "CreateProjectMetadataRestrictions.noSamplesSelected.title"
      )}
      description={i18n(
        "CreateProjectMetadataRestrictions.noSamplesSelected.description"
      )}
      type="info"
      showIcon
      className="t-no-samples-selected"
    />
  );
}
