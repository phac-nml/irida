import React from "react";
import { Collapse, Empty, List, notification, Progress, Space } from "antd";
import { IconLoading } from "../../icons/Icons";
import {
  downloadGenomeAssemblyFile,
  downloadSequencingObjectFile,
  fetchSampleFiles,
  useRemoveSampleFilesMutation,
} from "../../../apis/samples/samples";
import { PairedFileRenderer } from "../../sequence-files/PairedFileRenderer";
import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";
import { SingleEndFileRenderer } from "../../sequence-files/SingleEndFileRenderer";
import { DragUpload } from "../../files/DragUpload";
import { useDispatch, useSelector } from "react-redux";
import {
  removeFileObjectFromSample,
  setSampleFiles,
} from "../sampleFilesSlice";
import {
  uploadSequenceFiles,
  uploadAssemblyFiles,
  uploadFast5Files,
} from "../../../apis/samples/samples";
import { WarningAlert } from "../../alerts";
const { Panel } = Collapse;

/**
 * React component to display, remove, download, and upload sample files
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFiles() {
  const { sample, projectId } = useSelector((state) => state.sampleReducer);
  const { files, loading } = useSelector((state) => state.sampleFilesReducer);
  const dispatch = useDispatch();

  const [removeSampleFilesFromSample] = useRemoveSampleFilesMutation();

  const [seqFileprogress, setSeqFileProgress] = React.useState(0);
  const [assemblyProgress, setAssemblyProgress] = React.useState(0);
  const [fast5Progress, setFast5Progress] = React.useState(0);

  const [sequenceFiles, setSequenceFiles] = React.useState([]);
  const [assemblyFiles, setAssemblyFiles] = React.useState([]);
  const [fast5Files, setFast5Files] = React.useState([]);

  const [filesToUpload, setFilesToUpload] = React.useState([]);

  const acceptedFileTypes =
    ".fasta, .fastq, .fast5, .fastq.gz, .fast5.gz, .fna";

  /*
  Function to get sample files from the server and dispatch to the store
   */
  const getSampleFiles = () => {
    fetchSampleFiles({ sampleId: sample.identifier, projectId })
      .then((data) => {
        dispatch(setSampleFiles(data));
      })
      .catch((e) => notification.error({ message: e }));
  };

  /*
   Get the sample files and set them in the redux store on component load
   and to refetch if the sample identifier or project identifier change
   */
  React.useEffect(getSampleFiles, [sample.identifier, projectId]);

  /*
  Call function to upload files to server once files are
  selected through the open file dialog or if they are
  dragged and dropped
   */
  React.useEffect(() => {
    uploadFiles();
  }, [filesToUpload]);

  /*
  Custom function to upload sequence, assembly, and fast5 files uploaded
  using the ant design upload (DragUpload) component
   */
  const uploadFiles = () => {
    if (sequenceFiles.length) {
      const seqFileUploadconfig = {
        headers: { "content-type": "multipart/form-data" },
        onUploadProgress: (progressEvent) => {
          if (progressEvent.loaded === progressEvent.total) {
            setSequenceFiles([]);
            setSeqFileProgress(0);
          } else {
            setSeqFileProgress(
              (progressEvent.loaded / progressEvent.total) * 100.0
            );
          }
        },
      };

      let formData = new FormData();
      sequenceFiles.map((f, index) => {
        formData.append(`file[${index}]`, sequenceFiles[index]);
      });

      uploadSequenceFiles({
        sampleId: sample.identifier,
        formData,
        config: seqFileUploadconfig,
      })
        .then(() => {
          notification.success({
            message: i18n("SampleFiles.successfullyUploaded", "sequence"),
          });
          getSampleFiles();
        })
        .catch((error) => {
          notification.error({
            message: i18n("SampleFiles.uploadError", "sequence"),
          });
        });
    }

    if (assemblyFiles.length) {
      const assemblyUploadConfig = {
        headers: { "content-type": "multipart/form-data" },
        onUploadProgress: (progressEvent) => {
          if (progressEvent.loaded === progressEvent.total) {
            setAssemblyFiles([]);
            setAssemblyProgress(0);
          } else {
            setAssemblyProgress(
              (progressEvent.loaded / progressEvent.total) * 100.0
            );
          }
        },
      };

      let formData = new FormData();
      assemblyFiles.map((f, index) => {
        formData.append(`file[${index}]`, assemblyFiles[index]);
      });

      uploadAssemblyFiles({
        sampleId: sample.identifier,
        formData,
        config: assemblyUploadConfig,
      })
        .then(() => {
          notification.success({
            message: i18n("SampleFiles.successfullyUploaded", "assembly"),
          });
          getSampleFiles();
        })
        .catch((error) => {
          notification.error({
            message: i18n("SampleFiles.uploadError", "assembly"),
          });
        });
    }

    if (fast5Files.length) {
      const fast5UploadConfig = {
        headers: { "content-type": "multipart/form-data" },
        onUploadProgress: (progressEvent) => {
          if (progressEvent.loaded === progressEvent.total) {
            setFast5Files([]);
            setFast5Progress(0);
          } else {
            setFast5Progress(
              (progressEvent.loaded / progressEvent.total) * 100.0
            );
          }
        },
      };

      let formData = new FormData();
      fast5Files.map((f, index) => {
        formData.append(`file[${index}]`, fast5Files[index]);
      });

      uploadFast5Files({
        sampleId: sample.identifier,
        formData,
        config: fast5UploadConfig,
      })
        .then(() => {
          notification.success({
            message: i18n("SampleFiles.successfullyUploaded", "fast5"),
          });
          getSampleFiles();
        })
        .catch((error) => {
          notification.error({
            message: i18n("SampleFiles.uploadError", "fast5"),
          });
        });
    }
  };

  // Options for DragUpload component
  const sampleFileUploadOptions = {
    multiple: true,
    showUploadList: false,
    accept: acceptedFileTypes,
    progress: { strokeWidth: 5 },
    beforeUpload(file, fileList) {
      setFilesToUpload(fileList);

      /*
      Get the fastq, assembly, and fast5 files from the fileList (files selected to upload)
       */
      let fastqFilesList = fileList.filter((currFile) => {
        let name = currFile.name;
        let tokens = name.split(".");

        return (
          tokens[tokens.length - 1] === "fastq" ||
          (tokens[tokens.length - 2] === "fastq" &&
            tokens[tokens.length - 1] === "gz")
        );
      });

      let assemblyFilesList = fileList.filter((currFile) => {
        let name = currFile.name;
        let tokens = name.split(".");

        return (
          tokens[tokens.length - 1] === "fasta" ||
          tokens[tokens.length - 1] === "fna"
        );
      });

      let fast5FilesList = fileList.filter((currFile) => {
        let name = currFile.name;
        let tokens = name.split(".");

        return (
          tokens[tokens.length - 1] === "fast5" ||
          (tokens[tokens.length - 2] === "fast5" &&
            tokens[tokens.length - 1] === "gz")
        );
      });

      if (fastqFilesList.length) {
        setSequenceFiles(fastqFilesList);
      }

      if (assemblyFilesList.length) {
        setAssemblyFiles(assemblyFilesList);
      }

      if (fast5FilesList.length) {
        setFast5Files(fast5FilesList);
      }

      return false;
    },
  };

  /*
  Download sequence files (paired, single, fast5)
   */
  const downloadSequenceFile = ({ sequencingObjectId, sequenceFileId }) => {
    notification.success({
      message: i18n("SampleFiles.startingSequenceFileDownload"),
    });
    downloadSequencingObjectFile({ sequencingObjectId, sequenceFileId });
  };

  /*
   Download genome assembly files
   */
  const downloadAssemblyFile = ({ sampleId, genomeAssemblyId }) => {
    notification.success({
      message: i18n("SampleFiles.startingAssemblyDownload"),
    });
    downloadGenomeAssemblyFile({ sampleId, genomeAssemblyId });
  };

  /*
  Remove sequencingobjects and/or genomeassembly objects from sample
   */
  const removeSampleFiles = ({ fileObjectId, type }) => {
    removeSampleFilesFromSample({
      sampleId: sample.identifier,
      fileObjectId,
      type,
    })
      .then(({ data }) => {
        notification.success({ message: data.message });
        dispatch(removeFileObjectFromSample({ fileObjectId, type }));
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  return loading ? (
    <IconLoading />
  ) : (
    <Space size="large" direction="vertical" style={{ width: `100%` }}>
      <DragUpload
        className="t-upload-sample-files"
        uploadText={i18n("SampleFiles.uploadText")}
        uploadHint={i18n("SampleFiles.uploadHint")}
        options={sampleFileUploadOptions}
      />
      {sequenceFiles.length || assemblyFiles.length || fast5Files.length ? (
        <div>
          <div>
            <WarningAlert
              message={i18n("SampleFiles.doNotCloseWindowWarning")}
            />
            <div>
              {sequenceFiles.length ? (
                <span>
                  {i18n("SampleFiles.uploadProgress", "Sequence")}
                  :
                  <Progress percent={seqFileprogress} />
                  <Collapse>
                    <Panel
                      header={i18n("SampleFiles.filesUploading", "Sequence")}
                      key="1"
                    >
                      <List split={false}>
                        {sequenceFiles.map((currFile, index) => {
                          return (
                            <List.Item key={`seq-file-${index}`}>
                              - {currFile.name}
                            </List.Item>
                          );
                        })}
                      </List>
                    </Panel>
                  </Collapse>
                </span>
              ) : null}
              {assemblyFiles.length ? (
                <span>
                  {i18n("SampleFiles.uploadProgress", "Assembly")}
                  :
                  <Progress percent={assemblyProgress} />
                  <Collapse>
                    <Panel
                      header={i18n("SampleFiles.filesUploading", "Assembly")}
                      key="1"
                    >
                      <List split={false}>
                        {assemblyFiles.map((currFile, index) => {
                          return (
                            <List.Item key={`assembly-file-${index}`}>
                              - {currFile.name}
                            </List.Item>
                          );
                        })}
                      </List>
                    </Panel>
                  </Collapse>
                </span>
              ) : null}
              {fast5Files.length ? (
                <span>
                  {i18n("SampleFiles.uploadProgress", "Fast5")}
                  :
                  <Progress percent={fast5Progress} />
                  <Collapse>
                    <Panel
                      header={i18n("SampleFiles.filesUploading", "Fast5")}
                      key="1"
                    >
                      <List split={false}>
                        {fast5Files.map((currFile, index) => {
                          return (
                            <List.Item key={`fast5-file-${index}`}>
                              - {currFile.name}
                            </List.Item>
                          );
                        })}
                      </List>
                    </Panel>
                  </Collapse>
                </span>
              ) : null}
            </div>
          </div>
        </div>
      ) : null}

      {Object.keys(files).length !== 0 ? (
        <div style={{ maxHeight: "400px", overflowY: "auto" }}>
          <Space size="large" direction="vertical" style={{ width: `100%` }}>
            {files.singles && (
              <SequenceFileTypeRenderer title={i18n("SampleFiles.singles")}>
                <SingleEndFileRenderer
                  files={files.singles}
                  sampleId={sample.identifier}
                  downloadSequenceFile={downloadSequenceFile}
                  removeSampleFiles={removeSampleFiles}
                />
              </SequenceFileTypeRenderer>
            )}
            {files.paired && (
              <SequenceFileTypeRenderer title={i18n("SampleFiles.paired")}>
                {files.paired.map((pair) => (
                  <PairedFileRenderer
                    key={`pair-${pair.identifier}`}
                    pair={pair}
                    sampleId={sample.identifier}
                    downloadSequenceFile={downloadSequenceFile}
                    removeSampleFiles={removeSampleFiles}
                  />
                ))}
              </SequenceFileTypeRenderer>
            )}
            {files.fast5 && (
              <SequenceFileTypeRenderer title={i18n("SampleFiles.fast5")}>
                <SingleEndFileRenderer
                  files={files.fast5}
                  sampleId={sample.identifier}
                  downloadSequenceFile={downloadSequenceFile}
                  removeSampleFiles={removeSampleFiles}
                />
              </SequenceFileTypeRenderer>
            )}
            {files.assemblies && (
              <SequenceFileTypeRenderer title={i18n("SampleFiles.assemblies")}>
                <SingleEndFileRenderer
                  files={files.assemblies}
                  fastqcResults={false}
                  sampleId={sample.identifier}
                  downloadAssemblyFile={downloadAssemblyFile}
                  removeSampleFiles={removeSampleFiles}
                />
              </SequenceFileTypeRenderer>
            )}
          </Space>
        </div>
      ) : (
        <Empty description={i18n("SampleFiles.no-files")} />
      )}
    </Space>
  );
}
