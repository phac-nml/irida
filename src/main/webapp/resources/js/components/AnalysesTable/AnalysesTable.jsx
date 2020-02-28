import React, { useContext, useEffect, useState } from "react";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { Button, Icon, Popconfirm } from "antd";
import {
  dateColumnFormat,
  nameColumnFormat
} from "../ant.design/table-renderers";
import {
  deleteAnalysisSubmissions,
  fetchAllPipelinesStates,
  fetchAllPipelinesTypes
} from "../../apis/analysis/analysis";
import { AnalysisState } from "./AnalysisState";
import { getHumanizedDuration } from "../../utilities/date-utilities.js";
import { getTextSearchProps } from "../ant.design/table-search-props";
import { blue6 } from "../../styles/colors";
import { SPACE_MD } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";
import { AnalysesQueue } from "./../AnalysesQueue";

/**
 * Displays the Analyses Table for both user and admin pages.
 * @returns {*}
 * @constructor
 */
export function AnalysesTable() {
  const CAN_MANAGE = window.PAGE.canManage;
  const { updateTable } = useContext(PagedTableContext);

  /**
   * Handler for deleting an analysis.
   *
   * @param {array} ids
   * @returns {void | Promise<*>}
   */
  const deleteAnalyses = ids =>
    deleteAnalysisSubmissions({ ids }).then(updateTable);

  const [selected, setSelected] = useState([]);
  const [pipelineStates, setPipelineStates] = useState([]);
  const [pipelineTypes, setPipelineTypes] = useState([]);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    Promise.all([fetchAllPipelinesStates(), fetchAllPipelinesTypes()]).then(
      ([states, types]) => {
        setPipelineStates(states);
        setPipelineTypes(types);
      }
    );
  }, []);

  const columns = [
    {
      ...nameColumnFormat({
        url: setBaseUrl(`analysis/`)
      }),
      title: i18n("analyses.analysis-name"),
      key: "name",
      ...getTextSearchProps("name")
    },
    {
      title: i18n("analyses.state"),
      key: "state",
      dataIndex: "state",
      width: 170,
      filterMultiple: true,
      filters: pipelineStates,
      filterIcon(filtered) {
        return (
          <Icon
            type="filter"
            theme="filled"
            style={{ color: filtered ? blue6 : undefined }}
            className="t-state"
          />
        );
      },
      render(state) {
        return <AnalysisState state={state} />;
      }
    },
    {
      title: i18n("analyses.type"),
      key: "type",
      width: 250,
      dataIndex: "type",
      filterMultiple: true,
      filterIcon(filtered) {
        return (
          <Icon
            type="filter"
            theme="filled"
            style={{ color: filtered ? blue6 : undefined }}
            className="t-type"
          />
        );
      },
      filters: pipelineTypes
    },
    {
      title: i18n("analyses.submitter"),
      width: 200,
      key: "submitter",
      sorter: true,
      dataIndex: "submitter"
    },
    {
      ...dateColumnFormat(),
      title: "Created Date",
      dataIndex: "createdDate",
      key: "createdDate"
    },
    {
      title: i18n("analysis.duration"),
      key: "duration",
      width: 180,
      dataIndex: "duration",
      render(timestamp) {
        return getHumanizedDuration({ date: timestamp });
      }
    },
    {
      title: "",
      key: "download",
      fixed: "right",
      render(text, record) {
        return (
          <Button
            shape="circle-outline"
            disabled={record.state.value !== "COMPLETED"}
            href={setBaseUrl(`ajax/analyses/download/${record.id}`)}
            download
            icon="download"
          />
        );
      }
    }
  ];

  let rowSelection;
  if (CAN_MANAGE) {
    rowSelection = {
      selectedRowKeys: selected,
      onChange: selectedRowKeys => setSelected(selectedRowKeys),
      getCheckboxProps: record => ({
        name: record.name,
        disabled: !record.modifiable
      })
    };
  }

  const buttons = (
    <Popconfirm
      placement="bottomRight"
      title={i18n("analyses.delete-confirm").replace(
        "[COUNT]",
        selected.length
      )}
      onVisibleChange={visible => setDeleting(visible)}
      onConfirm={() => deleteAnalyses(selected).then(() => setSelected([]))}
    >
      <Button
        className="t-delete-selected"
        loading={deleting}
        disabled={!selected.length}
        onClick={() => setDeleting(true)}
      >
        {i18n("analyses.delete")}
      </Button>
    </Popconfirm>
  );

  return (
    <div>
      <div
        style={{
          marginBottom: SPACE_MD,
          display: "flex",
          flexDirection: "row-reverse"
        }}
      >
        <AnalysesQueue />
      </div>
      <PagedTable
        buttons={buttons}
        columns={columns}
        rowSelection={rowSelection}
        rowKey={record => record.id}
      />
    </div>
  );
}

AnalysesTable.propTypes = {};
