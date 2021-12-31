import React from "react";
import {
  Button,
  Empty,
  List,
  notification,
  Progress,
  Result,
  Space,
} from "antd";
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
  const [progress, setProgress] = React.useState(0);
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
  Custom function to upload sequence, assembly, and fast5 files uploaded
  using the ant design upload (DragUpload) component
   */
  const uploadFiles = () => {
    if (sequenceFiles.length) {
      const config = {
        headers: { "content-type": "multipart/form-data" },
        onUploadProgress: (progressEvent) => {
          setProgress((progressEvent.loaded / progressEvent.total) * 100.0);
          if (progressEvent.loaded === progressEvent.total) {
            setSequenceFiles([]);
          }
        },
      };

      let formData = new FormData();
      sequenceFiles.map((f, index) => {
        formData.append(`file[${index}]`, sequenceFiles[index]);
      });

      uploadSequenceFiles({ sampleId: sample.identifier, formData, config })
        .then(() => {
          notification.success({
            message: "Successfully uploaded fastq files",
          });
          getSampleFiles();
        })
        .catch((error) => {
          notification.error({
            message: "There was an error uploading the fastq files",
          });
        });
    }

    if (assemblyFiles.length) {
      const assemblyConfig = {
        headers: { "content-type": "multipart/form-data" },
        onUploadProgress: (progressEvent) => {
          setAssemblyProgress(
            (progressEvent.loaded / progressEvent.total) * 100.0
          );
          if (progressEvent.loaded === progressEvent.total) {
            setAssemblyFiles([]);
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
        config: assemblyConfig,
      })
        .then(() => {
          notification.success({
            message: "Successfully uploaded fasta files",
          });
          getSampleFiles();
        })
        .catch((error) => {
          notification.error({
            message: "There was an error uploading the fasta files",
          });
        });
    }

    if (fast5Files.length) {
      const fast5Config = {
        headers: { "content-type": "multipart/form-data" },
        onUploadProgress: (progressEvent) => {
          setFast5Progress(
            (progressEvent.loaded / progressEvent.total) * 100.0
          );
          if (progressEvent.loaded === progressEvent.total) {
            setFast5Files([]);
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
        config: fast5Config,
      })
        .then(() => {
          notification.success({
            message: "Successfully uploaded fast5 files",
          });
          getSampleFiles();
        })
        .catch((error) => {
          notification.error({
            message: "There was an error uploading the fast5 files",
          });
        });
    }
  };

  // Options for DragUpload component
  const options = {
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

  // Download sequence files
  const downloadSequenceFile = ({ sequencingObjectId, sequenceFileId }) => {
    notification.success({
      message: i18n("SampleFiles.startingSequenceFileDownload"),
    });
    downloadSequencingObjectFile({ sequencingObjectId, sequenceFileId });
  };

  // Download genome assembly files
  const downloadAssemblyFile = ({ sampleId, genomeAssemblyId }) => {
    notification.success({
      message: i18n("SampleFiles.startingAssemblyDownload"),
    });
    downloadGenomeAssemblyFile({ sampleId, genomeAssemblyId });
  };

  // Remove sequencingobjects and/or genomeassembly objects from sample
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
  ) : Object.keys(files).length !== 0 ? (
    <Space size="large" direction="vertical" style={{ width: `100%` }}>
      <DragUpload
        className="t-upload-sample-files"
        uploadText={i18n("SampleFiles.uploadText")}
        uploadHint={i18n("SampleFiles.uploadHint")}
        options={options}
      />
      {filesToUpload.length ? (
        <div>
          <Button type="primary" onClick={() => uploadFiles()}>
            Start Upload
          </Button>
          {sequenceFiles.length ? <Progress percent={progress} /> : null}
          {assemblyFiles.length ? (
            <Progress percent={assemblyProgress} />
          ) : null}
          {fast5Files.length ? <Progress percent={fast5Progress} /> : null}
          Files to upload to sample:
          <List split={false}>
            {filesToUpload.map((currFile) => {
              return <List.Item>- {currFile.name}</List.Item>;
            })}
          </List>
        </div>
      ) : null}

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
    </Space>
  ) : (
    <Space size={`large`} direction={`vertical`} style={{ width: `100%` }}>
      <DragUpload
        className="t-upload-sample-files"
        uploadText={i18n("SampleFiles.uploadText")}
        uploadHint={i18n("SampleFiles.uploadHint")}
        options={options}
      />
      <Empty description={i18n("SampleFiles.no-files")} />
      {filesToUpload.length ? (
        <div>
          <Button type="primary" onClick={() => uploadFiles()}>
            Start Upload
          </Button>
          {sequenceFiles.length ? <Progress percent={progress} /> : null}
          {assemblyFiles.length ? (
            <Progress percent={assemblyProgress} />
          ) : null}
          {fast5Files.length ? <Progress percent={fast5Progress} /> : null}
          Files to upload to sample:
          <List split={false}>
            {filesToUpload.map((currFile) => {
              return <List.Item>- {currFile.name}</List.Item>;
            })}
          </List>
        </div>
      ) : null}
    </Space>
  );
}
