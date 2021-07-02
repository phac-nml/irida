import React from "react";
import { Button, Input, notification, Row, Space, Table } from "antd";

import { IconDownloadFile } from "../../../../components/icons/Icons";

import { dateColumnFormat } from "../../../../components/ant.design/table-renderers";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { prepareAnalysisOutputsDownload } from "../../../../apis/analysis/analysis";
import {
  downloadIndividualOutputFile,
  downloadSelectedOutputFiles,
} from "../../../../apis/projects/analyses";

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

  const DOWNLOAD_BASE_URL = setBaseUrl("/ajax/analysis/download");
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
        return fileName + " (" + record.analysisOutputFileKey + ")";
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

  // Get the selected row ids (AnalysisOutputFile id)
  let rowSelection;
  rowSelection = {
    selectedRows: selected,
    onChange: (selectedRowKeys, selectedRows) => {
      setSelected(selectedRows);
    },
    getCheckboxProps: (record) => ({
      name: `analysis-output-file-id-${record.analysisOutputFileId}`,
    }),
  };

  function getFilename(path) {
    return path.replace(FILENAME_REGEX, "$1");
  }

  const downloadSelectFiles = () => {
    const currentlySelectedFiles = rowSelection.selectedRows;

    if (currentlySelectedFiles.length === 1) {
      const {
        analysisSubmissionId,
        analysisOutputFileId,
        sampleName,
        sampleId,
        filePath,
      } = currentlySelectedFiles[0];

      const fileName = `${sampleName}-sampleId-${sampleId}-analysisSubmissionId-${analysisSubmissionId}-${getFilename(
        filePath
      )}`;
      downloadIndividualOutputFile(
        analysisSubmissionId,
        analysisOutputFileId,
        fileName
      );
    } else if (currentlySelectedFiles.length > 1) {
      // Remove the workflowDescription from the objects as the server expects objects without it
      const outputsToDownload = currentlySelectedFiles.map(
        ({ workflowDescription, ...keepAttrs }) => keepAttrs
      );

      prepareAnalysisOutputsDownload(outputsToDownload).then(() => {
        const downloadUrl = `${DOWNLOAD_BASE_URL}/selection?filename=${
          projectId ? `projectId-${projectId}` : `user`
        }-batch-download-analysis-output-files`;
        notification.success({
          message: "Starting download of selected files",
        });
        downloadSelectedOutputFiles(downloadUrl);
      });
    }
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
    <Space direction="vertical" style={{ display: "block" }}>
      <Row justify={"space-between"}>
        <Button
          icon={<IconDownloadFile />}
          onClick={downloadSelectFiles}
          disabled={!selected.length}
        >
          {i18n("SingleSampleAnalysisOutputs.download")}
        </Button>
        <Search
          style={{ width: 300 }}
          onSearch={filterOutputsByTerm}
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
        pagination={{
          pageSizeOptions: PAGE_SIZE_OPTIONS,
          defaultPageSize: DEFAULT_PAGE_SIZE,
          showSizeChanger: true,
        }}
      />
    </Space>
  );
}
