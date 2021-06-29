import React from "react";
import { Button, Input, Table } from "antd";

import { SPACE_MD, SPACE_XS } from "../../../../styles/spacing";
import { IconDownloadFile } from "../../../../components/icons/Icons";

import { dateColumnFormat } from "../../../../components/ant.design/table-renderers";
import { setBaseUrl } from "../../../../utilities/url-utilities";

const { Search } = Input;

/**
 * React component for single sample analysis outputs
 * @param outputs The single sample analysis outputs
 * @param isLoading If data is still being retrieved from the server
 * @param projectId The project identifier
 * @returns {JSX.Element}
 * @constructor
 */

export default function SingleSampleAnalysisOutputs({
  outputs,
  isLoading,
  projectId,
}) {
  const [selected, setSelected] = React.useState([]);
  const [filteredOutputs, setFilteredOutputs] = React.useState(null);

  // Ant Design Table options
  const PAGE_SIZE_OPTIONS = [5, 10, 25, 50, 100];
  const DEFAULT_PAGE_SIZE = 10;

  const columns = [
    {
      title: i18n("SingleSampleAnalysisOutputs.id"),
      key: "analysisOutputFileId",
      dataIndex: "analysisOutputFileId",
    },
    {
      title: i18n("SingleSampleAnalysisOutputs.sampleName"),
      key: "sampleName",
      dataIndex: "sampleName",
      render(sampleName, record) {
        return (
          <a
            href={setBaseUrl(
              projectId
                ? `/projects/${projectId}/samples/${record["sampleId"]}`
                : `/samples/${record["sampleId"]}`
            )}
            target="_blank"
          >
            {sampleName}
          </a>
        );
      },
    },
    {
      title: i18n("SingleSampleAnalysisOutputs.file"),
      key: "fileName",
      dataIndex: "filePath",
      render(filePath, record) {
        let splitItems = filePath.split("/");
        let fileName = splitItems[splitItems.length - 1];
        return fileName + " (" + record["analysisOutputFileKey"] + ")";
      },
    },
    {
      title: i18n("SingleSampleAnalysisOutputs.analysisType"),
      key: "analysisType",
      dataIndex: "analysisType",
      render({ type }) {
        return type;
      },
    },
    {
      title: i18n("SingleSampleAnalysisOutputs.pipeline"),
      key: "pipeline",
      dataIndex: "workflowDescription",
      render(workflowDescription) {
        return workflowDescription.name + " " + workflowDescription.version;
      },
    },
    {
      title: i18n("SingleSampleAnalysisOutputs.analysisSubmission"),
      key: "analysisSubmissionName",
      dataIndex: "analysisSubmissionName",
      render(analysisSubmissionName, record) {
        return (
          <a
            href={setBaseUrl(`/analysis/${record["analysisSubmissionId"]}`)}
            target="_blank"
          >
            {analysisSubmissionName}
          </a>
        );
      },
    },
    {
      title: i18n("SingleSampleAnalysisOutputs.submitter"),
      key: "submitter",
      dataIndex: "userFirstName",
      render(userFirstName, record) {
        return userFirstName + " " + record["userLastName"];
      },
    },
    {
      ...dateColumnFormat(),
      title: i18n("SingleSampleAnalysisOutputs.createdDate"),
      key: "createdDate",
      dataIndex: "createdDate",
      sorter: false,
    },
  ];

  // Get the selected row ids (AnalysisOutputFile id)
  let rowSelection;
  rowSelection = {
    selectedRowKeys: selected,
    onChange: (selectedRowKeys) => setSelected(selectedRowKeys),
    getCheckboxProps: (record) => ({
      name: record.analysisOutputFileId,
    }),
  };

  // Function to filter out outputs by the search term
  const filterOutputsByTerm = (searchStr) => {
    if (
      searchStr.trim() === "" ||
      searchStr === "undefined" ||
      searchStr === null
    ) {
      setFilteredOutputs(null);
    } else {
      searchStr = String(searchStr).toLowerCase();
      // filePath contains the file name so we use it in the search
      const outputsContainingSearchValue = outputs.filter(
        (analysisOutput) =>
          analysisOutput.sampleName.toLowerCase().includes(searchStr) ||
          analysisOutput.analysisType.type.toLowerCase().includes(searchStr) ||
          analysisOutput.filePath.toLowerCase().includes(searchStr) ||
          analysisOutput.userFirstName.toLowerCase().includes(searchStr) ||
          analysisOutput.userLastName.toLowerCase().includes(searchStr)
      );
      setFilteredOutputs(outputsContainingSearchValue);
    }
  };

  return (
    <>
      <div
        style={{
          paddingBottom: SPACE_MD,
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <Button>
          <IconDownloadFile style={{ marginRight: SPACE_XS }} />
          {i18n("SingleSampleAnalysisOutputs.download")}
        </Button>
        <Search
          style={{ width: 300 }}
          onSearch={filterOutputsByTerm}
          placeholder={i18n("SingleSampleAnalysisOutputs.searchPlaceholder")}
          allowClear
        />
      </div>
      <Table
        rowKey={(record) => record.analysisOutputFileId}
        loading={isLoading}
        scroll={{ x: "max-content" }}
        columns={columns}
        dataSource={filteredOutputs || (!isLoading && outputs)}
        tableLayout="auto"
        rowSelection={rowSelection}
        pagination={{
          pageSizeOptions: PAGE_SIZE_OPTIONS,
          defaultPageSize: DEFAULT_PAGE_SIZE,
          showSizeChanger: true,
        }}
      />
    </>
  );
}
