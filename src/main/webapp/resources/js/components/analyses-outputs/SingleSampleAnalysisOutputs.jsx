import React from "react";
import { Button, Input, notification, Row, Space, Table } from "antd";

import { IconDownloadFile } from "../icons/Icons";

import { dateColumnFormat } from "../ant.design/table-renderers";
import { setBaseUrl } from "../../utilities/url-utilities";
import {
  downloadIndividualOutputFile,
  downloadSelectedOutputFiles,
  prepareAnalysisOutputsDownload,
} from "../../apis/analyses/analyses";
import debounce from "lodash/debounce";
import { getPaginationOptions } from "../../utilities/antdesign-table-utilities";

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
  const [selectedRows, setSelectedRows] = React.useState([]);
  /*
    Even though we are setting the checkbox value to the row data we also store the row keys
    which are used for clearing the selected checkboxes after a download has started
  */
  const [selectedRowKeys, setSelectedRowKeys] = React.useState([]);
  const [filteredOutputs, setFilteredOutputs] = React.useState(null);

  const paginationOptions = React.useMemo(
    () => getPaginationOptions(outputs.length),
    [outputs.length]
  );

  // Regex for getting file name from path
  const FILENAME_REGEX = /.*\/(.+\.\w+)/;

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
                ? `/projects/${projectId}/samples/${record.sampleId}`
                : `/samples/${record.sampleId}`
            )}
            target="_blank"
            className="t-sample-name"
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
        return (
          getFilename(filePath) + " (" + record.analysisOutputFileKey + ")"
        );
      },
    },
    {
      title: i18n("SingleSampleAnalysisOutputs.analysisType"),
      key: "analysisType",
      dataIndex: ["analysisType", "type"],
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
            href={setBaseUrl(`/analysis/${record.analysisSubmissionId}`)}
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
        return userFirstName + " " + record.userLastName;
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

  // Get the selected row objects
  let rowSelection;
  rowSelection = {
    selectedRows: selectedRows,
    selectedRowKeys: selectedRowKeys,
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedRows(selectedRows);
      setSelectedRowKeys(selectedRowKeys);
    },
    getCheckboxProps: (record) => ({
      name: `analysis-output-file-id-${record.analysisOutputFileId}`,
    }),
    fixed: true,
  };

  // Function to get file name from file path using regex
  const getFilename = (path) => {
    return path.replace(FILENAME_REGEX, "$1");
  };

  // Function to download an individual or collection of analysis output files
  const downloadSelectedFiles = () => {
    const currentlySelectedFiles = rowSelection.selectedRows;
    if (currentlySelectedFiles.length === 1) {
      const {
        analysisSubmissionId,
        analysisOutputFileId,
        filename,
      } = currentlySelectedFiles[0];

      //Download the selected output file
      downloadIndividualOutputFile(
        analysisSubmissionId,
        analysisOutputFileId,
        filename
      );
    } else if (currentlySelectedFiles.length > 1) {
      // Remove the workflowDescription from the objects as the server expects objects without it
      const outputsToDownload = currentlySelectedFiles.map(
        ({ workflowDescription, ...keepAttrs }) => keepAttrs
      );

      // Prepare the selected files and download
      prepareAnalysisOutputsDownload(outputsToDownload).then(() => {
        // Custom zipped directory name
        const zipFolderName = `${
          projectId ? `projectId-${projectId}` : `user`
        }-batch-download-analysis-output-files`;
        notification.success({
          message: i18n("SingleSampleAnalysisOutputs.startingDownload"),
        });
        //Download the selected output files into a zip folder
        downloadSelectedOutputFiles(zipFolderName);
      });
    }
    setSelectedRows([]);
    setSelectedRowKeys([]);
  };

  // Function to filter out outputs by the search term using debounce
  const filterOutputsByTerm = debounce((event) => {
    let searchStr = event.target.value;
    if (searchStr === "undefined" || searchStr === null) {
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
  }, 300);

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Row justify={"space-between"}>
        <Button
          icon={<IconDownloadFile />}
          onClick={downloadSelectedFiles}
          disabled={!selectedRows.length}
        >
          {i18n("SingleSampleAnalysisOutputs.download")}
        </Button>
        <Search
          style={{ width: 300 }}
          onChange={filterOutputsByTerm}
          placeholder={i18n("SingleSampleAnalysisOutputs.searchPlaceholder")}
          allowClear
        />
      </Row>
      <Table
        rowKey={(record) => record.analysisOutputFileId}
        loading={isLoading}
        scroll={{ x: "max-content" }}
        columns={columns}
        dataSource={filteredOutputs || (!isLoading && outputs)}
        tableLayout="auto"
        rowSelection={rowSelection}
        pagination={paginationOptions}
      />
    </Space>
  );
}
