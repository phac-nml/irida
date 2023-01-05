import React, { Suspense, useState } from "react";
import { useProjectSamples } from "../../useProjectSamplesContext";
import { seperateLockedAndUnlockedSamples } from "../../../../../utilities/sample-utilities";
import { SelectedSample } from "../../../../../types/irida";

const MergeModal = React.lazy(() => import("./MergeModal"));

type MergeTriggerProps = {
  children: JSX.Element;
};

export default function MergeTrigger({
  children,
}: MergeTriggerProps): JSX.Element {
  const { state } = useProjectSamples();
  const [samples, setSamples] =
    useState<[Array<SelectedSample, Array<SelectedSample>> | undefined]>(
      undefined
    );
  const [visible, setVisible] = useState<boolean>(false);

  function onClick() {
    const [locked, unlocked] = seperateLockedAndUnlockedSamples(
      Object.values(state.selection.selected)
    );

    if (unlocked.length > 2) {
      setSamples([locked, unlocked]);
    } else {
      alert("NOT ENOUGHT SMAPLES");
    }
  }

  return (
    <>
      {React.cloneElement(children, {
        onClick,
        disabled: state.selection.count < 2,
      })}
      {visible ? (
        <Suspense fallback={<span />}>
          <MergeModal visible={visible} locked={locked} unlocked={unlocked} />
        </Suspense>
      ) : null}
    </>
  );
}
