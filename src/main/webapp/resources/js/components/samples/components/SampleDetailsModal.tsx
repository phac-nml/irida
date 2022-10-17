import { Modal, Skeleton } from "antd";
import React, { Dispatch, SetStateAction } from "react";
import ViewerHeader, { HEADER_HEIGHT } from "./ViewerHeader";
import { useGetSampleDetailsQuery } from "../../../apis/samples/samples";
import SampleDetails from "./SampleDetails";
import { setProjectDetails, setSample } from "../sampleSlice";
import { useAppDispatch } from "../../../hooks/useState";

export interface DisplaySampleDetailsProps {
  sampleId: number;
  projectId: number;
  displayActions?: boolean;
  refetch?: () => void;
  visible: boolean;
  setVisible: Dispatch<SetStateAction<boolean>>;
}

export type ViewerTab = "details" | "metadata" | "files" | "analyses";

export default function SampleDetailsModal({
  sampleId,
  projectId,
  displayActions,
  refetch,
  visible,
  setVisible,
}: DisplaySampleDetailsProps) {
  const dispatch = useAppDispatch();
  const [component, setComponent] = React.useState<ViewerTab>("details");

  // Get the sample ready to display
  const { data: details = {}, isLoading } = useGetSampleDetailsQuery({
    sampleId,
    projectId,
  });

  React.useEffect(() => {
    if (!isLoading) {
      dispatch(
        setSample({
          sample: details.sample,
          modifiable: details.modifiable,
          inCart: details.inCart,
        })
      );
      dispatch(
        setProjectDetails({
          projectId: details.projectId,
          projectName: details.projectName,
        })
      );
    }
  }, [
    details.modifiable,
    details.projectId,
    details.projectName,
    details.sample,
    dispatch,
    isLoading,
  ]);

  return (
    <Modal
      className="t-sample-details-modal"
      bodyStyle={{
        padding: 0,
        height: "80vh",
        overflowY: "hidden",
        display: "grid",
        gridTemplateRows: `${HEADER_HEIGHT}px auto`,
      }}
      open={visible}
      onCancel={() => setVisible(false)}
      footer={null}
      width={900}
      maskClosable={false}
    >
      {isLoading ? (
        <Skeleton active title />
      ) : (
        <>
          <ViewerHeader
            projectId={projectId}
            sampleId={sampleId}
            tab={component}
            onMenuChange={setComponent}
            displayActions={!!displayActions}
            refetch={refetch}
          />
          <div
            style={{
              overflow: "hidden",
              margin: 16,
            }}
          >
            <SampleDetails component={component} />
          </div>
        </>
      )}
    </Modal>
  );
}
