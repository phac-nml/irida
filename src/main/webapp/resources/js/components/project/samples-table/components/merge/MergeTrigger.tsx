import React from "react";
import { useProjectSamples } from "../../useProjectSamplesContext";
import { seperateLockedAndUnlockedSamples } from "../../../../../utilities/sample-utilities";

type MergeTriggerProps = {
  children: JSX.Element;
};

export default function MergeTrigger({
  children,
}: MergeTriggerProps): JSX.Element {
  const { state } = useProjectSamples();

  function onClick() {
    const [locked, unlocked] = seperateLockedAndUnlockedSamples(
      Object.values(state.selection.selected)
    );

    if (unlocked.length > 2) {
    }
  }

  return React.cloneElement(children, {
    onClick,
    disabled: state.selection.count < 2,
  });
}
