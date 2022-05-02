import React, { lazy, Suspense } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Button, Dropdown, Menu, message, Row, Space } from "antd";
import {
  addToCart,
  clearFilterByFile,
  downloadSamples,
  exportSamplesToFile,
  filterByFile,
  reloadTable,
} from "../../redux/samplesSlice";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import {
  validateSamplesForLinker,
  validateSamplesForMerge,
  validateSamplesForRemove,
} from "../services/sample.utilities";
import {
  IconCloseSquare,
  IconCloudDownload,
  IconCloudUpload,
  IconCode,
  IconDropDown,
  IconFile,
  IconFileExcel,
  IconPlusSquare,
  IconShare,
  IconShoppingCart,
} from "../../../../components/icons/Icons";
import {
  CloseCircleOutlined,
  FileTextOutlined,
  MergeCellsOutlined,
} from "@ant-design/icons";
import { useGetProjectDetailsQuery } from "../../../../apis/projects/project";

const MergeModal = lazy(() => import("./MergeModal"));
const RemoveModal = lazy(() => import("./RemoveModal"));
const CreateModal = lazy(() => import("./CreateNewSample"));
const LinkerModal = lazy(() => import("./LinkerModal"));
const FilterByFileModal = lazy(() => import("./FilterByFileModal"));

/**
 * React element to render a row of actions that can be performed on
 * samples in the table
 * @returns {JSX.Element}
 * @constructor
 */
