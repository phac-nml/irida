import React from "react";
import { Button, Col, Empty, notification, Row, Spin } from "antd";
import { InfoAlert, WarningAlert } from "../../alerts";
import { SampleFileConcatenate } from "./SampleFileContenate";

import { DragUpload } from "../../files/DragUpload";
import { FileUploadProgress } from "./upload-progress/FileUploadProgress";
import { SampleFileList } from "./SampleFileList";
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";
import {
  addToSequenceFiles,
  addToAssemblyFiles,
  addToFast5Files,
  fetchFilesForSample,
} from "../sampleFilesSlice";

import {
  FileUpload,
  uploadSequenceFiles,
  uploadAssemblyFiles,
  uploadFast5Files,
  useGetSampleFilesQryQuery,
} from "../../../apis/samples/samples";
import { SPACE_MD, SPACE_XS } from "../../../styles/spacing";

let abortController: AbortController;
/**
 * React component to display sample files and upload files to sample
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function SampleFiles() {
  const { sample, projectId, modifiable } = useAppSelector(
    (state) => state.sampleReducer
  );

  const { data: files = {}, isLoading: loading } = useGetSampleFilesQryQuery({
    sampleId: sample.identifier,
    projectId,
  });

  const { concatenateSelected } = useAppSelector(
    (state) => state.sampleFilesReducer
  );
  const dispatch = useAppDispatch();

  const [seqFileProgress, setSeqFileProgress] = React.useState<number>(0);
  const [assemblyProgress, setAssemblyProgress] = React.useState<number>(0);
  const [fast5Progress, setFast5Progress] = React.useState<number>(0);

  const [sequenceFiles, setSequenceFiles] = React.useState<FileUpload[]>([]);
  const [assemblyFiles, setAssemblyFiles] = React.useState<FileUpload[]>([]);
  const [fast5Files, setFast5Files] = React.useState<FileUpload[]>([]);

  const [filesToUpload, setFilesToUpload] = React.useState<FileUpload[]>([]);

  const [uploadCancelled, setUploadCancelled] = React.useState<boolean>(false);

  const acceptedFileTypes =
    ".fasta, .fastq, .fast5, .fastq.gz, .fast5.tar.gz, .fna";

  /*
   Get the sample files and set them in the redux store on component load
   and to refetch if any of the dependencies in the dependency array change
   */
  React.useEffect(() => {
    if (Object.keys(files).length > 0) {
      dispatch(
        fetchFilesForSample({
          sampleFiles: files,
        })
      );
    }
  }, [sample.identifier, projectId, dispatch, loading, files]);

  /*
  Call function to upload files to server once files are
  selected through the open file dialog or if they are
  dragged and dropped
   */
  React.useEffect(
    () => {
      if (filesToUpload.length) {
        uploadFiles();
      }
    }, // eslint-disable-next-line react-hooks/exhaustive-deps
    [filesToUpload]
  );

  /*
  Custom function to upload sequence, assembly, and fast5 files uploaded
  using the ant design upload (DragUpload) component
   */
  const uploadFiles = () => {
    abortController = new AbortController();
    const { signal } = abortController;

    setUploadCancelled(false);

    uploadSampleSequenceFiles(signal);
    uploadSampleAssemblyFiles(signal);
    uploadSampleFast5Files(signal);
  };

  /*
  Function to cancel the current upload request
 */
  const cancelUpload = () => {
    if (abortController !== undefined) {
      if (filesToUpload.length) setFilesToUpload([]);

      if (sequenceFiles.length) {
        setSequenceFiles([]);
        setSeqFileProgress(0);
      }

      if (fast5Files.length) {
        setFast5Files([]);
        setFast5Progress(0);
      }

      if (assemblyFiles.length) {
        setAssemblyFiles([]);
        setAssemblyProgress(0);
      }
      abortController.abort();
    }
  };

  /*
  Function to upload sequence files
   */
  const uploadSampleSequenceFiles = (signal: AbortSignal) => {
    if (sequenceFiles.length) {
      const seqFileUploadConfig = {
        headers: { "content-type": "multipart/form-data" },
        signal,
        onUploadProgress: (progressEvent: {
          loaded: number;
          total: number;
        }) => {
          if (progressEvent.loaded === progressEvent.total) {
            setSeqFileProgress(99);
          } else {
            setSeqFileProgress(
              Math.round((progressEvent.loaded / progressEvent.total) * 100.0)
            );
          }
        },
      };

      const formData = new FormData();
      sequenceFiles.forEach((_f, index) => {
        formData.append(
          `file[${index}]`,
          sequenceFiles[index] as unknown as File as Blob
        );
      });

      uploadSequenceFiles({
        sampleId: sample.identifier,
        formData,
        config: seqFileUploadConfig,
      })
        .then((response) => {
          notification.success({
            message: i18n("SampleFiles.successfullyUploaded", "sequence"),
          });
          dispatch(addToSequenceFiles({ sequenceFiles: response }));
          setSequenceFiles([]);
        })
        .catch((error) => {
          if (error !== "canceled") {
            notification.error({
              message: i18n("SampleFiles.uploadError", "sequence"),
            });
          } else {
            setUploadCancelled(true);
          }
        });
    }
  };

  /*
  Function to upload assembly files
 */
  const uploadSampleAssemblyFiles = (signal: AbortSignal) => {
    if (assemblyFiles.length) {
      const assemblyUploadConfig = {
        headers: { "content-type": "multipart/form-data" },
        signal,
        onUploadProgress: (progressEvent: {
          loaded: number;
          total: number;
        }) => {
          if (progressEvent.loaded === progressEvent.total) {
            setAssemblyProgress(99);
          } else {
            setAssemblyProgress(
              Math.round((progressEvent.loaded / progressEvent.total) * 100.0)
            );
          }
        },
      };

      const formData = new FormData();
      assemblyFiles.forEach((_f, index) => {
        formData.append(
          `file[${index}]`,
          assemblyFiles[index] as unknown as File as Blob
        );
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
          setAssemblyFiles([]);
        })
        .catch((error) => {
          if (error !== "canceled") {
            notification.error({
              message: i18n("SampleFiles.uploadError", "assembly"),
            });
          } else {
            setUploadCancelled(true);
          }
        });
    }
  };

  /*
  Function to upload fast5 files
 */
  const uploadSampleFast5Files = (signal: AbortSignal) => {
    if (fast5Files.length) {
      const fast5UploadConfig = {
        headers: { "content-type": "multipart/form-data" },
        signal,
        onUploadProgress: (progressEvent: {
          loaded: number;
          total: number;
        }) => {
          if (progressEvent.loaded === progressEvent.total) {
            setFast5Progress(99);
          } else {
            setFast5Progress(
              Math.round((progressEvent.loaded / progressEvent.total) * 100.0)
            );
          }
        },
      };

      const formData = new FormData();
      fast5Files.forEach((_f, index) => {
        formData.append(
          `file[${index}]`,
          fast5Files[index] as unknown as File as Blob
        );
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
          setFast5Files([]);
        })
        .catch((error) => {
          if (error !== "canceled") {
            notification.error({
              message: i18n("SampleFiles.uploadError", "fast5"),
            });
          } else {
            setUploadCancelled(true);
          }
        });
    }
  };

  // Options for DragUpload component
  const sampleFileUploadOptions = {
    multiple: true,
    showUploadList: false,
    accept: acceptedFileTypes,
    progress: { strokeWidth: 5 },
    beforeUpload(_file: FileUpload, fileList: FileUpload[]) {
      setFilesToUpload(fileList);

      /*
      Get the fastq, assembly, and fast5 files from the fileList (files selected to upload)
       */
      const fastqFilesList = fileList.filter((currFile: FileUpload) => {
        const { name } = currFile;
        const tokens = name.split(".");

        return (
          tokens[tokens.length - 1] === "fastq" ||
          (tokens[tokens.length - 2] === "fastq" &&
            tokens[tokens.length - 1] === "gz")
        );
      });

      const assemblyFilesList = fileList.filter((currFile: FileUpload) => {
        const { name } = currFile;
        const tokens = name.split(".");

        return (
          tokens[tokens.length - 1] === "fasta" ||
          tokens[tokens.length - 1] === "fna"
        );
      });

      const fast5FilesList = fileList.filter((currFile: FileUpload) => {
        const { name } = currFile;
        const tokens = name.split(".");

        return (
          tokens[tokens.length - 1] === "fast5" ||
          (tokens[tokens.length - 3] === "fast5" &&
            tokens[tokens.length - 2] === "tar" &&
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
  Function to determine if user can upload files to the sample and have concatenate functionality
   */
  const sampleFilesTabActions = () => {
    if (!modifiable) return null;
    else {
      return (
        <Col span={24}>
          <div>
            <DragUpload
              uploadText={i18n("SampleFiles.uploadText")}
              uploadHint={i18n("SampleFiles.uploadHint")}
              options={sampleFileUploadOptions}
              props={{ className: "t-upload-sample-files" }}
            />
          </div>
          <SampleFileConcatenate>
            <Button
              className="t-concatenate-btn"
              disabled={concatenateSelected?.length < 2}
            >
              {i18n("SampleFiles.concatenate")}
            </Button>
          </SampleFileConcatenate>
        </Col>
      );
    }
  };

  /*
  Display the file upload progress for the files currently being uploaded
   */
  const displayFileUploadProgressForFileType = (
    fileUploadList: FileUpload[],
    fileType: string,
    progress: number
  ) => {
    if (!fileUploadList.length) return null;
    else {
      return (
        <FileUploadProgress
          files={fileUploadList}
          uploadProgress={progress}
          type={fileType}
        />
      );
    }
  };

  const displaySampleFileList = () => {
    if (Object.keys(files).length !== 0) {
      return <SampleFileList />;
    } else {
      return <Empty description={i18n("SampleFiles.no-files")} />;
    }
  };

  return loading ? (
    <Spin />
  ) : (
    <Row gutter={16}>
      {sampleFilesTabActions()}
      {uploadCancelled && (
        <InfoAlert
          message={i18n("SampleFiles.uploadCancelled")}
          style={{ margin: SPACE_XS, width: "100%" }}
        />
      )}
      {!!(
        sequenceFiles.length ||
        assemblyFiles.length ||
        fast5Files.length
      ) && (
        <Col span={24}>
          <WarningAlert
            message={i18n("SampleFiles.doNotCloseWindowWarning")}
            style={{ marginBottom: SPACE_MD }}
          />
          <Button
            style={{ marginBottom: SPACE_MD }}
            onClick={() => cancelUpload()}
          >
            {i18n("SampleFiles.cancelUpload")}
          </Button>
          {displayFileUploadProgressForFileType(
            sequenceFiles,
            "Sequence",
            seqFileProgress
          )}

          {displayFileUploadProgressForFileType(
            assemblyFiles,
            "Assembly",
            assemblyProgress
          )}

          {displayFileUploadProgressForFileType(
            fast5Files,
            "Fast5",
            fast5Progress
          )}
        </Col>
      )}

      <Col span={24} style={{ marginRight: 16 }}>
        {displaySampleFileList()}
      </Col>
    </Row>
  );
}
