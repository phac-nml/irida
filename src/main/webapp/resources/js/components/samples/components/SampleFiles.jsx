import React from "react";
import { Button, Col, Empty, notification, Row } from "antd";
import { IconLoading } from "../../icons/Icons";
import { WarningAlert } from "../../alerts";
import { SampleFileConcatenate } from "./SampleFileContenate";

import { DragUpload } from "../../files/DragUpload";
import { FileUploadProgress } from "./upload-progress/FileUploadProgress";
import { SampleFileList } from "./SampleFileList";
import { useDispatch, useSelector } from "react-redux";
import {
  addToSequenceFiles,
  addToAssemblyFiles,
  addToFast5Files,
  setSampleFiles,
} from "../sampleFilesSlice";

import {
  fetchSampleFiles,
  uploadSequenceFiles,
  uploadAssemblyFiles,
  uploadFast5Files,
} from "../../../apis/samples/samples";
import { SPACE_MD } from "../../../styles/spacing";

/**
 * React component to display sample files and upload files to sample
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFiles() {
  const { sample, projectId, modifiable } = useSelector(
    (state) => state.sampleReducer
  );
  const { files, loading, concatenateSelected } = useSelector(
    (state) => state.sampleFilesReducer
  );
  const dispatch = useDispatch();

  const [seqFileProgress, setSeqFileProgress] = React.useState(0);
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
    fetchSampleFiles({
      sampleId: sample.identifier,
      projectId,
    })
      .then((data) => {
        dispatch(setSampleFiles(data));
      })
      .catch((e) => notification.error({ message: e }));
  };

  /*
   Get the sample files and set them in the redux store on component load
   and to refetch if the sample identifier or project change
   */
  React.useEffect(getSampleFiles, [sample.identifier, projectId]);

  /*
  Call function to upload files to server once files are
  selected through the open file dialog or if they are
  dragged and dropped
   */
  React.useEffect(() => {
    if (filesToUpload.length) {
      uploadFiles();
    }
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
              Math.round((progressEvent.loaded / progressEvent.total) * 100.0)
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
        .then((response) => {
          notification.success({
            message: i18n("SampleFiles.successfullyUploaded", "sequence"),
          });
          dispatch(addToSequenceFiles({ sequenceFiles: response }));
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
              Math.round((progressEvent.loaded / progressEvent.total) * 100.0)
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
        .then((response) => {
          notification.success({
            message: i18n("SampleFiles.successfullyUploaded", "assembly"),
          });
          dispatch(addToAssemblyFiles({ assemblies: response }));
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
              Math.round((progressEvent.loaded / progressEvent.total) * 100.0)
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
        .then((response) => {
          notification.success({
            message: i18n("SampleFiles.successfullyUploaded", "fast5"),
          });
          dispatch(addToFast5Files({ fast5: response }));
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

  return loading ? (
    <IconLoading />
  ) : (
    <Row gutter={[16, 16]}>
      {modifiable ? (
        <Col span={24}>
          <div>
            <DragUpload
              className="t-upload-sample-files"
              uploadText={i18n("SampleFiles.uploadText")}
              uploadHint={i18n("SampleFiles.uploadHint")}
              options={sampleFileUploadOptions}
            />
          </div>
          <div>
            <SampleFileConcatenate>
              <Button
                className="t-concatenate-btn"
                disabled={concatenateSelected?.length < 2}
              >
                {i18n("SampleFiles.concatenate")}
              </Button>
            </SampleFileConcatenate>
          </div>
        </Col>
      ) : null}
      {sequenceFiles.length || assemblyFiles.length || fast5Files.length ? (
        <Col span={24}>
          <WarningAlert
            message={i18n("SampleFiles.doNotCloseWindowWarning")}
            style={{ marginBottom: SPACE_MD }}
          />
          {sequenceFiles.length ? (
            <FileUploadProgress
              files={sequenceFiles}
              uploadProgress={seqFileProgress}
              type="Sequence"
            />
          ) : null}
          {assemblyFiles.length ? (
            <FileUploadProgress
              files={assemblyFiles}
              uploadProgress={assemblyProgress}
              type="Assembly"
            />
          ) : null}
          {fast5Files.length ? (
            <FileUploadProgress
              files={fast5Files}
              uploadProgress={fast5Progress}
              type="Fast5"
            />
          ) : null}
        </Col>
      ) : null}

      <Col span={24}>
        {Object.keys(files).length !== 0 ? (
          <SampleFileList />
        ) : (
          <Empty description={i18n("SampleFiles.no-files")} />
        )}
      </Col>
    </Row>
  );
}