export default function SamplesMenu() {
  const dispatch = useDispatch();

  const {
    projectId,
    selected,
    selectedCount,
    filterByFile: fileFiltered,
  } = useSelector((state) => state.samples);
  const { data: details = {} } = useGetProjectDetailsQuery(projectId);

  const [mergeVisible, setMergeVisible] = React.useState(false);
  const [removedVisible, setRemovedVisible] = React.useState(false);
  const [createSampleVisible, setCreateSampleVisible] = React.useState(false);
  const [linkerVisible, setLinkerVisible] = React.useState(false);
  const [filterByFileVisible, setFilterByFileVisible] = React.useState(false);
  const [sorted, setSorted] = React.useState({});

  /**
   * When a merge is completed, hide the modal and ask
   * the table to reset
   */
  const onMergeComplete = () => {
    setMergeVisible(false);
    dispatch(reloadTable());
  };

  const onRemoveComplete = () => {
    setRemovedVisible(false);
    dispatch(reloadTable());
  };

  const onCreate = () => {
    setCreateSampleVisible(false);
    dispatch(reloadTable());
  };

  const onAddToCart = () => {
    dispatch(addToCart());
  };

  const onDownload = () => {
    dispatch(downloadSamples());
  };

  const onNCBI = () => {
    window.location.href = setBaseUrl(
      `/projects/${projectId}/export/ncbi?ids=${Object.values(selected)
        .map((s) => s.id)
        .join(",")}`
    );
  };

  const onExport = (type) => {
    dispatch(exportSamplesToFile(type));
  };

  /**
   * Format samples to share with other projects,
   * store minimal information in localStorage
   */
  const shareSamples = () => {
    if (selected.size === 0) return;

    const samples = Object.values(selected).map(
      ({ id, sampleName: name, owner }) => ({ id, name, owner })
    );

    // Store them to window storage for later use.
    window.sessionStorage.setItem(
      "share",
      JSON.stringify({
        samples,
        projectId,
        timestamp: Date.now(),
      })
    );

    // Redirect user to share page
    window.location.href = setBaseUrl(`/projects/${projectId}/share`);
  };

  /**
   * Validate samples for specific modals and open the appropriate modal
   * if the right samples are available.
   * @param {string} name - which modal to open
   */
  const validateAndOpenModalFor = (name) => {
    if (name === "merge") {
      const validated = validateSamplesForMerge(selected);
      if (validated.valid.length >= 2) {
        setSorted(validated);
        setMergeVisible(true);
      } else {
        message.error(i18n("SamplesMenu.merge.error"));
      }
    } else if (name === "remove") {
      const validated = validateSamplesForRemove(selected, projectId);
      if (validated.valid.length > 0) {
        setSorted(validated);
        setRemovedVisible(true);
      } else message.error(i18n("SamplesMenu.remove.error"));
    } else if (name === "linker") {
      const validated = validateSamplesForLinker(selected, projectId);
      if (validated.associated.length > 0) {
        message.error(i18n("SampleMenu.linker.error"));
      } else {
        setSorted(validated.valid);
        setLinkerVisible(true);
      }
    }
  };

  const onFilterByFile = ({ samples, filename }) => {
    dispatch(filterByFile({ filename, samples }));
    setFilterByFileVisible(false);
  };

  const toolsMenu = (
    <Menu className="t-tools-dropdown">
      {!details.remote && (
        <Menu.Item
          disabled={selectedCount < 2}
          key="merge-menu"
          icon={<MergeCellsOutlined />}
          onClick={() => validateAndOpenModalFor("merge")}
          className="t-merge"
        >
          {i18n("SamplesMenu.merge")}
        </Menu.Item>
      )}
      <Menu.Item
        disabled={selectedCount === 0}
        key="share-menu"
        icon={<IconShare />}
        onClick={shareSamples}
        className="t-share"
      >
        {i18n("SamplesMenu.share")}
      </Menu.Item>
      <Menu.Item
        disabled={selectedCount === 0}
        key="remove-menu"
        icon={<IconCloseSquare />}
        onClick={() => validateAndOpenModalFor("remove")}
        className="t-remove"
      >
        {i18n("SamplesMenu.remove")}
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item key="import-menu" icon={<IconCloudUpload />}>
        <a
          href={setBaseUrl(`projects/${projectId}/sample-metadata/upload/file`)}
        >
          {i18n("SamplesMenu.import")}
        </a>
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item
        key="create-menu"
        icon={<IconPlusSquare />}
        onClick={() => setCreateSampleVisible(true)}
        className="t-create-sample"
      >
        {i18n("SamplesMenu.createSample")}
      </Menu.Item>
    </Menu>
  );

  const exportMenu = (
    <Menu className="t-export-dropdown">
      <Menu.Item
        className="t-download"
        disabled={selectedCount === 0}
        key="download-menu"
        icon={<IconCloudDownload />}
        onClick={onDownload}
      >
        {i18n("SampleMenu.download")}
      </Menu.Item>
      <Menu.Item
        key="linker-menu"
        icon={<IconCode />}
        onClick={() => validateAndOpenModalFor("linker")}
        className="t-linker"
      >
        {i18n("SampleMenu.linker")}
      </Menu.Item>
      <Menu.Item
        disabled={selectedCount === 0}
        key="ncbi-menu"
        icon={<IconCloudUpload />}
        onClick={onNCBI}
        className="t-ncbi"
      >
        {i18n("SampleMenu.ncbi")}
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item
        key="menu-excel"
        icon={<IconFileExcel />}
        onClick={() => onExport("excel")}
      >
        {i18n("SampleMenu.excel")}
      </Menu.Item>
      <Menu.Item
        key="menu-csv"
        icon={<IconFile />}
        onClick={() => onExport("csv")}
      >
        {i18n("SampleMenu.csv")}
      </Menu.Item>
    </Menu>
  );

  return (
    <>
      <Row justify="space-between">
        <Space>
          {details.canManage && (
            <Dropdown overlay={toolsMenu}>
              <Button className="t-sample-tools">
                {i18n("SamplesMenu.label")} <IconDropDown />
              </Button>
            </Dropdown>
          )}
          <Dropdown overlay={exportMenu}>
            <Button className="t-export">
              {i18n("SampleMenu.export")} <IconDropDown />
            </Button>
          </Dropdown>
          <Button
            className="t-add-cart-btn"
            icon={<IconShoppingCart />}
            onClick={onAddToCart}
            disabled={selectedCount === 0}
          >
            {i18n("SampleMenu.cart")}
          </Button>
        </Space>
        {fileFiltered ? (
          <Button
            shape="round"
            icon={<CloseCircleOutlined />}
            onClick={() => dispatch(clearFilterByFile())}
          >
            {i18n("SampleMenu.fileFilter.clear", fileFiltered.filename)}
          </Button>
        ) : (
          <Button
            onClick={() => setFilterByFileVisible(true)}
            icon={<FileTextOutlined />}
          >
            {i18n("SampleMenu.fileFilter")}
          </Button>
        )}
      </Row>
      {mergeVisible && (
        <Suspense fallback={<span />}>
          <MergeModal
            visible={mergeVisible}
            onComplete={onMergeComplete}
            onCancel={() => setMergeVisible(false)}
            samples={sorted}
          />
        </Suspense>
      )}
      {removedVisible && (
        <Suspense fallback={<span />}>
          <RemoveModal
            visible={removedVisible}
            onComplete={onRemoveComplete}
            onCancel={() => setRemovedVisible(false)}
            samples={sorted}
          />
        </Suspense>
      )}
      {createSampleVisible && (
        <Suspense fallback={<span />}>
          <CreateModal
            visible={createSampleVisible}
            onCancel={() => setCreateSampleVisible(false)}
            onCreate={onCreate}
          />
        </Suspense>
      )}
      {linkerVisible && (
        <Suspense fallback={<span />}>
          <LinkerModal
            visible={linkerVisible}
            sampleIds={sorted}
            projectId={projectId}
            onFinish={() => setLinkerVisible(false)}
          />
        </Suspense>
      )}
      {filterByFileVisible && (
        <Suspense fallback={<span />}>
          <FilterByFileModal
            visible={filterByFileVisible}
            onCancel={() => setFilterByFileVisible(false)}
            onComplete={onFilterByFile}
          />
        </Suspense>
      )}
    </>
  );
}
