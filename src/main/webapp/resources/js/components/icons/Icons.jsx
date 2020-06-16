import React from "react";
import {
  AppstoreOutlined,
  BoldOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  CloseOutlined,
  CloudServerOutlined,
  CloudUploadOutlined,
  CodeOutlined,
  DeleteOutlined,
  DownloadOutlined,
  DownOutlined,
  EditOutlined,
  ExclamationCircleOutlined,
  ExperimentOutlined,
  FileExcelOutlined,
  FileOutlined,
  FilterOutlined,
  FolderOutlined,
  HomeOutlined,
  InfoCircleOutlined,
  ItalicOutlined,
  LinkOutlined,
  LoadingOutlined,
  LockOutlined,
  LoginOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  OrderedListOutlined,
  PlusCircleTwoTone,
  QuestionCircleOutlined,
  QuestionOutlined,
  SearchOutlined,
  ShoppingCartOutlined,
  SortAscendingOutlined,
  SortDescendingOutlined,
  StopOutlined,
  StrikethroughOutlined,
  SwapOutlined,
  SyncOutlined,
  TableOutlined,
  TeamOutlined,
  UnorderedListOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { blue6 } from "../../styles/colors";

/**
 * @fileOverview All user interface icons used in JSX files should be loaded
 * through this file to ensure standardization and ability to quickly update
 * all icons.
 *
 * Passing `props` in and spreading them allows any attribute to be passed
 * along to the icon (e.g. classes, styles, etc...).
 */

export const IconClock = ({ ...props }) => <ClockCircleOutlined {...props} />;

export const IconCloseCircle = ({ ...props }) => (
  <CloseCircleOutlined {...props} />
);

export const IconTableFilter = ({ filtered, ...props }) => (
  <FilterOutlined style={{ color: filtered ? blue6 : undefined }} {...props} />
);

export const IconSearch = ({ ...props }) => <SearchOutlined {...props} />;

export const IconDownloadFile = ({ ...props }) => (
  <DownloadOutlined {...props} />
);

export const IconSyncSpin = ({ ...props }) => <SyncOutlined spin {...props} />;

export const IconCloudServer = ({ ...props }) => (
  <CloudServerOutlined {...props} />
);

export const IconCloudUpload = ({ ...props }) => (
  <CloudUploadOutlined {...props} />
);

export const IconLoading = ({ ...props }) => <LoadingOutlined {...props} />;

export const IconDropDown = ({ ...props }) => <DownOutlined {...props} />;

export const IconShoppingCart = ({ ...props }) => (
  <ShoppingCartOutlined {...props} />
);

export const IconInfoCircle = ({ ...props }) => (
  <InfoCircleOutlined {...props} />
);

export const IconQuestionCircle = ({ ...props }) => (
  <QuestionCircleOutlined {...props} />
);

export const IconQuestion = ({ ...props }) => <QuestionOutlined {...props} />;

export const IconExclamationCircle = ({ ...props }) => (
  <ExclamationCircleOutlined {...props} />
);

export const IconLocked = ({ ...props }) => <LockOutlined {...props} />;

export const IconFolder = ({ ...props }) => <FolderOutlined {...props} />;

export const IconTable = ({ ...props }) => <TableOutlined {...props} />;

export const IconPlusCircle = ({ ...props }) => (
  <PlusCircleTwoTone {...props} />
);

export const IconTrash = ({ ...props }) => <DeleteOutlined {...props} />;

export const IconRemove = ({ ...props }) => <CloseOutlined {...props} />;

export const IconEdit = ({ ...props }) => <EditOutlined {...props} />;

export const IconStop = ({ ...props }) => <StopOutlined {...props} />;

export const IconSwap = ({ ...props }) => <SwapOutlined {...props} />;

export const IconMetadataTemplate = ({ ...props }) => (
  <AppstoreOutlined {...props} />
);

/*
Editor Icons
 */

export const IconBold = ({ ...props }) => <BoldOutlined {...props} />;

export const IconItalic = ({ ...props }) => <ItalicOutlined {...props} />;

export const IconStrikeThrough = ({ ...props }) => (
  <StrikethroughOutlined {...props} />
);

export const IconUnorderedList = ({ ...props }) => (
  <UnorderedListOutlined {...props} />
);

export const IconOrderedList = ({ ...props }) => (
  <OrderedListOutlined {...props} />
);

export const IconCode = ({ ...props }) => <CodeOutlined {...props} />;

export const IconLinkOut = ({ ...props }) => <LinkOutlined {...props} />;

export const IconExperiment = ({ ...props }) => (
  <ExperimentOutlined {...props} />
);

export const IconHome = ({ ...props }) => <HomeOutlined {...props} />;

export const IconLogin = ({ ...props }) => <LoginOutlined {...props} />;

export const IconMembers = ({ ...props }) => <TeamOutlined {...props} />;

export const IconUser = ({ ...props }) => <UserOutlined {...props} />;

/*
File Type Icons
 */

export const IconFileExcel = ({ ...props }) => <FileExcelOutlined {...props} />;

export const IconFile = ({ ...props }) => <FileOutlined {...props} />;

/*
Menu Icons
 */

export const IconMenuFold = ({ ...props }) => <MenuFoldOutlined {...props} />;

export const IconMenuUnfold = ({ ...props }) => (
  <MenuUnfoldOutlined {...props} />
);

export const IconSortAscending = ({ ...props }) => (
  <SortAscendingOutlined {...props} />
);

export const IconSortDescending = ({ ...props }) => (
  <SortDescendingOutlined {...props} />
);
