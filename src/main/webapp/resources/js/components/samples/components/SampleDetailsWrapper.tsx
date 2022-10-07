import React, { Dispatch, SetStateAction } from "react";
import { Provider } from "react-redux";
import { store } from "../store";
import SampleDetailsModal from "./SampleDetailsModal";

interface SampleDetailsWrapperProps {
  sampleId: number;
  projectId: number;
  displayActions: boolean;
  refetchSample?: () => void;
  visible: boolean;
  setVisible: Dispatch<SetStateAction<boolean>>;
}

export default function SampleDetailsWrapper({
  sampleId,
  projectId,
  displayActions,
  refetchSample,
  visible,
  setVisible,
}: SampleDetailsWrapperProps) {
  return (
    <Provider store={store}>
      <SampleDetailsModal
        sampleId={sampleId}
        projectId={projectId}
        displayActions={displayActions}
        refetch={refetchSample}
        visible={visible}
        setVisible={setVisible}
      />
    </Provider>
  );
}
