import React, { Suspense, useCallback, useState } from "react";

import { SelectedSample } from "../../../../../types/irida";
import { separateLockedAndUnlockedSamples } from "../../../../../utilities/sample-utilities";
import { useProjectSamples } from "../../useProjectSamplesContext";
import { Modal } from "antd";

const MergeModal = React.lazy(() => import("./MergeModal"));

type MergeTriggerProps = {
  children: JSX.Element;
};

export type Samples =
  | [Array<SelectedSample>, Array<SelectedSample>]
  | undefined;

/**
 * Wrapper for React Element which adds a click handler to it to open the merge samples modal.
 * Also checks to ensure that the correct number of samples are modifiable by the user (user must
 * be the owner).
 * @param children
 * @constructor
 */
export default function MergeTrigger({
  children,
}: MergeTriggerProps): JSX.Element {
  const { state } = useProjectSamples();
  const [samples, setSamples] = useState<Samples>(undefined);
  const [visible, setVisible] = useState<boolean>(false);

  function handleClick() {
    const [unlocked, locked] = separateLockedAndUnlockedSamples(
      Object.values(state.selection.selected)
    );

    if (unlocked.length >= 2) {
      setSamples([unlocked, locked]);
      setVisible(true);
    } else {
      Modal.info({
        title: i18n("MergeTrigger.error.locked.title"),
        content: i18n("MergeTrigger.error.locked.content", locked.length),
      });
    }
  }

  const handleClickCallback = useCallback(handleClick, [
    state.selection.selected,
  ]);
  const hideModalCallback = useCallback(() => setVisible(false), []);

  return (
    <>
      {React.cloneElement(children, {
        onClick: handleClickCallback,
        disabled: state.selection.count < 2,
      })}
      {visible && samples !== undefined ? (
        <Suspense fallback={<span />}>
          <MergeModal
            visible={visible}
            samples={samples}
            hideModal={hideModalCallback}
          />
        </Suspense>
      ) : null}
    </>
  );
}
