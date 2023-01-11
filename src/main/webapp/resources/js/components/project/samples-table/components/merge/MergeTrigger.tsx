import React, { Suspense, useCallback, useState } from "react";

import { SelectedSample } from "../../../../../types/irida";
import { separateLockedAndUnlockedSamples } from "../../../../../utilities/sample-utilities";
import { useProjectSamples } from "../../useProjectSamplesContext";

const MergeModal = React.lazy(() => import("./MergeModal"));

type MergeTriggerProps = {
  children: JSX.Element;
};

export type Samples =
  | [Array<SelectedSample>, Array<SelectedSample>]
  | undefined;

export default function MergeTrigger({
  children,
}: MergeTriggerProps): JSX.Element {
  const { state } = useProjectSamples();
  const [samples, setSamples] = useState<Samples>(undefined);
  const [visible, setVisible] = useState<boolean>(false);

  function onClick() {
    const [unlocked, locked] = separateLockedAndUnlockedSamples(
      Object.values(state.selection.selected)
    );

    if (unlocked.length >= 2) {
      setSamples([unlocked, locked]);
      setVisible(true);
    } else {
      alert("NOT ENOUGH SAMPLES");
    }
  }

  const hideModal = useCallback(() => setVisible(false), []);

  return (
    <>
      {React.cloneElement(children, {
        onClick,
        disabled: state.selection.count < 2,
      })}
      {visible ? (
        <Suspense fallback={<span />}>
          <MergeModal
            visible={visible}
            samples={samples}
            hideModal={hideModal}
          />
        </Suspense>
      ) : null}
    </>
  );
}
