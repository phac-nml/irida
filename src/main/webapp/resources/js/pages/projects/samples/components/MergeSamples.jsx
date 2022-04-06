import React, { lazy, Suspense } from "react";
import { useDispatch, useSelector } from "react-redux";
import { updateTable } from "../services/samplesSlice";

const MergeModal = lazy(() => import("./MergeModal"));

const VALID_MIN_COUNT = 2; // Bare minimum amount of samples to merge

export default function MergeSamples({ children }) {
  const dispatch = useDispatch();
  const { selected } = useSelector((state) => state.samples);

  const [visible, setVisible] = React.useState(false);

  const onComplete = () => {
    setVisible(false);
    dispatch(updateTable());
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      {visible && (
        <Suspense fallback={<span />}>
          <MergeModal
            visible={visible}
            onComplete={onComplete}
            onCancel={() => setVisible(false)}
            samples={selected}
          />
        </Suspense>
      )}
    </>
  );
}
